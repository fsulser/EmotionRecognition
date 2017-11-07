package panels;


import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;


public class KairosPanel extends ImagePanel {
    private static final String url = "https://api.kairos.com/v2/media";
    private static final String key = "XXXXXXXXXXXX";
    private static final String appId = "XXXXXXX";

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

            ArrayList<Integer[]> overlays = new ArrayList<Integer[]>();
            if (resultEntity != null) {
                String jsonString = EntityUtils.toString(resultEntity);
                JSONObject jsonResult = new JSONObject(jsonString);
                JSONArray faces = jsonResult.getJSONArray("frames").getJSONObject(0).getJSONArray("people");

                for (int index = 0; index < faces.length(); index++) {
                    JSONObject face = faces.getJSONObject(index).getJSONObject("face");

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


                    if(emotion == 0){
                        //anger
                        emotion = 6;
                    }else if (emotion == 1){
                        //disgust
                        emotion = 5;
                    }else if(emotion == 2){
                        //fear
                        emotion = 7;
                    }else if(emotion == 3){
                        //joy
                        emotion = 2;
                    }else if(emotion == 4){
                        //sadness
                        emotion = 4;
                    }else if(emotion == 5){
                        //surprise
                        emotion = 1;
                    }

                    if(max == 0){
                        //all equal --> neutral
                        emotion = 3;
                    }

                    System.out.println(emotions.toString());

                    overlays.add(new Integer[]{face.getInt("x"), face.getInt("y"), face.getInt("width"), face.getInt("width"), emotion});

                }

                drawToBackground(overlays);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}