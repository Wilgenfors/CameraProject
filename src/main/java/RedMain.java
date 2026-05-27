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
    static  boolean flagFoundEdge = false;

    private static ArrayList<ArrayList<EdgeCoords>> separateContours = null;

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
        //resizeImage(imageLabel, blackAndWhiteImg, imgIcon, myPicture);
        resizeImage(imageLabel, myPicture, imgIcon,myPicture);
        secondFrame.setLocationRelativeTo(null);
        imageLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
               // resizeImage(imageLabel, blackAndWhiteImg, imgIcon, myPicture);
                resizeImage(imageLabel, myPicture, imgIcon,myPicture);
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

       // мы один раз находим контуры кругов:
       if (!flagFoundEdge) {
           System.out.println("closedLineSearch");
           float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
           int newWidth = (int) (myPicture.getWidth() * dHeight);
           SimpleEdgeDetector edgeDetector = new SimpleEdgeDetector();

           Image dimg = myPicture.getScaledInstance(newWidth, imageLabel.getHeight(), Image.SCALE_SMOOTH);
           imgIcon.setImage(dimg);
           BufferedImage tempImage = toBufferedImage(dimg);

           // Присваиваем значение объявленной ранее переменной
           separateContours = edgeDetector.getSeparateContours(tempImage, 100);

           // Рисуем раздельные контуры
           imageLabel.drawSeparateContours(separateContours);

           System.out.println("Found Contour groups: " + separateContours.size());
           flagFoundEdge = true;
       }

       // Нахождение красной точки:
       Circle myPoint = detectedRedPointOnTarget(imageLabel, colorImg, imgIcon, Main.panelWebcam);

       // Добавляем каждую точку в Лист если она есть (не дефолтное значение):
       if (myPoint.getX() != 500 && myPoint.getY() != 500) {
           System.out.println("found red point");
           Main.addPointList(myPoint);

           boolean foundBoundary = false;
           int targetContour = -1;
           int boundaryY = -1;

           for (int y = myPoint.getY(); y > 0; y--) {
               // Проходимся по всем контурам
               for (int contourIdx = 0; contourIdx < separateContours.size(); contourIdx++) {
                   ArrayList<EdgeCoords> contour = separateContours.get(contourIdx);

                   // Проходимся по всем пикселям контура i
                   for (EdgeCoords edgePoint : contour) {
                       // Сравниваем координаты красной точки с пикселем контура
                       if (myPoint.getX() == edgePoint.getX() && y == edgePoint.getY()) {
                           // И если совпадают, то заканчиваем цикл через флаг
                           foundBoundary = true;
                           targetContour = contourIdx;
                           boundaryY = y;
                           break;
                       }
                   }
                   if (foundBoundary) break;
               }
               if (foundBoundary) break;
           }

           if (foundBoundary) {
               //System.out.println("=== RESULT ===");

               if (targetContour == 0 ){
                   //System.out.println("Mimooo )");
                   Main.addListHits(0);
               }
               if (targetContour == 1 || (targetContour == 2 )){
                   //System.out.println("hit !!! - 2 point");
                   Main.addListHits(2);
               }
               if (targetContour == 3 || (targetContour == 4 )){
                   //System.out.println("hit !!! - 4 point");
                   Main.addListHits(4);
               }
               if (targetContour == 5 || (targetContour == 6 )){
                   //System.out.println("hit !!! - 8 point");
                   Main.addListHits(8);
               }
               if (targetContour == 7 ){
                   //System.out.println("hit !!! - 16 point");
                   Main.addListHits(16);
               }
               Main.myTextArea.append("    " + ((Main.player) + 1) + "                    " + Main.listHits.get((Main.shot++)) + "\n");
               // Условия для перехода на следующего игрока:
               Main.playerChangeCondition();
           } else {
               //System.out.println("Mimooo )");
               Main.addListHits(0);
               Main.myTextArea.append("    " + ((Main.player) + 1) + "                    " + Main.listHits.get((Main.shot++)) + "\n");
               // Условия для перехода на следующего игрока:
               Main.playerChangeCondition();

           }
       }
   }


    // Взял с RedCircle на сколько понял для лучшей работы с цветами:
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
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

        ArrayList<Circle> pointList2 = new ArrayList<>();
        pointList2 = Main.returnedPointList();
        imageLabel.drawResult(pointList2,dHeight);
    }

    // ф-ия для обновления лейбла на втором фрейме:
    public void repaint() {
        imageLabel.repaint();

    }

}
