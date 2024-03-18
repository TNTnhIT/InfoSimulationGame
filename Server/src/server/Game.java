package server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import settings.Settings;

public class Game implements GameSettings{
    Server server;


    private JPanel GamePnl;
    private JButton btnStart;

    public Game() {
        this.server = new Server(8080);
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();


            }
        });
    }

    private void start() {
        int players = server.startFullGame();
        boolean evenNumberOfPlayers = players % 2 == 0;

        int anz = players;
        Stack<Integer> stack = new Stack<>();
        if(!evenNumberOfPlayers) {
            anz++;
        }


        for(int i = anz-1; i > 0; i--) {
            stack.push(0);
            for(int j = 0; j < (anz/2)-1; j++) {
                stack.push(((i+j)%(anz-1))+1);
            }
            for(int j = (anz/2)-1; j < anz-1; j++) {
                //Start Game
                int player1 = stack.pop();
                int player2 = (((i+j)%(anz-1))+1);
                System.out.println(player1 + " | " + player2);
                startGame(player1, player2);
            }
            System.out.println("-------------------");
        }




    }

    public void startGame(int player1, int player2) {
        new SmallGame(server.clients.get(player1), player1, server.clients.get(player2), player2);
    }

    public class SmallGame extends Thread{
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


        public SmallGame(ClientHandler player1, int player1Int, ClientHandler player2, int player2Int) {
            this.player1Int = player1Int;
            this.player2Int = player2Int;
            this.player1 = player1;
            this.player2 = player2;
        }

        @Override
        public void run() {
            initializeGame();
            startGameLoop();
        }

        private void startGameLoop() {
            isPlayer1Friendly = true;
            isPlayer2Friendly = true;

            for(int i = 0; i < NUMBER_OF_MOVES; i++) {
                sendMessage(2, Settings.RECEIVER.NEXT_ROUND);

                //waiting for messages
                while(messagesPlayer1.isEmpty() || messagesPlayer2.isEmpty()) {

                }





                if(isPlayer1Friendly && isPlayer2Friendly) {
                    pointsPlayer1 += 3;
                    pointsPlayer2 += 3;
                } else if(!isPlayer1Friendly && !isPlayer2Friendly) {
                    pointsPlayer1 += 1;
                    pointsPlayer2 += 1;
                } else if(!isPlayer1Friendly && isPlayer2Friendly) {
                    pointsPlayer1 += 5;
                    //player 2 += 0
                } else if(isPlayer1Friendly && !isPlayer2Friendly) {
                    //player 1 += 0
                    pointsPlayer2 += 5;
                }

            }


        }

        private void initializeGame() {
            pointsPlayer1 = 0;
            pointsPlayer2 = 0;
            server.setOpponent(player1Int, player2Int);
            player1.setSmallGame(this, 0);
            player2.setSmallGame(this, 1);
            sendMessage(2, Settings.RECEIVER.GAME_START);
            messagesPlayer1 = new CopyOnWriteArrayList<>();
            messagesPlayer2 = new CopyOnWriteArrayList<>();
        }


        private void sendMessage(int player, Settings.RECEIVER message) {
            if(player == 0) {
                player2.sendMessage(String.valueOf(message.num));
            }else if(player == 1){
                player1.sendMessage(String.valueOf(message.num));
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
                }else if(decodedMessage == Settings.SENDER.ERROR){
                    //TODO
                }else {
                    sendMessage(0, Settings.RECEIVER.TO_FAST);
                }
            }else if(player == 1) {
                if(messagesPlayer2.isEmpty()) {
                    messagesPlayer2.add(decodedMessage);
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
