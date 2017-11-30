package panels;


import Helper.Emoji;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;


public class KairosPanel extends ImagePanel {
    public KairosPanel(){
        super("kairos.png", 231);
    }

    private static final String url = "https://api.kairos.com/v2/media";
    private static final String key = "XXX";
    private static final String appId = "XXX";

    public void detectFaces() {

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost(url);
            request.setHeader("app_id", appId);
            request.setHeader("app_key", key);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // This attaches the file to the POST:
            File f = new File("test.jpg");
            builder.addBinaryBody(
                    "source",
                    new FileInputStream(f),
                    ContentType.APPLICATION_OCTET_STREAM,
                    f.getName()
            );

            HttpEntity multipart = builder.build();
            request.setEntity(multipart);
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity resultEntity = response.getEntity();

            ArrayList<Emoji> overlays = new ArrayList<>();
            if (resultEntity != null) {
                String jsonString = EntityUtils.toString(resultEntity);
                JSONObject jsonResult = new JSONObject(jsonString);
                JSONArray faces = jsonResult.getJSONArray("frames").getJSONObject(0).getJSONArray("people");

                System.out.println("KAIROS");
                System.out.println(faces.toString());
                for (int index = 0; index < faces.length(); index++) {
                    JSONObject face = faces.getJSONObject(index).getJSONObject("face");

                    JSONObject emotions = faces.getJSONObject(index).getJSONObject("emotions");

                    double max = 0.0;
                    String emotion = "neutral";
                    for(Iterator iterator = emotions.keys(); iterator.hasNext();) {
                        String key = (String) iterator.next();
                        double actual = emotions.getDouble(key);
                        if(actual > max) {
                            max = actual;
                            emotion = key;
                        }
                    }

                    String filename = "neutral.png";

                    switch (emotion) {
                        case "surprise":
                            filename = "surprise.png";
                            break;
                        case "joy":
                            filename = "happy.png";
                            break;
                        case "sadness":
                            filename = "sad.png";
                            break;
                        case "disgust":
                            filename = "disgust.png";
                            break;
                        case "anger":
                            filename = "anger.png";
                            break;
                        case "fear":
                            filename = "fear.png";
                            break;
                        default:
                            filename = "neutral.png";
                            break;
                    }

                    overlays.add(new Emoji(face.getInt("x"), face.getInt("y"), face.getInt("width"), face.getInt("width"), filename));
                }

                drawToBackground(overlays);
            }
        }catch(Exception e){
        }
    }
}