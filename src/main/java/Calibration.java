import com.github.sarxos.webcam.WebcamResolution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

//import static jdk.nio.zipfs.ZipFileAttributeView.AttrID.group;

public class Calibration {
    private static JFrame frameCalibration;
     MyLabel labelCalibration; // todo im deleted static
     RedSearch redSearch; // todo im deleted static
     BufferedImage myPicture; // todo im deleted static
//    static JCheckBox redCalibrationChBox = new JCheckBox("Red");
//    static JCheckBox blackCalibrationChBox = new JCheckBox("Black");

    ButtonGroup radioGroup = new ButtonGroup();
    JRadioButton redButton = new JRadioButton("Red", true);


    JRadioButton blackButton = new JRadioButton("Black", false);

//Calibration(BufferedImage image){
Calibration(BufferedImage _myPicture){

    // Буфф ер для изменения картинки в серый
    //BufferedImage image = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    myPicture = _myPicture;
    BufferedImage image = myPicture;
    Graphics2D graphics = image.createGraphics();
    graphics.drawImage(myPicture, 0, 0, null);

    labelCalibration = new MyLabel();  // Передаем объект камеры на специальную панель для вывода изображения с камеры
    frameCalibration = new JFrame("Calibration"); // Создаем главный фрейм

    ImageIcon imgIcon = new ImageIcon(image);

    labelCalibration.setIcon(imgIcon);

    frameCalibration.remove(labelCalibration);

    // Получаем размеры image и устанавливаем их как размеры лейбла:
    labelCalibration.setSize(image.getWidth(),image.getHeight());

    frameCalibration.setSize(762,634);
//    labelCalibration.setSize(745,559);

    float dHeight = labelCalibration.getHeight() / (float) image.getHeight();
    int newWidth = (int) (image.getWidth() * dHeight);
    Image dimg = image.getScaledInstance(newWidth, labelCalibration.getHeight(), Image.SCALE_SMOOTH);
    imgIcon.setImage(dimg);

   // frameCalibration.add(labelCalibration, BorderLayout.CENTER);

   // frameCalibration.remove(labelCalibration);
    frameCalibration.add(labelCalibration, BorderLayout.CENTER);

    // create panelNORTH for North:
    JPanel panelNORTH = new JPanel();

    // add JCheckBox for detected:
    //panelNORTH.add(redCalibrationChBox);
    //panelNORTH.add(blackCalibrationChBox);
//Устанавливаем стилистику и размер шрифта:
    redButton.setFont(new Font("Arial", Font.BOLD, 16));
    blackButton.setFont(new Font("Arial", Font.BOLD, 16));
    // add ragioButton:
    radioGroup.add(redButton);
    radioGroup.add(blackButton);

    panelNORTH.add(redButton);
    panelNORTH.add(blackButton);

    // add startButton for detected black circle and red point:
    JButton startButton = new JButton("Complete calibration");
    //Устанавливаем стилистику и размер шрифта:
    startButton.setFont(new Font("Complete calibration",Font.BOLD,14));
    panelNORTH.add(startButton);

    // add panel in frame - window:
    frameCalibration.add(panelNORTH, BorderLayout.NORTH);

    // И заканчиваем настройку главного фрейма:

    frameCalibration.setResizable(true);
    frameCalibration.setVisible(true);
    frameCalibration.pack();
    redSearch = new RedSearch(image);

//    System.out.println("frameCalibration.getWidth() "+ frameCalibration.getWidth());
//    System.out.println("frameCalibration.getHeight() "+ frameCalibration.getHeight());
//
//    System.out.println("labelCalibration.getWidth() "+ labelCalibration.getWidth());
//    System.out.println("labelCalibration.getHeight() "+ labelCalibration.getHeight());



    // Слушатель изменения размера лейбла:
    labelCalibration.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            //resizeImage(imageLabel, blackAndWhiteImg, imgIcon, myPicture);
            float dHeight = labelCalibration.getHeight() / (float) myPicture.getHeight();
            int newWidth = (int) (myPicture.getWidth() * dHeight);
            Image dimg = myPicture.getScaledInstance(newWidth, labelCalibration.getHeight(), Image.SCALE_SMOOTH);
            imgIcon.setImage(dimg);
            //RedSearch redSearch = new RedSearch(myPicture);
        }
    });

