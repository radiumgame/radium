package Radium.Engine.Graphics.Shader.Type;

import Radium.Engine.Color.Color;
import Radium.Engine.Graphics.Lighting.LightType;
import Radium.Engine.Math.Vector.Vector3;

public class ShaderLight {

    public Vector3 position = Vector3.Zero();
    public Color color = new Color(1f, 1f, 1f, 1f);
    public float intensity = 1f;
    public float attenuation = 0.045f;
    public LightType lightType = LightType.Point;

}
