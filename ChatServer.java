/* 
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JOptionPane;

public class ChatServer extends Frame implements ActionListener {
    private static final long serialVersionUID = 1L;
    TextArea chatArea = new TextArea();
    TextField messageField = new TextField();
    Socket socket = null;
    DataOutputStream outputStream = null;
    DataInputStream inputStream = null;
    String username = null;

    public ChatServer(String title) {
        super(title);
        this.setLayout(new BorderLayout());
        this.add(chatArea, BorderLayout.CENTER);
        this.add(messageField, BorderLayout.SOUTH);
        messageField.addActionListener(this);
        this.setSize(500, 500);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    outputStream.writeUTF(username + " has disconnected.");
                    socket.close();
                    System.exit(0);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        String message = messageField.getText();
        try {
            outputStream.writeUTF(username + ": " + message);
            messageField.setText("");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void connect(String serverIP, int port) {
        try {
            socket = new Socket(serverIP, port);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            username = JOptionPane.showInputDialog(this, "Enter your username:");
            outputStream.writeUTF(username + " has connected.");
            Thread thread = new Thread(new ReceiveThread(inputStream));
            thread.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private class ReceiveThread implements Runnable {
        DataInputStream inputStream;

        public ReceiveThread(DataInputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void run() {
            while (true) {
                try {
                    String message = inputStream.readUTF();
                    chatArea.append(message + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer("Chat Room");
        String serverIP = JOptionPane.showInputDialog(chatServer, "Enter the server IP address:");
        chatServer.connect(serverIP, 5000);
    }
}
*/
