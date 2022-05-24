package Radium.ParticleSystem;

import Radium.Graphics.Mesh;
import Radium.Graphics.Texture;
import Radium.Graphics.Vertex;
import Radium.Objects.GameObject;

import java.util.ArrayList;
import java.util.Collections;
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
        // Sort
        Collections.sort(particles, (p1, p2) -> {
            float dist1 = p1.CalculateDistance();
            float dist2 = p2.CalculateDistance();

            if (dist1 < dist2) {
                return -1;
            } else if (dist1 > dist2) {
                return 1;
            } else {
                return 0;
            }
        });

        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).Update();
        }
    }

}
