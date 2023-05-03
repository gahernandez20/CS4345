import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;

public class TestClient {
    private final Date CLIENT_JOIN_TIME;
    private final int port = 6001;
    static GUI gui;
    protected String username;
    protected Socket socket;
    protected ObjectOutputStream oos;
    protected DataOutputStream outputToServer;
    protected DataInputStream inputFromServer;

    public TestClient() {
        CLIENT_JOIN_TIME = new Date();
    }

    private void start() {
        gui = new GUI();
    }

    public void startConnection() throws UnknownHostException, IOException {
        socket = new Socket("127.0.0.1", port);

        // Informs user of successfully connection
        gui.groupChat.append("Connecting to server...\n");
        String connectionMsg = String.format("Connection started (Port %d).\n", port);
        gui.groupChat.append(connectionMsg);

        sendUsernameToServer();
        createIOStreams(socket);

        outputToServer.writeUTF(username + " has entered the chat on " + CLIENT_JOIN_TIME + "\n");
    }

    public void stopConnection() {
        try {
            outputToServer.writeUTF(username + " has left the chat");
            outputToServer.flush();
            socket.close();
            System.exit(0);
        } catch (IOException ioException) {
            System.out.println("Error closing connection");
        }
    }

    private void sendUsernameToServer() {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(new Username(username));
            oos.flush();
        } catch (IOException ioException) {
            System.out.println("Error sending username to client");
        }
    }

    private void createIOStreams(Socket socket) {
        try {
            inputFromServer = new DataInputStream(socket.getInputStream());
            outputToServer = new DataOutputStream(socket.getOutputStream());
            outputToServer.flush();
        } catch (IOException ioException) {
            System.out.println("Error creating IO streams");
        }
    }

    private void sendMessageToServer(String msg) {
        sendMessageToServer("", msg);
    }

    private void sendMessageToServer(String targetUser, String msg) {
        try {
            outputToServer.writeUTF(targetUser + username + ": " + msg);
            outputToServer.flush();
            gui.groupChat.append("You: " + msg + "\n"); // Appends message to the global chat textfield
            gui.groupChat.append("(Message sent at " + new Date() + ")\n"); // Used to display when a message was sent
                                                                            // to server
        } catch (IOException ioException) {
            System.out.println("Error sending message to server");
        }
    }

    public static void main(String[] args) {
        new TestClient().start();
    }

    // Inner class for receiving message. Once a client is logged in, the main
    // thread creates one instance
    // of this threadand starts this thread. This thread/class handles receiving
    // messages from the server
    private class ReceiveThread implements Runnable {
        private DataInputStream inputStream;

        public ReceiveThread(DataInputStream inputStream) {
            this.inputStream = inputStream;
        }

        // Method that runs when thread is started
        public void run() {
            try {
                String firstMsg = inputStream.readUTF(); // Reads first message sent from server. This first message is
                                                         // the
                                                         // allMessages variable, which contains all previous messages
                                                         // communicated
                                                         // since start of server
                System.out.println(firstMsg); // For debugging purposes
                // This method call ensures the GUI gets updated with the received message on
                // the Event thread, not this ReceiveThread
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        gui.groupChat.append(firstMsg + "\n");
                        gui.groupChat.append("You entered the chat on " + CLIENT_JOIN_TIME + ".\n"); // Displays the
                                                                                                     // time of a client
                                                                                                     // connection
                    }
                });
                // Thread enters infinite loop to constantly read for new messages
                while (true) {
                    try {
                        String message = inputStream.readUTF(); // Reads a message from the server
                        System.out.println(message); // For debugging purposes
                        // This method call ensures the GUI gets updated with the received message on
                        // the Event thread,
                        // not this ReceiveThread
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
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    class GUI {
        TextField userMessage, usernameBox, personalMessage;
        TextArea groupChat;
        Label lbl1, lbl2, lbl3, logIn, pmAtLabel, userLabel;
        Button logInButton, submitButton, terminateButton;
        Frame frame;

        public GUI() {
            createInitialPage();
        }

        private void createInitialPage() {
            // Initializes instance variables
            frame = new Frame("Messaging Program");
            logIn = new Label("Enter your username: ");
            usernameBox = new TextField(40);
            logInButton = new Button("Log In");

            usernameBox.requestFocus(); // Sets cursor to log in textbox

            // Calls method related to setting up GUI
            frame.setLayout(null);
            createPositionsForLogInComponents();
            addLogInComponentsToGUI();

            // Ensures window opened is closed when red X is pressed
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    stopConnection(); // Calls helper method to ensure proper closure of socket connection
                }
            });

            // Displays GUI
            frame.setSize(600, 400);
            frame.setVisible(true);

        }

        // Helper method that creates location of components on the GUI
        private void createPositionsForLogInComponents() {
            logIn.setBounds(80, 185, 150, 30);
            usernameBox.setBounds(240, 185, 150, 30);
            logInButton.setBounds(400, 185, 120, 30);
        }

        // Helper method to add log in components to log in GUI
        private void addLogInComponentsToGUI() {
            frame.add(logIn);
            frame.add(usernameBox);
            frame.add(logInButton);
            addLogInButtonListener();
        }

        private void addLogInButtonListener() {
            logInButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        username = usernameBox.getText(); // Gets entered username; Initializes the instance variable
                                                          // with that value
                        usernameBox.setText(null);
                        // Signal start of connection with server
                        createMainGUI(); // Calls method to create the main GUI

                        startConnection();

                        // Once main gui is created, creates a thread that listens for incoming messages
                        Thread receivingThread = new Thread(new ReceiveThread(inputFromServer));
                        receivingThread.start();

                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            });
        }

        // Helper method that adds event handler to the send button in the main GUI
        private void addButtonListenerToSendGeneralChat() {
            submitButton.addActionListener(new ActionListener() {
                // When button is pressed, this method is called
                public void actionPerformed(ActionEvent ae) {
                    String pmUser = personalMessage.getText();
                    if (!(pmUser.equals(null) || pmUser.equals(""))) {
                        String msg = messagePrep();
                        sendMessageToServer("@" + pmUser + "@", msg);
                    } else {
                        String msg = messagePrep();
                        sendMessageToServer(msg); // If exit is not entered, calls method to send message to server
                    }
                }
            });
        }

        private String messagePrep() {
            String msg = userMessage.getText(); // Gets message from textbox
            checkForExit(msg);
            clearText();
            return msg;
        }

        private void checkForExit(String msg) {
            if (msg.toLowerCase().equals("exit")) { // If exit , Exit, EXIT, or any other variation of the
                // word exit // is entered, the program will terminate
                stopConnection(); // Calls method to ensure proper closure of connection and exit of program
            }
        }

        private void addButtonListenerToEndChat() {
            terminateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    groupChat.append("Closing connection to server..."); // Informs client that connection will be
                                                                         // closed
                    stopConnection();
                }
            });
        }

        // Helper method used to create main GUI. Declared public as the client class
        // will need to call this method
        public void createMainGUI() {
            frame.removeAll(); // Removes components of the log ing GUI to allow main GUI to be set up

            // Initialize GUI variables
            groupChat = new TextArea();
            groupChat.setEditable(false);

            lbl1 = new Label("Type your message here: ");
            pmAtLabel = new Label("@");
            personalMessage = new TextField();
            userMessage = new TextField(40);
            userMessage.requestFocus();
            submitButton = new Button("Send");
            userLabel = new Label("Current User: " + username);
            terminateButton = new Button("End Chat");

            // Create elements of GUI
            frame.setLayout(null);
            frame.setSize(750, 600);
            createPositionsForComponents();
            addComponentsToGUI();
        }

        // Method that will size the components of GUI and set their positions
        private void createPositionsForComponents() {
            groupChat.setBounds(20, 40, 700, 200);

            lbl1.setBounds(20, 300, 200, 30);
            pmAtLabel.setBounds(225, 300, 20, 30);
            personalMessage.setBounds(250, 300, 90, 30);
            userMessage.setBounds(345, 300, 250, 30);
            submitButton.setBounds(600, 300, 120, 30);
            userLabel.setBounds(20, 560, 150, 30);
            terminateButton.setBounds(290, 560, 120, 30);
        }

        // Method that will add main components to main GUI
        private void addComponentsToGUI() {
            frame.add(groupChat);

            frame.add(lbl1);
            frame.add(pmAtLabel);
            frame.add(personalMessage);
            frame.add(userMessage);
            frame.add(userLabel);
            frame.add(submitButton);
            addButtonListenerToSendGeneralChat(); // Adds event handler for the send button within main GUI
            frame.add(terminateButton);
            addButtonListenerToEndChat();
        }

        // Helper method used to reset GUI after message is sent
        private static void clearText() {
            gui.userMessage.setText("");
            gui.userMessage.requestFocus();
        }
    }
}
