package Radium.Engine.Objects.Groups;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Groups {

    public static HashMap<String, Group> Groups = new HashMap<>();
    public static String[] GroupList = new String[0];

    public static boolean AddGroup(String name) {
        return AddGroup(new Group(name));
    }

    public static boolean AddGroup(Group group) {
        boolean exists = false;
        exists = Groups.containsKey(group.name);
        group.index = Groups.size();
        if (!exists) Groups.put(group.name, group);

        return exists;
    }

    public static Group GetGroup(String name) {
        return Groups.get(name);
    }

    public static String AddGroupID = "Add Group...##1431241234";
    public static String[] GetGroupList() {
        String[] groups = Groups.keySet().toArray(new String[0]);
        String[] result = new String[groups.length + 1];
        for (Group group : Groups.values()) {
            result[group.index] = group.name;
        }
        result[result.length - 1] = AddGroupID;

        return result;
    }

}
