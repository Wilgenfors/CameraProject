import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class RedMain {
    private static JFrame mainFrame;
    //private static JFrame pointSearchFrame;
    static BufferedImage myPicture = null;
    //static BufferedImage myPicture2 = null;
    static ArrayList<Circle> circlesList;

    public static void main(String[] args) {
//		consoleTest();
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open();
        guiTest(webcam);
    }

    public static void guiTest( Webcam webcam) {
        // frame for bounds detected:
        MyLabel imageLabel = new MyLabel();
        mainFrame = new JFrame("BoundsTarget");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        myPicture = webcam.getImage();
//		BufferedImage blackAndWhiteImg = webcam.getImage();
        //ImageIO.write(myPicture, ImageUtils.FORMAT_JPG, new File("selfie.jpg"));
//		WebcamUtils.capture(webcam, "grayImage.jpg");
//		System.out.println("image size = "+myPicture.getWidth()+" x "+ myPicture.getHeight());
//		String fileName = "target3_promah.png";
//		try {
//			myPicture = ImageIO.read(new File(fileName));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        // Буффер для изменнения картинки в серый
        BufferedImage blackAndWhiteImg = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//		BufferedImage blackAndWhiteImg = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(),BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D graphics = blackAndWhiteImg.createGraphics();

        graphics.drawImage(myPicture, 0, 0, null);
        ImageIcon imgIcon = new ImageIcon(blackAndWhiteImg);
        imageLabel.setIcon(imgIcon);
        mainFrame.add(imageLabel, BorderLayout.CENTER);
        mainFrame.setSize(800, 600);
        mainFrame.setVisible(true);
        resizeImage(imageLabel, blackAndWhiteImg, imgIcon);
        mainFrame.setLocationRelativeTo(null);
        imageLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeImage(imageLabel, myPicture, imgIcon);
            }
        });

        // todo Откоментировать если не поможет с Cannot invoke "Circle.getY()" because "circle" is null
        // Для того чтобы список не был пустой:
//        RedSearch redSearch = new RedSearch(myPicture);
//        Circle circle = redSearch.getCircle(); //находим внешний круг
//        ArrayList<Circle> circlesList = redSearch.getCircles(circle); //находим все внутренние круги
//        imageLabel.drawCircles(circlesList);
//        circlesList.add(circle); //если нужен список со всеми кругами
        // ---------------------------------------------------------------

//		var myCircle = detectedRedPointOnTarget(imageLabel, myPicture, imgIcon);
//		checkTarget(circlesList, myCircle);
    }
    private static void resizeImage(MyLabel imageLabel, BufferedImage myPicture, ImageIcon imgIcon) {
        float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
        int newWidth = (int) (myPicture.getWidth() * dHeight);
        Image dimg = myPicture.getScaledInstance(newWidth, imageLabel.getHeight(), Image.SCALE_SMOOTH);
        imgIcon.setImage(dimg);
        RedSearch redSearch = new RedSearch(myPicture);


        redSearch.findRedPoints(); //Находим координаты красной точки
        Circle circle = redSearch.getCircle(); //находим внешний круг
        if (circle==null) {
            System.out.println("--!! No circle !!--");
        } else {

            //red point detected:
            // Объектная переменная для синей обводки:
            Circle myPoint = detectedRedPointOnTarget(imageLabel, myPicture, imgIcon);
            imageLabel.drawPoint(myPoint, dHeight);
            //--------------------------------------------------------------------
            imageLabel.drawCircle(circle.getX(), circle.getY(), circle.getRadius(), dHeight);
            System.out.println("--- inner circles search ---");
            ArrayList<Circle> circlesList = redSearch.getCircles(circle); //находим все внутренние круги
            imageLabel.drawCircles(circlesList);
            circlesList.add(circle); //если нужен список со всеми кругами

            // Новый алгоритм точности попадания:
            int circleIndex = getCircleIndByXY(myPoint.getX(), myPoint.getY(), circlesList);
            System.out.println("circleIndex = "+circleIndex);

            // Если точка в центре или в области промаха:
            if (circleIndex == -1){
                int soloCircleIndex = getSoloCircleIndByXY(myPoint.getX(), myPoint.getY(), circlesList);
                if (soloCircleIndex==-99) {
                    System.out.println("circleIndex = "+soloCircleIndex);
                    System.out.println("Red point detected in center");
                }
                else if (soloCircleIndex==99){
                    System.out.println("circleIndex = "+soloCircleIndex);
                    System.out.println("Red point detected in miss");
                }
            }
            // Если точка находится между кругами
            else if (circleIndex < circlesList.size())
                System.out.println("Red point detected in  "+(circleIndex));
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
        System.out.println("");
        System.out.println("");
        System.out.println("xRedPoint-circle.getX()"+(xRedPoint-circle.getX()));
        System.out.println("yRedPoint-circle.getY()"+(yRedPoint-circle.getY()));
        System.out.println("leftPart_1 = "+leftPart_1);
        // Значения в leftPart_1 и leftPart_2 одинаковые!!!
        double leftPart_2 = Math.pow(xRedPoint-circleNext.getX(), 2) + Math.pow(yRedPoint-circleNext.getY(), 2);
        System.out.println("xRedPoint-circleNext.getX()"+(xRedPoint-circleNext.getX()));
        System.out.println("yRedPoint-circleNext.getY()"+(yRedPoint-circleNext.getY()));
        System.out.println("leftPart_2 = "+leftPart_2);
        System.out.println("");
        System.out.println("");
        // И сравниваем с радиусами текущего и следующего круга:
        if (leftPart_1>=((circleRc1)*(circleRc1)) && leftPart_2<=((circleRc2)*(circleRc2)))
        {
            System.out.println("\n\nleftPart_1 = "+leftPart_1);
            System.out.println("circleRc1^2 = "+(circleRc1)*(circleRc1));
            System.out.println("circleRc1^2 = "+(circleRc2)*(circleRc2));
            System.out.println("\n\n");
            return true;
        }

        return false;
    }

    // Методы для алгоритма точности попадания центр или в область промаха:
    private static int getSoloCircleIndByXY(int xRedPoint, int yRedPoint, ArrayList<Circle> circles2) {
        int i = 0;
        // Передаю первый и последний круг в метод circleSoloIs_OnXY:
        Circle circleCentre =  circles2.get(1);
        //Circle circleLast =  circles2.get(circles2.size());

        // Возвращает -99 если красная точка в центре или 99 если в области промаха.

        // Левая часть нужна для нахождения радиуса от центра до нашей красной точки:
        double leftPart = Math.pow(xRedPoint-circleCentre.getX(), 2) + Math.pow(yRedPoint-circleCentre.getY(), 2);

        // Возвращает -99 если красная точка в центре или 99 если в области промаха.
        if (leftPart<=((circleCentre.getRadius())*(circleCentre.getRadius()))) return -99;
        return 99;
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

        System.out.println("red dot circle = " + myPoint.getX() + " " + myPoint.getY() + " " + myPoint.getRadius());
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
