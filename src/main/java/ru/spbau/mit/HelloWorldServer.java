package ru.spbau.mit;


public class HelloWorldServer implements Server {

    @Override
    public void accept(final Connection connection) {
        Thread connectionHandlerThread =
                new Thread(new HelloWorldConnectionHandler(connection));
        connectionHandlerThread.start();
    }

    private class HelloWorldConnectionHandler implements Runnable {
        private final Connection connection;

        HelloWorldConnectionHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            synchronized (connection) {
                connection.send("Hello world");
                connection.close();
            }
        }
    }
}
