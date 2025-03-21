import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import java.awt.*;

public class MyWebcamPanel extends WebcamPanel {
    int circleR;
    int circleX;
    int circleY;
    Circle circle;
    float dHeight;
    boolean paintRedPoint = false;
    public MyWebcamPanel(Webcam webcam) {
        super(webcam);
    }

    public void drawPointOnWebPanel(Circle circle, float dHeight2){
        this.circle = circle;
        this.dHeight = dHeight2;
        paintRedPoint = true;
        repaint();
    }


    public void drawPointOnWebPanel(Circle circle) {
        this.circle = circle;
        paintRedPoint = true;
        repaint();
    }


    public void paint(Graphics g) {
        super.paint(g);
        //Graphics2D gr2D = (Graphics2D) g;
        Graphics2D grDot = (Graphics2D) g;
        BasicStroke pen;

//        if (paintCircle) {
//            float[] dash = { 20, 20 };
//            gr2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            pen = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dash, 1);
//            gr2D.setStroke(pen);
//            gr2D.setColor(Color.GREEN);
//            for (Circle circle : circlesList) {
//                int x = circle.getX();
//                int y = circle.getY();
//                int r = circle.getRadius();
//                circleR = (int) (r * dHeight);
//                circleX = (int) (x * dHeight);
//                circleY = (int) (y * dHeight);
//                gr2D.drawOval(circleX - circleR, circleY - circleR, 2 * circleR, 2 * circleR);
//            }
//        }
        if (paintRedPoint) {
            //System.out.println("Drawing blue");
            //	float[] dash = { 20, 20 };
            grDot.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pen = new BasicStroke(2);
            grDot.setStroke(pen);
            grDot.setColor(Color.BLUE);

            circleX = (int) (circle.getX() * dHeight);
            circleY =  (int) (circle.getY()* dHeight);
            circleR =  (int) (circle.getRadius()* dHeight);

            System.out.println("W "+" circleX =" + circleX);
            System.out.println("W "+" circleY =" + circleY);
            System.out.println("W "+" circleR =" + circleR);

            grDot.drawRect(circleX-circleR-1, circleY-circleR-1, circleR*2+1, circleR*2+1);
        }
    }

}
