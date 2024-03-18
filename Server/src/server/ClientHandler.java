package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import settings.Settings;

public class ClientHandler implements Runnable{

    private Server server;
    private Socket connectionToClient;
    private DataInputStream is;
    private DataOutputStream os;

    private String name;

    private int opponent;

    public ClientHandler(Server server, Socket connectionToClient) {
        this.server = server;
        this.connectionToClient = connectionToClient;
        name = connectionToClient.getInetAddress().getHostAddress();
        this.opponent = -1;

        new Thread(this).start();
    }

    public void sendMessage(String msg) {
        System.out.println("Hi");
        try {
            os.writeUTF(msg);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setOpponent(int opponent) {
        this.opponent = opponent;
    }

    @Override
    public void run() {
        try {
            this.is = new DataInputStream(connectionToClient.getInputStream());
            this.os = new DataOutputStream(connectionToClient.getOutputStream());


            while(!connectionToClient.isClosed()) {
                String msg = is.readUTF();
                if(msg != null) {
                    System.out.println("Message received:" + msg);
                    try {

                       int codeInt = Integer.parseInt(msg);
                       Settings.SENDER sender = Settings.SENDER.valueOf(codeInt);
                       switch (sender) {
                           case FRIENDLY:
                               sendMessage(String.valueOf(Settings.RECEIVER.FRIENDLY.num));
                               server.sendMessageToSelect(String.valueOf(Settings.RECEIVER.OTHER_PLAYER_FRIENDLY.num), opponent == -1 ? null: opponent);
                               break;
                           case AGGRESSIVE:
                               sendMessage(String.valueOf(Settings.RECEIVER.AGGRESSIVE.num));
                               server.sendMessageToSelect(String.valueOf(Settings.RECEIVER.OTHER_PLAYER_AGGRESSIVE.num), opponent == -1 ? null: opponent);
                               break;
                           case ERROR:
                               System.err.println("received Error");
                               break;
                       }
                    }catch (NumberFormatException e) {
                        System.err.println("Not a Number " + msg);
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            server.removeClient(this);
            //server.broadcastMessage(name, " disconnected.");

            if(is == null) return;
            try {
                is.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
            if(os == null) return;
            try {
               os.close();
            }catch (IOException e){
                e.printStackTrace();
            }


        }


    }
}
