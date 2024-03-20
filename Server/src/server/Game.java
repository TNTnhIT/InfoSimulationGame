package server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import client.TestClientForm;
import settings.Settings;

public class Game implements GameSettings{
    Server server;


    private JPanel GamePnl;
    private JButton btnStart;
    private JTextArea txaPoints;
    private JButton btnRedrawPoints;

    private List<Integer> points;

    private volatile int finished;

    private boolean even;

    private List<String> names;



    public Game() {
        this.server = new Server(8080);
        for (int i = 0; i < 5; i++) {
            TestClientForm.start();

        }
        //startGame(0,1);
        txaPoints.setText("Points: ");
        points = new CopyOnWriteArrayList<>();

        //start();

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
                //startGame(0, 1);

            }
        });
        btnRedrawPoints.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshPoints();
            }
        });
    }

    public synchronized void refreshPoints() {
        txaPoints.setText(printPoints(points, even));
        txaPoints.repaint();
    }
    private String printPoints(List<Integer> points, boolean evenNumberofPlayers) { //TODO eventuell Liste kopieren, damit nicht zwischendurch geändert werden kann
        System.out.println("Refreshing points");
        ArrayList<Integer> list = new ArrayList<>(points);
        String s = "Points: \n";
        for(int i = 0; i < list.size(); i++) {
            if(evenNumberofPlayers) {
                s += "Player " + i + ": " + list.get(i) + "\n";
            }else {
                if(i != list.size()-1) {
                    s += "Player " + i + ": " + list.get(i) + "\n";
                }
            }
        }
        return s;
    }

    private void start() {
        new Thread(){
            @Override
            public void run() {
                int players = server.startFullGame();
                boolean evenNumberOfPlayers = players % 2 == 0;
                even = evenNumberOfPlayers;

                int anz = players;
                Stack<Integer> stack = new Stack<>();
                if(!evenNumberOfPlayers) {
                    anz++;
                }
                points = new CopyOnWriteArrayList<>();
                for(int i = 0; i < anz; i++) {
                    points.add(0);
                }
                refreshPoints();

                for(int i = anz-1; i > 0; i--) {
                    if(evenNumberOfPlayers)
                        finished = 0;
                    else
                        finished = 1;


                    stack.push(0);
                    for(int j = 0; j < (anz/2)-1; j++) {
                        stack.push(((i+j)%(anz-1))+1);
                    }
                    for(int j = (anz/2)-1; j < anz-1; j++) {
                        //Start Game
                        int player1 = stack.pop();
                        int player2 = (((i+j)%(anz-1))+1);
                        System.out.println(player1 + " | " + player2);
                        if(evenNumberOfPlayers) {
                            startGame(player1, player2);
                        }else {//bei ungerader Anzahl muss einer aussetzen
                            if(player1 == anz-1 || player2 == anz-1) {

                            }else {
                                startGame(player1, player2);
                                //refreshPoints();
                            }
                        }

                    }
                    System.out.println("-------------------");

                    while(finished < anz/2) { //Waiting for all games to finish
                    }


                    System.out.println("Hi" + finished);
                    for(int x :points) {
                        System.out.print(x + "|");
                    }
                    System.out.println();
                    refreshPoints();



                }
            }
        }.start();
        refreshPoints();
    }

    public void startGame(int player1, int player2) {
        System.out.println("Started Game between Player " + player1 + " and Player " + player2);
        new SmallGame(server.clients.get(player1), player1, server.clients.get(player2), player2).startGame();
    }

    public class SmallGame extends Thread implements Settings{
        ClientHandler player1;
        ClientHandler player2;

        int player1Int;
        int player2Int;

        int pointsPlayer1;
        int pointsPlayer2;

        boolean isPlayer1Friendly;
        boolean isPlayer2Friendly;

        private List<Settings.SENDER> messagesPlayer1;
        private List<Settings.SENDER> messagesPlayer2;

        private boolean isGameRunning;


        public SmallGame(ClientHandler player1, int player1Int, ClientHandler player2, int player2Int) {
            this.player1Int = player1Int;
            this.player2Int = player2Int;
            this.player1 = player1;
            this.player2 = player2;
            isGameRunning = false;
        }

        @Override
        public void run() {
            initializeGame();
            startGameLoop();
        }

        public void startGame() {
            initializeGame();
            startGameLoop();
        }

        private void startGameLoop() {
            isPlayer1Friendly = true;
            isPlayer2Friendly = true;

            for(int i = 0; i < NUMBER_OF_MOVES; i++) {

                if(isGameRunning) {
                    //waiting for messages
                    while(messagesPlayer1.isEmpty() || messagesPlayer2.isEmpty()) {

                    }
                    //TODO boolean to stop new messages
                    SENDER movePlayer1 = messagesPlayer1.getFirst();
                    messagesPlayer1.removeFirst();
                    SENDER movePlayer2 = messagesPlayer2.getFirst();
                    messagesPlayer2.removeFirst();

                    //Eigentliche Logic des Programms
                    if(movePlayer1 == SENDER.FRIENDLY && movePlayer2 == SENDER.FRIENDLY) {
                        pointsPlayer1 += BOTH_FRIENDLY;
                        pointsPlayer2 += BOTH_FRIENDLY;
                    } else if(movePlayer1 == SENDER.AGGRESSIVE && movePlayer2 == SENDER.AGGRESSIVE) {
                        pointsPlayer1 += BOTH_AGGRESSIVE;
                        pointsPlayer2 += BOTH_AGGRESSIVE;
                    } else if(movePlayer1 == SENDER.AGGRESSIVE && movePlayer2 == SENDER.FRIENDLY) {
                        pointsPlayer1 += DIFFERENT_AGGRESSIVE;
                        pointsPlayer2 += DIFFERENT_FRIENDLY;
                    } else if(movePlayer1 == SENDER.FRIENDLY && movePlayer2 == SENDER.AGGRESSIVE) {
                        pointsPlayer1 += DIFFERENT_FRIENDLY;
                        pointsPlayer2 += DIFFERENT_AGGRESSIVE;
                    }

                    //Send response
                    switch(movePlayer1) {
                        case FRIENDLY -> sendMessage(1, RECEIVER.OTHER_PLAYER_FRIENDLY);
                        case AGGRESSIVE -> sendMessage(1, RECEIVER.OTHER_PLAYER_AGGRESSIVE);
                    }
                    switch(movePlayer2) {
                        case FRIENDLY -> sendMessage(0, RECEIVER.OTHER_PLAYER_FRIENDLY);
                        case AGGRESSIVE -> sendMessage(0, RECEIVER.OTHER_PLAYER_AGGRESSIVE);
                    }

                    //TODO waiting so that we done get any to fast issues
                    try {
                        Thread.sleep(1,10);
                    } catch (InterruptedException e) {
                        System.err.println("Interrupted");
                        throw new RuntimeException(e);
                    }


                    if(i != NUMBER_OF_MOVES -1) { //nach der letzten Runde gibt es keine weitere
                        sendMessage(2, Settings.RECEIVER.NEXT_ROUND);
                    }
                }
            }

            synchronized (this) { //just to be safe
                isGameRunning = false;
                sendMessage(2, RECEIVER.GAME_END);
                points.set(player1Int, pointsPlayer1+points.get(player1Int));
                points.set(player2Int, pointsPlayer2+points.get(player2Int));
                finished += 1;
            }
        }

        private void initializeGame() {
            pointsPlayer1 = 0;
            pointsPlayer2 = 0;
            server.setOpponent(player1Int, player2Int);
            player1.setSmallGame(this, 0);
            player2.setSmallGame(this, 1);
            isGameRunning = true;
            messagesPlayer1 = new CopyOnWriteArrayList<>();
            messagesPlayer2 = new CopyOnWriteArrayList<>();
            sendMessage(2, Settings.RECEIVER.GAME_START);
        }


        private void sendMessage(int player, Settings.RECEIVER message) {
            if(player == 0) {
                player1.sendMessage(String.valueOf(message.num));
            }else if(player == 1){
                player2.sendMessage(String.valueOf(message.num));
            }else {
                player1.sendMessage(String.valueOf(message.num));
                player2.sendMessage(String.valueOf(message.num));
            }
        }

        public void receiveMessage(int player, String message) {
            Settings.SENDER decodedMessage = decodeMessage(message);
            if(player == 0) {
                if(messagesPlayer1.isEmpty()) {
                    messagesPlayer1.add(decodedMessage);
                    //Feedback loop
                    switch(decodedMessage) {
                        case FRIENDLY -> sendMessage(0, RECEIVER.FRIENDLY);
                        case AGGRESSIVE -> sendMessage(0, RECEIVER.AGGRESSIVE);
                    }
                }else if(decodedMessage == Settings.SENDER.ERROR){
                    //TODO
                }else {
                    sendMessage(0, Settings.RECEIVER.TO_FAST);
                }
            }else if(player == 1) {
                if(messagesPlayer2.isEmpty()) {
                    messagesPlayer2.add(decodedMessage);
                    switch(decodedMessage) {
                        case FRIENDLY -> sendMessage(1, RECEIVER.FRIENDLY);
                        case AGGRESSIVE -> sendMessage(1, RECEIVER.AGGRESSIVE);
                    }
                }else if(decodedMessage == Settings.SENDER.ERROR) {

                }else {
                    sendMessage(1, Settings.RECEIVER.TO_FAST);
                }
            }else {

            }
        }

        public Settings.SENDER decodeMessage(String message) {
            try{
                return Settings.SENDER.valueOf(Integer.parseInt(message));
            }catch(NumberFormatException e) {
                return Settings.SENDER.ERROR;
            }
        }
    }




    public static void main(String[] args) {
        JFrame frame = new JFrame("Game");
        frame.setContentPane(new Game().GamePnl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
