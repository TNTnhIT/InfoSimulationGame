package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import settings.Settings;

public class Client implements Settings{
    private Socket connectionToServer;
    private List<String> messages;

    private ServerHandler serverHandler;

    public Client(String IPAddress, int port) {
        messages = new CopyOnWriteArrayList<>();
        try {
            connectionToServer = new Socket(IPAddress, port);
            serverHandler = new ServerHandler(connectionToServer, this);
            serverHandler.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }



    //Message Handling
    private void gotMessage() {
        String message = messages.getFirst();
        messages.removeFirst();
        System.out.println("Got Message: " + message); //TODO
        int codeInt = Integer.parseInt(message);
        RECEIVER code = RECEIVER.valueOf(codeInt);
        switch (code) {
            case FRIENDLY -> receivedMyselfFriendly();
            case AGGRESSIVE -> receivedMyselfAggressive();
            case OTHER_PLAYER_FRIENDLY -> receivedOtherFriendly();
            case OTHER_PLAYER_AGGRESSIVE -> receivedOtherAggressive();
            case GAME_START -> receivedGameStart();
            case GAME_END -> receivedGameEnd();
            case NEXT_ROUND -> receivedNextRound();
            case WRONG_MESSAGE -> receivedWrongMessage();
            case TO_FAST -> receivedToFast();
            case OTHER_ERROR -> receivedOtherError();
            default -> receivedOtherError();
        }

    }




    private void sendCode(Settings.SENDER sender) {
        sendIntCode(sender.num);
    }

    private void sendIntCode(int intCode) {
        sendMessage(String.valueOf(intCode));
    }

    private void sendMessage(String message) {
        serverHandler.sendMessage(message);
    }

    //Receiver

    public void receivedOtherFriendly() {
        System.out.println("Other: Friendly");
    }

    public void receivedOtherAggressive() {
        System.out.println("Other: Aggressive");
    }

    public void receivedMyselfFriendly() {
        System.out.println("Me: Friendly");
    }

    public void receivedMyselfAggressive() {
        System.out.println("Me: Aggressive");
    }

    public void receivedGameStart() {
        System.out.println("Game has started");
    }

    public void receivedGameEnd() {
        System.out.println("Game has ended");
    }

    public void receivedNextRound() {
        System.out.println("Next Round");
    }

    public void receivedWrongMessage() {
        System.err.println("Error: Wrong Message");
    }

    public void receivedToFast() {
        System.err.println("Error: To fast");
    }

    public void receivedOtherError() {
        System.err.println("Error: Other error");
    }


    //Sender
    public void sendFriendly() {
        sendCode(SENDER.FRIENDLY);
    }

    public void sendAggressive() {
        sendCode(SENDER.AGGRESSIVE);
    }


    private class ServerHandler extends Thread{
        private DataInputStream is;
        private DataOutputStream os;
        private Client client;



        public ServerHandler(Socket connectionToServer, Client client) {
            try {
                is =  new DataInputStream(connectionToServer.getInputStream());
                os = new DataOutputStream(connectionToServer.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }




        @Override
        public void run() {
            //this.setDaemon(true);

            while(true) {
                try{
                    String s = is.readUTF();
                    //Only adds messages with content
                    if(!s.isEmpty()) {
                        messages.add(s);
                        System.out.println(s);
                        gotMessage();
                    }
                }
                catch(IOException e){}
            }
        }

        protected void sendMessage(String message) {
            try {
                os.writeUTF(message);
                os.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
