//package TargetRecognize;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class EdgeCoords{
	private int x;
	private int y;
	
	public EdgeCoords(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}

class Contour{
	private ArrayList<EdgeCoords> edgeCoordsList = null;
	private EdgeCoords leftEdge;
	private EdgeCoords rightEdge;
	private EdgeCoords topEdge;
	private EdgeCoords buttomEdge;
	private int edgeWidth;

	public ArrayList<EdgeCoords> getEdgeCoordsList() {
		return edgeCoordsList;
	}

	public void setEdgeCoordsList(ArrayList<EdgeCoords> edgeCoordsList) {
		this.edgeCoordsList = edgeCoordsList;
	}
	
	public void addPointToEdgesArray(EdgeCoords point) {
		edgeCoordsList.add(point);
	}

	public EdgeCoords getLeftEdge() {
		return leftEdge;
	}

	public void setLeftEdge(EdgeCoords leftEdge) {
		this.leftEdge = leftEdge;
	}

	public EdgeCoords getRightEdge() {
		return rightEdge;
	}

	public void setRightEdge(EdgeCoords rightEdge) {
		this.rightEdge = rightEdge;
	}

	public EdgeCoords getTopEdge() {
		return topEdge;
	}

	public void setTopEdge(EdgeCoords topEdge) {
		this.topEdge = topEdge;
	}

	public EdgeCoords getButtomEdge() {
		return buttomEdge;
	}

	public void setButtomEdge(EdgeCoords buttomEdge) {
		this.buttomEdge = buttomEdge;
	}

	public int getEdgeWidth() {
		return edgeWidth;
	}

	public void setEdgeWidth(int edgeWidth) {
		this.edgeWidth = edgeWidth;
	}


}

public class MyContourSearch {
	private ArrayList<Contour> contoursList = null;
	
