package panels;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import Helper.Emoji;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.util.IOUtils;

import javax.imageio.ImageIO;

public class AmazonPanel extends ImagePanel {
    public AmazonPanel(){
        super("amazon.png", 145);
    }

    public void detectFaces() {
        AWSCredentials credentials;
        String photo = "test.jpg";

        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
            InputStream inputStream = new FileInputStream(new File(photo));
            ByteBuffer imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));

            AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
                    .standard()
                    .withRegion(Regions.US_WEST_2)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();

            DetectFacesRequest request = new DetectFacesRequest()
                    .withImage(new Image().withBytes(imageBytes))
                    .withAttributes(Attribute.ALL);

            // Replace Attribute.ALL with Attribute.DEFAULT to get default values.

            DetectFacesResult result = rekognitionClient.detectFaces(request);
            List<FaceDetail> faceDetails = result.getFaceDetails();

            System.out.println("AMAZON:");
            ArrayList<Emoji> overlays = new ArrayList<>();
            for (FaceDetail face : faceDetails) {
                System.out.print(face.toString());
                //HAPPY | SAD | ANGRY | CONFUSED | DISGUSTED | SURPRISED | CALM | UNKNOWN
                List<Emotion> emotions = face.getEmotions();
                float max = 0.0f;
                String emotionName = "";
                for (Emotion emotion: emotions) {
                    if(emotion.getConfidence()> max){
                        max = emotion.getConfidence();
                        emotionName = String.valueOf(emotion.getClass());
                    }
                }

                String filename = "neutral.png";
                switch (emotionName) {
                    case "Happy":
                        filename = "happy.png";
                        break;
                    case "SAD":
                        filename = "sad.png";
                        break;
                    case "ANGRY":
                        filename = "anger.png";
                        break;
                    case "CONFUSED":
                        filename = "neutral.png";
                        break;
                    case "DISGUSTED":
                        filename = "disgust.png";
                        break;
                    case "SURPRISED":
                        filename = "surprise.png";
                        break;
                    case "CALM":
                        filename = "neutral.png";
                        break;
                    case "UNKNOWN":
                        filename = "neutral.png";
                        break;

                    default:
                        break;
                }

                BufferedImage bi = ImageIO.read(new File("test.jpg"));
                //get width and height of image
                int imageWidth = bi.getWidth();
                int imageHeight = bi.getHeight();
                int x = (int) (face.getBoundingBox().getLeft() * imageWidth);
                int y = (int) (face.getBoundingBox().getTop() * imageHeight);
                int width = (int) (face.getBoundingBox().getWidth() * imageWidth);
                int height = (int) (face.getBoundingBox().getHeight() * imageHeight);

                overlays.add(new Emoji(x, y, width, height, filename));
            }
            drawToBackground(overlays);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}