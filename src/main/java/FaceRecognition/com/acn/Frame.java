package FaceRecognition.com.acn;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import panels.*;

class Frame extends JFrame {
    private MicrosoftPanel microsoftPanel = null;
    private GooglePanelRest googlePanel = null;
    private KairosPanel kairosPanel = null;
    private AmazonPanel amazonPanel = null;
    private Webcam w;
    private boolean takePicture = false;
    private WebcamPanel webcamPanel;
    private JFrame frame = this;
    private BufferedImage image;

    Frame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //create instances of the four panels
        microsoftPanel = new MicrosoftPanel();
        googlePanel = new GooglePanelRest();
        kairosPanel = new KairosPanel();
        amazonPanel = new AmazonPanel();

        instanciateWebcam();


        this.add(webcamPanel);
        pack();
        setLocationByPlatform(true);
        setVisible(true);
        setFocusable(true);

        addKeyListener();
    }

    private void instanciateWebcam(){
        Dimension[] nonStandardResolutions = new Dimension[] {
                WebcamResolution.PAL.getSize(),
                WebcamResolution.HD720.getSize(),
        };
        w = Webcam.getWebcams().get(0);

        w.setCustomViewSizes(nonStandardResolutions);
        w.setViewSize(WebcamResolution.HD720.getSize());

        webcamPanel = new WebcamPanel(w);
        webcamPanel.setMirrored(true);
    }

    private void addKeyListener() {
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //set keys for presenter
                if(e.getKeyCode() == 33 || e.getKeyCode() == 34 || e.getKeyCode() == 116 || e.getKeyCode() ==46){
                    if(takePicture){
                        //if results are shown remove them and show live image
                        frame.getContentPane().removeAll();
                        frame.setLayout(new BorderLayout());

                        frame.add(webcamPanel);

                        frame.invalidate();
                        frame.validate();
                        frame.repaint();
                        takePicture = false;
                    }else {
                        //if image should be captured and processed
                        try {
                            //flip image horizontally
                            image =flippedImage(w.getImage());

                            //save image to file
                            ImageIO.write(image, "JPG", new File("test.jpg"));
//                            image = ImageIO.read(new File("test.jpg"));

                            //add all four result panels and set images
                            microsoftPanel.setImage(image);
                            googlePanel.setImage(image);
                            kairosPanel.setImage(image);
                            amazonPanel.setImage(image);

                            frame.getContentPane().removeAll();
                            frame.setLayout(new GridLayout(2, 2));
                            getContentPane().add(microsoftPanel);
                            getContentPane().add(googlePanel);
                            getContentPane().add(kairosPanel);
                            getContentPane().add(amazonPanel);
                            frame.invalidate();
                            frame.validate();
                            frame.repaint();


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
                }

            }
        });
    }

    /**
     * flip image horizontal and return the flipped image
     * @param bufferedImage
     * @return
     */
    public BufferedImage flippedImage(BufferedImage bufferedImage){
        BufferedImage flipped = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        AffineTransform tran = AffineTransform.getTranslateInstance(bufferedImage.getWidth(),0);
        AffineTransform flip = AffineTransform.getScaleInstance(-1d, 1d);
        tran.concatenate(flip);

        Graphics2D g = flipped.createGraphics();
        g.setTransform(tran);
        g.drawImage(bufferedImage, 0, 0, null);
        g.dispose();

        return flipped;
    }

}
