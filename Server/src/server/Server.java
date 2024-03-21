package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server extends Thread{
    ServerSocket serverSocket;
    List<ClientHandler> clients;
    int port;

    boolean hasGameStarted;


    Game game;


    public Server(int port, Game game) {
        //quasi Thread Save Variante von ArrayList
        clients = new CopyOnWriteArrayList<>();
        this.port = port;
        this.hasGameStarted = false;
        //serverThread = new Thread(this);
        //serverThread.start();
        this.game = game;
        this.start();
    }


    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started chat server on port " + port);
            while(true) {
                while(!hasGameStarted) { //Before the game has started
                    System.out.println("Waiting for new client...");
                    Socket connectionToClient = serverSocket.accept();
                    if(!hasGameStarted) {
                        ClientHandler client = new ClientHandler(this, connectionToClient);
                        clients.add(client);
                        System.out.println("Accepted new client:" + connectionToClient.getInetAddress());
                        game.newPlayer();
                    }
                }
                System.out.println("Game is running");
                while(hasGameStarted)  { //While the game is running (waiting

                }
                System.out.println("Game has ended");
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





    public int startFullGame() {
        System.out.println("game has started");
           hasGameStarted = true;
           return clients.size();
    }

    public void gameHasEnded() {
        System.out.println("Hi");
        hasGameStarted = false;
        this.interrupt();
        System.out.println(Thread.interrupted()); //TODO
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
