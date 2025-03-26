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
    static Thread thread1;
    static RedMain redmain;
    static MyLabel imageLabel;
    public static void guiTest( Webcam webcam) {
        // frame for bounds detected:
        imageLabel = new MyLabel();

        if (mainFrame!=null) {
        mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        }

        mainFrame = new JFrame("BoundsTarget");
        myPicture = webcam.getImage();

        // Буфф ер для изменения картинки в серый
        BufferedImage blackAndWhiteImg = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D graphics = blackAndWhiteImg.createGraphics();

        graphics.drawImage(myPicture, 0, 0, null);
        ImageIcon imgIcon = new ImageIcon(blackAndWhiteImg);
        imageLabel.setIcon(imgIcon);

        mainFrame.remove(imageLabel);
        mainFrame.add(imageLabel, BorderLayout.CENTER);

        // задаем размер для одинакового отображения нахождения крассных точек на двух фреймах
        mainFrame.setSize(640+16, 480+39); //
        mainFrame.setVisible(true);
        resizeImage(imageLabel, blackAndWhiteImg, imgIcon, myPicture);
        mainFrame.setLocationRelativeTo(null);
        imageLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeImage(imageLabel, blackAndWhiteImg, imgIcon, myPicture);
            }
        });

        redmain = new RedMain();

//        // Создаем объектную переменную для потока и сам поток:
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
           // System.out.println("--!! No circle !!--");
        } else {

            // Объектная переменная для синей обводки:
            Circle myPoint = detectedRedPointOnTarget(imageLabel, colorImg, imgIcon, Main.panelWebcam);
            // Добавляем каждую точку в Лист ели он есть те не дефолтное значение:
            if (myPoint.getX() != 500 && myPoint.getY() != 500) {
                Main.pointList.add(myPoint);
            }

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
                    Main.listHits.add(10);
                    Main.myTextArea.append("Player "+((Main.player)+1)+" hit points"+Main.listHits.get((Main.shot++))+"\n");

                    // Условия для перехода на следующего игрока:
                    if (Main.shots == (Main.shot / (Main.player+1) ) ) Main.player++;
                    if (Main.player == Main.players) Main.totalScore();
                    else Main.restartingTheStream();
                }

                else if (myPoint.getRadius()!=-500){
                    Main.listHits.add(0);
                    Main.myTextArea.append("Player "+((Main.player)+1)+" hit points"+Main.listHits.get((Main.shot++))+"\n");

                    // Условия для перехода на следующего игрока:
                    if (Main.shots == (Main.shot / (Main.player+1) ) ) Main.player++;
                    if (Main.player == Main.players) Main.totalScore();
                    else Main.restartingTheStream();
                }
            }
            // Если точка находится между кругами
            else if (circleIndex < circlesList.size())
            {
                if (circleIndex == 0) Main.listHits.add(8);
                else if (circleIndex == 1) Main.listHits.add(5);
                else if (circleIndex == 2) Main.listHits.add(2);

                Main.myTextArea.append("Player "+((Main.player)+1)+" hit points"+Main.listHits.get((Main.shot++))+"\n");

                // Условия для перехода на следующего игрока:
                if (Main.shots == (Main.shot / (Main.player+1) ) ) Main.player++;
                if (Main.player == Main.players) Main.totalScore();
                else Main.restartingTheStream();

            }
        }
    }



    // Метод для алгоритма точности попадания между кругами:
    private static int getCircleIndByXY(int xRedPoint, int yRedPoint, ArrayList<Circle> circles2) {
        int i = 0;
        for (Circle circle : circles2) {
            int count = circles2.indexOf(circle);
            // Проверяем чтобы следующий круг не выходил за границы:
            if ((count) < circles2.size()-1 ){
                Circle circleNext = circles2.get(count + 1);
                if (circleIs_OnXY(circle, circleNext, xRedPoint, yRedPoint)){
                    //System.out.println("\n\n i = " + i);
                    return i;
                }
                i++;
            }
        }
        // Возвращает -1 если красная точка в центре или в области промаха.
        return -1;
    }

    // Метод для алгоритма точности попадания между кругами:
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
        int i = 0; // todo Возможно удалить эту переменную
        // Передаю первый и последний круг в метод circleSoloIs_OnXY:
        Circle circleCentre =  circles2.get(1);
        // Левая часть нужна для нахождения радиуса от центра до нашей красной точки:
        double leftPart = Math.pow(xRedPoint-circleCentre.getX(), 2) + Math.pow(yRedPoint-circleCentre.getY(), 2);
        // Возвращает -99 если красная точка в центре или 99 если в области промаха.
        if (leftPart<=((circleCentre.getRadius())*(circleCentre.getRadius()))) return -99;
        // Иначе если красная точка существует, то она в промахе (условие ниже не использовано потому что выходит за границы)
        return 100;
    }


    // ф-ия для нахождения красной точки на фреймах и прорисовки их:
    private static Circle detectedRedPointOnTarget(MyLabel imageLabel, BufferedImage myPicture, ImageIcon imgIcon,MyWebcamPanel panelWebcam) {
        float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
        int newWidth = (int) (myPicture.getWidth() * dHeight);
        Image dimg = myPicture.getScaledInstance(newWidth, imageLabel.getHeight(), Image.SCALE_SMOOTH);
        imgIcon.setImage(dimg);

        // Находим где наша точка на мишени и прорисовываем по координатам на первом и втором фрейме:
        RedSearch redSearch = new RedSearch(myPicture);
        Circle myPoint = redSearch.findRedPointsAsCircle(); // это наша красная точка

        imageLabel.drawPoint(myPoint, dHeight);//, обведенная синим квадратом

        panelWebcam.drawPointOnWebPanel(myPoint, dHeight);//, обведенная синим квадратом
        Main.mainFrame.add(panelWebcam, BorderLayout.CENTER);

        return myPoint;


    }


    // Ф-ия для прорисовки всех попаданий на втором фрейме
    public void trueDrawAllRentable() {
        float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
        imageLabel.drawResult(Main.pointList,dHeight);
    }

    // ф-ия для обновления лейбла на втором фрейме:
    public void repaint() {
        imageLabel.repaint();

    }
}
