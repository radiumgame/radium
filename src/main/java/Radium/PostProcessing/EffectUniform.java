package Radium.PostProcessing;

import java.util.UUID;

public class EffectUniform {

    public String name = "col";
    public Class type = Integer.class;
    public Object value;

    public transient String id = UUID.randomUUID().toString();

}
