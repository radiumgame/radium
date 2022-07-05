package Radium.Engine.ParticleSystem;

import Radium.Engine.Components.Particles.ParticleSystem;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public class ParticleBatch {

    public transient List<Particle> particles = new ArrayList<>();
    public transient Texture texture;

    public transient Mesh mesh;
    public transient GameObject obj;

    public transient ParticleSystem system;

    public ParticleBatch(ParticleSystem system, Texture texture) {
        this.system = system;
        this.texture = texture;

        this.mesh = Mesh.Plane(1, 1);
    }

    public void Update() {
        ParticleSorter.SortHighToLow(particles);

        if (particles.size() > system.maxParticles) {
            for (int i = 1; i <= particles.size() - system.maxParticles; i++) {
                particles.remove(particles.size() - i);
            }
        }
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).Update();
        }
    }

}
