import java.net.*;
import java.io.*;
import java.util.*;

class Server {
    // Static variables needed to start the server
    static ServerSocket serverSocket;
    static final int port = 6001; // Random port number is selected.
    public static void main(String[] args) {
        try {  
            serverSocket = new ServerSocket( port );
            System.out.println("Server started at "+ new Date() + '\n');

            Socket client = serverSocket.accept();
            DataInputStream inputFromClient = new DataInputStream( client.getInputStream() );
            DataOutputStream outputToClient = new DataOutputStream( client.getOutputStream() );

            Socket client2 = serverSocket.accept();
            DataInputStream inputFromClient2 = new DataInputStream( client2.getInputStream() );
            DataOutputStream outputToClient2 = new DataOutputStream( client2.getOutputStream() );
            
            while(true) {
                if(client.isClosed() && client2.isClosed()) {
                    System.out.println("No clients exists.");
                    break;
                }

                String message = inputFromClient.readUTF();
                System.out.println(message);

                outputToClient2.writeUTF(message);
                outputToClient2.flush();

                message = inputFromClient2.readUTF();
                System.out.println(message);

                outputToClient.writeUTF(message);
                outputToClient.flush();
            }

        }
        catch(IOException ioException) {
            System.err.println(ioException);
        }
        finally {
            try {
                serverSocket.close();
            }
            catch (IOException ioException){ 
                System.err.println(ioException);
            }
        }
    }
}

