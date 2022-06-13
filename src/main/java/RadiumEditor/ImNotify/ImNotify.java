package RadiumEditor.ImNotify;

import Radium.Window;
import imgui.*;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.List;

public class ImNotify {

    private static final List<ImNotification> notificationList = new ArrayList<>();

    private static ImFont bigFont;

    public static void initialize(ImFont largeFont) {
        bigFont = largeFont;
    }

    public static void notify(String title, String content) {
        notificationList.add(new ImNotification(title, content, 4.0f));
    }

    public static void notify(ImNotification notification) {
        notificationList.add(notification);
    }

    public static void deleteNotification(ImNotification notification) {
        notificationList.remove(notification);
    }

    private static final int flags = ImGuiWindowFlags.NoBackground |
            ImGuiWindowFlags.NoTitleBar |
            ImGuiWindowFlags.NoMove |
            ImGuiWindowFlags.NoResize |
            ImGuiWindowFlags.NoInputs |
            ImGuiWindowFlags.NoMouseInputs |
            ImGuiWindowFlags.NoScrollbar |
            ImGuiWindowFlags.NoNav;
    public static void renderNotifications() {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(1920, 1080);
        ImGui.begin("##NOTIFICATION_WINDOW", flags);

        ImDrawList list = ImGui.getWindowDrawList();
        for (int i = 0; i < notificationList.size(); i++) {
            float firstOpacity = notificationList.get(0).opacity;
            float beforePosition = Window.height - 100;
            if (i != 0) {
                beforePosition = Window.height - 100 - ((i - 1) * 100);
            }

            ImNotification notification = notificationList.get(i);
            notification.update();

            ImVec2 textSize = new ImVec2();
            ImGui.calcTextSize(textSize, notification.content);
            float width = textSize.x + 50;
            width = Math.max(width, 450);
            float yPos = lerp(beforePosition, Window.height - 100 - (i * 100), firstOpacity);

            list.addRectFilled(Window.width - width - 50, yPos, Window.width - 10, yPos + 90, ImColor.floatToColor(notification.backgroundColor[0], notification.backgroundColor[1], notification.backgroundColor[2], notification.opacity), 30);
            list.addImage(notification.icon, Window.width - width - 40, yPos + 20, Window.width - width + 10, yPos + 70);

            ImGui.pushFont(bigFont);
            list.addText(Window.width - width + 15, yPos + 5, ImColor.floatToColor(notification.textColor[0], notification.textColor[1], notification.textColor[2], notification.opacity), notification.title);
            ImGui.popFont();

            list.addText(Window.width - width + 20, yPos + 35, ImColor.floatToColor(notification.textColor[0], notification.textColor[1], notification.textColor[2], notification.opacity), notification.content);
        }

        ImGui.end();
    }

    private static float lerp(float one, float two, float t) {
        return one + (two - one) * t;
    }

}
