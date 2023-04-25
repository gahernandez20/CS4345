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
                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(socket);
                clients.add(client);
                client.start();
            }
        } catch (IOException ioException) {
            System.err.println(ioException);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                System.err.println(ioException);
            }
        }
    }

    public static void broadcastMessage(String msg, ClientHandler currentClientHandler) {
        System.out.println(msg);
        try {
            for (ClientHandler client : clients) {
                if(!client.equals(currentClientHandler)) {
                    client.sendMessage(msg);
                }
            }
        } catch (IOException ioException) {
            System.err.println(ioException);
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

        public void sendMessage(String msg) throws IOException {
            outputStream.writeUTF(msg);
            outputStream.flush();
        }

        public void run() {
            try {
                while (true) {
                    String msg = inputStream.readUTF();
                    broadcastMessage(msg, this); 
                }
            } catch (IOException ioException) {
                System.err.println(ioException);
            }
        }
    }
}
