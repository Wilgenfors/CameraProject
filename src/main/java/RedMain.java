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
	private static JFrame pointSearchFrame;
	static BufferedImage myPicture = null;
	static BufferedImage myPicture2 = null;

	public static void main(String[] args) {
//		consoleTest();
		guiTest();
	}

	private static void guiTest() {
		// frame for bounds detected:
		mainFrame = new JFrame("BoundsTarget");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MyLabel imageLabel = new MyLabel();
		Webcam webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open();

		myPicture = webcam.getImage();
		//ImageIO.write(myPicture, ImageUtils.FORMAT_JPG, new File("selfie.jpg"));
		WebcamUtils.capture(webcam, "selfie.jpg");
		System.out.println("image size = "+myPicture.getWidth()+" x "+ myPicture.getHeight());
//		String fileName = "target3_promah.png";
//		try {
//			myPicture = ImageIO.read(new File(fileName));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		// Буффер для изменнения картинки в серый
				BufferedImage blackAndWhiteImg = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
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

		webcam.close();


		// frame for searh red poinds
		pointSearchFrame = new JFrame("PointsTarget");
		pointSearchFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MyLabel pointImageLabel = new MyLabel();

		Webcam webcam2 = Webcam.getDefault();
		webcam2.setViewSize(WebcamResolution.VGA.getSize());
		webcam2.open();

		myPicture2 = webcam2.getImage();
		System.out.println("image2 size = "+myPicture2.getWidth()+" x "+ myPicture2.getHeight());


		Graphics2D graphics2 = myPicture2.createGraphics();

		graphics2.drawImage(myPicture2, 0, 0, null);
		ImageIcon pointImgIcon = new ImageIcon(myPicture2);
		imageLabel.setIcon(pointImgIcon);
		pointSearchFrame.add(pointImageLabel, BorderLayout.CENTER);
		pointSearchFrame.setSize(800, 600);
		pointSearchFrame.setVisible(true);
		resizeImage(pointImageLabel, myPicture2, pointImgIcon);
		pointSearchFrame.setLocationRelativeTo(null);
		pointImageLabel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				resizeImage(pointImageLabel, myPicture2, pointImgIcon);
			}
		});
	}

	private static void resizeImage(MyLabel imageLabel, BufferedImage myPicture, ImageIcon imgIcon) {
		float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
		int newWidth = (int) (myPicture.getWidth() * dHeight);
		Image dimg = myPicture.getScaledInstance(newWidth, imageLabel.getHeight(), Image.SCALE_SMOOTH);
		imgIcon.setImage(dimg);
		RedSearch redSearch = new RedSearch(myPicture);
		ArrayList<Circle> circleList = redSearch.getAllCircles();
		System.out.println("\ncircleList size = " + circleList.size() + "\n");
		imageLabel.drawCircles(circleList, dHeight);
//		MyPoint[] redPoints = redSearch.findRedPoints();
		Circle myCircle = redSearch.findRedPointsAsCircle(); // это наша красная точка, обведенная окружностью
		imageLabel.drawPoint(myCircle, dHeight);
//		int xRed = getXRed();
//		int yRed = getYRed();
		System.out.println("Прохождение по списку мишени: ");
		for (Circle circle0 : circleList) {
			int i = circleList.indexOf(circle0);
			Circle circle1 = circleList.get(i + 1);
			int Cx = circle0.getX();
			int Cy = circle0.getY();
			int R = circle0.getRadius();
			// Проверяем окружности на попадания без внутренего круга и промаха:
			if (i < circleList.size() - 1 && checkRedPointAndCircle(myCircle, circle0, circle1)) {
				System.out.println("между " + i + " и " + (i + 1));
				break;
			}

			// Проверяем на промах:
			if (i < circleList.size() - 1 && checkRedPointAndCircleOut(myCircle, circle0)) {
				System.out.println("Промах");
				break;
			}

			// Проверяем на попадание во внутрению окружность:
			if (i < circleList.size() - 1 && checkRedPointAndCircleCentre(myCircle, circle1)) {
				System.out.println("Попадание в центр");
				break;
			}
		}

		System.out.println("\ncircleList size = " + circleList.size() + "\n");
		imageLabel.drawCircles(circleList, dHeight);

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

	private static double getRadius(Circle myCircle, Circle circle0) {
		return Math.pow((myCircle.getX() - circle0.getX()), 2) + Math.pow((myCircle.getY() - circle0.getY()), 2);
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
