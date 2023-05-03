/*
 * Name: Giovanni Hernandez
 * Course: CS 4345. Operating Systems, Spring 2023
 */

import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

class Client {
    // Instance variables 
    static String username;
    static String socketID;
    static Socket sock;
    static final int port = 6001;
    static Date CLIENT_JOIN_TIME;
    static DataOutputStream outputToServer;
    static DataInputStream inputFromServer;
    static GUI gui;

    public static void main(String[] args) {
        try {
            // create a socket to make connection to server socket
            sock = new ClientSocket(username,"127.0.0.1", port);

            // create an output stream to send data to the server for a client
            outputToServer = new DataOutputStream(sock.getOutputStream());
            outputToServer.flush();

            // create an input stream to receive data from server
            inputFromServer = new DataInputStream(sock.getInputStream());

        } catch (IOException ioException) {
            System.out.println("Error connecting to server");
        }
        // Creates the GUI
        gui = new GUI();
        addLogInButtonListener(); // Used to add event handler for log inbutton
    }

    // Inner class for receiving message. Once a client is logged in, the main thread creates one instance 
    // of this threadand starts this thread. This thread/class handles receiving messages from the server
    private static class ReceiveThread implements Runnable {
        private DataInputStream inputStream;

        public ReceiveThread(DataInputStream inputStream) {
            this.inputStream = inputStream;
        }

        // Method that runs when thread is started
        public void run() {
            try {
                String firstMsg = inputStream.readUTF(); // Reads first message sent from server. This first message is the
                                                         // allMessages variable, which contains all previous messages communicated
                                                         // since start of server
                System.out.println(firstMsg); // For debugging purposes
                // This method call ensures the GUI gets updated with the received message on the Event thread, not this ReceiveThread
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        gui.groupChat.append(firstMsg + "\n");
                        gui.groupChat.append("You entered the chat on " + CLIENT_JOIN_TIME + ".\n"); // Displays the time of a client connection
                    }
                });
                // Thread enters infinite loop to constantly read for new messages
                while (true) {
                    try {
                        String message = inputStream.readUTF(); // Reads a message from the server
                        System.out.println(message); // For debugging purposes
                        // This method call ensures the GUI gets updated with the received message on the Event thread, 
                        //      not this ReceiveThread
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                gui.groupChat.append(message + "\n");
                            }
                        });
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        break;
                    }
                }
            }
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    /*
     *  NOTE: Both event handlers' source code are placed here in the client class rather than the GUI class as the event handlers
     *  need access to instance variables only the client class has access to, namely the input and output stream.
     */


    // Helper method that adds an event handler to the log in button on the GUI
    private static void addLogInButtonListener() {
        gui.logInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    username = gui.usrname.getText(); // Gets entered username; Initializes the instance variable with that value
                    gui.usrname.setText(null);

                    gui.createMainGUI(); // Calls method to create the main GUI 
                    addButtonListenerToSendGeneralChat(); // Adds event handler for the send button within main GUI

                    // Once main gui is created, creates a thread that listens for incoming messages
                    Thread receivingThread = new Thread(new ReceiveThread(inputFromServer));
                    receivingThread.start();

                    // Signal start of connection with server
                    startConnection();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    // Helper method that adds event handler to the send button in the main GUI
    private static void addButtonListenerToSendGeneralChat() {
        gui.submitButton.addActionListener(new ActionListener() {
            // When button is pressed, this method is called
            public void actionPerformed(ActionEvent ae) {
                try {
                    String msg = gui.userMessage.getText(); // Gets message from textbox
                    // This if statement is one of two ways to exit program. 
                    if (msg.toLowerCase().equals("exit")) { // If exit , Exit, EXIT, or any other variation of the word exit 
                                                                     // is entered, the program will terminate
                        stopConnection(); // Calls method to ensure proper closure of connection and exit of program
                    }
                    sendMessageToServer(msg); // If exit is not entered, calls method to send message to server
                } catch (IOException ioException) {
                    System.err.println(ioException);
                }
            }
        });
    }

    // Helper method to signal the client that connection has started. NOTE: Socket connection has already begun as it 
    // begins soon after client code is ran. This method merely executes several actions that need to occur before client
    // can begin to send messages
    private static void startConnection() throws IOException {
        // Informs user of successfully connection
        gui.groupChat.append("Connecting to server...\n");
        String connectionMsg = String.format("Connection started (Port %d).\n", port);
        gui.groupChat.append(connectionMsg);

        System.out.println("Socket address: " + sock.getLocalSocketAddress().toString()); // For debuggin purposes
        CLIENT_JOIN_TIME = new Date(); // Initializes variable with time of client connection
        outputToServer.writeUTF(username + " has entered the chat on " + CLIENT_JOIN_TIME + "\n"); // Sends message to server
                                                                                                   // to let other clients know
                                                                                                   // that a new client has joined
        outputToServer.flush(); // Ensures output stream is clean
    }

    // Helper method to ensure proper closure of connection to server. This is declared public as the GUI thread also needs access
    // to it in the event the program is closed via the red X button on the top left of the window
    public static void stopConnection() {
        try {
            // This method call ensures the GUI gets updated on the Event thread
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    gui.groupChat.append("Closing connection to server..."); // Informs client that connection will be closed
                }
            });
            outputToServer.writeUTF(username + " has left the server."); // Send messages to the server to let other client know that
                                                                         // this client has left 
            outputToServer.flush(); // Ensures output stream is clean
            Thread.sleep(1500); // Ensures all other thread finish
            sock.close(); // closing the socket at client
            System.exit(0); // Exits program
        } catch (IOException ioException) {
            System.out.println("Error closing connection");
            System.err.println(ioException);
        } catch (InterruptedException iException) {
            System.out.println("Error with thread");
            System.err.println(iException);
        }

    }

    // Helper method that sends an entered message to the server for processing
    private static void sendMessageToServer(String msg) throws IOException {
        // send the data to the server
        outputToServer.writeUTF(username + ": " + msg);
        outputToServer.flush(); // clean the client side sending port
        gui.groupChat.append("You: " + msg + "\n"); // Appends message to the global chat textfield
        gui.groupChat.append("(Message sent at " + new Date() + ")\n"); // Used to display when a message was sent to server
        clearText();
    }

    // Helper method used to reset GUI after message is sent
    private static void clearText() {
        gui.userMessage.setText("");
        gui.userMessage.requestFocus();
    }
}

