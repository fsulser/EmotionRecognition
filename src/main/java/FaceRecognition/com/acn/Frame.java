package FaceRecognition.com.acn;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import panels.AmazonPanel;
import panels.GooglePanel;
import panels.KairosPanel;
import panels.MicrosoftPanel;

class Frame extends JFrame {
    private MicrosoftPanel microsoftPanel = null;
    private GooglePanel googlePanel = null;
    private KairosPanel kairosPanel = null;
    private AmazonPanel amazonPanel = null;
    private final Webcam w;
    private boolean takePicture = false;
    WebcamPanel webcamPanel;

    Frame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        microsoftPanel = new MicrosoftPanel();
        googlePanel = new GooglePanel();
        kairosPanel = new KairosPanel();
        amazonPanel = new AmazonPanel();
        w = Webcam.getDefault();
        w.setViewSize(WebcamResolution.VGA.getSize());


        webcamPanel = new WebcamPanel(w);
        webcamPanel.setMirrored(true);

        this.add(webcamPanel);
        pack();
        setLocationByPlatform(true);
        setVisible(true);

        addKeyListener();
    }

    private void addKeyListener() {
        final Provider provider = Provider.getCurrentProvider(false);
        HotKeyListener listener = hotKey -> {

            if(takePicture){
                this.getContentPane().removeAll();
                this.setLayout(new BorderLayout());

                webcamPanel = new WebcamPanel(w);
                webcamPanel.setMirrored(true);

                this.add(webcamPanel);
                this.invalidate();
                this.validate();
                this.repaint();
                takePicture = false;
            }else {
                webcamPanel = null;
                this.getContentPane().removeAll();
                try {
                    this.setLayout(new GridLayout(2, 2));
                    getContentPane().add(microsoftPanel);
                    getContentPane().add(googlePanel);
                    getContentPane().add(kairosPanel);
                    getContentPane().add(amazonPanel);
                    this.invalidate();
                    this.validate();
                    this.repaint();

                    w.open(true);

                    BufferedImage image = w.getImage();

                    ImageIO.write(image, "JPG", new File("test.jpg"));
                    w.close();

                    microsoftPanel.setImage();
                    googlePanel.setImage();
                    kairosPanel.setImage();
                    amazonPanel.setImage();

                    new Thread(() -> microsoftPanel.detectFaces()) {{
                        start();
                    }};
                    new Thread(() -> googlePanel.detectFaces()) {{
                        start();
                    }};
                    new Thread(() -> kairosPanel.detectFaces()) {{
                        start();
                    }};
                    new Thread(() -> amazonPanel.detectFaces()) {{
                        start();
                    }};

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                takePicture = true;
            }
        };

        provider.register(KeyStroke.getKeyStroke("ENTER"), listener);
    }
}
