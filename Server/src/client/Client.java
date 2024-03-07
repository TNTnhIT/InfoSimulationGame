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
        int codeInt = Integer.parseInt(message);
        RECEIVER code = RECEIVER.valueOf(codeInt);
        switch (code) {
            case FRIENDLY -> gotMyselfFriendly();
            case AGGRESSIVE -> gotMyselfAggressive();
            case OTHER_PLAYER_FRIENDLY -> gotOtherFriendly();
            case OTHER_PLAYER_AGGRESSIVE -> gotOtherAggressive();
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


    public void sendFriendly() {
        sendCode(SENDER.FRIENDLY);
    }

    public void sendAggressive() {
        sendCode(SENDER.AGGRESSIVE);
    }

    public void gotOtherFriendly() {
        System.out.println("Other: Friendly");
    }

    public void gotOtherAggressive() {
        System.out.println("Other: Aggressive");
    }

    public void gotMyselfFriendly() {
        System.out.println("Me: Friendly");
    }

    public void gotMyselfAggressive() {
        System.out.println("Me: Aggressive");
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
                    if(!messages.isEmpty()) {
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
