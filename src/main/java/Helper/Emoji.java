package Helper;

public class Emoji {

    private int X = 0;
    private int Y = 0;
    private int width = 0;
    private int height = 0;
    private String file = "";

    public Emoji(int x, int y, int w, int h, String emoji){
        X = x;
        Y = y;
        width = w;
        height = h;
        file = emoji;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getFile() {
        return file;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    @Override
    public String toString() {
        return "Emoji{" +
                "X=" + X +
                ", Y=" + Y +
                ", width=" + width +
                ", height=" + height +
                ", file='" + file + '\'' +
                '}';
    }
}
