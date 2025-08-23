package game.client.rendering.renderer;

import engine.renderer.Shader;
import game.client.rendering.chunk.SimpleVertexBuilder;
import game.client.rendering.creature.CreatureVertexGenerator;
import game.client.rendering.creature.ItemCreatureVertexGenerator;
import game.client.rendering.creature.PlayerVertexGenerator;
import game.client.world.creature.ClientPlayer;
import game.shared.world.creature.Creature;
import game.shared.world.creature.ItemCreature;
import game.shared.world.creature.OtherPlayer;
import game.shared.world.creature.Player;
import org.joml.Matrix4f;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class CreatureRenderer {
    public int vaoId;
    public int vboId;

    public GameRenderer gameRenderer;

    public Shader shader;

    public HashMap<Class<? extends Creature>, CreatureVertexGenerator<?>> creatureToVertexGenerator = new HashMap<>();

    public CreatureRenderer(GameRenderer gameRenderer) {
        this.gameRenderer = gameRenderer;
        creatureToVertexGenerator.put(ClientPlayer.class, new PlayerVertexGenerator());
        creatureToVertexGenerator.put(OtherPlayer.class, new PlayerVertexGenerator());
        creatureToVertexGenerator.put(ItemCreature.class, new ItemCreatureVertexGenerator());
    }

    public void setup() {
        this.shader = new Shader("shaders/creature.vertex.glsl", "shaders/creature.fragment.glsl");
        this.vaoId = glGenVertexArrays();
        this.vboId = glGenBuffers();

        glBindVertexArray(this.vaoId);
        this.createVertexAttributes();

        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
        glBufferData(GL_ARRAY_BUFFER, (long) 10000 * Float.BYTES, GL_DYNAMIC_DRAW);
    }

    public void delete() {
        glDeleteVertexArrays(this.vaoId);
        glDeleteBuffers(this.vboId);
        this.shader.delete();
    }

    public void createVertexAttributes() {
        int stride = 5 * Float.BYTES;

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    public void render(Creature creature, double deltaTickTime, float light) {
        CreatureVertexGenerator<?> creatureVertexGenerator = creatureToVertexGenerator.get(creature.getClass());
        if(creatureVertexGenerator == null) return;

        SimpleVertexBuilder vertexBuilder = new SimpleVertexBuilder();
        creatureVertexGenerator.doRender(creature, vertexBuilder, deltaTickTime);
        if(vertexBuilder.data.isEmpty()) return;

        float[] data = vertexBuilder.compile(0,0,0);

        Matrix4f model = new Matrix4f();
        model.translate(
                (float) (creature.lastPosition.x + (creature.position.x - creature.lastPosition.x) * deltaTickTime),
                (float) (creature.lastPosition.y + (creature.position.y - creature.lastPosition.y) * deltaTickTime),
                (float) (creature.lastPosition.z + (creature.position.z - creature.lastPosition.z) * deltaTickTime)
        );

        this.shader.uploadMatrix4f("model", model);
        this.shader.uploadFloat("light", light);

        glBindVertexArray(this.vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);
        this.createVertexAttributes();
        glDrawArrays(GL_TRIANGLES, 0, data.length / 5);
    }
}