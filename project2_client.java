import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

class Client {
    static Socket sock;
    static DataOutputStream outputToServer;
    static DataInputStream inputFromServer;
    static GUI gui;

    public static void main(String[] args) {
        try {
            int port = 6001;
            // create a socket to make connection to server socket
            sock = new Socket("127.0.0.1", port);

            // create an output stream to send data to the server (Hint: DataOutPutStream)
            outputToServer = new DataOutputStream(sock.getOutputStream());
            // create an input stream to receive data from server
            inputFromServer = new DataInputStream(sock.getInputStream());

            gui = new GUI();
            addButtonListener();

            // Signal start of connection with server
            gui.groupChat.append("Connecting to server...\n");
            String connectionMsg = String.format("Connection started (Port %d).\n", port);
            gui.groupChat.append(connectionMsg);

            // receive the result from the server
            String answer = inputFromServer.readUTF();
            System.out.println(answer); 
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    private static void addButtonListener() {
        gui.submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    String msg = gui.userMessage.getText();
                    if (msg.toLowerCase().equals("exit")) {
                        gui.groupChat.append("Closing connection to server...");
                        Thread.sleep(2000);
                        sock.close(); // closing the socket at client
                        System.exit(0);
                    }
                    // send the data to the server
                    outputToServer.writeUTF(msg);
                    System.out.println("Data sent to server at " + new Date() + '\n');
                    outputToServer.flush(); // clean the client side sending port
                    gui.groupChat.append(msg);
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
    TextField userMessage, tf2;
    TextArea groupChat;
    Label lbl1, lbl2;
    Button submitButton;
    Frame frame;

    public GUI() {
        // Initialize GUI variables
        groupChat = new TextArea();
        groupChat.setEditable(false);
        frame = new Frame("Messaging Program");
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