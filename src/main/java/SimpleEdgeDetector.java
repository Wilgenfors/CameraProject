

import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SimpleEdgeDetector {
	
	private ArrayList<EdgeCoords> edgeCoordsList = null;
    
	public ArrayList<EdgeCoords> getEdgeCoords(BufferedImage image, int threshold){
		if (edgeCoordsList == null) {
			edgeCoordsList = new ArrayList<>();
	        int width = image.getWidth();
	        int height = image.getHeight();
//	        BufferedImage edges = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        
	        // Sobel operator kernels
	        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
	        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
	        
	        for (int x = 1; x < width - 1; x++) {
	            for (int y = 1; y < height - 1; y++) {
	                int gx = 0, gy = 0;
	                
	                // Apply Sobel operator
	                for (int i = -1; i <= 1; i++) {
	                    for (int j = -1; j <= 1; j++) {
	                        Color color = new Color(image.getRGB(x + i, y + j));
	                        int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
	                        
	                        gx += gray * sobelX[i + 1][j + 1];
	                        gy += gray * sobelY[i + 1][j + 1];
	                    }
	                }
	                
	                int magnitude = (int) Math.sqrt(gx * gx + gy * gy);
	                
	                if (magnitude > threshold) {
	                    edgeCoordsList.add(new EdgeCoords(x, y));
	                }
	            }
	        }
		}
		
		return edgeCoordsList;
	}
	
    public BufferedImage detectEdges(BufferedImage image, int threshold) {
    	edgeCoordsList = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage edges = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Sobel operator kernels
        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
        
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int gx = 0, gy = 0;
                
                // Apply Sobel operator
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        Color color = new Color(image.getRGB(x + i, y + j));
                        int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                        
                        gx += gray * sobelX[i + 1][j + 1];
                        gy += gray * sobelY[i + 1][j + 1];
                    }
                }
                
                int magnitude = (int) Math.sqrt(gx * gx + gy * gy);
                
                if (magnitude > threshold) {
                    edges.setRGB(x, y, Color.WHITE.getRGB()); //рисуем контур
                    edgeCoordsList.add(new EdgeCoords(x, y));
                } else {
                    edges.setRGB(x, y, Color.BLACK.getRGB()); //или фон
                }
            }
        }
        
        return edges;
    }
    
    public void drawEdges(BufferedImage image, Color color) {
    	edgeCoordsList.forEach(coords->{
    		image.setRGB(coords.getX(), coords.getY(), color.getRGB());
    	});
    }
    
    public static void main(String[] args) throws Exception {
    	System.out.println("Detection started");
        BufferedImage image = javax.imageio.ImageIO.read(new java.io.File("target4_1.png"));
        SimpleEdgeDetector edgeDetector = new SimpleEdgeDetector();
        BufferedImage edges = edgeDetector.detectEdges(image, 100);
        System.out.println("edges detected");
        javax.imageio.ImageIO.write(edges, "jpg", new java.io.File("edges_detected4_1.jpg"));
        edgeDetector.drawEdges(image, Color.YELLOW);
        javax.imageio.ImageIO.write(image, "png", new java.io.File("target_with_edges4_1.png"));
        System.out.println("image is written");
    }
}