import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.Date;
import java.util.Set;
import java.util.LinkedHashSet;

public class TestServer {
    private final int port = 6001;
    private final Date SERVER_START_TIME;
    private Set<ClientObject> clients = new LinkedHashSet<>();
    protected ServerSocket serverSocket;
    protected String allMessages;

    public TestServer() {
        SERVER_START_TIME = new Date();
        allMessages = "Server started on: " + SERVER_START_TIME + "\n";
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started at " + SERVER_START_TIME + '\n'); // For debugging purposes

            while (true) {
                Socket s = serverSocket.accept();
                Object o = receiveUsernameObject(s);
                if (o instanceof Username) {
                    addClientToSet(o, s);
                } else {
                    throw new IllegalArgumentException("Could not create client object");
                }
            }
        } catch (IOException ioException) {
            System.out.println("Error creating server");
        } catch (ClassNotFoundException classNotFoundException) {
            System.out.println("Could not get username");
        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println(illegalArgumentException);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                System.err.println(ioException);
                System.out.println("Error closing server");
            }
        }
    }

    private Object receiveUsernameObject(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Object o = ois.readObject();
        return Objects.requireNonNull(o);
    }

    private void addClientToSet(Object o, Socket s) throws IOException {
        Username u = (Username) o;
        String username = u.getUsername();
        ClientObject co = new ClientObject(username, s, this);
        co.start();
        clients.add(co);
        System.out.println("Client " + co.getName() + " has been added to server list");
    }

    private void removeClosedConnections() throws IOException {
        Iterator<ClientObject> iter = clients.iterator();
        while (iter.hasNext()) {
            ClientObject client = iter.next();
            if (client.getSocket().isClosed()) {
                iter.remove();
            }
        }
    }

    public void broadcastMessage(String msg, ClientObject co) {
        try {
            removeClosedConnections();
            for (ClientObject client : clients) {
                if (!client.getName().equals(co.getName())) {
                    client.sendMessage(msg);
                }
            }
            printListOfCurrentClients();
        } catch (IOException ioException) {
            System.out.println("Error sending message to other clients");
            ioException.printStackTrace();
        }
    }

    public void sendPersonalMessage(String targetUser, String msg) {
        try {
            removeClosedConnections();
            for (ClientObject client : clients) {
                if (client.getName().equals(targetUser)) {
                    client.sendMessage("PM From: " + msg);
                }
            }
        } catch (IOException ioException) {
            System.out.println("Could not send personal message");
        }
    }

    public String getAllMessages() {
        return this.allMessages;
    }

    public void appendToAllMessages(String msg) {
        allMessages += msg;
    }

    private void printListOfCurrentClients() {
        System.out.println("List of Current Clients: ");
        for (ClientObject co : clients) {
            System.out.println(co.getName());
        }
    }

    public static void main(String[] args) {
        new TestServer().start();
    }
}

class ClientObject extends Thread {
    private TestServer server;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ClientObject(String username, Socket socket, TestServer server) throws IOException {
        super(username);
        this.socket = socket;
        this.server = server;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void sendMessage(String msg) {
        try {
            System.out.println("Sending message to: " + this.getName() + "|" + this.getSocket().getInetAddress()); 
            outputStream.writeUTF(msg); // Sends messages to this client
            outputStream.flush(); // Ensures output stream is clean
        } catch (IOException ioException) {
            System.out.println("Error sending message to a client, presumably closed");
            try {
                System.out.println("Closing => " + socket.getInetAddress()); // Used for debugging purposes
                socket.close();
            } catch (IOException ioException2) {
                System.out.println("Error closing client on server end");
            }
        }
    }

    // Method that will constantly run to receive messages from its client socket
    // Two separate try/catch blocks are used to differentiate exceptions there and
    // where they were thrown for debugging purposes
    @Override
    public void run() {
        try {
            System.out.println("Sending all messages to new client");
            outputStream.writeUTF(server.getAllMessages());
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.out.println("Error sending first message to client");
        }
        try {
            while (true) {
                String msg = inputStream.readUTF();
                if(checkForPersonalMessage(msg)) {
                    processPersonalMessage(msg);
                } else {
                    processNormalMessage(msg);
                }
            }
        } catch (IOException ioException) {
            System.err.println(ioException);
            System.out.println("Error receiving a message from client");
        }
    }

    private boolean checkForPersonalMessage(String msg) {
        if (msg.charAt(0) == '@') {
            return true;
        }
        return false;
    }

    private void processNormalMessage(String msg) {
        System.out.println(msg);
        server.appendToAllMessages(msg + "\n");
        server.broadcastMessage(msg, this);
    }

    private void processPersonalMessage(String msg) {
        System.out.println(msg);
        msg = msg.substring(1);
        int delimiter = msg.indexOf('@');
        String targetUser = msg.substring(0, delimiter);
        msg = msg.substring(delimiter+1);
        server.sendPersonalMessage(targetUser, msg);
    }
}