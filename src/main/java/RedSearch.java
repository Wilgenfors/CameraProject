import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class RedSearch {
	private String path;
	private BufferedImage image;

	public RedSearch(BufferedImage img) {
		image = img;
	}

	public RedSearch(String filePath) {
		path = filePath;
//		BufferedImage img = null;
		File f = null;

		// read image
		try {
			f = new File(path);
			image = ImageIO.read(f);

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public MyPoint[] findRedPoints() {
		// https://www.geeksforgeeks.org/image-processing-in-java-get-and-set-pixels/
//		Point[] points;
		int width = image.getWidth();
		int height = image.getHeight();
		// переменные для поиска минимальных и максимальных значений x и y:
		int xMax = -1, xMin = 1000, yMax = -1, yMin = 1000;
		// переменная для нахождения радиуса:
		int radius = 0;
		
		ArrayList<MyPoint> pointsList = new ArrayList<MyPoint>();
		System.out.println("img width = " + width + " height = " + height);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int p = image.getRGB(i, j);
				int r = (p >> 16) & 0xff; // get red
				int g = (p >> 8) & 0xff; // get green
				int b = p & 0xff; // get blue

//				if (r > 200 && g < 190 && b < 190) {
//				if (r > 230 && g < 230 && b < 230) {
				if (r > 250 && g < 250 && b < 250) {
					if (i >= xMax) xMax = i;
					if (i <= xMin) xMin = i;
					if (j >= yMax) yMax = j;
					if (j <= yMin) yMin = j;
		        	//System.out.println("red detected at x = "+i+", y = "+j);
					//pointsList.add(new MyPoint(i, j));
				}
			}
		}
		
		// Максимальные и минимальные значения расных точек по x:
		System.out.println("\nMax red detected at x = "+xMax);
		System.out.println("Min detected at x = "+xMin);
		
		// Максимальные и минимальные значения расных точек по y:
		System.out.println("\nMax red detected at  y = "+yMax);
		System.out.println("Min red detected at y = "+yMin);
		
		// Нахождение радиуса по x:
		radius = (xMax - xMin) / 2;
		System.out.println("red detected radius = "+radius);
		
		pointsList.add(new MyPoint(xMin+radius/2, yMin)); //верхняя
		pointsList.add(new MyPoint(xMin, yMin+radius/2)); //левая
		pointsList.add(new MyPoint(xMin+radius/2, yMax)); //нижняя
		pointsList.add(new MyPoint(xMax, yMin+radius/2)); //правая
		MyPoint[] points = new MyPoint[pointsList.size()];
		int i = 0;
		for (MyPoint point : pointsList) {
			points[i] = point;
			i++;
		}
//		return (MyPoint[]) pointsList.toArray();
		return points;
	}
	public Circle findRedPointsAsCircle() {
		// https://www.geeksforgeeks.org/image-processing-in-java-get-and-set-pixels/
//		Point[] points;
		int width = image.getWidth();
		int height = image.getHeight();
		// переменные для поиска минимальных и максимальных значений x и y:
		int xMax = -1, xMin = 1000, yMax = -1, yMin = 1000;
		// переменная для нахождения радиуса:
		int radius = 0;
		
		ArrayList<MyPoint> pointsList = new ArrayList<MyPoint>();
		System.out.println("img width = " + width + " height = " + height);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int p = image.getRGB(i, j);
				int r = (p >> 16) & 0xff; // get red
				int g = (p >> 8) & 0xff; // get green
				int b = p & 0xff; // get blue

//				if (r > 200 && g < 190 && b < 190) {
//				if (r > 230 && g < 230 && b < 230) {
				if (r > 250 && g < 250 && b < 250) {
					if (i >= xMax) xMax = i;
					if (i <= xMin) xMin = i;
					if (j >= yMax) yMax = j;
					if (j <= yMin) yMin = j;
		        	//System.out.println("red detected at x = "+i+", y = "+j);
					//pointsList.add(new MyPoint(i, j));
				}
			}
		}
		
		// Максимальные и минимальные значения расных точек по x:
		System.out.println("\nMax red detected at x = "+xMax);
		System.out.println("Min detected at x = "+xMin);
		
		// Максимальные и минимальные значения расных точек по y:
		System.out.println("\nMax red detected at  y = "+yMax);
		System.out.println("Min red detected at y = "+yMin);
		
		// Нахождение радиуса по x:
		if (((xMax - xMin) / 2) != -500)
			radius = (xMax - xMin) / 2;

			System.out.println("\nred detected radius = " + radius);


			pointsList.add(new MyPoint(xMin + radius / 2, yMin)); //верхняя
			pointsList.add(new MyPoint(xMin, yMin + radius / 2)); //левая
			pointsList.add(new MyPoint(xMin + radius / 2, yMax)); //нижняя
			pointsList.add(new MyPoint(xMax, yMin + radius / 2)); //правая
//		return (MyPoint[]) pointsList.toArray();
			//Circle meCircle =
			return new Circle(xMin + radius, yMin + radius, radius);

	}
