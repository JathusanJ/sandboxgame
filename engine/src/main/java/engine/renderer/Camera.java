package engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix;
    public Vector3f position;

    public float pitch = 0F; // Rotation on the x-axis
    public float yaw = 0F;
    public float sensitivity = 0.1F;
    public float fov = 70;

    public Camera() {
        this(new Vector3f(0,0,0));
    }

    public Camera(Vector3f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
    }

    public void perspectiveProjection(float width, float height, float fov) {
        this.projectionMatrix.identity();
        this.projectionMatrix.perspective((float) Math.toRadians(fov), width / height, 0.1F, 1024F);
        this.fov = fov;
    }

    public void orthographicProjection(float width, float height){
        this.projectionMatrix.identity();
        this.projectionMatrix.ortho(0.0F, width, 0.0F, height, 0.0F, 100.0F);
    }

    public Matrix4f getViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.lookAt(this.position, this.position.add(this.getFront(), new Vector3f()), this.getUp());

        return viewMatrix;
    }

    public Matrix4f getViewMatrixLookOnly() {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.lookAt(new Vector3f(), this.getFront(), this.getUp());

        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Vector3f getDirection() {
        return new Vector3f(
                (float) (Math.cos(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch))),
                (float) Math.sin(Math.toRadians(this.pitch)),
                (float) (Math.sin(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch)))
        );
    }

    public Vector3f getFront() {
        return this.getDirection().normalize();
    }

    public Vector3f getRight() {
        return this.getFront().cross(0,1F,0, new Vector3f()).normalize();
    }

    public Vector3f getUp() {
        return this.getRight().cross(this.getFront(), new Vector3f()).normalize();
    }

    public void mouseMovement(double offsetX, double offsetY) {
        this.yaw = this.yaw + (float) offsetX * this.sensitivity;
        this.pitch = this.pitch + (float) offsetY * this.sensitivity;

        if(this.pitch > 89F) {
            this.pitch = 89F;
        } else if(this.pitch < -89F) {
            this.pitch = -89F;
        }
    }
}
