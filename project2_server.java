import java.net.*;
import java.io.*;
import java.util.*;

class Server {
    public static void main(String[] args) {
        try {
            int port = 6001;
            // create a server socket
            ServerSocket servSock = new ServerSocket( port );
            Scanner scnr = new Scanner(System.in);
            System.out.println("Server started at "+ new Date() + '\n');
            
            // Listen for a connection request
            Socket client = servSock.accept();
            
            while(!client.isClosed()) {
                // create data input and data output streams
                DataInputStream inputFromClient = new DataInputStream( client.getInputStream() );
                DataOutputStream outputToClient = new DataOutputStream( client.getOutputStream() );
    
                String receivedMsg = inputFromClient.readUTF();
    
                outputToClient.writeUTF(receivedMsg);
            }
            System.out.println("No clients are connected. Closing server.");
            // closing the socket at server
            scnr.close();
            client.close();
            servSock.close();
        }
        catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}