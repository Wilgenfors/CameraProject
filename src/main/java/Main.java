

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
    static Webcam webcam = Webcam.getDefault();
    static JTextArea myTextArea;
    static JTextField inputPlayerCount;
    static JTextField inputCountShot;
    static ArrayList<Integer> listScorePlayers = new ArrayList<>();
    static int player = 0;
    static int shot = 0;

    static int playerCount = 0;
    static int  countShot = 0;
    static SimpleRunnable stream;
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
        inputPlayerCount = new JTextField(5);
        panelNORTH.add(inputPlayerCount);

        // add label before startButton:
        JLabel labelCountShot = new JLabel("Введите кол-во выстрелов: ");
        panelNORTH.add(labelCountShot);

        // add for input PlayerCount:
        inputCountShot = new JTextField(5);
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
        //RedMain.guiTest(webcam);

        // Слушатель конпки для начала потока (расспознования красных точек):
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Обработка исключения пустой стоки преобразованной в целое:
                try{
                    if ( Integer.parseInt(inputPlayerCount.getText()) > 0 && Integer.parseInt(inputCountShot.getText()) > 0){

                        playerCount = Integer.parseInt(inputPlayerCount.getText());
                       countShot = Integer.parseInt(inputCountShot.getText());



                        // Запускаем поток и второй фрейм:
                                //SimpleRunnable.running();

                        // Эксперементальное создание потока и передача объектов конструктору:::::::::::::::::::::::::::

                        // Создаем необходимые объекты для создания конструктора:
                                RedMain.guiTest(webcam);
                        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::



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

                RedMain.thread1.stop();
            }
        });
        //_______________________________________________


        // todo после получения всех значений попаданий игроков сделать цикл который подсчитывает общий счет для
        // для каждого игрока и выводил победителя:
//
//        BufferedImage image = webcam.getImage();
//
//        ImageIO.write(image, ImageUtils.FORMAT_JPG, new File("selfie.jpg"));
    }

    public static void restartingTheStream() {
        // Эксперементальное создание потока и передача объектов конструктору:::::::::::::::::::::::::::

        // Создаем необходимые объекты для создания конструктора:

        //RedMain.guiTest(webcam);
        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    }
}