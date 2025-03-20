import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class RedMain {
    private static JFrame mainFrame;
    //private static JFrame pointSearchFrame;
    static BufferedImage myPicture = null;
    //static BufferedImage myPicture2 = null;
    static ArrayList<Circle> circlesList;
    private static String text;
    private static int countStepGame;
    static Thread thread1;
    static RedMain redmain;
    public static void closeRedMain(){

        mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
    }

    public static void guiTest( Webcam webcam) {
        // frame for bounds detected:
        MyLabel imageLabel = new MyLabel();
        if (mainFrame!=null) {
        mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
//            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        mainFrame = new JFrame("BoundsTarget");
        myPicture = webcam.getImage();

        // Буффер для изменнения картинки в серый
        BufferedImage blackAndWhiteImg = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//		BufferedImage blackAndWhiteImg = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(),BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D graphics = blackAndWhiteImg.createGraphics();

        graphics.drawImage(myPicture, 0, 0, null);
        ImageIcon imgIcon = new ImageIcon(blackAndWhiteImg);
        imageLabel.setIcon(imgIcon);

        mainFrame.remove(imageLabel);
        mainFrame.add(imageLabel, BorderLayout.CENTER);
        mainFrame.setSize(800, 600);
        mainFrame.setVisible(true);
        resizeImage(imageLabel, blackAndWhiteImg, imgIcon, myPicture);
        System.out.println("Black");
        mainFrame.setLocationRelativeTo(null);
        imageLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeImage(imageLabel, blackAndWhiteImg, imgIcon, myPicture);
                System.out.println("color");
            }
        });

        countStepGame = 1;
        redmain = new RedMain();
        // Создаем объектную переменую для потока и сам поток:
        SimpleRunnable run1 = new SimpleRunnable(redmain, mainFrame, Main.webcam, imageLabel);
         thread1 = new Thread(run1); //создаем поток и передаем ему наш объект
        thread1.start();
    }
    public static void resizeImage(MyLabel imageLabel, BufferedImage myPicture, ImageIcon imgIcon, BufferedImage colorImg) {


        float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
        int newWidth = (int) (myPicture.getWidth() * dHeight);
        Image dimg = myPicture.getScaledInstance(newWidth, imageLabel.getHeight(), Image.SCALE_SMOOTH);
        imgIcon.setImage(dimg);
        RedSearch redSearch = new RedSearch(myPicture);


       // redSearch.findRedPoints(); //Находим координаты красной точки
        Circle circle = redSearch.getCircle(); //находим внешний круг
        if (circle==null) {
            System.out.println("--!! No circle !!--");
        } else {

            //red point detected:
            // Объектная переменная для синей обводки:
            Circle myPoint = detectedRedPointOnTarget(imageLabel, colorImg, imgIcon);
            imageLabel.drawPoint(myPoint, dHeight);
            //--------------------------------------------------------------------
            imageLabel.drawCircle(circle.getX(), circle.getY(), circle.getRadius(), dHeight);
//            System.out.println("--- inner circles search ---");
            ArrayList<Circle> circlesList = redSearch.getCircles(circle); //находим все внутренние круги
            imageLabel.drawCircles(circlesList);
            circlesList.add(circle); //если нужен список со всеми кругами

            // Новый алгоритм точности попадания:
            int circleIndex = getCircleIndByXY(myPoint.getX(), myPoint.getY(), circlesList);
//            System.out.println("circleIndex = "+circleIndex);

            // Если точка в центре или в области промаха:
            if (circleIndex == -1){
                int soloCircleIndex = getSoloCircleIndByXY(myPoint.getX(), myPoint.getY(), circlesList);
                if (soloCircleIndex==-99) {

                    System.out.println("Red point detected in center");
                    Main.listScorePlayers.add(10);
                    // Возможно счет будет отоброжаться не верно из-за - Main.listScorePlayers.get(Main.shot+Main.player)
                    Main.myTextArea.append("Player "+((Main.player)+1)+" hit points"+Main.listScorePlayers.get((Main.shot++))+"\n");

                    if (Main.countShot == (Main.shot / (Main.player+1) ) ) Main.player++;
                    System.out.println("Main.shot = "+Main.shot);
                    System.out.println("Main.countShot = "+Main.countShot);

                    System.out.println("Main.player = "+Main.player);
                    System.out.println("Main.playerCount = "+Main.playerCount);
                    if (Main.player == Main.playerCount) Main.totalScore();
                    else Main.restartingTheStream(thread1,redmain, mainFrame, Main.webcam, imageLabel);
//                    thread1.stop();
//
//                    //if ((Main.player + Main.shot + 2) != Main.playerCount + Main.countShot) Main.restartingTheStream();
//
//                    // ++countStepGame;
//                    //  if (countStepGame < ((Main.playerCount+1)* Main.countShot)){
//                    // Restart of stream:
//                    //RedMain redmain = new RedMain(); // Возможно не нужно пересоздавать объектную переменную redmain,
//                    // а вместо этого сделать её глобальной.
//
//                    System.out.println("Stream after stop");
//
//                    SimpleRunnable run1 = new SimpleRunnable(redmain, mainFrame, Main.webcam, imageLabel);
//                    thread1 = new Thread(run1); //создаем поток и передаем ему наш объект
//                    thread1.start();
//
//                    System.out.println("Stream restart");
                    // }


                    //SimpleRunnable.SetText(Main.myTextArea,"Red point detected in miss");
                    //text = "Red point detected in miss";



                }
                //else if (soloCircleIndex==99){
                else if (myPoint.getRadius()!=-500){

                    //System.out.println("circleIndex = "+soloCircleIndex);
                    System.out.println("Red point detected in miss");
                    //Main.myTextArea.append(""+"Red point detected in miss"+"\n");

                    Main.listScorePlayers.add(0);
                    // Возможно счет будет отоброжаться не верно из-за - Main.listScorePlayers.get(Main.shot+Main.player)
                    Main.myTextArea.append("Player "+((Main.player)+1)+" hit points"+Main.listScorePlayers.get((Main.shot++))+"\n");

                    if (Main.countShot == (Main.shot / (Main.player+1) ) ) Main.player++;
                    System.out.println("Main.shot = "+Main.shot);
                    System.out.println("Main.countShot = "+Main.countShot);

                    System.out.println("Main.player = "+Main.player);
                    System.out.println("Main.playerCount = "+Main.playerCount);
                    if (Main.player == Main.playerCount) Main.totalScore();
                    else Main.restartingTheStream(thread1,redmain, mainFrame, Main.webcam, imageLabel);
//                    thread1.stop();
//
//                    //if ((Main.player + Main.shot + 2) != Main.playerCount + Main.countShot) Main.restartingTheStream();
//
//                    // ++countStepGame;
//                    //  if (countStepGame < ((Main.playerCount+1)* Main.countShot)){
//                    // Restart of stream:
//                    //RedMain redmain = new RedMain(); // Возможно не нужно пересоздавать объектную переменную redmain,
//                    // а вместо этого сделать её глобальной.
//
//                    System.out.println("Stream after stop");
//
//                    SimpleRunnable run1 = new SimpleRunnable(redmain, mainFrame, Main.webcam, imageLabel);
//                    thread1 = new Thread(run1); //создаем поток и передаем ему наш объект
//                    thread1.start();
//
//                    System.out.println("Stream restart");
                    // }


                    //SimpleRunnable.SetText(Main.myTextArea,"Red point detected in miss");
                    //text = "Red point detected in miss";
                }
            }
            // Если точка находится между кругами
            else if (circleIndex < circlesList.size())
            {

                System.out.println("Red point detected in between circles");
                if (circleIndex == 0) Main.listScorePlayers.add(8);
                else if (circleIndex == 1) Main.listScorePlayers.add(5);
                else if (circleIndex == 2) Main.listScorePlayers.add(2);

                // Возможно счет будет отоброжаться не верно из-за - Main.listScorePlayers.get(Main.shot+Main.player)
                Main.myTextArea.append("Player "+((Main.player)+1)+" hit points"+Main.listScorePlayers.get((Main.shot++))+"\n");

                if (Main.countShot == (Main.shot / (Main.player+1) ) ) Main.player++;
                System.out.println("Main.shot = "+Main.shot);
                System.out.println("Main.countShot = "+Main.countShot);

                System.out.println("Main.player = "+Main.player);
                System.out.println("Main.playerCount = "+Main.playerCount);
                if (Main.player == Main.playerCount) Main.totalScore();
                else Main.restartingTheStream(thread1,redmain, mainFrame, Main.webcam, imageLabel);


//                    thread1.stop();
//
//                    //if ((Main.player + Main.shot + 2) != Main.playerCount + Main.countShot) Main.restartingTheStream();
//
//                    // ++countStepGame;
//                    //  if (countStepGame < ((Main.playerCount+1)* Main.countShot)){
//                    // Restart of stream:
//                    //RedMain redmain = new RedMain(); // Возможно не нужно пересоздавать объектную переменную redmain,
//                    // а вместо этого сделать её глобальной.
//
//                    System.out.println("Stream after stop");
//
//                    SimpleRunnable run1 = new SimpleRunnable(redmain, mainFrame, Main.webcam, imageLabel);
//                    thread1 = new Thread(run1); //создаем поток и передаем ему наш объект
//                    thread1.start();
//
//                    System.out.println("Stream restart");
                // }


                //SimpleRunnable.SetText(Main.myTextArea,"Red point detected in miss");
                //text = "Red point detected in miss";
            }

            // ----------------------------------------------------------------------


        }

    }



    // Методы для алгоритма точности попадания между кругами:
    private static int getCircleIndByXY(int xRedPoint, int yRedPoint, ArrayList<Circle> circles2) {
        int i = 0;
        for (Circle circle : circles2) {
            int count = circles2.indexOf(circle);
            // Проверяем чтобы следующий круг не выходил за границы:
            if ((count) < circles2.size()-1 ){
                Circle circleNext = circles2.get(count + 1);
                if (circleIs_OnXY(circle, circleNext, xRedPoint, yRedPoint)){
                    System.out.println("\n\n i = " + i);
                    return i;
                }
                i++;
            }
        }
        // Возвращает -1 если красная точка в центре или в области промаха.
        return -1;
    }

    private static boolean circleIs_OnXY(Circle circle,Circle circleNext, int xRedPoint, int yRedPoint) {
//		(x – a)2 + (y – b)2 = R2
        int circleRc1 = circle.getRadius();
        int circleRc2 = circleNext.getRadius();
        // Левая часть нужна для нахождения радиуса от центра до нашей красной точки:
        double leftPart_1 = Math.pow(xRedPoint-circle.getX(), 2) + Math.pow(yRedPoint-circle.getY(), 2);
//        System.out.println("");
//        System.out.println("");
//        System.out.println("xRedPoint-circle.getX()"+(xRedPoint-circle.getX()));
//        System.out.println("yRedPoint-circle.getY()"+(yRedPoint-circle.getY()));
//        System.out.println("leftPart_1 = "+leftPart_1);
        // Значения в leftPart_1 и leftPart_2 одинаковые!!!
        double leftPart_2 = Math.pow(xRedPoint-circleNext.getX(), 2) + Math.pow(yRedPoint-circleNext.getY(), 2);
//        System.out.println("xRedPoint-circleNext.getX()"+(xRedPoint-circleNext.getX()));
//        System.out.println("yRedPoint-circleNext.getY()"+(yRedPoint-circleNext.getY()));
//        System.out.println("leftPart_2 = "+leftPart_2);
//        System.out.println("");
//        System.out.println("");
        // И сравниваем с радиусами текущего и следующего круга:
        if (leftPart_1>=((circleRc1)*(circleRc1)) && leftPart_2<=((circleRc2)*(circleRc2)))
        {
//            System.out.println("\n\nleftPart_1 = "+leftPart_1);
//            System.out.println("circleRc1^2 = "+(circleRc1)*(circleRc1));
//            System.out.println("circleRc1^2 = "+(circleRc2)*(circleRc2));
//            System.out.println("\n\n");
            return true;
        }

        return false;
    }

    // Методы для алгоритма точности попадания центр или в область промаха:
    private static int getSoloCircleIndByXY(int xRedPoint, int yRedPoint, ArrayList<Circle> circles2) {
        int i = 0;
        // Передаю первый и последний круг в метод circleSoloIs_OnXY:
        Circle circleCentre =  circles2.get(1);
        //Circle circleLast =  circles2.get(circles2.size()-1);
        System.out.println(circles2.size()-1);

        // Возвращает -99 если красная точка в центре или 99 если в области промаха.

        // Левая часть нужна для нахождения радиуса от центра до нашей красной точки:
        double leftPart = Math.pow(xRedPoint-circleCentre.getX(), 2) + Math.pow(yRedPoint-circleCentre.getY(), 2);

        // Возвращает -99 если красная точка в центре или 99 если в области промаха.

        if (leftPart<=((circleCentre.getRadius())*(circleCentre.getRadius()))) return -99;
        // Иначе если красная точка существует, то она в промахе (условие ниже не использовано потому что выходит за границы)
        //if (leftPart>=((circleLast.getRadius())*(circleLast.getRadius()))) return 99;
        return 100;
    }


    //__________________________________________________________________________________________________________________


    // Метод добaвленный из IDEA:
    // metod for detected red poins on target:
    private static Circle detectedRedPointOnTarget(MyLabel imageLabel, BufferedImage myPicture, ImageIcon imgIcon) {
        float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
        int newWidth = (int) (myPicture.getWidth() * dHeight);
        Image dimg = myPicture.getScaledInstance(newWidth, imageLabel.getHeight(), Image.SCALE_SMOOTH);
        imgIcon.setImage(dimg);

        RedSearch redSearch = new RedSearch(myPicture);

        Circle myPoint = redSearch.findRedPointsAsCircle(); // это наша красная точка

        imageLabel.drawPoint(myPoint, dHeight);//, обведенная синим квадратом

        //System.out.println("red dot circle = " + myPoint.getX() + " " + myPoint.getY() + " " + myPoint.getRadius());
        return myPoint;


    }

//    private static void consoleTest() {
//        RedSearch redSearch = new RedSearch("img.png");
//        redSearch.findRedPoints();
//
//    }

}