	public ArrayList<Contour> getContours(BufferedImage image, Color edgesColor, Color backColor) {
//		ArrayList<EdgeCoords> coordsList = new ArrayList<>();
		ArrayList<Contour> contoursList = new ArrayList<>();
		Contour firstContour = new Contour();

		// Сохраняю изображение для отладки того, что оно корректно:
		// create a File object with the path and filename for the output image file
		File output = new File("C:\\IdeaProject\\CameraProject\\CameraProject\\test.png");
		//File output = new File("test.png");

		try {
			// write the BufferedImage to the file using ImageIO.write()
			ImageIO.write(image, "png", output);
			System.out.println("Удалось сохранить изображение");
		} catch (Exception e) {
			System.out.println("Не удалось сохранить изображение!!!");
			e.printStackTrace();
		}



		EdgeCoords topEdge = getTopEdge(image, edgesColor, backColor);
		firstContour.setTopEdge(topEdge);
		EdgeCoords buttomEdge = getButtomEdge(image, edgesColor, backColor);
		firstContour.setButtomEdge(buttomEdge);
		EdgeCoords leftEdge = getLeftEdge(image, edgesColor, backColor);
		firstContour.setLeftEdge(leftEdge);
		EdgeCoords rightEdge = getRightEdge(image, edgesColor, backColor);
		firstContour.setLeftEdge(rightEdge);
		
//		drawRedCross(image, firstContour, "target_redcross.png");
//		String curPath = System.getProperty("user.dir");
//		File directory = new File(curPath);
//		try {
//			Desktop.getDesktop().open(directory);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		//режем картинку на 4 части
		var img_1 = image.getSubimage(0, 0, topEdge.getX(), leftEdge.getY());
		var img_3 = image.getSubimage(topEdge.getX(), 0,image.getWidth()-topEdge.getX(), leftEdge.getY());
		var img_2 = image.getSubimage(0, leftEdge.getY()+1, topEdge.getX(), image.getHeight()-leftEdge.getY()-1);
		var img_4 = image.getSubimage(topEdge.getX(), leftEdge.getY()+1, image.getWidth()-topEdge.getX(), image.getHeight()-leftEdge.getY()-1);
		//создаем потоки для поиска контуров на этих частях
		ThreadForContour contourThread1 = new ThreadForContour(img_1, 1, Color.BLACK);
		ThreadForContour contourThread3 = new ThreadForContour(img_3, 3, Color.BLACK);
		ThreadForContour contourThread2 = new ThreadForContour(img_2, 2, Color.BLACK);
		ThreadForContour contourThread4 = new ThreadForContour(img_4, 4, Color.BLACK);
		
		System.out.println("----------");
		contourThread1.start();
		contourThread3.start();
		contourThread2.start();
		contourThread4.start();
		while (contourThread1.isAlive() || contourThread3.isAlive() || contourThread2.isAlive() || contourThread4.isAlive()) {}
		
		//получаем контуры из потоков
		ArrayList<ArrayList<EdgeCoords>> contours1 = contourThread1.getContoursList();
		System.out.println("contours1 size in main = " + contours1.size());
		ArrayList<ArrayList<EdgeCoords>> contours3 = contourThread3.getContoursList();
		System.out.println("contours3 size in main = " + contours3.size());
		ArrayList<ArrayList<EdgeCoords>> contours2 = contourThread2.getContoursList();
		System.out.println("contours2 size in main = " + contours2.size());
		ArrayList<ArrayList<EdgeCoords>> contours4 = contourThread4.getContoursList();
		System.out.println("contours4 size in main = " + contours4.size());
		contours1.forEach(contour->{ //идем по первому массиву с контурами из первой части картинки  
			int i = contours1.indexOf(contour);
			contours3.get(i).forEach(point->{ //добавляем к первому массиву точек контуров точки из третьего массива
				contour.add(new EdgeCoords(point.getX()+topEdge.getX(), point.getY()));
			});
			contours2.get(i).forEach(point->{//добавляем к первому массиву точек контуров точки из второго массива
				contour.add(new EdgeCoords(point.getX(), point.getY()+leftEdge.getY()));
			});
			contours4.get(i).forEach(point->{ //добавляем к первому массиву точек контуров точки из четвертого массива
				contour.add(new EdgeCoords(point.getX()+topEdge.getX(), point.getY()+leftEdge.getY()));
			});
			var myContour = new Contour();
			myContour.setEdgeCoordsList(contour);
			contoursList.add(myContour);
		});
		
//		drawContour(image, contours1, "target_contour.png");
//		drawContour2(image, contoursList, "target_contour.png");
//		drawContour(image, contours3, "target_contour3.png");
		//		int contourWidth = getContourWidthFromTopEdge(image, topEdge, edgesColor, backColor); 
//		System.out.println("contourWidth = "+contourWidth);
		
		return contoursList;
	}
	
