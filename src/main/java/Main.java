import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
//import jdk.internal.icu.text.UnicodeSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static MyWebcamPanel panelWebcam; // Панель для работы с камерой
    public static JFrame mainFrame; // Фрейм на котором отображается поток с веб-камеры и todo будет производиться настройка цвета лазера и черных кругов - калибровка
    static Webcam webcam = Webcam.getDefault(); // Объектная переменная для работы с веб-камерой
    static JTextArea myTextArea; // текстовое поле для вывода попаданий игрока, подсчет всех попаданий для каждого игрока и вывода лучшего игрока
    static JTextField inputPlayerCount; // текстовое поле для ввода кол-ва игроков
    static RedMain redMain; // Объектная переменная для работы с классом
    static JTextField inputCountShot; // текстовое поле для ввода допустимого кол-ва попаданий на игрока
    static ArrayList<Integer> listHits = new ArrayList<>(); // Лист хранящий каждое попадание всеми игроками - Пример 5,5,8,10,0
    static ArrayList<Circle> pointList = new ArrayList<>(); // Лист хранящий в себе координаты каждого попадания
    static int player; // переменная для хранения с каким игроком по счеты мы сейчас взаимодействуем
    static int shot; // переменная для хранения какой выстрел по счету
    static int players; // переменная для хранения кол-ва всех игроков участвующих в игре
    static int shots;// переменная для хранения допустимого кол-ва попаданий
    static boolean printAllHits = false; // переменная для вывода всех попаданий после конца игры
    static JCheckBox redCalibrationChBox = new JCheckBox("Red");
    static JCheckBox blackCalibrationChBox = new JCheckBox("Black");
    //static JCheckBox whiteCalibrationChBox = new JCheckBox("White");

    public static void main(String[] args) throws IOException {
        // проверять кол-во камее, и если 1, то брать дефолтную, иначе вторую
        var cams = Webcam.getWebcams();

        if (cams.size()>1) {
            webcam = cams.get(1);
        }


        webcam.setViewSize(WebcamResolution.VGA.getSize());// Настраиваем разрешение для камеры

        panelWebcam = new MyWebcamPanel(webcam);  // Передаем объект камеры на специальную панель для вывода изображения с камеры
        panelWebcam.setImageSizeDisplayed(true);  // Делаем веб-панель видимой

        mainFrame = new JFrame("Webcam"); // Создаем главный фрейм
        mainFrame.setPreferredSize(new Dimension(986,661));  // Настраиваем размер фрейма
        mainFrame.add(panelWebcam, BorderLayout.CENTER); // добавляем веб-панель на главный фрейм

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

        // add JCheckBox for detected:
        panelNORTH.add(redCalibrationChBox);
        panelNORTH.add(blackCalibrationChBox);
       // panelNORTH.add(whiteCalibrationChBox);

        // add panel in frame - window:
        mainFrame.add(panelNORTH, BorderLayout.NORTH);

        // add stopButton for stopped Runnable:
        JButton stopButton = new JButton("Stopped detected");
        mainFrame.add(stopButton, BorderLayout.SOUTH);

        // Создаем текстовое поле для вывода информации об выстрелах игроков
        myTextArea = new JTextArea(10,20);
        // И помещаем текстовое поле на скол
        JScrollPane scrollPane = new JScrollPane(myTextArea);
        // и добавляем этот скол на главный фрейм
        mainFrame.add(scrollPane,BorderLayout.EAST);

        // И заканчиваем настройку главного фрейма
        mainFrame.setResizable(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
        // открываем видео поток для камеры
        webcam.open();

        // Слушатель кнопки для начала потока (распознавания красных точек):
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
                        pointList = new ArrayList<>();
                        redMain.repaint();

                    }
                }
                catch(NumberFormatException ex){
                    ex.getMessage();
                }
            }
        });



        // Слушатель для остановки потока:
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SimpleRunnable.stopped();
            }
        });

        // Слушатель созданный для калибровки цветов через мышь:
        mainFrame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                BufferedImage image = webcam.getImage();
                // Если нажали на калибровку красной точки:
                if (redCalibrationChBox.isSelected()) {
                    //System.out.println("---!! image clicked at x = "+e.getX()+" y="+e.getY()+ " !!---");
                    int p = image.getRGB(e.getX(), e.getY() / 2);
                    int r = (p >> 16) & 0xff; // get red
                    int g = (p >> 8) & 0xff; // get green
                    int b = p & 0xff; // get blue

                    // Метод для передачи диапазона цвета нашей красной точки:
                    RedSearch.passDiaposoneColorRedPoint(r,g,b);

                    redCalibrationChBox.setSelected(false);
                }
                // Если нажали на калибровку черного круга:
                else if (blackCalibrationChBox.isSelected()) {
                    //System.out.println("---!! image clicked at x = "+e.getX()+" y="+e.getY()+ " !!---");

                    int p = image.getRGB(e.getX(), e.getY() / 2);
                    int r = (p >> 16) & 0xff; // get red
                    int g = (p >> 8) & 0xff; // get green
                    int b = p & 0xff; // get blue

                    // Метод для передачи диапазона цвета нашего черного круга:
                    RedSearch.blackCirclePassDiaposoneColor(r,g,b);

                    blackCalibrationChBox.setSelected(false);
                }
                // Если нажали на калибровку белого фона:
