

import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
import java.util.ArrayList;

//import javax.imageio.ImageIO;

public class RedSearch {
	//private String path;
	private BufferedImage image;

	public RedSearch(BufferedImage img) {
		image = img;
	}

//	public RedSearch(String filePath) {
//		path = filePath;
//		File f = null;
//
//		// read image
//		try {
//			f = new File(path);
//			image = ImageIO.read(f);
//
//		} catch (IOException e) {
//			System.out.println(e);
//		}
//	}
	int redDiaposonePoint = 250; // todo im deleted static
	int greenDiaposonePoinPoint= 250;// todo im deleted static
	int blueDiaposonePoinPoint= 250;// todo im deleted static

	int circleRedDiaposone = 150;// todo im deleted static
	int circleGreenDiaposone = 150;// todo im deleted static
	int circleBlueDiaposone = 150;// todo im deleted static


	public Circle findRedPointsAsCircle() {
		// https://www.geeksforgeeks.org/image-processing-in-java-get-and-set-pixels/
		int width = image.getWidth();
		int height = image.getHeight();
		// переменные для поиска минимальных и максимальных значений x и y:
		int xMax = -1, xMin = 1000, yMax = -1, yMin = 1000;

		ArrayList<MyPoint> pointsList = new ArrayList<MyPoint>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int p = image.getRGB(i, j);
				int r = (p >> 16) & 0xff; // get red
				int g = (p >> 8) & 0xff; // get green
				int b = p & 0xff; // get blue

				//if (r > 250 && g < 250 && b < 250) {
				if (r > redDiaposonePoint && g < greenDiaposonePoinPoint && b < blueDiaposonePoinPoint) {
					if (i >= xMax) xMax = i;
					if (i <= xMin) xMin = i;
					if (j >= yMax) yMax = j;
					if (j <= yMin) yMin = j;
				}
			}
		}

		// Нахождение радиуса по x:
		int radius = (xMax - xMin) / 2;

		// Добавляем координаты нашей точки в список
		pointsList.add(new MyPoint(xMin + radius / 2, yMin)); //верхняя
		pointsList.add(new MyPoint(xMin, yMin + radius / 2)); //левая
		pointsList.add(new MyPoint(xMin + radius / 2, yMax)); //нижняя
		pointsList.add(new MyPoint(xMax, yMin + radius / 2)); //правая

		return new Circle(xMin + radius, yMin + radius, radius);

	}


	public MyPoint[] boundCircleSearch() { //ф-ия для нахождения внешнего круга
		ArrayList<MyPoint> pointsList = new ArrayList<MyPoint>();
		int width = image.getWidth();
		int height = image.getHeight();
		MyPoint lowerPoint;
		MyPoint upperPoint;
		MyPoint leftPoint;
		for (int j = 0; j < height; j++) {
			int p = image.getRGB(width / 2, j);
			int r = (p >> 16) & 0xff; // get red
			int g = (p >> 8) & 0xff; // get green
			int b = p & 0xff; // get blue
			//if (r < 150 && g < 150 && b < 150) {
			if (r < circleRedDiaposone && g < circleGreenDiaposone && b < circleBlueDiaposone) {
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
			//if (r < 150 && g < 150 && b < 150) {
			if (r < circleRedDiaposone && g < circleGreenDiaposone && b < circleBlueDiaposone) {
				lowerPoint = new MyPoint(width / 2, j);
				pointsList.add(lowerPoint);
				break;
			}
		}
		for (int i = 1; i < width; i++) {
			int p = image.getRGB(i, height / 2);
			int r = (p >> 16) & 0xff; // get red
			int g = (p >> 8) & 0xff; // get green
			int b = p & 0xff; // get blue
			//if (r < 150 && g < 150 && b < 150) {
			if (r < circleRedDiaposone && g < circleGreenDiaposone && b < circleBlueDiaposone) {
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
		return points;
	}

	public Circle getCircle(/*Point[] z*/) {
		//https://shra.ru/2019/10/koordinaty-centra-okruzhnosti-po-trem-tochkam/
		MyPoint[] z = boundCircleSearch();
		if (z.length<3) {
			return null;
		}
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
		// вернем параметры круга
		return new Circle(Cx, Cy, R);
	}

	public Circle getCircle(MyPoint[] z) {
		//https://shra.ru/2019/10/koordinaty-centra-okruzhnosti-po-trem-tochkam/
		if (z==null || z.length<3) {
			//System.out.println("--!! No circle !!--");
			return null;
		}
		for (int i = 0; i < z.length; i++) {
			if (z[i] == null) return null;
		}
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
		// вернем параметры круга
		return new Circle(Cx, Cy, R);
	}

	/**
	 *Ф-ия для нахождения всех внутренних кругов
	 *@param circle - самый внешний круг
	 *@return ArrayList<Circle> - список всех внутренних кругов (без самого внешнего)
	 */
	public ArrayList<Circle> getCircles(Circle circle) {
		ArrayList<Circle> circlesList = new ArrayList<Circle>();
		MyPoint[] z = searchCircleFromCenter(circle, 0); //находим точки самого внутреннего круга
		if (getCircle(z)!=null)
			circlesList.add(getCircle(z)); //добавляем в список круг по найденным точкам
		//цикл пока последний добавленный круг не приблизится к самому внешнему
		try {
			if (!circlesList.isEmpty()) {
				while (circlesList.get(circlesList.size() - 1).getRadius() + 40 < circle.getRadius()) {
					//ищем следующий внутренний круг за последним найденным до этого
					z = searchCircleFromCenter(circle, circlesList.get(circlesList.size() - 1).getRadius() + 40);
					circlesList.add(getCircle(z));
				}
				circlesList.remove(circlesList.size() - 1); //убираем последний круг, он обычно совпадает с самым внешним
			}
		} catch (java.util.NoSuchElementException err) {
			//System.out.println("--!! NoSuchElementException !!--");
		}
		return circlesList;
	}

	/**
	 *Ф-ия для поиска внутренних кругов
	 *@return MyPoint[] - массив с 3 точками на потенциальном круге
	 *@param circle - самый внешний круг
	 *@param k - отступ от центра для начала поиска
	 */
	private MyPoint[] searchCircleFromCenter(Circle circle, int k) {
		MyPoint[] blackPoints = new MyPoint[3];
		//идем в цикле от центра круга вверх
		for (int j = circle.getY()-k; j > circle.getY()-k - circle.getRadius(); j--) {
			int p = image.getRGB(circle.getX(), j);
			int r = (p >> 16) & 0xff; // get red
			int g = (p >> 8) & 0xff; // get green
			int b = p & 0xff; // get blue
			//if (r < 150 && g < 150 && b < 150) {
			if (r < circleRedDiaposone && g < circleGreenDiaposone && b < circleBlueDiaposone) {
				blackPoints[0] = new MyPoint(circle.getX(), j); //upper point
				break;
			}
		}
		//идем в цикле от центра круга вниз
		for (int j = circle.getY()+k; j < circle.getY()+k + circle.getRadius(); j++) {
			int p = image.getRGB(circle.getX(), j);
			int r = (p >> 16) & 0xff; // get red
			int g = (p >> 8) & 0xff; // get green
			int b = p & 0xff; // get blue
			//if (r < 150 && g < 150 && b < 150) {
			if (r < circleRedDiaposone && g < circleGreenDiaposone && b < circleBlueDiaposone) {
				blackPoints[1] = new MyPoint(circle.getX(), j); //lower point
				break;
			}
		}
		//идем в цикле от центра круга вправо
		for (int i = circle.getX()+k; i < circle.getX()+k + circle.getRadius(); i++) {
			int p = image.getRGB(i, circle.getY());
			int r = (p >> 16) & 0xff; // get red
			int g = (p >> 8) & 0xff; // get green
			int b = p & 0xff; // get blue
			//if (r < 150 && g < 150 && b < 150) {
			if (r < circleRedDiaposone && g < circleGreenDiaposone && b < circleBlueDiaposone) {
				blackPoints[2] = new MyPoint(i, circle.getY()); //right point
				break;
			}
		}
		return blackPoints;
	}

	public  void passDiaposoneColorRedPoint(int r, int g, int b) {
		redDiaposonePoint = r;
		greenDiaposonePoinPoint = g;
		blueDiaposonePoinPoint = b;

	}

	public  void blackCirclePassDiaposoneColor(int r, int g, int b) {
		circleRedDiaposone = r;
		circleGreenDiaposone = g;
		circleBlueDiaposone = b;
	}
}
