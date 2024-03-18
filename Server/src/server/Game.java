package server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class Game {
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

    }

    private class SmallGame extends Thread{
        int player1;
        int player2;

        int pointsPlayer1;
        int pointsPlayer2;

        boolean isPlayer1Friendly;
        boolean isPlayer2Friendly;


        public SmallGame(int player1, int player2) {
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

        private void initializeGame() {
            pointsPlayer1 = 0;
            pointsPlayer2 = 0;
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
