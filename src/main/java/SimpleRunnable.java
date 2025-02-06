import com.github.sarxos.webcam.Webcam;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SimpleRunnable implements Runnable {
    private RedMain redMain_obj;
    JFrame Frame;
    private Webcam webcam_obj;
    private MyLabel imageLabel_obj;
    private BufferedImage myPicture_obj;
    private ImageIcon imgIcon_obj;
    private BufferedImage colorImg_obj;

    static private JTextArea ta; //локальный объект типа JTextArea
    static private String text; //текст, который поток будет выводить

    static private boolean stop = false;

    public SimpleRunnable (RedMain redmain, JFrame redmainFrame, Webcam webcam, MyLabel imageLabel){
        redMain_obj = redmain;
        Frame =redmainFrame;
        webcam_obj = webcam;
        imageLabel_obj = imageLabel;

    }
    // метод для остановки потока и расспознавания красных точек:
    static public void stopped(){
        stop = true;
    }

    // метод для остановки потока и расспознавания красных точек:
    static public void running(){
        stop = false;
    }
    public static void SetText(JTextArea textArea, String textOut){

        ta = textArea; //локальный объект присваиваем переданному параметру
        text=textOut;
    }

    @Override
    public void run(){
        while (Thread.currentThread().getState()==Thread.State.RUNNABLE && !stop ){
            colorImg_obj = webcam_obj.getImage();
//            if (ta!=null){
//                ta.append(text+"\n"); //выводим текст в объект JTextArea
//            }


            // Буффер для изменнения картинки в серый
            BufferedImage blackAndWhiteImg = new BufferedImage(colorImg_obj.getWidth(), colorImg_obj.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//		BufferedImage blackAndWhiteImg = new BufferedImage(myPicture.getWidth(), myPicture.getHeight(),BufferedImage.TYPE_BYTE_BINARY);

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

