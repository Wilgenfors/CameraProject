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
    static ArrayList<Circle> circleList;

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

        var myCircle = detectedRedPointOnTarget(imageLabel, myPicture, imgIcon);
        checkTarget(circleList, myCircle);
    }

    private static void resizeImage(MyLabel imageLabel, BufferedImage myPicture, ImageIcon imgIcon) {
        float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
        int newWidth = (int) (myPicture.getWidth() * dHeight);
        Image dimg = myPicture.getScaledInstance(newWidth, imageLabel.getHeight(), Image.SCALE_SMOOTH);
        imgIcon.setImage(dimg);
        RedSearch redSearch = new RedSearch(myPicture);
        circleList = redSearch.getAllCircles();
        System.out.println("\ncircleList size = " + circleList.size() + "\n");
        imageLabel.drawCircles(circleList, dHeight);
        MyPoint[] redPoints = redSearch.findRedPoints();
        //Circle myCircle = redSearch.findRedPointsAsCircle(); // это наша красная точка, обведенная окружностью
        Circle myCircle = detectedRedPointOnTarget(imageLabel, myPicture, imgIcon);
        imageLabel.drawPoint(myCircle, dHeight);
//		int xRed = getXRed();
//		int yRed = getYRed();

        //checkTarget(circleList, myCircle);

        System.out.println("\ncircleList size = " + circleList.size() + "\n");
        imageLabel.drawCircles(circleList, dHeight);

    }

    private static void checkTarget(ArrayList<Circle> circleList, Circle myCircle) {
        System.out.println("Going through the target: ");
        System.out.println("circles = "+circleList.size());

        boolean miss = false, between = false;
        for (Circle circle0 : circleList) {
            int i = circleList.indexOf(circle0);

            Circle circle1 = circleList.get(i + 1);

            int Cx = circle0.getX();
            int Cy = circle0.getY();
            int R = circle0.getRadius();


            // Проверяем на промах:
            if (i < circleList.size() - 1 && checkRedPointAndCircleOut(myCircle, circle0)) {
                System.out.println("miss");
                miss = true;
                break;
            }

//            // Проверяем на попадание во внутрению окружность:
//            if (i == circleList.size() - 1 && checkRedPointAndCircleCentre(myCircle, circle1)) {
//                System.out.println("in center");
//                break;
//            }

//           //  Проверяем на попадание во внутрению окружность:
//            if (i == circleList.size() - 1 && (!miss && !center)) {
//                System.out.println("in center");
//                break;
//            }

            // Проверяем окружности на попадания без внутренего круга и промаха:
            if ( i < circleList.size() - 1 && checkRedPointAndCircle(myCircle, circle0, circle1)) {
                System.out.println("between " + i + " & " + (i + 1));
                between = true;
                break;
            }
//  Проверяем на попадание во внутрению окружность:
            if (circle1 == circleList.get(2) && (!miss && !between)) {
                System.out.println("in center");
                break;
            }

        }


    }

    // metod for detected red poins on target:
    //todo передавть в аргументы метода параметры 1 фрейма и на строке 165 обводить точку
    private static Circle detectedRedPointOnTarget(MyLabel imageLabel, BufferedImage myPicture, ImageIcon imgIcon) {
                

        float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
        int newWidth = (int) (myPicture.getWidth() * dHeight);
        Image dimg = myPicture.getScaledInstance(newWidth, imageLabel.getHeight(), Image.SCALE_SMOOTH);
        imgIcon.setImage(dimg);
        RedSearch redSearch = new RedSearch(myPicture);
        ArrayList<Circle> circleList = redSearch.getAllCircles();
        System.out.println("\ncircleList size = " + circleList.size() + "\n");
        //imageLabel.drawCircles(circleList, dHeight);
//		MyPoint[] redPoints = redSearch.findRedPoints();
        Circle myCircle = redSearch.findRedPointsAsCircle(); // это наша красная точка, обведенная окружностью
        imageLabel.drawPoint(myCircle, dHeight);
        System.out.println("red dot circle = " + myCircle.getX() + " " + myCircle.getY() + " " + myCircle.getRadius());
        return myCircle;
//		int xRed = getXRed();
//		int yRed = getYRed();
//		System.out.println("Прохождение по списку мишени: ");
//		for (Circle circle0 : circleList) {
//			int i = circleList.indexOf(circle0);
//			Circle circle1 = circleList.get(i + 1);
//			int Cx = circle0.getX();
//			int Cy = circle0.getY();
//			int R = circle0.getRadius();
//			// Проверяем окружности на попадания без внутренего круга и промаха:
//			if (i < circleList.size() - 1 && checkRedPointAndCircle(myCircle, circle0, circle1)) {
//				System.out.println("between " + i + " and " + (i + 1));
//				break;
//			}
//
//			// Проверяем на промах:
//			if (i < circleList.size() - 1 && checkRedPointAndCircleOut(myCircle, circle0)) {
//				System.out.println("Miss");
//				break;
//			}
//
//			// Проверяем на попадание во внутрению окружность:
//			if (i < circleList.size() - 1 && checkRedPointAndCircleCentre(myCircle, circle1)) {
//				System.out.println("Hitting the center");
//				break;
//			}
//		}
//
//		System.out.println("\ncircleList size = " + circleList.size() + "\n");
//		//imageLabel.drawCircles(circleList, dHeight);

    }

    private static double getRadius(Circle myCircle, Circle circle0) {
        return Math.pow((myCircle.getX() - circle0.getX()), 2) + Math.pow((myCircle.getY() - circle0.getY()), 2);
    }

    private static boolean checkRedPointAndCircleOut(Circle myCircle, Circle circle) {
        if (getRadius(myCircle, circle) > Math.pow(circle.getRadius(), 2)) return true;
        return false;
    }

    private static boolean checkRedPointAndCircleCentre(Circle myCircle, Circle circle) {
        if (getRadius(myCircle, circle) < Math.pow(circle.getRadius(), 2)) return true;
        return false;
    }

    private static boolean checkRedPointAndCircle(Circle myCircle, Circle circle0, Circle circle1) {
        if (getRadius(myCircle, circle0) < Math.pow(circle0.getRadius(), 2)
                && getRadius(myCircle, circle1) > Math.pow(circle1.getRadius(), 2))
            return true;

        return false;
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
