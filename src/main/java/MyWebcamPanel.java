import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import java.awt.*;

public class MyWebcamPanel extends WebcamPanel { // Класс наследуется для прорисовки попаданий во время игры
    int circleR;
    int circleX;
    int circleY;
    Circle circle;
    float dHeight;
    boolean paintRedPoint = false;
    public MyWebcamPanel(Webcam webcam) {
        super(webcam);
    }

    public void drawPointOnWebPanel(Circle circle, float dHeight2){ // Метод для вхождения в условие в void paint
        this.circle = circle;
        this.dHeight = dHeight2;
        paintRedPoint = true;
        repaint();
    }


//    public void drawPointOnWebPanel(Circle circle) {
//        this.circle = circle;
//        paintRedPoint = true;
//        repaint();
//    }


    public void paint(Graphics g) {
        super.paint(g);
        //Graphics2D gr2D = (Graphics2D) g;
        Graphics2D grDot = (Graphics2D) g;
        BasicStroke pen;

        // Если мы вошли в метод для прорисовки, при попадании на панели в реальном времени отображается синий квадрат:
        if (paintRedPoint) {
            grDot.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Создаем перо
            pen = new BasicStroke(2);
            // делаем жирным
            grDot.setStroke(pen);
            // делаем синим
            grDot.setColor(Color.BLUE);

            // Присваиваем координаты нашей точки:
            circleX = (int) (circle.getX() * dHeight);
            circleY =  (int) (circle.getY()* dHeight);
            circleR =  (int) (circle.getRadius()* dHeight);
            // и прорисовываем синий квадрат в области нашего попадания
            grDot.drawRect(circleX-circleR-1, circleY-circleR-1, circleR*2+1, circleR*2+1);

            // Что бы не циклился вне потока:
            paintRedPoint  = false;
        }
    }

}
