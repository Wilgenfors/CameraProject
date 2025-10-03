import com.github.sarxos.webcam.Webcam;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class RedMain {
    private static JFrame secondFrame;
    static BufferedImage myPicture = null;
    static Thread thread1;
    static RedMain redmain;
    static MyLabel imageLabel;
    public static void guiTest( Webcam webcam) {
        // frame for bounds detected:
        imageLabel = new MyLabel();

        if (secondFrame !=null) {
        secondFrame.dispatchEvent(new WindowEvent(secondFrame, WindowEvent.WINDOW_CLOSING));
        }

        secondFrame = new JFrame("BoundsTarget");
        myPicture = webcam.getImage();

        // Буфф ер для изменения картинки в серый
        BufferedImage blackAndWhiteImg = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D graphics = blackAndWhiteImg.createGraphics();

        graphics.drawImage(myPicture, 0, 0, null);
        ImageIcon imgIcon = new ImageIcon(blackAndWhiteImg);
        imageLabel.setIcon(imgIcon);

        secondFrame.remove(imageLabel);
        secondFrame.add(imageLabel, BorderLayout.CENTER);

        // задаем размер для одинакового отображения нахождения крассных точек на двух фреймах
        secondFrame.setSize(640+16, 480+39); //
        secondFrame.setVisible(true);
        resizeImage(imageLabel, blackAndWhiteImg, imgIcon, myPicture);
        secondFrame.setLocationRelativeTo(null);
        imageLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeImage(imageLabel, blackAndWhiteImg, imgIcon, myPicture);
            }
        });

        redmain = new RedMain();
//        // Создаем объектную переменную для потока и сам поток:
        SimpleRunnable run1 = new SimpleRunnable(redmain, secondFrame, Main.webcam, imageLabel);
        thread1 = new Thread(run1); //создаем поток и передаем ему наш объект
        thread1.start();

        // Слушатель, который останавливает поток после закрытия окна:
        secondFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SimpleRunnable.stopped();
            }
        });

    }
    public static void resizeImage(MyLabel imageLabel, BufferedImage myPicture, ImageIcon imgIcon, BufferedImage colorImg) {

        float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
        int newWidth = (int) (myPicture.getWidth() * dHeight);
        Image dimg = myPicture.getScaledInstance(newWidth, imageLabel.getHeight(), Image.SCALE_SMOOTH);
        imgIcon.setImage(dimg);
        RedSearch redSearch = new RedSearch(myPicture);

        Circle circle = redSearch.getCircle(); //находим внешний круг
        if (circle == null) {
            // System.out.println("--!! No circle !!--");
        } else {
            Circle myPoint = detectedRedPointOnTarget(imageLabel, colorImg, imgIcon, Main.panelWebcam);
                // Добавляем каждую точку в Лист ели он есть те не дефолтное значение:
                if (myPoint.getX() != 500 && myPoint.getY() != 500) {
                    Main.addPointList(myPoint);
                }

                imageLabel.drawCircle(circle.getX(), circle.getY(), circle.getRadius(), dHeight);
                ArrayList<Circle> circlesList = redSearch.getCircles(circle); //находим все внутренние круги
                imageLabel.drawCircles(circlesList);
                circlesList.add(circle); //если нужен список со всеми кругами

                // Новый алгоритм точности попадания:
                int circleIndex = getCircleIndByXY(myPoint.getX(), myPoint.getY(), circlesList);

                // Если точка в центре или в области промаха:
                if (circleIndex == -1) {
                    int soloCircleIndex = getSoloCircleIndByXY(myPoint.getX(), myPoint.getY(), circlesList);
                    if (soloCircleIndex == -99) {
                        Main.addListHits(10);
                        //Main.myTextArea.append("Player " + ((Main.player) + 1) + " | hit points  " + Main.listHits.get((Main.shot++)) + "\n");
                        Main.myTextArea.append("    " + ((Main.player) + 1) + "                    " + Main.listHits.get((Main.shot++)) + "\n");

                        // Условия для перехода на следующего игрока:
                        Main.playerChangeCondition();

                    } else if (myPoint.getRadius() != -500) {
                        Main.addListHits(0);
                        //Main.myTextArea.append("Player " + ((Main.player) + 1) + " | hit points  " + Main.listHits.get((Main.shot++)) + "\n");
                        Main.myTextArea.append("    " + ((Main.player) + 1) + "                    " + Main.listHits.get((Main.shot++)) + "\n");
                        // Условия для перехода на следующего игрока:
                        Main.playerChangeCondition();
                    }
                }
                // Если точка находится между кругами
                else if (circleIndex < circlesList.size()) {
                    if (circleIndex == 0) Main.addListHits(8);
                    else if (circleIndex == 1) Main.addListHits(5);
                    else if (circleIndex == 2) Main.addListHits(2);

                    //Main.myTextArea.append("Player "+((Main.player)+1)+" hit points"+Main.listHits.get((Main.shot++))+"\n");
                    //Main.myTextArea.append("Player " + ((Main.player) + 1) + " | hit points  " + Main.listHits.get((Main.shot++)) + "\n");
                    Main.myTextArea.append("    " + ((Main.player) + 1) + "                    " + Main.listHits.get((Main.shot++)) + "\n");
                    // Условия для перехода на следующего игрока:
                    Main.playerChangeCondition();

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
        //Main.mainFrame.add(panelWebcam, BorderLayout.CENTER);
        Main.mainFrame.add(panelWebcam, BorderLayout.CENTER);
        //mainObj.addMainFrame();

        return myPoint;

    }


    // Ф-ия для прорисовки всех попаданий на втором фрейме
    public void trueDrawAllRentable() {
        float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
        // Метод возвращает список точек - pointList
        ArrayList<Circle> pointList2 = new ArrayList<>();
        pointList2 = Main.returnedPointList();
        //imageLabel.drawResult(Main.pointList,dHeight);
        imageLabel.drawResult(pointList2,dHeight);
    }

    // ф-ия для обновления лейбла на втором фрейме:
    public void repaint() {
        imageLabel.repaint();

    }

}
