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
    static BufferedImage myPicture = null;
    static ArrayList<Circle> circlesList;
    private static String text;

    public static void closeRedMain(){

        mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
    }

    public static void guiTest( Webcam webcam) {

        MyLabel imageLabel = new MyLabel();
        // Условие для проверки создан ли второй фрейм или нет
        if (mainFrame!=null) {
        mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        }
        // Второй фрейм для распознавания красных точек и кругов:
        mainFrame = new JFrame("BoundsTarget");
        myPicture = webcam.getImage();

        // Буффер для изменения картинки в серый
        BufferedImage blackAndWhiteImg = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//		BufferedImage blackAndWhiteImg = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(),BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D graphics = blackAndWhiteImg.createGraphics();

        // Преобразуем рисунок в серый:
        graphics.drawImage(myPicture, 0, 0, null);
        // Создаем иконку рисунка на основе серой картинки:
        ImageIcon imgIcon = new ImageIcon(blackAndWhiteImg);
        // И добавляем на лейбл:
        imageLabel.setIcon(imgIcon);
        // И удаляем лейбл What!?!?:
        mainFrame.remove(imageLabel);
        mainFrame.add(imageLabel, BorderLayout.CENTER);
        mainFrame.setSize(800, 600);
        mainFrame.setVisible(true);
        // Вызываем метод для распознавания:
        resizeImage(imageLabel, blackAndWhiteImg, imgIcon, myPicture);
        System.out.println("Black");
        mainFrame.setLocationRelativeTo(null);
        // :
        imageLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeImage(imageLabel, blackAndWhiteImg, imgIcon, myPicture);
                System.out.println("color");
            }
        });

        // Создаем наш поток для постоянного распознавания:
        RedMain redmain = new RedMain();
        // Создаем объектную переменую для потока и сам поток:
        SimpleRunnable run1=new SimpleRunnable(redmain, mainFrame,webcam, imageLabel);
        Thread thread1=new Thread(run1); //создаем поток и передаем ему наш объект
        thread1.start();
    }

    public static void resizeImage(MyLabel imageLabel, BufferedImage myPicture, ImageIcon imgIcon, BufferedImage colorImg) {

        float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
        int newWidth = (int) (myPicture.getWidth() * dHeight);
        Image dimg = myPicture.getScaledInstance(newWidth, imageLabel.getHeight(), Image.SCALE_SMOOTH);
        imgIcon.setImage(dimg);
        RedSearch redSearch = new RedSearch(myPicture);

        //находим внешний круг:
        Circle circle = redSearch.getCircle();
        if (circle==null) {
            System.out.println("--!! No circle !!--");
        } else {

            // Находим красную точку и обводим её синим:
            Circle myPoint = detectedRedPointOnTarget(imageLabel, colorImg, imgIcon);
            imageLabel.drawPoint(myPoint, dHeight);
            //--------------------------------------------------------------------
            // Рисуем первый найденный круг:
            imageLabel.drawCircle(circle.getX(), circle.getY(), circle.getRadius(), dHeight);
            //находим все внутренние круги:
            ArrayList<Circle> circlesList = redSearch.getCircles(circle);
            imageLabel.drawCircles(circlesList);
            //если нужен список со всеми кругами:
            circlesList.add(circle);

            // Новый алгоритм точности попадания:
            int circleIndex = getCircleIndByXY(myPoint.getX(), myPoint.getY(), circlesList);

            // Если точка в центре или в области промаха:
            if (circleIndex == -1){
                int soloCircleIndex = getSoloCircleIndByXY(myPoint.getX(), myPoint.getY(), circlesList);
                // Если попали в центр:
                if (soloCircleIndex==-99) {
                    System.out.println("Red point detected in center");
                    // Выводим информацию в текстовое поле фрейма класса Main:
                    Main.myTextArea.append(""+"Red point detected in center"+"\n");
                }
                // Радиус == - 500 это дефолтное значение при отсутствии красной точки
                // значит иначе красная точка есть и если она не в центре, то в области промаха:
                else if (myPoint.getRadius()!=-500){
                    System.out.println("Red point detected in miss");
                    // Выводим информацию в текстовое поле фрейма класса Main:
                    Main.myTextArea.append(""+"Red point detected in miss"+"\n");
                }
            }
            // Если точка находится между кругами:
            else if (circleIndex < circlesList.size())
            {
                System.out.println("Red point detected in  "+(circleIndex));
                String textOut = "Red point detected in  "+(circleIndex);
                // Выводим информацию в текстовое поле фрейма класса Main:
                Main.myTextArea.append(""+textOut+"\n");
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
                // Если точка находится между кругов возвращаем значение области попадания:
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
//		формула сравнения: (x – a)2 + (y – b)2 = R2
        int circleRc1 = circle.getRadius();
        int circleRc2 = circleNext.getRadius();
        // Левая часть нужна для нахождения радиуса от центра до нашей красной точки:
        double leftPart_1 = Math.pow(xRedPoint-circle.getX(), 2) + Math.pow(yRedPoint-circle.getY(), 2);
        double leftPart_2 = Math.pow(xRedPoint-circleNext.getX(), 2) + Math.pow(yRedPoint-circleNext.getY(), 2);

        // И сравниваем с радиусами текущего и следующего круга:
        if (leftPart_1>=((circleRc1)*(circleRc1)) && leftPart_2<=((circleRc2)*(circleRc2)))
        {
            // Возвращаем если красная точка находится в области между кругами:
            return true;
        }

        return false;
    }

    // Методы для алгоритма точности попадания центр или в область промаха:
    private static int getSoloCircleIndByXY(int xRedPoint, int yRedPoint, ArrayList<Circle> circles2) {
        int i = 0;
        // Передаю первый и последний круг в метод circleSoloIs_OnXY:
        Circle circleCentre = circles2.get(1);

        System.out.println(circles2.size()-1);

        // Левая часть нужна для нахождения радиуса от центра до нашей красной точки:
        double leftPart = Math.pow(xRedPoint-circleCentre.getX(), 2) + Math.pow(yRedPoint-circleCentre.getY(), 2);

        // Возвращает -99 если красная точка в центре или 99 если в области промаха иначе возвращается 100.

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

        return myPoint;


    }

    private static void consoleTest() {
        RedSearch redSearch = new RedSearch("img.png");
        redSearch.findRedPoints();
//		redSearch = new RedSearch("img2.png");
//		redSearch.findRedPoints();
//		redSearch = new RedSearch("target.png");
//		redSearch.boundCircleSearch();
    }

}
