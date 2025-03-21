

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import jdk.internal.icu.text.UnicodeSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static MyWebcamPanel panelWebcam;
    public static JFrame window;
    static Webcam webcam = Webcam.getDefault();
    static JTextArea myTextArea;
    static JTextField inputPlayerCount;
    static JTextField inputCountShot;
    static ArrayList<Integer> listHits = new ArrayList<>();
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

        panelWebcam = new MyWebcamPanel(webcam);
        panelWebcam.setImageSizeDisplayed(true);

        window = new JFrame("Webcam");
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
                        
                        RedMain.guiTest(webcam);
                        //Отчистка предыдущих значений:
                        players = Integer.parseInt(inputPlayerCount.getText());
                        shots = Integer.parseInt(inputCountShot.getText());
                        player = 0;
                        shot = 0;
                        listHits = new ArrayList<>();
                        myTextArea.setText("");
                        
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



    public static void restartingTheStream() {
        System.out.println("Enter restartingTheStream");
        SimpleRunnable.stopped();
        System.out.println("Stream after stop");
        SimpleRunnable.contented();
        System.out.println("Stream restart");
    }

    public static void totalScore() {
        System.out.println("\nGame the end, stream stop");
        SimpleRunnable.stopped();

        ArrayList<Integer> listPlayersTotal = new ArrayList<>();

        // Подсчитываем кол-во очков попадания для каждого игрока:
        Main.myTextArea.append("-------------------------------------------\n");
        Main.myTextArea.append("Players total score:\n");

        int countPlayer = 0;
        int totalScore = 0;
        int hitStep = 0; ;
        for(Integer i : listHits) {

                totalScore += i;
                hitStep++;
                System.out.println("totalScore = "+totalScore);

            if (hitStep == Integer.parseInt(inputCountShot.getText())){
                listPlayersTotal.add(totalScore);
                Main.myTextArea.append("Player - "+(++countPlayer)+" total score - "+totalScore+"\n");
                totalScore = 0;
                hitStep = 0;
            }
        }

        // Определяем лучшего игрока:
        Main.myTextArea.append("-------------------------------------------\n");

        int winnerPlayer = 0;
        int scoreWinner = -1;
            countPlayer = 0;
        for(Integer i : listPlayersTotal) {
            countPlayer++;
            if (i >= scoreWinner ){
                scoreWinner = i;
                winnerPlayer = (countPlayer);
            }
        }
        Main.myTextArea.append("Winner player № "+(winnerPlayer) + "\n");


    }
}