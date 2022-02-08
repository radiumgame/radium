package Radium.ParticleSystem;

import Radium.Graphics.Material;
import Radium.Graphics.Mesh;

import java.util.ArrayList;
import java.util.List;

/**
 * Efficient batch for rendering particles
 */
public class ParticleBatch {

    /**
     * Particles in batch
     */
    public List<Particle> particles = new ArrayList<>();
    /**
     * Particle mesh
     */
    public Mesh mesh;
    /**
     * Material of mesh
     */
    public Material material;

    /**
     * Create batch with a mesh
     * @param mesh Particle mesh
     */
    public ParticleBatch(Mesh mesh) {
        this.mesh = mesh;

        this.material = new Material("EngineAssets/Textures/Misc/blank.jpg");
    }

    /**
     * Clears the particle batch and destroys mesh
     */
    public void Destroy() {
        particles.clear();
        mesh.Destroy();
        material.DestroyMaterial();
    }

}