	private void drawContour2(BufferedImage image, ArrayList<Contour> contoursList2, String fileName) {
		contoursList2.forEach(contour->{
			contour.getEdgeCoordsList().forEach(point->{
				image.setRGB(point.getX(), point.getY(), Color.RED.getRGB());
			});
		});
		try {
			javax.imageio.ImageIO.write(image, "png", new java.io.File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Trouble with writing image");
		}
		
	}
	
	private void drawContour(BufferedImage image, ArrayList<ArrayList<EdgeCoords>> contours, String fileName) {
		contours.forEach(contour->{
			contour.forEach(point->{
				image.setRGB(point.getX(), point.getY(), Color.RED.getRGB());
			});
		});
		try {
			javax.imageio.ImageIO.write(image, "png", new java.io.File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Trouble with writing image");
		}
		
	}

	private void drawRedCross(BufferedImage image, Contour firstContour, String fileName) {
		int topX = firstContour.getTopEdge().getX();
		int leftY = firstContour.getLeftEdge().getY();
		for (int y = 0; y < image.getHeight(); y++) { 
			image.setRGB(topX, y, Color.RED.getRGB());
		}
		for (int x = 0; x < image.getWidth(); x++) { 
			image.setRGB(x, leftY, Color.RED.getRGB());
		}
		try {
			javax.imageio.ImageIO.write(image, "png", new java.io.File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Trouble with writing image");
		}
	}

	private int getContourWidthFromTopEdge(BufferedImage image, EdgeCoords topEdge, Color edgesColor, Color backColor) {
		int x = topEdge.getX();
		int y = topEdge.getY();
		Color curDotColor = getDotColor(image, x, y);
		int dotsFound = 0;
		while (curDotColor==edgesColor) {
			y++;
			curDotColor = getDotColor(image, x, y);
			dotsFound++;
		}

		return dotsFound;
	}

	private Color getDotColor(BufferedImage image, int x, int y) {
		Color curDotColor = null;
    	int p = image.getRGB(x, y);
		int r = (p >> 16) & 0xff; // get red
		int g = (p >> 8) & 0xff; // get green
		int b = p & 0xff; // get blue
		//if (r < 2 && g < 2 && b < 2) curDotColor = Color.BLACK;
		if (r < 150 && g < 150 && b < 150) {
			curDotColor = new Color(r, g, b);
			//System.out.println("color detected:");
			//System.out.println("r"+r);
			//System.out.println("g"+g);
			//System.out.println("b"+b);
		}

		return curDotColor;
	}

	private EdgeCoords getRightEdge(BufferedImage image, Color edgesColor, Color backColor) {
		System.out.println("Started getRightEdge");
		EdgeCoords rightEdge = new EdgeCoords(0, 0);
		int width = image.getWidth();
        int height = image.getHeight();
        boolean found = false;
        int dotsCount = 0; 
        int dotsFound = 0;
        int y = height;
        int x = width - 1;
        for (x = width - 1; x > width / 2; x--) {
        	for (y = 1; y < height-1; y++) {
            	dotsCount++;
            	Color curDotColor = getDotColor(image, x, y);
            	if (dotsFound>0 && curDotColor != edgesColor) {
            		System.out.printf("cur right edge x = %d, y = %d\n", x, y);
            		found = true;
            		break;
            	}
            	if (curDotColor == edgesColor) {
//            		System.out.printf("---!! found right edge in x = %d  y = %d!!\n", x, y);
            		dotsFound++;
            		curDotColor = null;
            	}
            }
            if (found) break;
        }
		System.out.println("ended getRightEdge, dotsCount = " + dotsCount + ", dotsFound = " + dotsFound);
		System.out.printf("right edge x = %d, y = %d\n", x, y-dotsFound/2);
		rightEdge.setX(x);
		rightEdge.setY(y-dotsFound/2);
		return rightEdge;	
	}
	
	private EdgeCoords getLeftEdge(BufferedImage image, Color edgesColor, Color backColor) {
		System.out.println("Started getLeftEdge");
		EdgeCoords leftEdge = new EdgeCoords(0, 0);
		int width = image.getWidth();
        int height = image.getHeight();
        boolean found = false;
        int dotsCount = 0; 
        int dotsFound = 0;
        int y = height;
        int x = 1;
        for (x = 1; x < width - 1; x++) {
        	for (y = 1; y < height-1; y++) {
            	dotsCount++;
            	Color curDotColor = getDotColor(image, x, y);
            	if (dotsFound>0 && curDotColor != edgesColor) {
            		System.out.printf("cur left edge x = %d, y = %d\n", x, y);
            		found = true;
            		break;
            	}
            	if (curDotColor == edgesColor) {
//            		System.out.printf("---!! found left edge in x = %d  y = %d!!\n", x, y);
            		dotsFound++;
            		curDotColor = null;
            	}
            }
            if (found) break;
        }
		System.out.println("ended getLeftEdge, dotsCount = " + dotsCount + ", dotsFound = " + dotsFound);
		System.out.printf("left edge x = %d, y = %d\n", x, y-dotsFound/2);
		leftEdge.setX(x);
		leftEdge.setY(y-dotsFound/2);
		return leftEdge;	
	}
	
	private EdgeCoords getButtomEdge(BufferedImage image, Color edgesColor, Color backColor) {
		System.out.println("Started getButtomEdge");
		EdgeCoords buttomEdge = new EdgeCoords(0, 0);
		int width = image.getWidth();
        int height = image.getHeight();
        boolean found = false;
        int dotsCount = 0; 
        int dotsFound = 0;
        int y = height;
        int x = 1;
        for (y = height-2; y > height/2; y--) {
        	for (x = 1; x < width - 1; x++) {
            	dotsCount++;
            	Color curDotColor = getDotColor(image, x, y);
            	if (dotsFound>0 && curDotColor != edgesColor) {
            		System.out.printf("cur buttom edge x = %d, y = %d\n", x, y);
            		found = true;
            		break;
            	}
            	if (curDotColor == edgesColor) {
//            		System.out.printf("---!! found buttom edge in x = %d  y = %d!!\n", x, y);
            		dotsFound++;
            		curDotColor = null;
            	}
            }
            if (found) break;
        }
		System.out.println("ended getButtomEdge, dotsCount = " + dotsCount + ", dotsFound = " + dotsFound);
		System.out.printf("buttom edge x = %d, y = %d\n", x-dotsFound/2, y);
		buttomEdge.setX(x-dotsFound/2);
		buttomEdge.setY(y);
		return buttomEdge;	
	}
	
	private EdgeCoords getTopEdge(BufferedImage image, Color edgesColor, Color backColor) {
		System.out.println("Started getTopEdge");
		EdgeCoords topEdge = new EdgeCoords(0, 0);
		int width = image.getWidth();
        int height = image.getHeight();
        boolean found = false;
        int dotsCount = 0; 
        int dotsFound = 0;
        int y = 1;
        int x = 1;
        for (y = 1; y < height - 1; y++) {
        	for (x = 1; x < width - 1; x++) {
            	dotsCount++;
            	Color curDotColor = getDotColor(image, x, y);
//            	int p = image.getRGB(x, y);
//    			int r = (p >> 16) & 0xff; // get red
//    			int g = (p >> 8) & 0xff; // get green
//    			int b = p & 0xff; // get blue
//    			if (r < 2 && g < 2 && b < 2) curDotColor = Color.BLACK;
            	if (dotsFound>0 && curDotColor != edgesColor) {
            		System.out.printf("cur edge x = %d, y = %d\n", x, y);
            		found = true;
            		break;
            	}
            	if (curDotColor == edgesColor) {
//            		System.out.printf("---!! found edge in x = %d  y = %d!!\n", x, y);
            		dotsFound++;
            		curDotColor = null;
            	}
            }
            if (found) break;
        }
		System.out.println("ended getTopEdge, dotsCount = " + dotsCount + ", dotsFound = " + dotsFound);
		System.out.printf("top edge x = %d, y = %d\n", x-dotsFound/2, y);
		topEdge.setX(x-dotsFound/2);
		topEdge.setY(y);
		return topEdge;	
	}


	public static void main(String[] args) throws Exception{
		System.out.println("Started MyContourSearch");
		MyContourSearch mySearch = new MyContourSearch();
		//BufferedImage image = javax.imageio.ImageIO.read(new java.io.File("target.png"));
		// todo мне нужно брать изображение с лейбла (камеры)
		RedMain.myPicture = Main.webcam.getImage();

		// Буфф ер для изменения картинки в серый
		BufferedImage image = new BufferedImage(RedMain.myPicture.getWidth(), RedMain.myPicture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		// todo мне нужно здесь тоже поменять цвет круков и фона:




		// Лютый костыль, из-за создания нового объекта может все посыпаться:
		RedSearch rSobj = new RedSearch(image);

		Color colorCounter = new Color(rSobj.circleRedDiaposone,rSobj.circleGreenDiaposone,rSobj.circleBlueDiaposone);

		// Искать цвет фона будем аналогично с поиском в классе Calibration:

		// todo Возможно нужны другие координаты точки
		int pBG = image.getRGB(520, 390);


		int rBG = (pBG >> 16) & 0xff; // get red
		int gBG = (pBG >> 8) & 0xff; // get green
		int bBG = pBG & 0xff; // get blue

		Color colorBG = new Color(rBG,gBG,bBG);




		//________________________________________________________________________________
		mySearch.getContours(image, colorCounter, colorBG);
	}
	
}
