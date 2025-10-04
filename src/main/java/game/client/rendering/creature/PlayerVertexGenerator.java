package game.client.rendering.creature;

import game.client.rendering.chunk.SimpleVertexBuilder;
import game.shared.world.creature.Player;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class PlayerVertexGenerator extends CreatureVertexGenerator<Player> {
    public static CubeUV headUV = new CubeUV(
            new CubeFaceUV(
                    new Vector2f(8 / 64F, 56 / 64F),
                    new Vector2f(16 / 64F,1)
            ),
            new CubeFaceUV(
                    new Vector2f(16 / 64F, 56 / 64F),
                    new Vector2f(24 / 64F,1)
            ),
            new CubeFaceUV(
                    new Vector2f(8 / 64F, 48 / 64F),
                    new Vector2f(16 / 64F,56 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(24 / 64F, 48 / 64F),
                    new Vector2f(32 / 64F,56 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(16 / 64F, 48 / 64F),
                    new Vector2f(24 / 64F,56 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(0, 48 / 64F),
                    new Vector2f(8 / 64F,56 / 64F)
            )
    );

    public static CubeUV torsoUV = new CubeUV(
            new CubeFaceUV(
                    new Vector2f(20 / 64F, 44 / 64F),
                    new Vector2f(28 / 64F,48 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(28 / 64F, 44 / 64F),
                    new Vector2f(36 / 64F,48 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(20 / 64F, 32 / 64F),
                    new Vector2f(28 / 64F,44 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(32 / 64F, 32 / 64F),
                    new Vector2f(40 / 64F,44 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(16 / 64F, 32 / 64F),
                    new Vector2f(20 / 64F,40 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(28 / 64F, 32 / 64F),
                    new Vector2f(32 / 64F,40 / 64F)
            )
    );

    public static CubeUV rightArmUV = new CubeUV(
            new CubeFaceUV(
                    new Vector2f(44 / 64F, 44 / 64F),
                    new Vector2f(48 / 64F,48 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(48 / 64F, 44 / 64F),
                    new Vector2f(52 / 64F,48 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(44 / 64F, 32 / 64F),
                    new Vector2f(48 / 64F,44 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(52 / 64F, 32 / 64F),
                    new Vector2f(56 / 64F,44 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(48 / 64F, 32 / 64F),
                    new Vector2f(52 / 64F,44 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(40 / 64F, 32 / 64F),
                    new Vector2f(44 / 64F,44 / 64F)
            )
    );

    public static CubeUV leftArmUV = new CubeUV(
            new CubeFaceUV(
                    new Vector2f(36 / 64F, 12 / 64F),
                    new Vector2f(40 / 64F,16 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(40 / 64F, 12 / 64F),
                    new Vector2f(44 / 64F,16 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(36 / 64F, 0),
                    new Vector2f(40 / 64F,12 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(44 / 64F, 0),
                    new Vector2f(48 / 64F,12 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(40 / 64F, 0),
                    new Vector2f(44 / 64F,12 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(32 / 64F, 0),
                    new Vector2f(36 / 64F,12 / 64F)
            )
    );

    public static CubeUV rightLegUV = new CubeUV(
            new CubeFaceUV(
                    new Vector2f(4 / 64F, 44 / 64F),
                    new Vector2f(8 / 64F,48 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(8 / 64F, 44 / 64F),
                    new Vector2f(12 / 64F,48 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(4 / 64F, 32 / 64F),
                    new Vector2f(8 / 64F,44 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(12 / 64F, 32 / 64F),
                    new Vector2f(16 / 64F,44 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(8 / 64F, 32 / 64F),
                    new Vector2f(12 / 64F,44 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(0, 32 / 64F),
                    new Vector2f(4 / 64F,44 / 64F)
            )
    );

    public static CubeUV leftLegUV = new CubeUV(
            new CubeFaceUV(
                    new Vector2f(20 / 64F, 12 / 64F),
                    new Vector2f(24 / 64F,16 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(24 / 64F, 12 / 64F),
                    new Vector2f(28 / 64F,16 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(20 / 64F, 0),
                    new Vector2f(24 / 64F,12 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(28 / 64F, 0),
                    new Vector2f(32 / 64F,12 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(24 / 64F, 0),
                    new Vector2f(28 / 64F,12 / 64F)
            ),
            new CubeFaceUV(
                    new Vector2f(16 / 64F, 0),
                    new Vector2f(20 / 64F,12 / 64F)
            )
    );

    @Override
    public void render(Player creature, SimpleVertexBuilder vertexBuilder, double deltaTickTime) {
        insertCube(vertexBuilder, new Vector3f(0F, 1.75F, 0F), new Vector3f(0.5F), headUV);
        insertCube(vertexBuilder, new Vector3f(0F, 1.125F, 0F), new Vector3f(0.25F, 0.75F, 0.5F), torsoUV);
        insertCube(vertexBuilder, new Vector3f(0F, 1.125F, -0.375F), new Vector3f(0.25F, 0.75F, 0.25F), rightArmUV);
        insertCube(vertexBuilder, new Vector3f(0F, 1.125F, 0.375F), new Vector3f(0.25F, 0.75F, 0.25F), leftArmUV);
        insertCube(vertexBuilder, new Vector3f(0F, 0.375F, 0.125F), new Vector3f(0.25F, 0.75F, 0.25F), leftLegUV);
        insertCube(vertexBuilder, new Vector3f(0F, 0.375F, -0.125F), new Vector3f(0.25F, 0.75F, 0.25F), rightLegUV);

        vertexBuilder.rotate((float) ((float) (creature.yaw + (creature.yaw - creature.lastYaw) * deltaTickTime) / 180 * Math.PI) * -1);

        creature.getSkin().texture.bind();
    }
}
