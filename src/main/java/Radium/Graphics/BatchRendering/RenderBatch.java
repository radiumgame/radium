package Radium.Graphics.BatchRendering;

import Radium.Graphics.Material;
import Radium.Graphics.Mesh;
import Radium.Math.Transform;

import java.util.ArrayList;
import java.util.List;

/**
 * A batch that can be used by the BatchRenderer
 */
public class RenderBatch {

    /**
     * Transforms of each object
     */
    public List<Transform> batchObjectTransforms = new ArrayList<>();
    /**
     * Mesh that each object uses
     */
    public Mesh mesh;
    /**
     * Material that renderer uses
     */
    public Material material;

    /**
     * Create batch with predefined parameters
     * @param batchObjectTransforms Transforms of each object
     * @param mesh Mesh used by renderer
     * @param texture Texture used in material
     */
    public RenderBatch(List<Transform> batchObjectTransforms, Mesh mesh, String texture) {
        this.batchObjectTransforms = batchObjectTransforms;
        this.mesh = mesh;
        this.material = new Material(texture);
    }

    /**
     * Adds object to the batch
     * @param transform Transform of new object
     */
    public void AddObject(Transform transform) {
        batchObjectTransforms.add(transform);
    }

}