//    // Слушатель который возращает размеры окна и лейбла
//    labelCalibration.addComponentListener(new ComponentAdapter() {
//        @Override
//        public void componentResized(ComponentEvent e) {
//            System.out.println("\n\nframeCalibration.getWidth() "+ frameCalibration.getWidth());
//            System.out.println("frameCalibration.getHeight() "+ frameCalibration.getHeight());
//            System.out.println("labelCalibration.getWidth() "+ labelCalibration.getWidth());
//            System.out.println("labelCalibration.getHeight() "+ labelCalibration.getHeight());
//        }
//    });

    // Добавляем слушателя к первому чекбоксу
    redButton.addItemListener(new ItemListener() {
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

            //blackCalibrationChBox.setSelected(false);
            //redCalibrationChBox.setSelected(true);
        }
    });

    // Добавляем слушателя ко второму чекбоксу
    blackButton.addItemListener(new ItemListener() {
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

        }
    });



    // Слушатель созданный для калибровки цветов через мышь:
    labelCalibration.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);

            // Если нажали на калибровку красной точки:
            if (redButton.isSelected()) {

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
                //System.out.println("---!! image clicked at x = " + e.getX() + " y=" + e.getY() + " !!---");
                int p = image.getRGB(e.getX(), e.getY());
                //int p = myPicture.getRGB(e.getX(), e.getY());

                int r = (p >> 16) & 0xff; // get red
                int g = (p >> 8) & 0xff; // get green
                int b = p & 0xff; // get blue

//                System.out.println("---!! circle clicked at r = " + r);
//                System.out.println("---!! circle clicked at g = " + g);
//                System.out.println("---!! circle clicked at b = " + b);

                // Проверяем, что координаты в пределах изображения
//                System.out.println("-image.getWidth() = " + image.getWidth());
//                System.out.println("-image.getHeight() = " + image.getHeight());

                double scaleX = (double)labelCalibration.getWidth() / image.getWidth();
                double scaleY = (double)labelCalibration.getHeight() / image.getHeight();

                int imgX = (int)(630 / scaleX);
                int imgY = (int)(450 / scaleY);

// Проверяем границы
                if (imgX >= 0 && imgX < image.getWidth() && imgY >= 0 && imgY < image.getHeight()) {
                    // Сравнение с фоновым цветом:
                    int pBG = image.getRGB(520, 390);
                    //int p = myPicture.getRGB(e.getX(), e.getY());
                    int rBG = (pBG >> 16) & 0xff; // get red
                    int gBG = (pBG >> 8) & 0xff; // get green
                    int bBG = pBG & 0xff; // get blue

//                    System.out.println("(rBG = " + rBG);
//                    System.out.println("(r = " + r);
//                    System.out.println("(gBG = " + gBG);
//
//                    System.out.println("\n(g = " + g);
//                    System.out.println("(bBG = " + bBG);
//                    System.out.println("(b = " + b);

                    if ((r - rBG) > 20 && (g - gBG) > 20 && (b - bBG) > 20) {
                        JDialog dialog = new JDialog(frameCalibration, "Уведомление", true); // true - модальное
                        dialog.setSize(320, 250);
                        dialog.setLayout(new FlowLayout());

                        // Добавляем компоненты
                        dialog.add(new JLabel("Диапазон цвета красной точки успешно передан!"));
                        dialog.add(new JTextArea("Красная точка:\nR = "+r+"\nG = "+g+"\nB = "+b+"\n\nФон:\nR = "+rBG+"\nG = "+gBG+"\nB = "+bBG));
                        JButton closeButton = new JButton("Закрыть");
                        closeButton.addActionListener(ev -> dialog.dispose());
                        dialog.add(closeButton);

                        // Центрируем относительно родительского окна
                        dialog.setLocationRelativeTo(frameCalibration);
                        dialog.setVisible(true);

                       // System.out.println("(rBG - r ) > 40 && (gBG - g ) > 40 && (bBG - b ) > 40)");
                        redButton.setSelected(false);
                        blackButton.setSelected(true);
                        // Метод для передачи диапазона цвета нашей красной точки:
                        //RedSearch.passDiaposoneColorRedPoint(r, g, b); // todo закоментировал
                        redSearch = new RedSearch(myPicture);
                        redSearch.passDiaposoneColorRedPoint(r, g, b);

                    }
                }



                //redCalibrationChBox.setSelected(false);
            }
            // Если нажали на калибровку черного круга:
            else if (blackButton.isSelected()) {
               // System.out.println("---!! image clicked at x = " + e.getX() + " y=" + e.getY() + " !!---");


                // Буфф ер для изменения картинки в серый
                BufferedImage image2 = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                Graphics2D graphics = image2.createGraphics();
                graphics.drawImage(myPicture, 0, 0, null);
                ImageIcon imgIcon = new ImageIcon(image2);
//                labelCalibration.setIcon(imgIcon);
//                frameCalibration.remove(labelCalibration);

                float dHeight = labelCalibration.getHeight() / (float) image2.getHeight();
                int newWidth = (int) (image2.getWidth() * dHeight);
                Image dimg = image2.getScaledInstance(newWidth, labelCalibration.getHeight(), Image.SCALE_SMOOTH);
                imgIcon.setImage(dimg);

                //frameCalibration.add(labelCalibration, BorderLayout.CENTER);
//                frameCalibration.remove(labelCalibration);
//                frameCalibration.add(labelCalibration, BorderLayout.CENTER);

                int p = image2.getRGB(e.getX(), e.getY());

                int r = (p >> 16) & 0xff; // get red
                int g = (p >> 8) & 0xff; // get green
                int b = p & 0xff; // get blue

//                System.out.println("---!! point clicked at r = " + r);
//                System.out.println("---!! point clicked at g = " + g);
//                System.out.println("---!! point clicked at b = " + b);

                // Сравнение с фоновым цветом:
                double scaleX = (double)labelCalibration.getWidth() / image2.getWidth();
                double scaleY = (double)labelCalibration.getHeight() / image2.getHeight();

                int imgX = (int)(630 / scaleX);
                int imgY = (int)(450 / scaleY);

// Проверяем границы
                if (imgX >= 0 && imgX < image2.getWidth() && imgY >= 0 && imgY < image2.getHeight()) {

                    // остальная обработка
                    int pBG = image2.getRGB(520, 390);
                    //int p = myPicture.getRGB(e.getX(), e.getY());
                    int rBG = (pBG >> 16) & 0xff; // get red
                    int gBG = (pBG >> 8) & 0xff; // get green
                    int bBG = pBG & 0xff; // get blue

//                System.out.println("(rBG = "+rBG);
//                System.out.println("(r = "+r);
//                System.out.println("(gBG = "+gBG);
//
//                System.out.println("\n(g = "+g);
//                System.out.println("(bBG = "+bBG);
//                System.out.println("(b = "+b);

                    if ( (rBG - r ) > 40 && (gBG - g ) > 40 && (bBG - b ) > 40){

                        JDialog dialog = new JDialog(frameCalibration, "Уведомление", true); // true - модальное
                        dialog.setSize(320, 250);
                        dialog.setLayout(new FlowLayout());

                        // Добавляем компоненты
                        dialog.add(new JLabel("Диапазон цвета черного круга успешно передан!"));
                        dialog.add(new JTextArea("Черный круг:\nR = "+r+"\nG = "+g+"\nB = "+b+"\n\nФон:\nR = "+rBG+"\nG = "+gBG+"\nB = "+bBG));
                        JButton closeButton = new JButton("Закрыть");
                        closeButton.addActionListener(ev -> dialog.dispose());
                        dialog.add(closeButton);

                        // Центрируем относительно родительского окна
                        dialog.setLocationRelativeTo(frameCalibration);
                        dialog.setVisible(true);

                    //    System.out.println("(rBG - r ) > 40 && (gBG - g ) > 40 && (bBG - b ) > 40)");
                        redButton.setSelected(true);
                        blackButton.setSelected(false);
                        // Метод для передачи диапазона цвета нашего черного круга:
                        //RedSearch.blackCirclePassDiaposoneColor(r, g, b); // todo comentary
                        redSearch = new RedSearch(myPicture);
                        redSearch.blackCirclePassDiaposoneColor(r, g, b);
                    }
                }




               // blackCalibrationChBox.setSelected(false);
            }
        }
    });

    // Слушатель кнопки для закрытия окна после калибровки цветов:
    startButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            myPicture = Main.newPicher();
            frameCalibration.dispose(); // или frame.setVisible(false);

        }
    });





}



}
