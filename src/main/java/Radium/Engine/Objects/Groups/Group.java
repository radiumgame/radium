package Radium.Engine.Objects.Groups;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Group {

    public String name;
    @JsonIgnore
    public int index;
    @JsonIgnore
    protected boolean exists;

    public static Group Default = Default();

    protected Group(String name) {
        this.name = name;
        this.exists = Groups.AddGroup(this);
    }

    public static Group CreateGroup(String name) {
        Group group = new Group(name);
        if (group.exists) {
            return Groups.GetGroup(name);
        }

        return group;
    }

    protected static Group Default() {
        return new Group("Default Layer");
    }

}
