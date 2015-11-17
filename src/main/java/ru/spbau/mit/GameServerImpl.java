package ru.spbau.mit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class GameServerImpl implements GameServer {
    private final Game game;
    private volatile int currentId = 0;
    private final List<ConnectionHandler> handlersList =
            Collections.synchronizedList(new ArrayList<ConnectionHandler>());

    public GameServerImpl(String gameClassName, Properties properties)
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException,
            InvocationTargetException {
        Class<?> clazz = Class.forName(gameClassName);
        game = (Game) clazz.getConstructor(GameServer.class).newInstance(this);

        for (String propName : properties.stringPropertyNames()) {
            String value = properties.getProperty(propName);
            String name = Character.toUpperCase(propName.charAt(0))
                    + propName.substring(1);
            Method method;

            try {
                int intValue = Integer.parseInt(value);
                Class<?>[] args = new Class[1];
                args[0] = Integer.class;
                method = clazz.getMethod("set" + name, args);
                method.invoke(game, intValue);
            } catch (NumberFormatException e) {
                Class<?>[] args = new Class[1];
                args[0] = String.class;
                method = clazz.getMethod("set" + name, args);
                method.invoke(game, value);
            }
        }
    }

    @Override
    public void accept(final Connection connection) {
        ConnectionHandler connectionHandler =
                new ConnectionHandler(connection, Integer.toString(getNextId()));
        handlersList.add(connectionHandler);
        Thread connectionHandlerThread =
                new Thread(connectionHandler);
        connectionHandlerThread.start();
    }

    @Override
    public void broadcast(String message) {
        synchronized (handlersList) {
            for (ConnectionHandler handler : handlersList) {
                handler.addMsgToSend(message);
            }
        }
    }

    @Override
    public void sendTo(String id, String message) {
        synchronized (handlersList) {
            for (ConnectionHandler handler : handlersList) {
                if (handler.getThisConnectionId().equals(id)) {
                    handler.addMsgToSend(message);
                    break;
                }
            }
        }
    }

    private int getNextId() {
        return currentId++;
    }

    private class ConnectionHandler implements Runnable {
        private final Connection connection;
        private String thisConnectionId;
        private final List<String> toSend =
                Collections.synchronizedList(new ArrayList<String>());

        public ConnectionHandler(Connection connection, String thisConnectionId) {
            this.connection = connection;
            this.thisConnectionId = thisConnectionId;
            synchronized (this.connection) {
                tryToSend(thisConnectionId);
            }
        }

        private void tryToSend(String msg) {
            try { // connection might close in the middle of send operation
                connection.send(msg);
            } catch (Exception e) {
            }
        }

        private String tryToReceive(int timeout) {
            try { // connection might close in the middle of receive operation
                return connection.receive(timeout);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void run() {
            synchronized (game) {
                game.onPlayerConnected(thisConnectionId);
            }

            synchronized (connection) {
                while (!connection.isClosed()) {
                    while (toSend.size() == 0 && !connection.isClosed()) {
                        try {
                            connection.wait();
                        } catch ( InterruptedException e) {}
                    }
                    if (connection.isClosed())
                        break;
                    while (toSend.size() != 0) {
                        tryToSend(toSend.remove(0));
                    }
                    String msg = tryToReceive(1000);
                    if (msg != null) {
                        synchronized (game) {
                            game.onPlayerSentMsg(thisConnectionId, msg);
                        }
                    }
                }
                handlersList.remove(this);
            }
        }

        public void addMsgToSend(String msg) {
            toSend.add(msg);
            /*synchronized (connection) {
                connection.notify();
            }*/
        }

        public String getThisConnectionId() {
            return thisConnectionId;
        }
    }
}
