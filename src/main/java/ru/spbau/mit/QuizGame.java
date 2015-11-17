package ru.spbau.mit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class QuizGame implements Game {
    private int delayUntilNextLetter;
    private int maxLettersToOpen;

    private boolean isStarted;
    private boolean isFinished;
    private boolean wasRightAnswer;

    private final GameServer server;

    private String dictionaryFilename;
    private BufferedReader reader;

    private String currentQuiz;
    private String currentAnswer;
    private int currentLine; // just for exception info
    private int currentLetterNumber;

    private String currentWinnerId;
    private String finishedBy;

    private final GameHandler gameHandler;
    private final CommunicationHandler comHandler;

    private List<String> newConnectedIds =
            Collections.synchronizedList(new ArrayList<String>());
    private List<MyPair> sentMessages =
            Collections.synchronizedList(new ArrayList<MyPair>());

    public QuizGame(GameServer server) throws IOException {
        this.server = server;
        isStarted = false;
        isFinished = false;
        comHandler = new CommunicationHandler();
        gameHandler = new GameHandler();
        Thread comHandlerThread = new Thread(comHandler);
        comHandlerThread.start();
        Thread gameHandlerThread = new Thread(gameHandler);
        gameHandlerThread.start();
    }

    @Override
    public void onPlayerConnected(String id) {
        comHandler.onPlayerConnected(id);
    }

    @Override
    public void onPlayerSentMsg(String id, String msg) {
        comHandler.onPlayerSentMsg(id, msg);
    }

    public void setDelayUntilNextLetter(Integer delayUntilNextLetter) {
        this.delayUntilNextLetter = delayUntilNextLetter;
    }

    public void setMaxLettersToOpen(Integer maxLettersToOpen) {
        this.maxLettersToOpen = maxLettersToOpen;
    }

    public void setDictionaryFilename(String dictionaryFilename) {
        this.dictionaryFilename = dictionaryFilename;
    }

    private void reloadFile() throws IOException {
        reader = new BufferedReader(new FileReader(dictionaryFilename));
        currentLine = 0;
    }

    private void parseNextLine() throws IOException {
        String nextString = reader.readLine();
        if (nextString == null) {
            reloadFile();
            parseNextLine();
        }

        String nextPair[];
        try {
            nextPair = nextString.split(";");
        } catch (NullPointerException e) {
            throw new IOException("string " + currentLine + " of file" +
                    dictionaryFilename + "has wrong format");
        }

        currentQuiz   = nextPair[0];
        currentAnswer = nextPair[1];
        currentLetterNumber = 0;
    }

    private String formNewRoundQuiz() {
        return "New round started: " + currentQuiz + " (" +
                Integer.toString(currentAnswer.length()) + " letters)";
    }

    private String formCurrentPrefix(int index) {
        return "Current prefix is " + currentAnswer.substring(0, index + 1);
    }

    private String formNobodyGuessed() {
        return "Nobody guessed, the word was " + currentAnswer;
    }

    private String formFinishMsg() {
        return "Game has been stopped by " + finishedBy;
    }

    private void revealTheWinner() {
        synchronized (server) {
            server.broadcast("The winner is " + currentWinnerId);
        }
    }

    private boolean startNextRound() {
        try {
            parseNextLine();
        } catch (IOException e) {
            synchronized (server) {
                server.broadcast("Wrong file format. Game over");
                return false;
            }
        }

        wasRightAnswer = false;
        currentWinnerId = "-1";
        synchronized (server) {
            server.broadcast(formNewRoundQuiz());
        }

        return true;
    }

    private boolean startNewGame() {
        try {
            reloadFile();
        } catch (IOException e) {
            synchronized (server) {
                server.broadcast("Wrong file format. Game over");
            }
            return false;
        }
        return startNextRound();
    }

    private void trywait(final Object obj, int timeout) {
        synchronized (obj) {
            try {
                if (timeout != 0)
                    obj.wait();
                else
                    obj.wait(timeout);
            } catch (InterruptedException e) {
            }
        }
    }

    private class GameHandler implements Runnable {
        public GameHandler() {

        }

        @Override
        public synchronized void run() {
            while (!isStarted) {
                trywait(this, 0);
            }

            if (!startNewGame()) {
                return;
            }

            while (!isFinished) {
                trywait(this, delayUntilNextLetter);
                if (isFinished) {
                    synchronized (server) {
                        server.broadcast(formFinishMsg());
                    }
                }
                if (wasRightAnswer) {
                    revealTheWinner();
                    if (!startNextRound()) {
                        return;
                    }
                } else {
                    if (currentLetterNumber == maxLettersToOpen) {
                        synchronized (server) {
                            server.broadcast(formNobodyGuessed());
                        }
                        if (!startNextRound()) {
                            return;
                        }
                    } else {
                        synchronized (server) {
                            server.broadcast(formCurrentPrefix(currentLetterNumber++));
                        }
                    }
                }
            }
        }
    }

    private class CommunicationHandler implements Runnable {
        public CommunicationHandler() {
        }

        @Override
        public void run() {
            while (!isFinished) {
                while (newConnectedIds.size() != 0) {
                    String id = newConnectedIds.remove(0);
                    if (isStarted) {
                        synchronized (server) {
                            server.sendTo(id, currentQuiz);
                            for (int i = 0; i < currentLetterNumber; i++) {
                                server.sendTo(id, formCurrentPrefix(i));
                            }
                        }
                    }
                }
                while (sentMessages.size() != 0) {
                    MyPair pair = sentMessages.remove(0);
                    String id = pair.id;
                    String msg = pair.msg;
                    if (msg.equals("!start")) {
                        if (!isFinished)
                            isStarted ^= true;
                        if (!isStarted) {
                            finishedBy = id;
                            isFinished = true;
                        }
                    } else {
                        if (msg.equals(currentAnswer)) {
                            wasRightAnswer = true;
                            currentWinnerId = id;
                        } else {
                            synchronized (server) {
                                server.sendTo(id, "Wrong try");
                            }
                        }
                    }
                }
                synchronized (gameHandler) {
                    gameHandler.notify();
                }
                while (newConnectedIds.size() == 0 && sentMessages.size() == 0) {
                    trywait(this, 0);
                }
            }
        }

        public synchronized void onPlayerConnected(String id) {
            newConnectedIds.add(id);
            notify();
        }

        public synchronized void onPlayerSentMsg(String id, String msg) {
            sentMessages.add(new MyPair(id, msg));
            notify();
        }
    }

    private class MyPair {
        public String id;
        public String msg;

        public MyPair(String id, String msg) {
            this.id = id;
            this.msg = msg;
        }
    }
}