// Class containing method and data related to the creation of log in GUI and main GUI
class GUI {
    // Instance variables
    TextField userMessage, usrname;
    TextArea groupChat;
    Label lbl1, lbl2, lbl3, logIn, activeUserLabel;
    Button logInButton, submitButton;
    Frame frame;

    public GUI() {
        createInitialPage();
    }

    private void createInitialPage() {
        // Initializes instance variables
        frame = new Frame("Messaging Program");
        logIn = new Label("Enter your username: ");
        usrname = new TextField(40);
        logInButton = new Button("Log In");

        usrname.requestFocus(); // Sets cursor to log in textbox

        // Calls method related to setting up GUI
        frame.setLayout(null);
        createPositionsForLogInComponents();
        addLogInComponentsToGUI();

        // Ensures window opened is closed when red X is pressed
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                Client.stopConnection(); // Calls helper method to ensure proper closure of socket connection
            }
        });

        // Displays GUI
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    // Helper method that creates location of components on the GUI
    private void createPositionsForLogInComponents() {
        logIn.setBounds(80, 185, 150, 30);
        usrname.setBounds(240, 185, 150, 30);
        logInButton.setBounds(400, 185, 120, 30);
    }

    // Helper method to add log in components to log in GUI
    private void addLogInComponentsToGUI() {
        frame.add(logIn);
        frame.add(usrname);
        frame.add(logInButton);
    }

    // Helper method used to create main GUI. Declared public as the client class will need to call this method
    public void createMainGUI() {
        frame.removeAll(); // Removes components of the log ing GUI to allow main GUI to be set up

        // Initialize GUI variables
        groupChat = new TextArea();
        groupChat.setEditable(false);

        lbl1 = new Label("Type your message here: ");
        userMessage = new TextField(40);
        userMessage.requestFocus();
        submitButton = new Button("Send");

        // Create elements of GUI
        frame.setLayout(null);
        frame.setSize(700, 600);
        createPositionsForComponents();
        addComponentsToGUI();
    }

    // Method that will size the components of GUI and set their positions
    private void createPositionsForComponents() {
        groupChat.setBounds(20, 40, 650, 200);

        lbl1.setBounds(20, 300, 200, 30);
        userMessage.setBounds(225, 300, 255, 30);
        submitButton.setBounds(485, 300, 120, 30);
    }

    // Method that will add main components to main GUI
    private void addComponentsToGUI() {
        frame.add(groupChat);

        frame.add(lbl1);
        frame.add(userMessage);
        frame.add(submitButton);
    }
}