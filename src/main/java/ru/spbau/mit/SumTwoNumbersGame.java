package ru.spbau.mit;

import java.util.*;


public class SumTwoNumbersGame implements Game {
    private int firstNumber, secondNumber;
    private final GameServer server;
    private Random rand;
    private GameHandler gameHandler;
    private String currentPair;
    private boolean isFinished;

    public SumTwoNumbersGame(GameServer server) {
        this.server  = server;
        rand = new Random();
        gameHandler = new GameHandler();
        isFinished = false;
        Thread gameHandlerThread = new Thread(gameHandler);
        gameHandlerThread.start();
    }

    @Override
    public void onPlayerConnected(String id) {
        gameHandler.onPlayerConnected(id);
    }

    @Override
    public void onPlayerSentMsg(String id, String msg) {
        gameHandler.onPlayerSentMsg(id, msg);
    }

    private void generateNewGame() {
        firstNumber  = rand.nextInt(1000);
        secondNumber = rand.nextInt(1000);
        currentPair = Integer.toString(firstNumber) + " " +
                Integer.toString(secondNumber);
        synchronized (server) {
            server.broadcast(currentPair);
        }
    }

    private class GameHandler implements Runnable {
        private List<String> newConnectedIds =
                Collections.synchronizedList(new ArrayList<String>());
        private List<MyPair> sentMessages =
                Collections.synchronizedList(new ArrayList<MyPair>());

        @Override
        public synchronized void run() {
            generateNewGame();

            while (!isFinished) {
                while (newConnectedIds.size() != 0) {
                    synchronized (server) {
                        server.sendTo(newConnectedIds.remove(0), currentPair);
                    }
                }

                while (sentMessages.size() != 0) {
                    MyPair pair = sentMessages.remove(0);
                    String id  = pair.id;
                    String msg = pair.message;
                    try {
                        int answer = Integer.parseInt(msg);
                        if (answer == firstNumber + secondNumber) {
                            synchronized (server) {
                                server.sendTo(id, "Right");
                                server.broadcast("<" + id + "> won");
                            }
                            generateNewGame();
                        } else {
                            synchronized (server) {
                                server.sendTo(id, "Wrong");
                            }
                        }
                    } catch (NumberFormatException e) {
                    }
                }

                while (sentMessages.size() == 0 && newConnectedIds.size() == 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        public void onPlayerConnected(String id) {
            newConnectedIds.add(id);
            synchronized (this) {
                notify();
            }
        }

        public void onPlayerSentMsg(String id, String msg) {
            sentMessages.add(new MyPair(id, msg));
            synchronized (this) {
                notify();
            }
        }

        private class MyPair {
            public String id;
            public String message;

            public MyPair (String id, String message) {
                this.id = id;
                this.message = message;
            }
        }
    }
}
