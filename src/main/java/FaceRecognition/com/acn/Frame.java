package FaceRecognition.com.acn;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import com.github.sarxos.webcam.Webcam;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import panels.AmazonPanel;
import panels.GooglePanel;
import panels.KairosPanel;
import panels.MicrosoftPanel;

public class Frame extends JFrame {
    MicrosoftPanel microsoftPanel = null;
    GooglePanel googlePanel = null;
    KairosPanel kairosPanel = null;
    AmazonPanel amazonPanel = null;

    Frame() {
        microsoftPanel = new MicrosoftPanel();
        googlePanel = new GooglePanel();
        kairosPanel = new KairosPanel();
        amazonPanel = new AmazonPanel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new GridLayout(2, 2));
        getContentPane().add(microsoftPanel);
        getContentPane().add(googlePanel);
        getContentPane().add(kairosPanel);
        getContentPane().add(amazonPanel);

        pack();
        setLocationByPlatform(true);
        setVisible(true);

        addKeyListener();
    }

    public void addKeyListener() {
        final Provider provider = Provider.getCurrentProvider(false);
        HotKeyListener listener = new HotKeyListener() {

            public void onHotKey(HotKey hotKey) {
                System.out.println("started");
                try {
                    Webcam w = Webcam.getDefault();
                    w.open(true);

                    BufferedImage image = w.getImage();

                    ImageIO.write(image, "JPG", new File("test.jpg"));
                    w.close();

                    BufferedImage bImg = ImageIO.read(new File("test.jpg"));
                    microsoftPanel.setImage(bImg);
                    googlePanel.setImage(bImg);
                    kairosPanel.setImage(bImg);
                    
//                    microsoftPanel.detectFaces();
//                    googlePanel.detectFaces();
                    kairosPanel.detectFaces();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        };

        provider.register(KeyStroke.getKeyStroke("ENTER"), listener);
    }
}
