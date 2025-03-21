import com.github.sarxos.webcam.Webcam;

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
    static BufferedImage myPicture = null;
    private static int countStepGame;
    static Thread thread1;
    static RedMain redmain;
    public static void guiTest( Webcam webcam) {
        // frame for bounds detected:
        MyLabel imageLabel = new MyLabel();

        if (mainFrame!=null) {
        mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        }

        mainFrame = new JFrame("BoundsTarget");
        myPicture = webcam.getImage();

        // Буффер для изменнения картинки в серый
        BufferedImage blackAndWhiteImg = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

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
//        // Создаем объектную переменую для потока и сам поток:
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

        Circle circle = redSearch.getCircle(); //находим внешний круг
        if (circle==null) {
            System.out.println("--!! No circle !!--");
        } else {

            // Объектная переменная для синей обводки:
            Circle myPoint = detectedRedPointOnTarget(imageLabel, colorImg, imgIcon);

            // todo ниже строчка кода для эксперементов с MyWebcamPanel:
            // Здесь буду передавать отображение красных точек на panelWebcam:
            detectedRedPointOn___WebcamPanel(Main.panelWebcam, myPicture, imgIcon);
            //____________________________________________________________________


            // todo если убрать  imageLabel.drawPoint(myPoint, dHeight); ничего не происходит - решить убратьь его или оставить:
            //imageLabel.drawPoint(myPoint, dHeight);

            //--------------------------------------------------------------------
            imageLabel.drawCircle(circle.getX(), circle.getY(), circle.getRadius(), dHeight);
            ArrayList<Circle> circlesList = redSearch.getCircles(circle); //находим все внутренние круги
            imageLabel.drawCircles(circlesList);
            circlesList.add(circle); //если нужен список со всеми кругами

            // Новый алгоритм точности попадания:
            int circleIndex = getCircleIndByXY(myPoint.getX(), myPoint.getY(), circlesList);

            // Если точка в центре или в области промаха:
            if (circleIndex == -1){
                int soloCircleIndex = getSoloCircleIndByXY(myPoint.getX(), myPoint.getY(), circlesList);
                if (soloCircleIndex==-99) {



                    System.out.println("Red point detected in center");
                    Main.listHits.add(10);
                    Main.myTextArea.append("Player "+((Main.player)+1)+" hit points"+Main.listHits.get((Main.shot++))+"\n");

                    if (Main.shots == (Main.shot / (Main.player+1) ) ) Main.player++;
                    System.out.println("Main.shot = "+Main.shot);
                    System.out.println("Main.countShot = "+Main.shots);

                    System.out.println("Main.player = "+Main.player);
                    System.out.println("Main.playerCount = "+Main.players);
                    if (Main.player == Main.players) Main.totalScore();
                    else Main.restartingTheStream();

                }

                else if (myPoint.getRadius()!=-500){



                    System.out.println("Red point detected in miss");

                    Main.listHits.add(0);
                    Main.myTextArea.append("Player "+((Main.player)+1)+" hit points"+Main.listHits.get((Main.shot++))+"\n");

                    if (Main.shots == (Main.shot / (Main.player+1) ) ) Main.player++;
                    System.out.println("Main.shot = "+Main.shot);
                    System.out.println("Main.countShot = "+Main.shots);
                    System.out.println("Main.player = "+Main.player);
                    System.out.println("Main.playerCount = "+Main.players);
                    if (Main.player == Main.players) Main.totalScore();
                    else Main.restartingTheStream();
                }
            }
            // Если точка находится между кругами
            else if (circleIndex < circlesList.size())
            {



                System.out.println("Red point detected in between circles");
                if (circleIndex == 0) Main.listHits.add(8);
                else if (circleIndex == 1) Main.listHits.add(5);
                else if (circleIndex == 2) Main.listHits.add(2);

                Main.myTextArea.append("Player "+((Main.player)+1)+" hit points"+Main.listHits.get((Main.shot++))+"\n");

                if (Main.shots == (Main.shot / (Main.player+1) ) ) Main.player++;
                System.out.println("Main.shot = "+Main.shot);
                System.out.println("Main.countShot = "+Main.shots);
                System.out.println("Main.player = "+Main.player);
                System.out.println("Main.playerCount = "+Main.players);
                if (Main.player == Main.players) Main.totalScore();
                else Main.restartingTheStream();

            }
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

        double leftPart_2 = Math.pow(xRedPoint-circleNext.getX(), 2) + Math.pow(yRedPoint-circleNext.getY(), 2);

        if (leftPart_1>=((circleRc1)*(circleRc1)) && leftPart_2<=((circleRc2)*(circleRc2)))
        {
            return true;
        }

        return false;
    }

    // Методы для алгоритма точности попадания центр или в область промаха:
    private static int getSoloCircleIndByXY(int xRedPoint, int yRedPoint, ArrayList<Circle> circles2) {
        int i = 0;
        // Передаю первый и последний круг в метод circleSoloIs_OnXY:
        Circle circleCentre =  circles2.get(1);

        System.out.println(circles2.size()-1);

        // Возвращает -99 если красная точка в центре или 99 если в области промаха.

        // Левая часть нужна для нахождения радиуса от центра до нашей красной точки:
        double leftPart = Math.pow(xRedPoint-circleCentre.getX(), 2) + Math.pow(yRedPoint-circleCentre.getY(), 2);

        // Возвращает -99 если красная точка в центре или 99 если в области промаха.

        if (leftPart<=((circleCentre.getRadius())*(circleCentre.getRadius()))) return -99;
        // Иначе если красная точка существует, то она в промахе (условие ниже не использовано потому что выходит за границы)
        return 100;
    }


    // metod for detected red poins on target:
    private static Circle detectedRedPointOnTarget(MyLabel imageLabel, BufferedImage myPicture, ImageIcon imgIcon) {
        float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
        int newWidth = (int) (myPicture.getWidth() * dHeight);
        Image dimg = myPicture.getScaledInstance(newWidth, imageLabel.getHeight(), Image.SCALE_SMOOTH);
        imgIcon.setImage(dimg);

        RedSearch redSearch = new RedSearch(myPicture);

        Circle myPoint = redSearch.findRedPointsAsCircle(); // это наша красная точка

        imageLabel.drawPoint(myPoint, dHeight);//, обведенная синим квадратом

        return myPoint;


    }

    private static void detectedRedPointOn___WebcamPanel(MyWebcamPanel panelWebcam, BufferedImage myPicture, ImageIcon imgIcon) {
//        float dHeight = panelWebcam.getHeight() / (float) myPicture.getHeight();
//        int newWidth = (int) (myPicture.getWidth() * dHeight);
//        Image dimg = myPicture.getScaledInstance(newWidth, panelWebcam.getHeight(), Image.SCALE_SMOOTH);
//        imgIcon.setImage(dimg);

        RedSearch redSearch = new RedSearch(myPicture);
        Circle myPoint = redSearch.findRedPointsAsCircle(); // это наша красная точка
        panelWebcam.drawPointOnWebPanel(myPoint);//, обведенная синим квадратом
        Main.window.add(panelWebcam, BorderLayout.CENTER);
        //return myPoint;


    }

}
