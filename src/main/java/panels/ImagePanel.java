package panels;

import Helper.Emoji;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private BufferedImage backgroundImg;
	private String vendorImage = "";
	private int vendorX = 0;
	private int vendorY = 0;
	private int vendorWidth = 0;
	private int vendorHeight = 0;

	public ImagePanel(String vendor, int X, int Y, int width, int height){
	    this.vendorImage = vendor;
	    this.vendorX = X;
	    this.vendorY = Y;
	    this.vendorWidth = width;
	    this.vendorHeight = height;
    }

	public void setImage(BufferedImage image) throws IOException {
		BufferedImage bImg = ImageIO.read(new File("test.jpg"));
		System.out.println(bImg.getWidth() + " , " + bImg.getHeight());
		backgroundImg = new BufferedImage(bImg.getWidth(), bImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = backgroundImg.getGraphics();
		g.drawImage(bImg, 0, 0, this);

        BufferedImage vendorImg = ImageIO.read(new File(vendorImage));
        g.drawImage(vendorImg, vendorX, vendorY, vendorWidth, vendorHeight, null);
		g.dispose();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		super.paintComponent(g2);
		if (backgroundImg != null) {
			Image scaledImage = backgroundImg.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
			g2.drawImage(scaledImage, 0, 0, null);
		}
	}

	public void drawToBackground(ArrayList<Emoji> emojis) {
		try {
			for (Emoji emoji: emojis) {
                Graphics g = backgroundImg.getGraphics();
                g.setColor(Color.white);
				BufferedImage emojiImg = ImageIO.read(new File(emoji.getFile()));
				g.drawImage(emojiImg, emoji.getX(), emoji.getY(), emoji.getWidth(), emoji.getHeight(), null);
                g.dispose();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		repaint();
	}
	
}
