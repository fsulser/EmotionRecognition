package panels;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

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

	public void detectFaces() throws Exception {

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

			BufferedImage image = ImageIO.read(new File("test.jpg"));

			this.setImage(image);
			ArrayList<Integer[]> overlays = new ArrayList<Integer[]>();
			
			//get all recognized faces
			for (int i = 0; i < json.length(); i++) {
				JSONObject object = json.getJSONObject(i);
				//contains bounding box of faces
				JSONObject faceRectangle = object.getJSONObject("faceRectangle");
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
				for(Iterator iterator = scores.keys(); iterator.hasNext();) {
				    String key = (String) iterator.next();
				    double actual = scores.getDouble(key);
				    if(actual > max) {
				    	max = actual;
				    	emotion = j;
				    }
				    j++;
				}

				overlays.add(new Integer[] { faceRectangle.getInt("left"), faceRectangle.getInt("top"),
						faceRectangle.getInt("width"), faceRectangle.getInt("height"), emotion });
			}

			drawToBackground(overlays);
		}
	}

}