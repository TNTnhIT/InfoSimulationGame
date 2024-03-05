package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    ServerSocket serverSocket;
    List<ClientHandler> clients;


    public Server(int port) {
        //quasi Thread Save Variante von ArrayList
        clients = new CopyOnWriteArrayList<>();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started chat server on port " + port);
            while(true) {
                System.out.println("Waiting for new client...");
                Socket connectionToClient = serverSocket.accept();
                ClientHandler client = new ClientHandler(this, connectionToClient);
                clients.add(client);
                System.out.println("Accepted new client:" + connectionToClient.getInetAddress());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(serverSocket == null) return;
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessage(String msg) {
        System.out.println("Message broadcast: " + msg);
        if(msg == null) return;
        for(ClientHandler client: clients)
            client.sendMessage(msg);
    }

    public void sendMessageToSelect(String msg, int... recipients) {
        System.out.println("Message \"" + msg + "\" send to " + recipients);
        if(msg == null) return;
        for(int i: recipients)
            clients.get(i).sendMessage(msg);
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }



}
