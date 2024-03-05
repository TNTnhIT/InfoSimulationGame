package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private Server server;
    private Socket connectionToClient;
    private DataInputStream is;
    private DataOutputStream os;

    private String name;

    public ClientHandler(Server server, Socket connectionToClient) {
        this.server = server;
        this.connectionToClient = connectionToClient;
        name = connectionToClient.getInetAddress().getHostAddress();

        new Thread(this).start();
    }

    public void sendMessage(String msg) {
        try {
            os.writeUTF(msg);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
