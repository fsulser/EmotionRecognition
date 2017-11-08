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
    private static final String key = "8205d2eceeee6a1d2054096a9f8fa6ef";
    private static final String appId = "2b2c56d5";

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
                for (int index = 0; index < faces.length(); index++) {
                    JSONObject face = faces.getJSONObject(index).getJSONObject("face");
                    System.out.println(face.toString());

                    JSONObject emotions = faces.getJSONObject(index).getJSONObject("emotions");

                    double max = 0.0;
                    int emotion = 0;
                    int j = 0;
                    for(Iterator iterator = emotions.keys(); iterator.hasNext();) {
                        String key = (String) iterator.next();
                        double actual = emotions.getDouble(key);
                        if(actual > max) {
                            max = actual;
                            emotion = j;
                        }
                        j++;
                    }

                    String file = "neutral.png";

                    if(emotion == 0){
                        //anger
                        file = "anger.png";
                    }else if (emotion == 1){
                        //disgust
                        file = "disgust.png";
                    }else if(emotion == 2){
                        //fear
                        file = "fear.png";
                    }else if(emotion == 3){
                        //joy
                        file = "happy.png";
                    }else if(emotion == 4){
                        //sadness
                        file = "sad.png";
                    }else if(emotion == 5){
                        //surprise
                        file = "surprise.png";
                    }

                    if(max == 0){
                        //all equal --> neutral
                        file = "neutral.png";
                    }

                    overlays.add(new Emoji(face.getInt("x"), face.getInt("y"), face.getInt("width"), face.getInt("width"), file));
                }

                drawToBackground(overlays);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}