package Radium.ParticleSystem;

import Radium.Graphics.Mesh;
import Radium.Graphics.Texture;
import Radium.Objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public class ParticleBatch {

    public List<Particle> particles = new ArrayList<>();
    public Texture texture;

    public Mesh mesh;
    public GameObject obj;

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
