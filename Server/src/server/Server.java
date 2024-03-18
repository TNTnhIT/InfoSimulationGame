package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements Runnable{
    ServerSocket serverSocket;
    List<ClientHandler> clients;
    int port;

    boolean hasGameStarted;


    public Server(int port) {
        //quasi Thread Save Variante von ArrayList
        clients = new CopyOnWriteArrayList<>();
        this.port = port;
        this.hasGameStarted = false;
        new Thread(this).start();
    }


    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started chat server on port " + port);
            while(!hasGameStarted) {
                if(clients.size() == 2) //TODO test code
                    setOpponent(0,1);
                System.out.println("Waiting for new client...");
                Socket connectionToClient = serverSocket.accept();
                if(!hasGameStarted) {
                    ClientHandler client = new ClientHandler(this, connectionToClient);
                    clients.add(client);
                    System.out.println("Accepted new client:" + connectionToClient.getInetAddress());
                }
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

    public void setOpponent(int player1, int player2) {
        System.out.println("Opponent"); //TODO test code
        clients.get(player1).setOpponent(player2);
        clients.get(player2).setOpponent(player1);
    }



    public int startFullGame() {
           hasGameStarted = false;
           return clients.size();
    }

    public boolean startGame() {
        return false;
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
