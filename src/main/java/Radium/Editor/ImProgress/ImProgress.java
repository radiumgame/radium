package Radium.Editor.ImProgress;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;

public class ImProgress {

    protected ImProgress() {}

    public static void renderProgressBar(ImProgressBar bar, float width) {
        ImDrawList list = ImGui.getWindowDrawList();
        ImVec2 position = ImGui.getCursorScreenPos();
        float height = 10;
        float rounding = 30;

        list.addRectFilled(position.x, position.y, position.x + width, position.y + height, ImColor.floatToColor(0.2f, 0.2f, 0.2f, 1.0f), rounding);
        list.addRectFilled(position.x, position.y, position.x + (width * bar.progress), position.y + height, ImColor.floatToColor(0.65f, 0.65f, 0.65f, 1.0f), rounding);

        int roundedProgress = Math.round(bar.progress * 100.0f);
        list.addText(position.x + width + 5, position.y, ImColor.floatToColor(0.9f, 0.9f, 0.9f, 1.0f), roundedProgress + "%");

        ImGui.setCursorPosY(position.y + 20);
    }

}