//	public int getXRed(int xMax, int xMin) {
//		int xRed = (xMax+xMin) / 2;
//		return xRed;
//	}
//	
//	public int getYRed(int yMax, int yMin) {
//		int yRed = (yMax+yMin) / 2;
//		return yRed;
//	}
	
	public MyPoint[] boundCircleSearch(int x0, int y0, int height) {
		ArrayList<MyPoint> pointsList = new ArrayList<MyPoint>();
		int width = image.getWidth();
		int diapason = 180 ;

//		int height = image.getHeight();
		MyPoint lowerPoint;
		MyPoint upperPoint;
		MyPoint leftPoint;
		for (int j = y0; j < height; j++) {
			int p = image.getRGB(width / 2, j);
			int r = (p >> 16) & 0xff; // get red
			int g = (p >> 8) & 0xff; // get green
			int b = p & 0xff; // get blue
//			if (r < 2 && g < 2 && b < 2) {
			if (r < diapason && g < diapason && b < diapason) {
				System.out.println("\nblack at top detected at y = " + j);
				upperPoint = new MyPoint(width / 2, j);
				pointsList.add(upperPoint);
				break;
			}
		}
		for (int j = height - 1; j > 1; j--) {
			int p = image.getRGB(width / 2, j);
			int r = (p >> 16) & 0xff; // get red
			int g = (p >> 8) & 0xff; // get green
			int b = p & 0xff; // get blue
//			if (r < 2 && g < 2 && b < 2) {
			if (r < diapason && g < diapason && b < diapason) {
				System.out.println("black at buttom detected at y = " + j);
				lowerPoint = new MyPoint(width / 2, j);
				pointsList.add(lowerPoint);
				break;
			}
		}
		for (int i = x0; i < width; i++) {
			int p = image.getRGB(i, height / 2);
			int r = (p >> 16) & 0xff; // get red
			int g = (p >> 8) & 0xff; // get green
			int b = p & 0xff; // get blue
//			if (r < 2 && g < 2 && b < 2) {
			if (r < diapason && g < diapason && b < diapason) {
				System.out.println("black at left detected at x = " + i);
				leftPoint = new MyPoint(i, height / 2);
				pointsList.add(leftPoint);
				break;
			}
		}
		MyPoint[] points = new MyPoint[pointsList.size()];
		int i = 0;
		for (MyPoint point : pointsList) {
			points[i] = point;
			i++;
		}
//		return (MyPoint[]) pointsList.toArray();
		return points;
	}
	
	public Circle getCircle(int x, int y, int height) {
		MyPoint[] z = boundCircleSearch(x, y, height);
		System.out.println("z.size = "+z.length);
		int a = z[1].getX() - z[0].getX();
		int b = z[1].getY() - z[0].getY();
		int c = z[2].getX() - z[0].getX();
		int d = z[2].getY() - z[0].getY();
		int e = a * (z[0].getX() + z[1].getX()) + b * (z[0].getY() + z[1].getY());
		int f = c * (z[0].getX() + z[2].getX()) + d * (z[0].getY() + z[2].getY());
		int g = 2 * (a * (z[2].getY() - z[1].getY()) - b * (z[2].getX() - z[1].getX()));
		if (g == 0) {
			// если точки лежат на одной линии,
			// или их координаты совпадают,
			// то окружность вписать не получится
			return null;
		}
		// координаты центра
		int Cx = (int) ((d * e - b * f) / (float) g);
		int Cy = (int) ((a * f - c * e) / (float) g);
		// радиус
		int R = (int) Math.sqrt(Math.pow(z[0].getX() - Cx, 2) + Math.pow(z[0].getY() - Cy, 2));
		return new Circle(Cx, Cy, R);
	}

	public ArrayList<Circle> getAllCircles(/*Point[] z*/) {
		//https://shra.ru/2019/10/koordinaty-centra-okruzhnosti-po-trem-tochkam/ 
		ArrayList<Circle> circleList = new ArrayList<>();
		Circle circle = getCircle(0, 0, image.getHeight());
//		MyPoint[] z = boundCircleSearch(0,0, image.getHeight());
//		int a = z[1].getX() - z[0].getX();
//		int b = z[1].getY() - z[0].getY();
//		int c = z[2].getX() - z[0].getX();
//		int d = z[2].getY() - z[0].getY();
//		int e = a * (z[0].getX() + z[1].getX()) + b * (z[0].getY() + z[1].getY());
//		int f = c * (z[0].getX() + z[2].getX()) + d * (z[0].getY() + z[2].getY());
//		int g = 2 * (a * (z[2].getY() - z[1].getY()) - b * (z[2].getX() - z[1].getX()));
//		if (g == 0) {
//			// если точки лежат на одной линии,
//			// или их координаты совпадают,
//			// то окружность вписать не получится
//			return null;
//		}
//		// координаты центра
//		int Cx = (int) ((d * e - b * f) / (float) g);
//		int Cy = (int) ((a * f - c * e) / (float) g);
//		// радиус
//		int R = (int) Math.sqrt(Math.pow(z[0].getX() - Cx, 2) + Math.pow(z[0].getY() - Cy, 2));
		// вернем параметры круга
		circleList.add(circle);
		int Cx = circle.getX();
		int Cy = circle.getY();
		int R = circle.getRadius();
		int  d = 30; // d = 15; d = 27
		int height = Cy + R - d;
		int y = Cy - R + d;
		int x = Cx - R + d;
		//int count = 0;
		
		while (height >= 320) { // height >= 340 height >= 326
			Circle tempCircle = getCircle(x, y, height);
			circleList.add(tempCircle);
			R = tempCircle.getRadius();
			height = Cy + R - d;
			y = Cy - R + d;
			x = Cx - R + d;
			System.out.println("y = "+ y + " h = "+height+" x = "+x);
			//count++;
			//if (count >=500) break;
			//break;
		}
		
//		return new Circle(Cx, Cy, R);
		return circleList;
	}

}
