package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestClientForm extends Client{
    private JPanel mainPnl;
    private JButton sendFriendlyButton;
    private JButton sendAggressiveButton;
    private JTextField txtMyself;
    private JTextField txtOther;
    private JLabel lblOther;
    private JLabel lblMyself;

    public TestClientForm() { //TODO Test
        this("127.0.0.1", 8080);
        sendFriendlyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFriendly();
            }
        });
        sendAggressiveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendAggressive();
            }
        });
    }


    public TestClientForm(String IPAddress, int port) {
        super(IPAddress, port);
    }

    @Override
    public void gotMyselfFriendly() {
        super.gotMyselfFriendly();
        txtMyself.setText("Friendly");
    }

    @Override
    public void gotMyselfAggressive() {
        super.gotMyselfAggressive();
        txtMyself.setText("Aggressive");
    }

    @Override
    public void gotOtherFriendly() {
        super.gotOtherFriendly();
        txtOther.setText("Friendly");

    }

    @Override
    public void gotOtherAggressive() {
        super.gotOtherAggressive();
        txtOther.setText("Aggressive");
    }

    public static void start() {
        JFrame frame = new JFrame("TestClientForm");
        frame.setContentPane(new TestClientForm().mainPnl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
