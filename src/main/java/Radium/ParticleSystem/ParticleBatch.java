package Radium.ParticleSystem;

import Radium.Graphics.Mesh;

import java.util.ArrayList;
import java.util.List;

public class ParticleBatch {

    public List<Particle> particles = new ArrayList<>();
    public Mesh mesh;

    public ParticleBatch(Mesh mesh) {
        this.mesh = mesh;
    }

    public void Destroy() {
        particles.clear();
        mesh.DestroyMesh();
    }

}
