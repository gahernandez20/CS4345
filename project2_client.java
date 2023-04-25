import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

class Client {
    static String username;
    static Socket sock;
    static int port;
    static DataOutputStream outputToServer;
    static DataInputStream inputFromServer;
    static GUI gui;

    public static void main(String[] args) {
        try {
            port = 6001;
            // create a socket to make connection to server socket
            sock = new Socket("127.0.0.1", port);

            // create an output stream to send data to the server for a client
            outputToServer = new DataOutputStream(sock.getOutputStream());
            // create an input stream to receive data from server
            inputFromServer = new DataInputStream(sock.getInputStream());

            // Creates the GUI
            gui = new GUI();
            addLogInButtonListener(); // Used to add event handler for button

        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    private static class ReceiveThread implements Runnable {
        private DataInputStream inputStream;

        public ReceiveThread(DataInputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void run() {
            while (true) {
                try {
                    String message = inputStream.readUTF();
                    System.out.println(message);
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
    }

    private static void addLogInButtonListener() {
        gui.logInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    username = gui.usrname.getText();
                    gui.usrname.setText(null);
                    gui.createMainGUI();
                    addButtonListener();

                    // Once main gui is created, creates a thread that listens for incoming messages
                    Thread receivingThread = new Thread(new ReceiveThread(inputFromServer));
                    receivingThread.start();
                
                    // Signal start of connection with server
                    gui.groupChat.append("Connecting to server...\n");
                    String connectionMsg = String.format("Connection started (Port %d).\n", port);
                    gui.groupChat.append(connectionMsg + "\n");
                    outputToServer.writeUTF(username + " has entered the chat.\n");
                    outputToServer.flush();
                    gui.groupChat.append("You entered the chat on" + new Date() + ".\n");
                }
                catch (IOException ioException) {
                    System.err.println(ioException);
                }
            }
        });
    }

    private static void addButtonListener() {
        gui.submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    String msg = gui.userMessage.getText();
                    if (msg.toLowerCase().equals("exit")) {
                        outputToServer.writeUTF(username + " has left the server.");
                        outputToServer.flush();
                        gui.groupChat.append("Closing connection to server...");
                        Thread.sleep(2000);
                        sock.close(); // closing the socket at client
                        System.exit(0);
                    }
                    // send the data to the server
                    outputToServer.writeUTF(username + ": " + msg);
                    outputToServer.flush(); // clean the client side sending port
                    gui.groupChat.append("You: " + msg + "\n");
                    gui.groupChat.append("(Message sent at " + new Date() + ")\n");
                    clearText();
                }
                catch (IOException ioException) {
                    System.err.println(ioException);
                }
                catch (InterruptedException ie) {
                    System.err.println(ie);
                }
            }
        });
    }

    private static void clearText() {
        gui.userMessage.setText("");
        gui.tf2.setText("");
    }
}

class GUI {
    TextField userMessage, tf2, usrname;
    TextArea groupChat;
    Label lbl1, lbl2, logIn;
    Button logInButton, submitButton;
    Frame frame;

    public GUI() {
        createInitialPage();
    }
    private void createInitialPage() {
        frame = new Frame("Messaging Program");
        logIn = new Label("Enter your username: ");
        usrname = new TextField(40);
        logInButton = new Button("Log In");

        usrname.requestFocus();

        frame.setLayout(null);
        createPositionsForLogInComponents();
        addLogInComponentsToGUI();

        // Ensures window opened is closed when red X is pressed
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        // Displays GUI
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    private void createPositionsForLogInComponents() {
        logIn.setBounds(20, 150, 150, 30);
        usrname.setBounds(175, 150, 150, 30);
        logInButton.setBounds(325, 150, 120, 30);
    }

    private void addLogInComponentsToGUI() {
        frame.add(logIn);
        frame.add(usrname);
        frame.add(logInButton);
    }

    public void createMainGUI() {
        // Initialize GUI variables
        frame.removeAll();
        groupChat = new TextArea();
        groupChat.setEditable(false);
        userMessage = new TextField(40);
        userMessage.requestFocus();
        tf2 = new TextField(40);
        lbl1 = new Label("Type your message here: ");
        lbl2 = new Label("Your personal messages: ");

        submitButton = new Button("Send");

        // Create elements of GUI
        frame.setLayout(null);
        createPositionsForComponents();
        addComponentsToGUI();
    }

    // Method that will size the components of GUI and set their positions
    private void createPositionsForComponents() {

        groupChat.setBounds(20, 40, 500, 150);
        lbl1.setBounds(20, 300, 200, 30);
        userMessage.setBounds(225, 300, 150, 30);
        lbl2.setBounds(20, 340, 200, 30);
        tf2.setBounds(225, 340, 150, 30);
        submitButton.setBounds(380, 300, 120, 30);

    }

    // Method that will add all components to GUI
    private void addComponentsToGUI() {
        frame.add(groupChat);
        frame.add(lbl1);
        frame.add(userMessage);
        frame.add(lbl2);
        frame.add(tf2);
        frame.add(submitButton);
    }
}