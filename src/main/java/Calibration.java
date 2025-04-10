import com.github.sarxos.webcam.WebcamResolution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Calibration {
    private static JFrame frameCalibration;
    static MyLabel labelCalibration;
    static RedSearch redSearch;

    static JCheckBox redCalibrationChBox = new JCheckBox("Red");
    static JCheckBox blackCalibrationChBox = new JCheckBox("Black");
    //static JCheckBox whiteCalibrationChBox = new JCheckBox("White");

Calibration(BufferedImage image){


    labelCalibration = new MyLabel();  // Передаем объект камеры на специальную панель для вывода изображения с камеры
    //labelCalibration.setImageSizeDisplayed(true);  // Делаем веб-панель видимой

    frameCalibration = new JFrame("Calibration"); // Создаем главный фрейм
    frameCalibration.setPreferredSize(new Dimension(986,661));  // Настраиваем размер фрейма

    ImageIcon imgIcon = new ImageIcon(image);
    labelCalibration.setIcon(imgIcon);
    frameCalibration.add(labelCalibration, BorderLayout.CENTER);


    // create panelNORTH for North:
    JPanel panelNORTH = new JPanel();

    // add JCheckBox for detected:
    panelNORTH.add(redCalibrationChBox);
    panelNORTH.add(blackCalibrationChBox);
    // panelNORTH.add(whiteCalibrationChBox);

    // add startButton for detected black circle and red point:
    JButton startButton = new JButton("Complete calibration");
    panelNORTH.add(startButton);

    // add panel in frame - window:
    frameCalibration.add(panelNORTH, BorderLayout.NORTH);


    // И заканчиваем настройку главного фрейма
    frameCalibration.setResizable(true);
    frameCalibration.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frameCalibration.pack();
    frameCalibration.setVisible(true);



    // Слушатель созданный для калибровки цветов через мышь:
    labelCalibration.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);

            // Если нажали на калибровку красной точки:
            if (redCalibrationChBox.isSelected()) {

                float dHeight = labelCalibration.getHeight() / (float) image.getHeight();
                int newWidth = (int) (image.getWidth() * dHeight);
                Image dimg = image.getScaledInstance(newWidth, labelCalibration.getHeight(), Image.SCALE_SMOOTH);
                imgIcon.setImage(dimg);
                redSearch = new RedSearch(image);


                //BufferedImage blackAndWhiteImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                Graphics2D graphics = image.createGraphics();
                graphics.drawImage(image, 0, 0, null);
                ImageIcon imgIcon = new ImageIcon(image);
                labelCalibration.setIcon(imgIcon);
                frameCalibration.remove(labelCalibration);
                frameCalibration.add(labelCalibration, BorderLayout.CENTER);

                redSearch = new RedSearch(image);
                System.out.println("---!! image clicked at x = " + e.getX() + " y=" + e.getY() + " !!---");
                int p = image.getRGB(e.getX(), e.getY() / 2);
                int r = (p >> 16) & 0xff; // get red
                int g = (p >> 8) & 0xff; // get green
                int b = p & 0xff; // get blue

                System.out.println("---!! circle clicked at r = " + r);
                System.out.println("---!! circle clicked at g = " + g);
                System.out.println("---!! circle clicked at b = " + b);
                // Метод для передачи диапазона цвета нашей красной точки:
                redSearch.passDiaposoneColorRedPoint(r, g, b);

                redCalibrationChBox.setSelected(false);
            }
            // Если нажали на калибровку черного круга:
            else if (blackCalibrationChBox.isSelected()) {
                System.out.println("---!! image clicked at x = " + e.getX() + " y=" + e.getY() + " !!---");

                // Буфф ер для изменения картинки в серый
                BufferedImage blackAndWhiteImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                Graphics2D graphics = blackAndWhiteImg.createGraphics();
                graphics.drawImage(blackAndWhiteImg, 0, 0, null);
                ImageIcon imgIcon = new ImageIcon(blackAndWhiteImg);
                labelCalibration.setIcon(imgIcon);
                frameCalibration.remove(labelCalibration);
                frameCalibration.add(labelCalibration, BorderLayout.CENTER);

                redSearch = new RedSearch(blackAndWhiteImg);

                int p = blackAndWhiteImg.getRGB(e.getX(), e.getY() / 2);
                int r = (p >> 16) & 0xff; // get red
                int g = (p >> 8) & 0xff; // get green
                int b = p & 0xff; // get blue

                System.out.println("---!! point clicked at r = " + r);
                System.out.println("---!! point clicked at g = " + g);
                System.out.println("---!! point clicked at b = " + b);
                // Метод для передачи диапазона цвета нашего черного круга:
                redSearch.blackCirclePassDiaposoneColor(r, g, b);

                blackCalibrationChBox.setSelected(false);
            }
            // Если нажали на калибровку белого фона:
//                else if (whiteCalibrationChBox.isSelected()) {
//                    //System.out.println("---!! image clicked at x = "+e.getX()+" y="+e.getY()+ " !!---");
//                    RedSearch.backgroundFoundDiaposonColor(e.getX(), e.getY());
//                    whiteCalibrationChBox.setSelected(false);
//                }
        }
    });



    // Слушатель кнопки для закрытия окна после калибровки цветов:
    startButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            frameCalibration.dispose(); // или frame.setVisible(false);
        }
    });



}

}
