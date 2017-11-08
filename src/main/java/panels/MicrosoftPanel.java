package panels;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

import Helper.Emoji;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class MicrosoftPanel extends ImagePanel {
    private static final String url = "https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize";
    private static final String key1 = "8be954144c48494bb9adb738567e8f9c";

    public MicrosoftPanel(){
        super("azure.png", 153);
    }

	public void detectFaces() {
		try {
			HttpClient httpclient = HttpClients.createDefault();

			URIBuilder uriBuilder = new URIBuilder(url);

			URI uri = uriBuilder.build();
			HttpPost request = new HttpPost(uri);

			request.setHeader("Content-Type", "application/octet-stream");
			request.setHeader("Ocp-Apim-Subscription-Key", key1);

			FileEntity fileEnt = new FileEntity(new File("test.jpg"));

			request.setEntity(fileEnt);
			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				String jsonString = EntityUtils.toString(entity);
				JSONArray json = new JSONArray(jsonString);

				ArrayList<Emoji> overlays = new ArrayList<>();
				//get all recognized faces
                System.out.println("MICROSOFT");
				for (int i = 0; i < json.length(); i++) {
					JSONObject object = json.getJSONObject(i);
					//contains bounding box of faces
					JSONObject faceRectangle = object.getJSONObject("faceRectangle");
                    System.out.println(faceRectangle.toString());

					//containes scores of face emotions
					JSONObject scores = object.getJSONObject("scores");


					// "anger": 0.00300731952,
					// "contempt": 5.14648448E-08,
					// "disgust": 9.180124E-06,
					// "fear": 0.0001912825,
					// "happiness": 0.9875571,
					// "neutral": 0.0009861537,
					// "sadness": 1.889955E-05,
					// "surprise": 0.008229999
					double max = 0.0;
					int emotion = 0;
					int j = 0;
					for (Iterator iterator = scores.keys(); iterator.hasNext(); ) {
						String key = (String) iterator.next();
						double actual = scores.getDouble(key);
						if (actual > max) {
							max = actual;
							emotion = j;
						}
						j++;
					}

					String filename = "neutral.png";
					switch (emotion) {
						case 0:
							filename = "contempt.png";
							break;
						case 1:
							filename = "surprise.png";
							break;
						case 2:
							filename = "happy.png";
							break;
						case 3:
							filename = "neutral.png";
							break;
						case 4:
							filename = "sad.png";
							break;
						case 5:
							filename = "disgust.png";
							break;
						case 6:
							filename = "anger.png";
							break;
						case 7:
							filename = "fear.png";
							break;

						default:
							break;
					}


					overlays.add(new Emoji(faceRectangle.getInt("left"), faceRectangle.getInt("top"), faceRectangle.getInt("width"), faceRectangle.getInt("height"), filename));
				}

				drawToBackground(overlays);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}