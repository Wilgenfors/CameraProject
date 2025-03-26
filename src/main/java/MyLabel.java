import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JLabel;

public class MyLabel extends JLabel {
    int circleR; // радиус нашей точки
    int circleX; // координаты нашей точки по оси х
    int circleY; // координаты нашей точки по оси у
    float dHeight; // переменная для корректировки картинки на фрейме при изменения размера фрейма
    Circle circle; // объектная переменая для координат точки или кругов
    ArrayList<Circle> circlesList = null; // Лист для передачи всех найденных кругов и дальнейшего отображения на втором фрейме
    ArrayList<Circle> resultList = null; // Лист для передачи всех точек - попаданий
    boolean paintCircle = false; // переменная для отображения всех найденных кругов
    boolean paintRedPoint = false; // переменная для прорисовки найденной красной точки
    boolean resultDraw = false; // переменная для отображения всех попаданий

    // ф-ия для входа в прорисовку круга
    public void drawCircle(int x, int y, int r, float dHeight) {
        this.dHeight = dHeight;
        circleR = (int) (r * dHeight);
        circleX = (int) (x * dHeight);
        circleY = (int) (y * dHeight);
        paintCircle = true;
        repaint();
    }
    // если добавить в параметры еще float dHeight, то можно не рисовать отдельно первый круг
    // ф-ия для входа в прорисовку всех кругов
    public void drawCircles(ArrayList<Circle> circlesList) { //для рисования всех кругов
        this.circlesList = circlesList;
        repaint();
    }

    // "менил метод добавив параметр и инициализацию redPoints:"
    // ф-ия для входа в прорисовку точки
    public void drawPoint(Circle circle, float dHeight2) {
        this.circle = circle;
        this.dHeight = dHeight2;
        paintRedPoint = true;
        repaint();
    }

    // ф-ия для входа в прорисовку всех точек после конца игры
    public void drawResult(ArrayList<Circle> pointList, float dHeight2) {
        System.out.println("Enter to drawResult");
        this.dHeight = dHeight2;
        this.resultList = pointList;
        resultDraw = true;
        paintCircle = false;
        repaint();
    }

    @Override
    // ф-ия различной прорисовки:
    public void paint(Graphics g) {
        // настраиваем графику:
        super.paint(g);
        Graphics2D gr2D = (Graphics2D) g;
        Graphics2D grDot = (Graphics2D) g;
        Graphics2D playerHitsDot = (Graphics2D) g;
        BasicStroke pen;
        BasicStroke pen2;

        // прорисовываем все круги:
        if (paintCircle) {
            float[] dash = {20, 20};
            gr2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pen = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dash, 1);
            gr2D.setStroke(pen);
            gr2D.setColor(Color.GREEN);
            for (Circle circle : circlesList) {
                int x = circle.getX();
                int y = circle.getY();
                int r = circle.getRadius();
                circleR = (int) (r * dHeight);
                circleX = (int) (x * dHeight);
                circleY = (int) (y * dHeight);
                gr2D.drawOval(circleX - circleR, circleY - circleR, 2 * circleR, 2 * circleR);
            }
        }
        // прорисовываем одно попадание:
        if (paintRedPoint) {
            grDot.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pen = new BasicStroke(2);
            grDot.setStroke(pen);
            grDot.setColor(Color.BLUE);

            circleX = (int) (circle.getX() * dHeight);
            circleY = (int) (circle.getY() * dHeight);
            circleR = (int) (circle.getRadius() * dHeight);

            grDot.drawRect(circleX - circleR - 1, circleY - circleR - 1, circleR * 2 + 1, circleR * 2 + 1);
        }
        // прорисовываем все попадания после окончания игры:
        if (resultDraw) {
            // настраиваем графику:
            grDot.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pen = new BasicStroke(2);
            grDot.setStroke(pen);
            grDot.setColor(Color.BLUE);

            pen2 = new BasicStroke(2);
            playerHitsDot.setStroke(pen2);
            playerHitsDot.setColor(Color.BLUE);

            // Проходимся по нашим точкам- попаданиям:
            int player =1;
            int hit =0;
            for (Circle point : resultList) {
                circleX = (int) (point.getX() * dHeight);
                circleY = (int) (point.getY() * dHeight);
                circleR = (int) (point.getRadius() * dHeight);

                //  И выводим попадание на второй фрейм с описанием какой игрок попал и каким по счету выстрелом:
                playerHitsDot.drawString("P" + player+" H"+(hit+1),circleX - circleR - 1, circleY - circleR - 1);
                grDot.drawRect(circleX - circleR - 1, circleY - circleR - 1, circleR * 2 + 1, circleR * 2 + 1);
                hit++;

                if (hit==Main.shots){
                    player++;
                    hit =0;
                    // меняем цвет описания-попадания для каждого игрока:
                    if (player == 2) playerHitsDot.setColor(Color.GREEN);
                    if (player == 3) playerHitsDot.setColor(Color.YELLOW);
                    if (player == 4) playerHitsDot.setColor(Color.BLACK);
                    if (player == 5) playerHitsDot.setColor(Color.CYAN);
                    if (player == 6) playerHitsDot.setColor(Color.GRAY);
                    if (player == 7) playerHitsDot.setColor(Color.MAGENTA);
                    if (player == 8) playerHitsDot.setColor(Color.RED);
                    if (player == 9) playerHitsDot.setColor(Color.white);
                    if (player == 10) playerHitsDot.setColor(Color.PINK);
                }
            }
        }


    }
}
