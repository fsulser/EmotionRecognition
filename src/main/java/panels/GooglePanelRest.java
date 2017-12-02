package panels;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;

import Helper.Emoji;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;

public class GooglePanelRest extends ImagePanel {
    private final HashMap<String, Integer> myMap = new HashMap<>();

    public GooglePanelRest() {
        super("google.png", 153);
        myMap.put("UNKNOWN", 0);
        myMap.put("VERY_UNLIKELY", 1);
        myMap.put("UNLIKELY", 2);
        myMap.put("POSSIBLE", 3);
        myMap.put("LIKELY", 4);
        myMap.put("VERY_LIKELY", 5);
    }

    public void detectFaces() {
        try {
            String url = "https://vision.googleapis.com/v1/images:annotate?key=XXXX";

            String filePath = "test.jpg";
            File file = new File(filePath);

            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            String encodedfile = new String(Base64.encodeBase64(bytes), "UTF-8");


            String jsonString = "{\n" +
                    "  \"requests\": [\n" +
                    "    {\n" +
                    "      \"image\": {\n" +
                    "        \"content\": \"" + encodedfile + "\" \n" +
                    "      },\n" +
                    "      \"features\": [\n" +
                    "        {\n" +
                    "          \"type\": \"FACE_DETECTION\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            HttpClient httpclient = HttpClients.createDefault();

            URIBuilder uriBuilder = new URIBuilder(url);

            URI uri = uriBuilder.build();
            HttpPost request = new HttpPost(uri);

            request.setHeader("Content-Type", "application/json");

            StringEntity reqEntity = new StringEntity(jsonString);

            request.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String jsonResult = EntityUtils.toString(entity);
                System.out.println("GOOGLE:");
                System.out.println(jsonResult);


                drawToBackground(parseResult(jsonResult));
            }
        } catch (Exception e) {
        }
    }

    private ArrayList<Emoji> parseResult(String jsonResult) {
        ArrayList<Emoji> overlays = new ArrayList<>();
        JSONArray annotations = new JSONObject(jsonResult).getJSONArray("responses").getJSONObject(0).getJSONArray("faceAnnotations");

        for (int i = 0; i < annotations.length(); i++) {

            int anger = myMap.get(annotations.getJSONObject(i).getString("angerLikelihood"));
            int joy = myMap.get(annotations.getJSONObject(i).getString("joyLikelihood"));
            int surprise = myMap.get(annotations.getJSONObject(i).getString("surpriseLikelihood"));
            int sorrow = myMap.get(annotations.getJSONObject(i).getString("sorrowLikelihood"));

            int max = Math.max(anger, Math.max(joy, Math.max(surprise, sorrow)));

            String fileName = "neutral.png";
            if (max == anger) {
                fileName = "anger.png";
            } else if (max == surprise) {
                fileName = "surprise.png";
            } else if (max == joy) {
                fileName = "happy.png";
            } else if (max == sorrow) {
                fileName = "sad.png";
            }

            //if all are equal then it should be neutral
            if (anger == joy && joy == surprise && surprise == sorrow) {
                fileName = "neutral.png";
            }
            //if max is unlike or less then neutral
            if (max <= 3) {
                fileName = "neutral.png";
            }

            try {
                JSONArray vertices = annotations.getJSONObject(i).getJSONObject("boundingPoly").getJSONArray("vertices");
                int vert0X = vertices.getJSONObject(0).getInt("x");
                int vert0Y = vertices.getJSONObject(0).getInt("y");
                int vert1X = vertices.getJSONObject(1).getInt("x");
                int vert2Y = vertices.getJSONObject(2).getInt("y");
                int width = -(vert0X - vert1X);
                int height = -(vert0Y - vert2Y);
                overlays.add(new Emoji(vert0X, vert0Y, width, height, fileName));
            } catch (Exception e) {
                //if x or y is missing (google inconstistency) use fdBoundingPoly
                JSONArray vertices = annotations.getJSONObject(i).getJSONObject("fdBoundingPoly").getJSONArray("vertices");
                int vert0X = vertices.getJSONObject(0).getInt("x");
                int vert0Y = vertices.getJSONObject(0).getInt("y");
                int vert1X = vertices.getJSONObject(1).getInt("x");
                int vert2Y = vertices.getJSONObject(2).getInt("y");
                int width = -(vert0X - vert1X);
                int height = -(vert0Y - vert2Y);
                overlays.add(new Emoji(vert0X, vert0Y, width, height, fileName));
            }
        }
        return overlays;
    }
}