package Radium.ParticleSystem;

import Radium.Graphics.Material;
import Radium.Graphics.Mesh;

import java.util.ArrayList;
import java.util.List;

public class ParticleBatch {

    public List<Particle> particles = new ArrayList<>();
    public Mesh mesh;
    public Material material;

    public ParticleBatch(Mesh mesh) {
        this.mesh = mesh;

        this.material = new Material("EngineAssets/Textures/Misc/blank.jpg");
    }

    public void Destroy() {
        particles.clear();
        mesh.DestroyMesh();
    }

}
