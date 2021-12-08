package Radium.Graphics.BatchRendering;

import Radium.Graphics.Mesh;
import Radium.Math.Transform;

import java.util.ArrayList;
import java.util.List;

public class RenderBatch {

    public List<Transform> batchObjectTransforms = new ArrayList<>();
    public Mesh mesh;

    public RenderBatch() {

    }

    public RenderBatch(List<Transform> batchObjectTransforms, Mesh mesh) {
        this.batchObjectTransforms = batchObjectTransforms;
        this.mesh = mesh;
    }

    public void AddObject(Transform transform) {
        batchObjectTransforms.add(transform);
    }

}
