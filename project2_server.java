import java.net.*;
import java.io.*;
import java.util.*;

class Server {
    // Static variables needed to start the server
    static ServerSocket serverSocket;
    static final int port = 6001; // Random port number is selected.
    static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started at " + new Date() + '\n');

            while(true) {
                removeClosedConnections();

                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(socket);
                client.start();
                clients.add(client);
            }
        } catch (IOException ioException) {
            System.out.println("Error creating client handler");
            System.err.println(ioException);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                System.out.println("Error closing server");
                System.err.println(ioException);
            }
        }
    }
    private static void removeClosedConnections() throws IOException {
        Iterator<ClientHandler> iter = clients.iterator();
        while(iter.hasNext()) {
            ClientHandler client = iter.next();
            if(client.getSocket().isClosed()) {
                client.getInputStream().close();
                client.getOutputStream().close();
                iter.remove();
            }
        }
    }

    public static void broadcastMessage(String msg, ClientHandler currentClientHandler) {
        System.out.println(msg);
        try {
            removeClosedConnections();
            for (ClientHandler client : clients) {
                if(!client.equals(currentClientHandler)) {
                    client.sendMessage(msg);
                }
            }
        } catch (IOException ioException) {
            System.out.println("Error sending message to other clients");
            ioException.printStackTrace();
        }
    }

    // Class that will handle messages from each client
    static class ClientHandler extends Thread {
        private Socket socket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;

        public ClientHandler (Socket socket) throws IOException {
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        }

        public Socket getSocket() {
            return this.socket;
        }

        public DataInputStream getInputStream() {
            return this.inputStream;
        }

        public DataOutputStream getOutputStream() {
            return this.outputStream;
        }

        public void sendMessage(String msg) {
            try {
                outputStream.writeUTF(msg);
                outputStream.flush();
            }
            catch(IOException ioException) {
                System.out.println("Error sending message to a client, presumably closed");
                try {
                    this.getSocket().close();
                } catch (IOException ioException2) {
                    System.out.println("Error closing client on server end");
                }
            }
        }

        public void run() {
            try {
                while (true) {
                    String msg = inputStream.readUTF();
                    broadcastMessage(msg, this); 
                }
            } catch (IOException ioException) {
                System.out.println("Error in reading message from client ");
                System.err.println(ioException);
            }
        }
    }
}
