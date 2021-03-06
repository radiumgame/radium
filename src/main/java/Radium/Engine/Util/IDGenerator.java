package Radium.Engine.Util;

import Radium.Engine.Math.Random;

import java.util.ArrayList;
import java.util.List;

public class IDGenerator {

    private List<Integer> IDs = new ArrayList<>();
    private IDGenType genType;

    public IDGenerator() {
        genType = IDGenType.Random;
    }

    public IDGenerator(IDGenType type) {
        genType = type;
    }

    public int NewID() {
        if (genType == IDGenType.Order) {
            return IDs.size() + 1;
        }

        int id = Random.RandomInt(1, Integer.MAX_VALUE);
        if (IDs.contains(id)) {
            id = NewID();
        }
        IDs.add(id);

        return id;
    }

    public int IDCount() {
        return IDs.size();
    }

    public enum IDGenType {

        Random,
        Order,

    }

}
