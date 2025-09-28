package engine.renderer;

public class NineSliceTexture extends Texture {
    public float top;
    public float bottom;
    public float left;
    public float right;

    public NineSliceTexture(String filePath, float top, float bottom, float left, float right) {
        super(filePath);
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }
}
