package FaceRecognition.com.acn;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import panels.AmazonPanel;
import panels.GooglePanel;
import panels.KairosPanel;
import panels.MicrosoftPanel;

public class Frame extends JFrame {
    private MicrosoftPanel microsoftPanel = null;
    private GooglePanel googlePanel = null;
    private KairosPanel kairosPanel = null;
    private AmazonPanel amazonPanel = null;
    private boolean takePicture = true;
    private Webcam w;

    Frame() {
        microsoftPanel = new MicrosoftPanel();
        googlePanel = new GooglePanel();
        kairosPanel = new KairosPanel();
        amazonPanel = new AmazonPanel();
        w = Webcam.getDefault();

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

/*                if(takePicture){
                    w.setViewSize(WebcamResolution.VGA.getSize());

                    WebcamPanel panel = new WebcamPanel(w);
                    panel.setFPSDisplayed(true);
                    panel.setDisplayDebugInfo(true);
                    panel.setImageSizeDisplayed(true);
                    panel.setMirrored(true);
                    takePicture = false;
                }else {

*/
                    try {
                        w.open(true);

                        BufferedImage image = w.getImage();

                        ImageIO.write(image, "JPG", new File("test.jpg"));
                        w.close();

                        BufferedImage bImg = ImageIO.read(new File("test.jpg"));
                        microsoftPanel.setImage(bImg);
                        googlePanel.setImage(bImg);
                        kairosPanel.setImage(bImg);
                        amazonPanel.setImage(bImg);

                        new Thread(() ->{
                            microsoftPanel.detectFaces();
                        }){{start();}};
                        new Thread(() ->{
                            googlePanel.detectFaces();
                        }){{start();}};
                        new Thread(() ->{
                            kairosPanel.detectFaces();
                        }){{start();}};
                        new Thread(() ->{
                            amazonPanel.detectFaces();
                        }){{start();}};

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    takePicture = true;
                }
//            }
        };

        provider.register(KeyStroke.getKeyStroke("ENTER"), listener);
    }
}
