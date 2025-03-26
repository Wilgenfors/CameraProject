import com.github.sarxos.webcam.Webcam;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SimpleRunnable implements Runnable {

    private RedMain redMain_obj;
    JFrame frame;
    private Webcam webcam_obj;
    private MyLabel imageLabel_obj;
    private BufferedImage colorImg_obj;
    public static boolean stop;

    public SimpleRunnable (RedMain redmain, JFrame redmainFrame, Webcam webcam, MyLabel imageLabel){
        redMain_obj = redmain;
        frame =redmainFrame;
        webcam_obj = webcam;
        imageLabel_obj = imageLabel;

        stop = false;

    }
    // метод для остановки потока и распознавания красных точек:
    static public void stopped(){
        stop = true;
    }

    // ф-ия для возобновления потока:
    public static void contented() {
        stop = false;
    }

    @Override
    public void run(){

        // ока не остановили поток он будет обновлять картинку на втором фрейме
        while (Thread.currentThread().getState()==Thread.State.RUNNABLE && !stop ){
            colorImg_obj = webcam_obj.getImage();
            // Буфф ер для изменения картинки в серый
            BufferedImage blackAndWhiteImg = new BufferedImage(colorImg_obj.getWidth(), colorImg_obj.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D graphics = blackAndWhiteImg.createGraphics();

            graphics.drawImage(colorImg_obj, 0, 0, null);
            ImageIcon imgIcon = new ImageIcon(blackAndWhiteImg);
            //imageLabel_obj.setIcon(imgIcon);

            frame.remove(imageLabel_obj);
            frame.add(imageLabel_obj, BorderLayout.CENTER);
            frame.setSize(800, 600);
            frame.setVisible(true);
            redMain_obj.resizeImage(imageLabel_obj, blackAndWhiteImg, imgIcon, colorImg_obj);

            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}

