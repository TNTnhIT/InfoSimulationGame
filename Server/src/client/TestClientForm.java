package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class TestClientForm extends Client{
    private JPanel mainPnl;
    private JButton sendFriendlyButton;
    private JButton sendAggressiveButton;
    private JTextField txtMyself;
    private JTextField txtOther;
    private JLabel lblOther;
    private JLabel lblMyself;
    private JTextField txtPointsMe;
    private JTextField txtPointsOther;
    private JLabel lblPointsMe;
    private JLabel lblPointsOther;
    private JRadioButton rdbFriendly;
    private JRadioButton rdbAggressive;
    private JRadioButton rdbAlternate;
    private JRadioButton rdbRandom;
    private JRadioButton rdbManuel;
    private JLabel lblGameEnd;

    ///////////
    private int pointsMyself;
    private int pointsOther;

    private boolean myself;
    private boolean other;

    private int round;

    private enum Modes{
        MANUEL, FRIENDLY, AGGRESSIVE, ALTERNATE, RANDOM
    }
    private Modes mode;
    private boolean alternate;

    private boolean nextMove;
    Random rand;

    public TestClientForm() { //TODO Test
        this("127.0.0.1", 8080);
        mode = Modes.MANUEL;
        rand = new Random();
        alternate = rand.nextBoolean();
        lblGameEnd.setVisible(false);
        sendFriendlyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(mode == Modes.MANUEL) {
                    nextMove = true;
                    sendFriendly();
                }

            }
        });
        sendAggressiveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(mode == Modes.MANUEL) {
                    nextMove = false;
                    sendAggressive();
                }

            }
        });
        rdbManuel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = Modes.MANUEL;
            }
        });
        rdbFriendly.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = Modes.FRIENDLY;
                sendFriendly();
            }
        });
        rdbAggressive.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = Modes.AGGRESSIVE;
                sendAggressive();
            }
        });
        rdbAlternate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = Modes.ALTERNATE;
                if(alternate)
                    sendFriendly();
                else
                    sendAggressive();
                alternate = !alternate;
            }
        });
        rdbRandom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = Modes.RANDOM;
                if(rand.nextBoolean())
                    sendFriendly();
                else
                    sendAggressive();
            }
        });
    }


    public TestClientForm(String IPAddress, int port) {
        super(IPAddress, port);
    }

    @Override
    public void receivedMyselfFriendly() {
        super.receivedMyselfFriendly();
        txtMyself.setText("Friendly");
        myself = true;
    }

    @Override
    public void receivedMyselfAggressive() {
        super.receivedMyselfAggressive();
        txtMyself.setText("Aggressive");
        myself = false;
    }

    @Override
    public void receivedOtherFriendly() {
        super.receivedOtherFriendly();
        txtOther.setText("Friendly");
        other = true;


    }

    @Override
    public void receivedOtherAggressive() {
        super.receivedOtherAggressive();
        txtOther.setText("Aggressive");
        other = false;


    }

    @Override
    public void receivedNextRound() {
        super.receivedNextRound();
        txtMyself.setText("");

        if(myself && other) {
            pointsMyself += 3;
            pointsOther += 3;
        }else if(!myself && !other) {
            pointsMyself += 1;
            pointsOther += 1;
        }else if(!myself && other) {
            pointsMyself += 5;
            pointsOther += 0;
        }else if(myself && !other) {
            pointsMyself += 0;
            pointsOther += 5;
        }
        txtPointsMe.setText(String.valueOf(pointsMyself));
        txtPointsOther.setText(String.valueOf(pointsOther));

        switch(mode) {
            case MANUEL -> {}
            case FRIENDLY -> {
                nextMove = true;
                sendFriendly();
            }
            case AGGRESSIVE -> {
                nextMove = false;
                sendAggressive();
            }
            case ALTERNATE -> {
                nextMove = alternate;
                if(alternate)
                    sendFriendly();
                else
                    sendAggressive();
                alternate = !alternate;
            }
            case RANDOM -> {
                nextMove = rand.nextBoolean();
                if (nextMove)
                    sendFriendly();
                else
                    sendAggressive();
            }
        }

        //txtOther.setText("");
    }

    @Override
    public void receivedGameStart() {
        super.receivedGameStart();
        txtMyself.setText("");
        txtOther.setText("");
        txtPointsMe.setText("");
        txtPointsOther.setText("");
        pointsMyself = 0;
        pointsOther = 0;
        myself = true;
        other = true;
        lblGameEnd.setVisible(false);
        //mode = Modes.MANUEL;
        //rdbManuel.setSelected(true);
        switch(mode) {
            case MANUEL -> {}
            case FRIENDLY -> sendFriendly();
            case AGGRESSIVE -> sendAggressive();
            case ALTERNATE -> {
                if(alternate)
                    sendFriendly();
                else
                    sendAggressive();
                alternate = !alternate;
            }
            case RANDOM -> {
                if (rand.nextBoolean())
                    sendFriendly();
                else
                    sendAggressive();
            }
        }
        round = 0;

    }

    private void makeMove() {

    }

    @Override
    public void receivedGameEnd() {
        super.receivedGameEnd();
        lblGameEnd.setVisible(true);
        //mode = Modes.MANUEL;
        //rdbManuel.setSelected(true);

        if(myself && other) {
            pointsMyself += 3;
            pointsOther += 3;
        }else if(!myself && !other) {
            pointsMyself += 1;
            pointsOther += 1;
        }else if(!myself && other) {
            pointsMyself += 5;
            pointsOther += 0;
        }else if(myself && !other) {
            pointsMyself += 0;
            pointsOther += 5;
        }
        txtPointsMe.setText(String.valueOf(pointsMyself));
        txtPointsOther.setText(String.valueOf(pointsOther));


    }

    @Override
    public void receivedToFast() {
        super.receivedToFast();

    }

    public static void start() {
        JFrame frame = new JFrame("TestClientForm");
        frame.setContentPane(new TestClientForm().mainPnl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
