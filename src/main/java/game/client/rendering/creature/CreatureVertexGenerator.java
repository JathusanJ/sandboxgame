package game.client.rendering.creature;

import game.client.rendering.chunk.SimpleVertexBuilder;
import game.shared.world.creature.Creature;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class CreatureVertexGenerator<T extends Creature> {
    // Little workaround to cast and not have to cast it manually in every implementation of this class
    @SuppressWarnings("unchecked")
    public void doRender(Creature creature, SimpleVertexBuilder vertexBuilder, double deltaTickTicks) {
        this.render((T) creature, vertexBuilder, deltaTickTicks);
    }

    public abstract void render(T creature, SimpleVertexBuilder vertexBuilder, double deltaTickTime);

    public static void insertVertices(SimpleVertexBuilder simpleVertexBuilder, Vector4f corner1, Vector4f corner2, Vector4f corner3, Vector4f corner4) {
        simpleVertexBuilder.vertex(corner1.x, corner1.y, corner1.z, 0, 0);
        simpleVertexBuilder.vertex(corner2.x, corner2.y, corner2.z, 1, 0);
        simpleVertexBuilder.vertex(corner3.x, corner3.y, corner3.z, 1, 1);

        simpleVertexBuilder.vertex(corner3.x, corner3.y, corner3.z, 1, 1);
        simpleVertexBuilder.vertex(corner4.x, corner4.y, corner4.z, 0, 1);
        simpleVertexBuilder.vertex(corner1.x, corner1.y, corner1.z, 0, 0);
    }

    public static void insertCube(SimpleVertexBuilder builder, Vector3f center, Vector3f size, CubeUV uv) {
        // Top side
        builder.vertex(center.x + size.x / 2, center.y + size.y / 2, center.z + size.z / 2, uv.top.bottomLeft.x, uv.top.bottomLeft.y);
        builder.vertex(center.x + size.x / 2, center.y + size.y / 2, center.z - size.z / 2, uv.top.topRight.x, uv.top.bottomLeft.y);
        builder.vertex(center.x - size.x / 2, center.y + size.y / 2, center.z + size.z / 2, uv.top.bottomLeft.x, uv.top.topRight.y);

        builder.vertex(center.x + size.x / 2, center.y + size.y / 2, center.z - size.z / 2, uv.top.topRight.x, uv.top.bottomLeft.y);
        builder.vertex(center.x - size.x / 2, center.y + size.y / 2, center.z - size.z / 2, uv.top.topRight.x, uv.top.topRight.y);
        builder.vertex(center.x - size.x / 2, center.y + size.y / 2, center.z + size.z / 2, uv.top.bottomLeft.x, uv.top.topRight.y);

        // Bottom side
        builder.vertex(center.x - size.x / 2, center.y - size.y / 2, center.z + size.z / 2, uv.bottom.bottomLeft.x, uv.bottom.bottomLeft.y);
        builder.vertex(center.x - size.x / 2, center.y - size.y / 2, center.z - size.z / 2, uv.bottom.topRight.x, uv.bottom.bottomLeft.y);
        builder.vertex(center.x + size.x / 2, center.y - size.y / 2, center.z + size.z / 2, uv.bottom.bottomLeft.x, uv.bottom.topRight.y);

        builder.vertex(center.x - size.x / 2, center.y - size.y / 2, center.z - size.z / 2, uv.bottom.topRight.x, uv.bottom.bottomLeft.y);
        builder.vertex(center.x + size.x / 2, center.y - size.y / 2, center.z - size.z / 2, uv.bottom.topRight.x, uv.bottom.topRight.y);
        builder.vertex(center.x + size.x / 2, center.y - size.y / 2, center.z + size.z / 2, uv.bottom.bottomLeft.x, uv.bottom.topRight.y);

        // Front side
        builder.vertex(center.x + size.x / 2, center.y - size.y / 2, center.z + size.z / 2, uv.front.bottomLeft.x, uv.front.bottomLeft.y);
        builder.vertex(center.x + size.x / 2, center.y - size.y / 2, center.z - size.z / 2, uv.front.topRight.x, uv.front.bottomLeft.y);
        builder.vertex(center.x + size.x / 2, center.y + size.y / 2, center.z + size.z / 2, uv.front.bottomLeft.x, uv.front.topRight.y);

        builder.vertex(center.x + size.x / 2, center.y - size.y / 2, center.z - size.z / 2, uv.front.topRight.x, uv.front.bottomLeft.y);
        builder.vertex(center.x + size.x / 2, center.y + size.y / 2, center.z - size.z / 2, uv.front.topRight.x, uv.front.topRight.y);
        builder.vertex(center.x + size.x / 2, center.y + size.y / 2, center.z + size.z / 2, uv.front.bottomLeft.x, uv.front.topRight.y);

        // Back side
        builder.vertex(center.x - size.x / 2, center.y - size.y / 2, center.z - size.z / 2, uv.back.bottomLeft.x, uv.back.bottomLeft.y);
        builder.vertex(center.x - size.x / 2, center.y - size.y / 2, center.z + size.z / 2, uv.back.topRight.x, uv.back.bottomLeft.y);
        builder.vertex(center.x - size.x / 2, center.y + size.y / 2, center.z - size.z / 2, uv.back.bottomLeft.x, uv.back.topRight.y);

        builder.vertex(center.x - size.x / 2, center.y - size.y / 2, center.z + size.z / 2, uv.back.topRight.x, uv.back.bottomLeft.y);
        builder.vertex(center.x - size.x / 2, center.y + size.y / 2, center.z + size.z / 2, uv.back.topRight.x, uv.back.topRight.y);
        builder.vertex(center.x - size.x / 2, center.y + size.y / 2, center.z - size.z / 2, uv.back.bottomLeft.x, uv.back.topRight.y);

        // Right side
        builder.vertex(center.x + size.x / 2, center.y - size.y / 2, center.z - size.z / 2, uv.right.bottomLeft.x, uv.right.bottomLeft.y);
        builder.vertex(center.x - size.x / 2, center.y - size.y / 2, center.z - size.z / 2, uv.right.topRight.x, uv.right.bottomLeft.y);
        builder.vertex(center.x + size.x / 2, center.y + size.y / 2, center.z - size.z / 2, uv.right.bottomLeft.x, uv.right.topRight.y);

        builder.vertex(center.x - size.x / 2, center.y - size.y / 2, center.z - size.z / 2, uv.right.topRight.x, uv.right.bottomLeft.y);
        builder.vertex(center.x - size.x / 2, center.y + size.y / 2, center.z - size.z / 2, uv.right.topRight.x, uv.right.topRight.y);
        builder.vertex(center.x + size.x / 2, center.y + size.y / 2, center.z - size.z / 2, uv.right.bottomLeft.x, uv.right.topRight.y);

        // Left side
        builder.vertex(center.x - size.x / 2, center.y - size.y / 2, center.z + size.z / 2, uv.left.bottomLeft.x, uv.left.bottomLeft.y);
        builder.vertex(center.x + size.x / 2, center.y - size.y / 2, center.z + size.z / 2, uv.left.topRight.x, uv.left.bottomLeft.y);
        builder.vertex(center.x - size.x / 2, center.y + size.y / 2, center.z + size.z / 2, uv.left.bottomLeft.x, uv.left.topRight.y);

        builder.vertex(center.x + size.x / 2, center.y - size.y / 2, center.z + size.z / 2, uv.left.topRight.x, uv.left.bottomLeft.y);
        builder.vertex(center.x + size.x / 2, center.y + size.y / 2, center.z + size.z / 2, uv.left.topRight.x, uv.left.topRight.y);
        builder.vertex(center.x - size.x / 2, center.y + size.y / 2, center.z + size.z / 2, uv.left.bottomLeft.x, uv.left.topRight.y);
    }

    public record CubeUV(CubeFaceUV top, CubeFaceUV bottom, CubeFaceUV front, CubeFaceUV back, CubeFaceUV right, CubeFaceUV left) {}

    public record CubeFaceUV(Vector2f bottomLeft, Vector2f topRight) {}
}
