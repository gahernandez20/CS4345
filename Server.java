import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/* 
public class Server {
    private static final int PORT = 8000;

    private List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Client connected: " + socket);

            ClientHandler clientHandler = new ClientHandler(socket, this);
            clients.add(clientHandler);
            clientHandler.start();
        }
    }

    public void broadcast(String message, ClientHandler excludeClient) throws IOException {
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client disconnected: " + client.getSocket());
    }
}
*/
