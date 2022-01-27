package Radium.Graphics.BatchRendering;

import Radium.Graphics.Material;
import Radium.Graphics.Mesh;
import Radium.Math.Transform;

import java.util.ArrayList;
import java.util.List;

public class RenderBatch {

    public List<Transform> batchObjectTransforms = new ArrayList<>();
    public Mesh mesh;
    public Material material;

    public RenderBatch() {

    }

    public RenderBatch(List<Transform> batchObjectTransforms, Mesh mesh, String texture) {
        this.batchObjectTransforms = batchObjectTransforms;
        this.mesh = mesh;
        this.material = new Material(texture);
    }

    public void AddObject(Transform transform) {
        batchObjectTransforms.add(transform);
    }

}
