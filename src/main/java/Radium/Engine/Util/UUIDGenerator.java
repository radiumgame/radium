package Radium.Engine.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDGenerator {

    private List<String> ids = new ArrayList<>();

    public String NewUUID() {
        String uuid = UUID.randomUUID().toString();
        if (ids.contains(uuid)) {
            return NewUUID();
        }

        ids.add(uuid);
        return uuid;
    }

}
