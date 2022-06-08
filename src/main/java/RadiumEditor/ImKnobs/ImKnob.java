package RadiumEditor.ImKnobs;

import imgui.ImGui;
import imgui.ImVec2;

public class ImKnob {

    public float radius;
    public float value = 0.00f;
    public boolean value_changed;
    public ImVec2 center;
    public boolean is_active;
    public boolean is_hovered;
    public float angle_min;
    public float angle_max;
    public float t;
    public float angle;
    public float angle_cos;
    public float angle_sin;

    public ImKnob(String _label, int data_type, float p_value, float v_min, float v_max, float speed, float _radius, String format, int flags) {
        radius = _radius;
        t = (p_value - v_min) / (v_max - v_min);
        ImVec2 screen_pos = ImGui.getCursorScreenPos();

        // Handle dragging
        ImGui.invisibleButton(_label, radius * 2.0f, radius * 2.0f);
        int gid = ImGui.getID(_label);
        int drag_flags = 0;

        //value_changed = ImGui::DragBehavior(gid, data_type, p_value, speed, &v_min, &v_max, format, drag_flags);

        angle_min = ImKnobs.IMGUIKNOBS_PI * 0.75f;
        angle_max = ImKnobs.IMGUIKNOBS_PI * 2.25f;
        center = new ImVec2(screen_pos.x + radius, screen_pos.y + radius);
        is_active = ImGui.isItemActive();
        is_hovered = ImGui.isItemHovered();
        angle = angle_min + (angle_max - angle_min) * t;
        angle_cos = (float)Math.cos(angle);
        angle_sin = (float)Math.sin(angle);
    }

    void drawDot(float size, float radius, float angle, int color, boolean filled, int segments) {
        float dot_size = size * this.radius;
        float dot_radius = radius * this.radius;

        ImGui.getWindowDrawList().addCircleFilled(
                center.x + (float)Math.cos(angle) * dot_radius, center.y + (float)Math.sin(angle) * dot_radius,
                dot_size,
                color,
                segments);
    }

    void drawTick(float start, float end, float width, float angle, int color) {
        float tick_start = start * radius;
        float tick_end = end * radius;
        float angle_cos = (float)Math.cos(angle);
        float angle_sin = (float)Math.sin(angle);

        ImGui.getWindowDrawList().addLine(
                center.x + angle_cos * tick_end, center.y + angle_sin * tick_end,
                center.x + angle_cos * tick_start, center.y + angle_sin * tick_start,
                color,
                width * radius);
    }

    void drawCircle(float size, int color, boolean filled, int segments) {
        float circle_radius = size * radius;

        ImGui.getWindowDrawList().addCircleFilled(
                center.x, center.y,
                circle_radius,
                color);
    }

    void drawArc(float radius, float size, float start_angle, float end_angle, int color, int segments, int bezier_count) {
        float track_radius = radius * this.radius;
        float track_size = size * this.radius * 0.5f + 0.0001f;

        ImKnobs.drawArc(
                center,
                track_radius,
                start_angle,
                end_angle,
                track_size,
                color,
                segments,
                bezier_count);
    }

}
