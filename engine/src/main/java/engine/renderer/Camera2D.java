package engine.renderer;

import org.joml.Vector3f;

public class Camera2D extends Camera {

    @Override
    public Vector3f getFront() {
        return new Vector3f(0, 0, -1F);
    }
}
