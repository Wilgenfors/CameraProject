import com.github.sarxos.webcam.WebcamResolution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Calibration {
    private static JFrame frameCalibration;
    static MyLabel labelCalibration;
    static RedSearch redSearch;

    static JCheckBox redCalibrationChBox = new JCheckBox("Red");
    static JCheckBox blackCalibrationChBox = new JCheckBox("Black");

//Calibration(BufferedImage image){
Calibration(BufferedImage myPicture){

    // Буфф ер для изменения картинки в серый
    //BufferedImage image = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    BufferedImage image = myPicture;
    Graphics2D graphics = image.createGraphics();
    graphics.drawImage(myPicture, 0, 0, null);

    labelCalibration = new MyLabel();  // Передаем объект камеры на специальную панель для вывода изображения с камеры
    frameCalibration = new JFrame("Calibration"); // Создаем главный фрейм

    ImageIcon imgIcon = new ImageIcon(image);

    labelCalibration.setIcon(imgIcon);

    frameCalibration.remove(labelCalibration);


    frameCalibration.setSize(656,519);
    labelCalibration.setSize(640,480);

    float dHeight = labelCalibration.getHeight() / (float) image.getHeight();
    int newWidth = (int) (image.getWidth() * dHeight);
    Image dimg = image.getScaledInstance(newWidth, labelCalibration.getHeight(), Image.SCALE_SMOOTH);
    imgIcon.setImage(dimg);

    frameCalibration.add(labelCalibration, BorderLayout.CENTER);

    frameCalibration.remove(labelCalibration);
    frameCalibration.add(labelCalibration, BorderLayout.CENTER);

    // create panelNORTH for North:
    JPanel panelNORTH = new JPanel();

    // add JCheckBox for detected:
    panelNORTH.add(redCalibrationChBox);
    panelNORTH.add(blackCalibrationChBox);

    // add startButton for detected black circle and red point:
    JButton startButton = new JButton("Complete calibration");
    panelNORTH.add(startButton);

    // add panel in frame - window:
    frameCalibration.add(panelNORTH, BorderLayout.NORTH);

    // И заканчиваем настройку главного фрейма:

    frameCalibration.setResizable(true);
    frameCalibration.setVisible(true);
    frameCalibration.pack();
    redSearch = new RedSearch(image);

    //frameCalibration.getWidth();

    // Добавляем слушателя к первому чекбоксу
    redCalibrationChBox.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            //updateStatusLabel(checkBox1, checkBox2, statusLabel);
            BufferedImage image = myPicture;
            Graphics2D graphics = image.createGraphics();
            graphics.drawImage(myPicture, 0, 0, null);
            ImageIcon imgIcon = new ImageIcon(image);
            labelCalibration.setIcon(imgIcon);
            frameCalibration.remove(labelCalibration);

            float dHeight = labelCalibration.getHeight() / (float) image.getHeight();
            int newWidth = (int) (image.getWidth() * dHeight);
            Image dimg = image.getScaledInstance(newWidth, labelCalibration.getHeight(), Image.SCALE_SMOOTH);
            imgIcon.setImage(dimg);

            frameCalibration.remove(labelCalibration);
            frameCalibration.add(labelCalibration, BorderLayout.CENTER);

           // redCalibrationChBox.setSelected(false);
        }
    });

    // Добавляем слушателя ко второму чекбоксу
    blackCalibrationChBox.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            //updateStatusLabel(checkBox1, checkBox2, statusLabel);
            BufferedImage image = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D graphics = image.createGraphics();
            graphics.drawImage(myPicture, 0, 0, null);
            ImageIcon imgIcon = new ImageIcon(image);
            labelCalibration.setIcon(imgIcon);
            frameCalibration.remove(labelCalibration);

            float dHeight = labelCalibration.getHeight() / (float) image.getHeight();
            int newWidth1 = (int) (image.getWidth() * dHeight);
            Image dimg = image.getScaledInstance(newWidth1, labelCalibration.getHeight(), Image.SCALE_SMOOTH);
            imgIcon.setImage(dimg);

            frameCalibration.remove(labelCalibration);
            frameCalibration.add(labelCalibration, BorderLayout.CENTER);

            //blackCalibrationChBox.setSelected(false);
        }
    });



    // Слушатель созданный для калибровки цветов через мышь:
    labelCalibration.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);

            // Если нажали на калибровку красной точки:
            if (redCalibrationChBox.isSelected()) {

                // Буфф ер для изменения картинки в серый
               // BufferedImage image = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

                BufferedImage image = myPicture;
                Graphics2D graphics = image.createGraphics();
                graphics.drawImage(myPicture, 0, 0, null);
                ImageIcon imgIcon = new ImageIcon(image);
//                labelCalibration.setIcon(imgIcon);
//                frameCalibration.remove(labelCalibration);

                float dHeight = labelCalibration.getHeight() / (float) image.getHeight();
                int newWidth = (int) (image.getWidth() * dHeight);
                Image dimg = image.getScaledInstance(newWidth, labelCalibration.getHeight(), Image.SCALE_SMOOTH);
                imgIcon.setImage(dimg);

                //frameCalibration.add(labelCalibration, BorderLayout.CENTER);
//                frameCalibration.remove(labelCalibration);
//                frameCalibration.add(labelCalibration, BorderLayout.CENTER);

                //redSearch = new RedSearch(image);
                System.out.println("---!! image clicked at x = " + e.getX() + " y=" + e.getY() + " !!---");
                int p = image.getRGB(e.getX(), e.getY() / 2);
                int r = (p >> 16) & 0xff; // get red
                int g = (p >> 8) & 0xff; // get green
                int b = p & 0xff; // get blue

                System.out.println("---!! circle clicked at r = " + r);
                System.out.println("---!! circle clicked at g = " + g);
                System.out.println("---!! circle clicked at b = " + b);
                // Метод для передачи диапазона цвета нашей красной точки:
                RedSearch.passDiaposoneColorRedPoint(r, g, b);

                redCalibrationChBox.setSelected(false);
            }
            // Если нажали на калибровку черного круга:
            else if (blackCalibrationChBox.isSelected()) {
                System.out.println("---!! image clicked at x = " + e.getX() + " y=" + e.getY() + " !!---");


                // Буфф ер для изменения картинки в серый
                BufferedImage image = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                Graphics2D graphics = image.createGraphics();
                graphics.drawImage(myPicture, 0, 0, null);
                ImageIcon imgIcon = new ImageIcon(image);
//                labelCalibration.setIcon(imgIcon);
//                frameCalibration.remove(labelCalibration);

                float dHeight = labelCalibration.getHeight() / (float) image.getHeight();
                int newWidth = (int) (image.getWidth() * dHeight);
                Image dimg = image.getScaledInstance(newWidth, labelCalibration.getHeight(), Image.SCALE_SMOOTH);
                imgIcon.setImage(dimg);

                //frameCalibration.add(labelCalibration, BorderLayout.CENTER);
//                frameCalibration.remove(labelCalibration);
//                frameCalibration.add(labelCalibration, BorderLayout.CENTER);

                int p = image.getRGB(e.getX(), e.getY() / 2);
                int r = (p >> 16) & 0xff; // get red
                int g = (p >> 8) & 0xff; // get green
                int b = p & 0xff; // get blue

                System.out.println("---!! point clicked at r = " + r);
                System.out.println("---!! point clicked at g = " + g);
                System.out.println("---!! point clicked at b = " + b);
                // Метод для передачи диапазона цвета нашего черного круга:
                RedSearch.blackCirclePassDiaposoneColor(r, g, b);

                blackCalibrationChBox.setSelected(false);
            }
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
