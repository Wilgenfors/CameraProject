

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
    static JTextArea myTextArea;

    public static void main(String[] args) throws IOException {

        //проверяет кол-во камер, и если 1, то брать дефолтную, иначе вторую:
        var cams = Webcam.getWebcams();
        if (cams.size()>1) {
            webcam = cams.get(1);
        }
        // Задаем разрешение камеры:
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        // Создаем панель для нашего объекта вебкамеры:
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setImageSizeDisplayed(true);

        // Создаем Фрейм для нашей отображения полученного изображения
        // И добавляем туда кнопки и панель для отображения веб-камеры:
        JFrame window = new JFrame("Webcam");
        window.add(panel, BorderLayout.CENTER);
        // add button for detected black circle and red point^
        JButton button = new JButton("Red detected");
        window.add(button, BorderLayout.NORTH);
        // add button for stopped Runnable:
        JButton button_stop = new JButton("Stopped detected");
        window.add(button_stop, BorderLayout.SOUTH);

        //_____________________________________________________________
        // Текстовое пол для вывода места попадания или промаха:
        myTextArea = new JTextArea(10,20);
        // В текстовом поле добавляем скролинг:
        JScrollPane scrollPane = new JScrollPane(myTextArea);
        window.add(scrollPane,BorderLayout.EAST);

        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
        webcam.open();
        //RedMain.guiTest(webcam);

        // Слушатель конпки для начала потока (расспознования красных точек):
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SimpleRunnable.running();
                RedMain.guiTest(webcam);
            }
        });
        //_______________________________________________

        // Слашатель для остановки потока:
        button_stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SimpleRunnable.stopped();
            }
        });
        //_______________________________________________

    }

}