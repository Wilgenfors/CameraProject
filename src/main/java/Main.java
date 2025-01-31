

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.util.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    static Webcam webcam = Webcam.getDefault();

    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        //todo проверять кол-во камеер, и если 1, то брать дефолтную, иначе вторую
        var cams = Webcam.getWebcams();
        if (cams.size()>1) {
            webcam = cams.get(1);
        }
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setImageSizeDisplayed(true);

        JFrame window = new JFrame("Webcam");
        window.add(panel, BorderLayout.CENTER);
        // add button for...
        JButton button = new JButton("Red detected");
        window.add(button, BorderLayout.NORTH);
        //_____________________________________________________________
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
        webcam.open();
        //RedMain.guiTest(webcam);

        // Слушатель для кнопки:
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RedMain.guiTest(webcam);
            }
        });
        //_______________________________________________
//
//        BufferedImage image = webcam.getImage();
//
//        ImageIO.write(image, ImageUtils.FORMAT_JPG, new File("selfie.jpg"));
    }

}