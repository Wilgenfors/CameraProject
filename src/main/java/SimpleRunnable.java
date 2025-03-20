import com.github.sarxos.webcam.Webcam;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SimpleRunnable implements Runnable {

    private RedMain redMain_obj;
    JFrame Frame;
    private Webcam webcam_obj;
    private MyLabel imageLabel_obj;
    private BufferedImage colorImg_obj;
    public static boolean stop;

    public SimpleRunnable (RedMain redmain, JFrame redmainFrame, Webcam webcam, MyLabel imageLabel){
        redMain_obj = redmain;
        Frame =redmainFrame;
        webcam_obj = webcam;
        imageLabel_obj = imageLabel;

        stop = false;

    }
    // метод для остановки потока и расспознавания красных точек:
    static public void stopped(){
        stop = true;
    }

    public static void contented() {
        stop = false;
    }

    @Override
    public void run(){

        while (Thread.currentThread().getState()==Thread.State.RUNNABLE && !stop ){
            colorImg_obj = webcam_obj.getImage();

            // Буффер для изменнения картинки в серый
            BufferedImage blackAndWhiteImg = new BufferedImage(colorImg_obj.getWidth(), colorImg_obj.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D graphics = blackAndWhiteImg.createGraphics();

            graphics.drawImage(colorImg_obj, 0, 0, null);
            ImageIcon imgIcon = new ImageIcon(blackAndWhiteImg);
            imageLabel_obj.setIcon(imgIcon);

            Frame.remove(imageLabel_obj);
            Frame.add(imageLabel_obj, BorderLayout.CENTER);
            Frame.setSize(800, 600);
            Frame.setVisible(true);
            redMain_obj.resizeImage(imageLabel_obj, blackAndWhiteImg, imgIcon, colorImg_obj);

            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}

