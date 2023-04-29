/*
 * Name: Giovanni Hernandez
 * Course: CS 4345. Operating Systems, Spring 2023
 */

import java.net.*;
import java.io.*;
import java.util.*;

class Server {
    // Static variables needed to start the server
    static ServerSocket serverSocket; // Instance variable for the server socket
    static final int port = 6001; // Random port number is selected.
    static List<ClientHandler> clients = new ArrayList<>(); // Instance variable that maintains list of all active clients
    static final Date SERVER_START_TIME = new Date(); // Notes the server start time, instantiated at time of execution
    static String allMessages = "Server started on: " + SERVER_START_TIME + "\n"; // Instance variable that holds all messages
                                                                                  // entered into the chat since start of server;
                                                                                  // Begins with the server start time
    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(port); // Initializes the server socket with the port number
            System.out.println("Server started at " + SERVER_START_TIME + '\n'); // For debugging purposes

            // This infinite loop ensures the server will constantly handle new connections from new clients
            while (true) {
                removeClosedConnections(); // Calls a helper method to remove any closed sockets (Client left server) prior to adding additional clients

                Socket socket = serverSocket.accept(); // Blocks main thread until a new connection is receivec
                ClientHandler client = new ClientHandler(socket); // Creates a new thread for each client that handles receiving 
                                                                  //messages from said client
                client.start();
                clients.add(client); // Adds client thread to collection that maintains list of all active messages
            }
        } catch (IOException ioException) {
            System.out.println("Error creating client handler"); // For debugging purposes
            System.err.println(ioException);
        } finally { // Since server only ends after CTRL + C, this block of code is technically unreachable;
                    // However, it is maintained here in case of addition of code that times server out after
                    // certain amount of time
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                System.out.println("Error closing server"); // For debugging purposes
                System.err.println(ioException);
            }
        }
    }

    // Helper method that removes closed connections from the list of client that the server maintains
    private static void removeClosedConnections() throws IOException {
        Iterator<ClientHandler> iter = clients.iterator(); // Using iterator to prevent issues with looping and removing
        while (iter.hasNext()) {
            ClientHandler client = iter.next();
            if (client.getSocket().isClosed()) { // If socket is closed on server side
                iter.remove();
            }
        }
    }

    // Method used to send messages to all clients currently on the server
    // This method resides in the server class as the server maintains the list of all clients 
    // currently connected. A ClientHandler object exists as a parameter as it ensures the message 
    // is not sent back to the sender.
    public static void broadcastMessage(String msg, ClientHandler currentClientHandler) {
        System.out.println(msg);
        try {
            removeClosedConnections(); // Calls helper method to attempt to remove any closed clients before sending messages
            // Loops through each client in the server's list of active clients
            for (ClientHandler client : clients) {
                // This if statement ensures a received message is not sent back to the sender client
                if (!client.equals(currentClientHandler)) {
                    client.sendMessage(msg); // Calls a method within the client thread that sends the messages through their output stream
                }
            }
        } catch (IOException ioException) {
            System.out.println("Error sending message to other clients");
            ioException.printStackTrace();
        }
    }

    // Class that will handle messages from each client; Each thread represents one client from the server side
    static class ClientHandler extends Thread {
        private Socket socket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream()); // Gets input stream from passed socket and initializes the instance variable
            this.outputStream = new DataOutputStream(socket.getOutputStream()); // Gets output stream from passed socket and initializes the instance variable
        }

        // Getter for client's socket
        public Socket getSocket() {
            return this.socket;
        }

        // Getter for client's data input stream
        public DataInputStream getInputStream() {
            return this.inputStream;
        }

        // Getter for client's data output stream
        public DataOutputStream getOutputStream() {
            return this.outputStream;
        }

        public void sendMessage(String msg) {
            try {
                System.out.println("Sending message to: " +  this.socket.getLocalSocketAddress()); // Used for debugging purposes
                outputStream.writeUTF(msg); // Sends messages to this client
                outputStream.flush(); // Ensures output stream is clean
            } 
            // If an exception is thrown here, it means the socket on the client side is closed and therefore the client has left
            // the server. Thus, the socket is closed on the server side so our removedClosedConnections() can remove it when 
            // called elsewhere
            catch (IOException ioException) {
                System.out.println("Error sending message to a client, presumably closed");
                try {
                    System.out.println("Closing => " + socket.getInetAddress()); // Used for debugging purposes
                    socket.close();
                } catch (IOException ioException2) {
                    System.out.println("Error closing client on server end");
                }
            }
        }
        
        // Method that runs when thread is started
        public void run() {
            try {
                outputStream.writeUTF(allMessages); // Sends newly connected client all previous messages since start of server
                // Thread enters an infinite loop that constantly attempts to read for any messages sent from the socket (client) 
                while (true) {
                    String msg = inputStream.readUTF(); // Reads a sent message from this respective client
                    allMessages += msg + "\n"; // Adds a received message to string containing all messages
                    broadcastMessage(msg, this); // Passes message to the server method that will send message to all client
                }
            } catch (IOException ioException) {
                System.out.println("Error in reading message from client ");
                System.err.println(ioException);
            }
        }
    }
}
