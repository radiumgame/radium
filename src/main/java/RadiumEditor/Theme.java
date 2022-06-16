package RadiumEditor;

import Radium.Color.Color;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Theme {

    public Color textColor;
    public Color headerColor;
    public Color areaColor;
    public Color bodyColor;
    public Color tabColor;
    public Color popupColor;

    public Theme(Color textColor, Color headerColor, Color areaColor, Color bodyColor, Color tabColor, Color popupColor) {
        this.textColor = textColor;
        this.headerColor = headerColor;
        this.areaColor = areaColor;
        this.bodyColor = bodyColor;
        this.tabColor = tabColor;
        this.popupColor = popupColor;
    }

    public static Theme Load(String content) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(content, Theme.class);
    }

}
