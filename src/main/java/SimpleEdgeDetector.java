

import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

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



    // НОВЫЙ МЕТОД: нахождение раздельных контуров
    public ArrayList<ArrayList<EdgeCoords>> getSeparateContours(BufferedImage image, int threshold) {
        ArrayList<EdgeCoords> allEdges = getEdgeCoords(image, threshold);

        boolean[][] visited = new boolean[image.getWidth()][image.getHeight()];
        Set<String> edgeSet = new HashSet<>();
        for (EdgeCoords e : allEdges) {
            edgeSet.add(e.getX() + "," + e.getY());
        }

        ArrayList<ArrayList<EdgeCoords>> contours = new ArrayList<>();

        for (EdgeCoords start : allEdges) {
            if (!visited[start.getX()][start.getY()]) {
                ArrayList<EdgeCoords> contour = bfsTrace(start.getX(), start.getY(), visited, edgeSet, image.getWidth(), image.getHeight());
                if (contour.size() > 10) {
                    contours.add(contour);
                }
            }
        }

        System.out.println("Contours found: " + contours.size());
        return contours;
    }

    // BFS метод - не требует Stack, использует Queue
    private ArrayList<EdgeCoords> bfsTrace(int startX, int startY, boolean[][] visited,
                                           Set<String> edgeSet, int width, int height) {
        ArrayList<EdgeCoords> contour = new ArrayList<>();
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];
            contour.add(new EdgeCoords(x, y));

            // Проверяем всех 8 соседей
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    int nx = x + dx;
                    int ny = y + dy;
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height && !visited[nx][ny]) {
                        if (edgeSet.contains(nx + "," + ny)) {
                            visited[nx][ny] = true;
                            queue.add(new int[]{nx, ny});
                        }
                    }
                }
            }
        }
        return contour;
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