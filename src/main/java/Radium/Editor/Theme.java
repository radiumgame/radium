package Radium.Editor;

import Radium.Engine.Color.Color;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Theme {

    public Color textColor;
    public Color headerColor;
    public Color areaColor;
    public Color bodyColor;
    public Color tabColor;
    public Color popupColor;

    public Theme() {
        textColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        headerColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        areaColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        bodyColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        tabColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        popupColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public Theme(Color textColor, Color headerColor, Color areaColor, Color bodyColor, Color tabColor, Color popupColor) {
        this.textColor = textColor;
        this.headerColor = headerColor;
        this.areaColor = areaColor;
        this.bodyColor = bodyColor;
        this.tabColor = tabColor;
        this.popupColor = popupColor;
    }

    public static Theme Load(String content) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
            return mapper.readValue(content, Theme.class);
        } catch (Exception e) {
            Console.Error(e);
            return new Theme();
        }
    }

}