//                else if (whiteCalibrationChBox.isSelected()) {
//                    //System.out.println("---!! image clicked at x = "+e.getX()+" y="+e.getY()+ " !!---");
//                    RedSearch.backgroundFoundDiaposonColor(e.getX(), e.getY());
//                    whiteCalibrationChBox.setSelected(false);
//                }
            }
        });
    }



    // ф-ия перезапуска потока:
    public static void restartingTheStream() {
        SimpleRunnable.stopped();
        SimpleRunnable.contented();
    }

    // ф-ия для подсчета очков попадания для игрока и для определения лучшего игрока:
    public static void totalScore() {
        //   System.out.println("\nGame the end, stream stop");
        SimpleRunnable.stopped();

        ArrayList<Integer> listPlayersTotal = new ArrayList<>();

        // Подсчитываем кол-во очков попадания для каждого игрока:
        myTextArea.append("-------------------------------------------\n");
        myTextArea.append("Players total score:\n");

        int countPlayer = 0;
        int totalScore = 0;
        int hitStep = 0; ;
        for(Integer i : listHits) {

            totalScore += i;
            hitStep++;
            //       System.out.println("totalScore = "+totalScore);

            if (hitStep == Integer.parseInt(inputCountShot.getText())){
                listPlayersTotal.add(totalScore);
                myTextArea.append("Player - "+(++countPlayer)+" total score - "+totalScore+"\n");
                totalScore = 0;
                hitStep = 0;
            }
        }

        // Определяем лучшего игрока:
        myTextArea.append("-------------------------------------------\n");

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
        myTextArea.append("Winner player № "+(winnerPlayer) + "\n");

        // Вызываем метод для итогово изображения всех попаданий по мешени:
        printAllHits();

    }
    // После конца игры и остановки потока выводим все попадания на второй фрейм
    public static void printAllHits(){
        printAllHits = true;
        // Заменяем веб панель на обычную:
        BufferedImage totalPicture = webcam.getImage();
        Graphics2D graphics = totalPicture.createGraphics();
        graphics.drawImage(totalPicture, 0, 0, null);
        mainFrame.remove(panelWebcam);
        // Выводим все попадания на второй фрейм:
        redMain = new RedMain();
        redMain.trueDrawAllRentable();

    }

    // метод, который добавляет координаты красной точки в массив:
    public static void addPointList(Circle myPoint){
        pointList.add(myPoint);
    }

    // метод, который добавляет координаты красной точки в массив:
    public static void addListHits(int pointsForOneHit){
        listHits.add(pointsForOneHit);
    }

    // метод, проверяющий условия перехода очереди для игроков:
    public static void playerChangeCondition(){
        if (shots == (shot / (player+1) ) ) player++;
        if (player == players) totalScore();
        else restartingTheStream();
    }


    // метод вернёт список точек:
    public static ArrayList<Circle> returnedPointList(){
       return pointList;
    }



}