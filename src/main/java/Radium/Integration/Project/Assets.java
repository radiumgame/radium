package Radium.Integration.Project;

import java.util.ArrayList;
import java.util.List;

public class Assets {

    protected Assets() {}

    public static List<FileEvent> eventsToCall = new ArrayList<>();

    public static void Update() {
        if (eventsToCall.size() > 0) {
            for (int i = 0; i < eventsToCall.size(); i++) {
                eventsToCall.get(i).Call();
            }

            eventsToCall.clear();
        }
    }

}
