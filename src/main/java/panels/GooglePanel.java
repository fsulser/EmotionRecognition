package panels;

import java.awt.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import java.io.PrintStream;

import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;

import java.util.HashMap;
import java.util.List;


public class GooglePanel extends ImagePanel {
    HashMap<String, Integer> myMap = new HashMap<>();

    public GooglePanel(){
        myMap.put("UNKNOWN", 0);
        myMap.put("VERY_UNLIKELY", 1);
        myMap.put("UNLIKELY", 2);
        myMap.put("POSSIBLE", 3);
        myMap.put("LIKELY", 4);
        myMap.put("VERY_LIKELY", 5);
    }

    public void detectFaces() throws Exception, IOException {
        List<AnnotateImageRequest> requests = new ArrayList<AnnotateImageRequest>();
        String filePath = "test.jpg";

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.FACE_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try {
            ImageAnnotatorClient client = ImageAnnotatorClient.create();
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            ArrayList<Integer[]> overlays = new ArrayList<Integer[]>();
            for (AnnotateImageResponse res : responses) {
                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {

                    int anger = myMap.get(annotation.getAngerLikelihood().toString());
                    int joy = myMap.get(annotation.getJoyLikelihood().toString());
                    int surprise = myMap.get(annotation.getSurpriseLikelihood().toString());
                    int sorrow = myMap.get(annotation.getSorrowLikelihood().toString());

                    int max = Math.max(anger, Math.max(joy, surprise));

                    int imageId = 3;
                    if(max == anger){
                        imageId = 6;
                    }else if (max == surprise){
                        imageId = 1;
                    }else if(max == joy){
                        imageId = 2;
                    }else if(max == sorrow){
                        imageId = 4;
                    }

                    //if all are equal then it should be neutral
                    if(anger == joy && joy == surprise && surprise == sorrow){
                        imageId = 3;
                    }

                    //if max is unlike or less then neutral
                    if(max <=3){
                        imageId = 3;
                    }

                    Vertex vert0 = annotation.getBoundingPoly().getVertices(0);
                    Vertex vert1 = annotation.getBoundingPoly().getVertices(1);
                    Vertex vert2 = annotation.getBoundingPoly().getVertices(2);
                    int x = vert0.getX();
                    int y = vert0.getY();
                    int width = -(vert0.getX()-vert1.getX());
                    int height = -(vert0.getY()-vert2.getY());
                    overlays.add(new Integer[] {x, y, width, height, imageId});

                    System.out.println("googl " + x+ " " + y+ " " + width + " " + height);

                }
                drawToBackground(overlays);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}