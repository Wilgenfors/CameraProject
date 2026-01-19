//package TargetRecognize;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ThreadForContour extends Thread{
	private BufferedImage img;
	private int part_number;
	private Color contourColor;
	private ArrayList<ArrayList<EdgeCoords>> contours;
	public ThreadForContour(BufferedImage img_1, int number, Color edgeColor) {
		img = img_1;
		part_number = number;
		contourColor = edgeColor;
	}
	
	@Override
	public void run() {
		super.run();
		contours = new ArrayList<>();
		int contourIndex = -1;
//		contours.add(new ArrayList<EdgeCoords>());
		int width = img.getWidth();
        int height = img.getHeight();
//        System.out.println("part_img width = " + width + " height = " + height);
        boolean foundFirst = false;
        Color curDotColor = getDotColor(img, 0, height-1);
		if (part_number % 2 == 1) {
	        for (int x = 0; x < width; x++) {
		        curDotColor = getDotColor(img, x, 0);
		        for (int y = 0; y < height; y++) {
//		        	System.out.print("x = "+x+" y = "+y+"; ");
	        		while (curDotColor != contourColor && y < height) {
	        			y++;
	        			if (y == height) break;
	        			curDotColor = getDotColor(img, x, y);
	        			if (curDotColor == contourColor) {
	        				contourIndex++;
//	        				System.out.println("in white x = "+x+" y = "+y+"; contourIndex = "+contourIndex);
	        				if (contours.size()<contourIndex+1) contours.add(new ArrayList<EdgeCoords>());
	        			}
	        		}
	        		while (curDotColor == contourColor && y < height) {
//	        			System.out.println(" black x = "+x+" y = "+y+"; contourIndex = "+contourIndex);
	        			contours.get(contourIndex).add(new EdgeCoords(x, y));
	        			curDotColor = getDotColor(img, x, y);
	        			y++;
	        		}
		        }
		        contourIndex = -1;
			}
		} else {
	        for (int x = 0; x < width; x++) {
		        curDotColor = getDotColor(img, x, height-1);
		        for (int y = height-1; y > 0; y--) {
//		        	if (part_number==2) System.out.print("x = "+x+" y = "+y+"; ");
	        		while (curDotColor != contourColor && y >=0 ) {
	        			y--;
	        			if (y == 0) break;
	        			curDotColor = getDotColor(img, x, y);
	        			if (curDotColor == contourColor) {
	        				contourIndex++;
//	        				if (part_number==2) System.out.println("in white x = "+x+" y = "+y+"; contourIndex = "+contourIndex);
	        				if (contours.size()<contourIndex+1) contours.add(new ArrayList<EdgeCoords>());
	        			}
	        		}
	        		while (curDotColor == contourColor && y >=0) {
//	        			if (part_number==2) System.out.println(" black x = "+x+" y = "+y+"; contourIndex = "+contourIndex);
	        			contours.get(contourIndex).add(new EdgeCoords(x, y));
	        			curDotColor = getDotColor(img, x, y);
	        			y--;
	        		}
		        }
		        contourIndex = -1;
			}
		}
		System.out.println("contours"+part_number +"size in thread = " + contours.size());
        System.out.println("part_img"+part_number +" width = " + width + " height = " + height);
        
	}
	
	private Color getDotColor(BufferedImage image, int x, int y) {
		Color curDotColor = null;
    	int p = image.getRGB(x, y);
		int r = (p >> 16) & 0xff; // get red
		int g = (p >> 8) & 0xff; // get green
		int b = p & 0xff; // get blue
		// todo will change this color:
		// Лютый костыль, из-за создания нового объекта может все посыпаться:
		RedSearch rSobj = new RedSearch(image);
		curDotColor = new Color(rSobj.circleRedDiaposone,rSobj.circleGreenDiaposone,rSobj.circleBlueDiaposone);
		//if (r < 2 && g < 2 && b < 2) curDotColor = Color.BLACK;
		return curDotColor;
	}

	public ArrayList<ArrayList<EdgeCoords>> getContoursList() {
		return contours;
	}
	
}
