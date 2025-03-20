

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static boolean GameContinuation = true;
    static Webcam webcam = Webcam.getDefault();
    static JTextArea myTextArea;
    static JTextField inputPlayerCount;
    static JTextField inputCountShot;
    static ArrayList<Integer> listScorePlayers = new ArrayList<>();
    static int player;
    static int shot;
    static int players;
    static int shots;
    //static SimpleRunnable stream;
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        // проверять кол-во камеер, и если 1, то брать дефолтную, иначе вторую
        var cams = Webcam.getWebcams();
        if (cams.size()>1) {
            webcam = cams.get(1);
        }
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel panelWebcam = new WebcamPanel(webcam);
        panelWebcam.setImageSizeDisplayed(true);

        JFrame window = new JFrame("Webcam");
        window.add(panelWebcam, BorderLayout.CENTER);

        // create panelNORTH for North:
        JPanel panelNORTH = new JPanel();

        // add label before startButton:
        JLabel labelPlayerCount = new JLabel("Введите кол-во игроков: ");
        panelNORTH.add(labelPlayerCount);

        // add for input PlayerCount:
        inputPlayerCount = new JTextField("2",5);
        panelNORTH.add(inputPlayerCount);

        // add label before startButton:
        JLabel labelCountShot = new JLabel("Введите кол-во выстрелов: ");
        panelNORTH.add(labelCountShot);

        // add for input PlayerCount:
        inputCountShot = new JTextField("2",5);
        panelNORTH.add(inputCountShot);

        // add startButton for detected black circle and red point:
        JButton startButton = new JButton("Red detected");
        panelNORTH.add(startButton);

        // add panel in frame - window:
        window.add(panelNORTH, BorderLayout.NORTH);

        // add stopButton for stopped Runnable:
        JButton stopButton = new JButton("Stopped detected");
        window.add(stopButton, BorderLayout.SOUTH);

        //_____________________________________________________________
        myTextArea = new JTextArea(10,20);
        JScrollPane scrollPane = new JScrollPane(myTextArea);
        window.add(scrollPane,BorderLayout.EAST);

        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
        webcam.open();

        // Слушатель конпки для начала потока (расспознования красных точек):
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Обработка исключения пустой стоки преобразованной в целое:
                try{
                    if ( Integer.parseInt(inputPlayerCount.getText()) > 0 && Integer.parseInt(inputCountShot.getText()) > 0){

                       players = Integer.parseInt(inputPlayerCount.getText());
                       shots = Integer.parseInt(inputCountShot.getText());

                       RedMain.guiTest(webcam);
                    }
                }
                catch(NumberFormatException ex){
                    ex.getMessage();
                }
            }
        });
        //_______________________________________________

        // Слашатель для остановки потока:
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SimpleRunnable.stopped();
            }
        });
    }



    public static void restartingTheStream(Thread thread1,RedMain redmain, JFrame mainFrame, Webcam webcam, MyLabel imageLabel) {
        System.out.println("Enter restartingTheStream");
        SimpleRunnable.stopped();
        System.out.println("Stream after stop");
        SimpleRunnable.contented();
        System.out.println("Stream restart");
    }

    public static void totalScore() {
        System.out.println("Game the end, stream stop");
        SimpleRunnable.stopped();

        int winnerPlayer = -1;
        int shotsOfPlayer = shots / players;
        int winnerScore = 0;
        // todo Изменить алгоритм чтобы коректно находил максимальный счет:

        for (int i = 1; i< players; i++){
            int playerScore = 0;
            for (int j = 1; j<shotsOfPlayer;j++){
                playerScore += listScorePlayers.get(Main.shot++);
            }
            Main.myTextArea.append("--------------------------------------------\n");
            Main.myTextArea.append("Player - "+i+" total score - "+playerScore+"\n");
            if (playerScore >= winnerPlayer)
            {
                winnerPlayer = i;
                winnerScore = playerScore;
            }
        }
        // Выводим победителя и его общий счет попаданий:
        Main.myTextArea.append("\n--------------------------------------------\n");
        Main.myTextArea.append("Winner Player - "+(winnerPlayer)+" total score - "+winnerScore+"\n");

    }
}