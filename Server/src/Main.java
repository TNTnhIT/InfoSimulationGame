import client.TestClientForm;
import server.Server;

import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(8080);
        System.out.println("Test");
        TestClientForm.start();
        TestClientForm.start();

    }
}