package panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	private static final long serialVersionUID = 1L;

	static final String url = "https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize";
	static final String key1 = "8be954144c48494bb9adb738567e8f9c";
	static final String key2 = "9cae81ef93fc4184b00673a4193ae007";

	private BufferedImage backgroundImg;

	public void setImage(BufferedImage image) throws IOException {
		BufferedImage bImg = ImageIO.read(new File("test.jpg"));
		System.out.println(bImg.getWidth() + " , " + bImg.getHeight());
		backgroundImg = new BufferedImage(bImg.getWidth(), bImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = backgroundImg.getGraphics();
		g.drawImage(bImg, 0, 0, this);
		g.dispose();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (backgroundImg != null) {
			Image scaledImage = backgroundImg.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
			g.drawImage(scaledImage, 0, 0, null);
		}
	}

	public void drawToBackground(ArrayList<Integer[]> overlays) {
		for (Integer[] overlay : overlays) {
			try {
				// "contempt"
				// "surprise"
				// "happiness"
				// "neutral"
				// "sadness"
				// "disgust"
				// "anger"
				// "fear"
				String filename = "";		
				switch (overlay[4]) {
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
				
				
				Graphics g = backgroundImg.getGraphics();
				g.setColor(Color.red);
				BufferedImage bImg;
				bImg = ImageIO.read(new File(filename));
				g.drawImage(bImg, overlay[0], overlay[1], overlay[2], overlay[3], null);
				g.dispose();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		repaint();
	}
	
}
