package game.client.rendering.creature;

import game.client.rendering.chunk.SimpleVertexBuilder;
import game.logic.world.creature.Creature;
import org.joml.Vector4f;

public abstract class CreatureVertexGenerator<T extends Creature> {
    // Little workaround to cast and not have to cast it manually in every implementation of this class
    @SuppressWarnings("unchecked")
    public void doRender(Creature creature, SimpleVertexBuilder vertexBuilder, double deltaTickTicks) {
        this.render((T) creature, vertexBuilder, deltaTickTicks);
    }

    public abstract void render(T creature, SimpleVertexBuilder vertexBuilder, double deltaTickTime);

    protected void insertVertices(SimpleVertexBuilder simpleVertexBuilder, Vector4f corner1, Vector4f corner2, Vector4f corner3, Vector4f corner4) {
        simpleVertexBuilder.vertex(corner1.x, corner1.y, corner1.z, 0, 0);
        simpleVertexBuilder.vertex(corner2.x, corner2.y, corner2.z, 1, 0);
        simpleVertexBuilder.vertex(corner3.x, corner3.y, corner3.z, 1, 1);

        simpleVertexBuilder.vertex(corner3.x, corner3.y, corner3.z, 1, 1);
        simpleVertexBuilder.vertex(corner4.x, corner4.y, corner4.z, 0, 1);
        simpleVertexBuilder.vertex(corner1.x, corner1.y, corner1.z, 0, 0);
    }
}
