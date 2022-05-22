package Radium.ParticleSystem;

import Radium.Graphics.Mesh;
import Radium.Graphics.Texture;
import Radium.Graphics.Vertex;
import Radium.Objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public class ParticleBatch {

    public transient List<Particle> particles = new ArrayList<>();
    public transient Texture texture;

    public transient Mesh mesh;
    public transient GameObject obj;

    public ParticleBatch(Texture texture) {
        this.texture = texture;

        this.mesh = Mesh.Plane(1, 1);
    }

    public void Update() {
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).Update();
        }
    }

}
