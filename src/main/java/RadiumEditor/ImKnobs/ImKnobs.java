// https://github.com/altschuler/imgui-knobs

package RadiumEditor.ImKnobs;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiSliderFlags;
import imgui.type.ImFloat;

public class ImKnobs {

    public static float IMGUIKNOBS_PI = 3.14159265358979323846f;

    private static int Primary = ImColor.floatToColor(1.0f, 1.0f, 1.0f, 1.0f);
    private static int Secondary = ImColor.floatToColor(0.0f, 0.0f, 0.0f, 1.0f);
    private static int Track = ImColor.floatToColor(0.5f, 0.5f, 0.5f, 1.0f);

    protected ImKnobs() {}

    public static void drawArc1(ImVec2 center, float radius, float start_angle, float end_angle, float thickness, int color, int num_segments) {
        ImVec2 start = new ImVec2(center.x + (float)Math.cos(start_angle) * radius, center.y + (float)Math.sin(start_angle) * radius);

        ImVec2 end = new ImVec2(center.x + (float)Math.cos(end_angle) * radius, center.y + (float)Math.sin(end_angle) * radius);

        // Calculate bezier arc points
        float ax = start.x - center.x;
        float ay = start.y - center.y;
        float bx = end.x - center.x;
        float by = end.y - center.y;
        float q1 = ax * ax + ay * ay;
        float q2 = q1 + ax * bx + ay * by;
        float k2 = (4.0f / 3.0f) * ((float)Math.sqrt((2.0f * q1 * q2)) - q2) / (ax * by - ay * bx);
        ImVec2 arc1 = new ImVec2(center.x + ax - k2 * ay, center.y + ay + k2 * ax);
        ImVec2 arc2 = new ImVec2(center.x + bx + k2 * by, center.y + by - k2 * bx);

        ImDrawList draw_list = ImGui.getWindowDrawList();
        draw_list.addBezierCubic(start.x, start.y, arc1.x, arc1.y, arc2.x, arc2.y, end.x, end.y, color, thickness, num_segments);
    }

    public static void drawArc(ImVec2 center, float radius, float start_angle, float end_angle, float thickness, int color, int num_segments, int bezier_count) {
        // Overlap and angle of ends of bezier curves needs work, only looks good when not transperant
        float overlap = thickness * radius * 0.00001f * IMGUIKNOBS_PI;
        float delta = end_angle - start_angle;
        float bez_step = 1.0f / bezier_count;
        float mid_angle = start_angle + overlap;

        for (int i = 0; i < bezier_count - 1; i++) {
            float mid_angle2 = delta * bez_step + mid_angle;
            drawArc1(center, radius, mid_angle - overlap, mid_angle2 + overlap, thickness, color, num_segments);
            mid_angle = mid_angle2;
        }

        drawArc1(center, radius, mid_angle - overlap, end_angle, thickness, color, num_segments);
    }

    public static ImKnob knob(String label, float p_value, float v_min, float v_max, float _speed, String format, float size, int flags, ImKnobType type, int steps) {
        float speed = _speed == 0 ? (v_max - v_min) / 250.f : _speed;
        ImGui.pushID(label);
        float width = size == 0 ? ImGui.getTextLineHeight() * 4.0f : size * ImGui.getIO().getFontGlobalScale();
        ImGui.pushItemWidth(width);

        ImGui.beginGroup();

        // Draw knob
        ImKnob k = new ImKnob(label, ImGuiDataType.Float, p_value, v_min, v_max, speed, width * 0.5f, format, flags);

        // There's an issue with `SameLine` and Groups, see https://github.com/ocornut/imgui/issues/4190.
        // This is probably not the best solution, but seems to work for now
        //ImGui::GetCurrentWindow()->DC.CurrLineTextBaseOffset = 0;

        // Draw title
        if ((flags & ImGuiKnobFlags.NoTitle) == 0) {
            ImVec2 dest = new ImVec2();
            ImGui.calcTextSize(dest, label, false, width);

            // Center title
            ImGui.setCursorPosX(ImGui.getCursorPosX() + (width - dest.x) * 0.5f);
            ImGui.text(label);
        }

        // Draw tooltip
        if ((flags & ImGuiKnobFlags.ValueTooltip) != 0 && (ImGui.isItemHovered(ImGuiHoveredFlags.AllowWhenDisabled) || ImGui.isItemActive())) {
            ImGui.beginTooltip();
            ImGui.text(format);
            ImGui.endTooltip();
        }

        // Draw input
        float scalarHeight = 0.0f;
        if ((flags & ImGuiKnobFlags.NoInput) == 0) {
            int drag_flags = ImGuiSliderFlags.None;
            ImFloat imFloat = new ImFloat(p_value);
            k.value_changed = ImGui.dragScalar("###knob_drag", ImGuiDataType.Float, imFloat, speed, v_min, v_max, format, drag_flags);
            scalarHeight = ImGui.getItemRectSizeY();
            k.value = imFloat.get();
        }

        ImVec2 cursorPos = ImGui.getCursorScreenPos();
        if (ImGui.isMouseDragging(0)) {
            boolean hovering = ImGui.isMouseHoveringRect(cursorPos.x - Float.MAX_VALUE, cursorPos.y - size - scalarHeight, cursorPos.x + Float.MAX_VALUE, cursorPos.y - scalarHeight);

            if (hovering) {
                k.value = k.value + ImGui.getIO().getMouseDelta().x * speed;

                if (k.value < v_min) k.value = v_min;
                if (k.value > v_max) k.value = v_max;
            }
        }

        ImGui.endGroup();
        ImGui.popItemWidth();
        ImGui.popID();

        switch (type) {
            case Tick: {
                k.drawCircle(0.85f, Secondary, true, 32);
                k.drawTick(0.5f, 0.85f, 0.08f, k.angle, Primary);
                break;
            }
            case Dot: {
                k.drawCircle(0.85f, Secondary, true, 32);
                k.drawDot(0.12f, 0.6f, k.angle, Primary, true, 12);
                break;
            }

            case Wiper: {
                k.drawCircle(0.7f, Secondary, true, 32);
                k.drawArc(0.8f, 0.41f, k.angle_min, k.angle_max, Track, 16, 2);

                if (k.t > 0.01) {
                    k.drawArc(0.8f, 0.43f, k.angle_min, k.angle, Primary, 16, 2);
                }
                break;
            }
            case OnlyWiper: {
                k.drawArc(0.8f, 0.41f, k.angle_min, k.angle_max, Track, 32, 2);

                if (k.t > 0.01) {
                    k.drawArc(0.8f, 0.43f, k.angle_min, k.angle, Primary, 16, 2);
                }
                break;
            }
            case WiperDot: {
                k.drawCircle(0.6f, Secondary, true, 32);
                k.drawArc(0.85f, 0.41f, k.angle_min, k.angle_max, Track, 16, 2);
                k.drawDot(0.1f, 0.85f, k.angle, Primary, true, 12);
                break;
            }
            case Stepped: {
                for (float n = 0.f; n < steps; n++) {
                    float a = n / (steps - 1);
                    float angle = k.angle_min + (k.angle_max - k.angle_min) * a;
                    k.drawTick(0.7f, 0.9f, 0.04f, angle, Primary);
                }

                k.drawCircle(0.6f, Secondary, true, 32);
                k.drawDot(0.12f, 0.4f, k.angle, Primary, true, 12);
                break;
            }
        }

        return k;
    }

    public static ImKnob onlyWiperKnob(String label, float value, float min, float max, float speed, float size, int flags) {
        return knob(label, value, min, max, speed, "%.0f", size, flags, ImKnobType.OnlyWiper, 0);
    }

    public static ImKnob wiperKnob(String label, float value, float min, float max, float speed, float size, int flags) {
        return knob(label, value, min, max, speed, "%.0f", size, flags, ImKnobType.Wiper, 0);
    }

    public static ImKnob wiperDotKnob(String label, float value, float min, float max, float speed, float size, int flags) {
        return knob(label, value, min, max, speed, "%.0f", size, flags, ImKnobType.WiperDot, 0);
    }

    public static ImKnob steppedKnob(String label, float value, float min, float max, float speed, float size, int numSteps, int flags) {
        return knob(label, value, min, max, speed, "%.0f", size, flags, ImKnobType.Stepped, numSteps);
    }

    public static ImKnob dotKnob(String label, float value, float min, float max, float speed, float size, int flags) {
        return knob(label, value, min, max, speed, "%.0f", size, flags, ImKnobType.Dot, 0);
    }

    public static ImKnob tickKnob(String label, float value, float min, float max, float speed, float size, int flags) {
        return knob(label, value, min, max, speed, "%.0f", size, flags, ImKnobType.Tick, 0);
    }

    public static void SetPrimaryColor(float r, float g, float b, float a) {
        Primary = ImColor.floatToColor(r, g, b, a);
    }

    public static void SetPrimaryColor(int col) {
        Primary = col;
    }

    public static void SetSecondaryColor(float r, float g, float b, float a) {
        Secondary = ImColor.floatToColor(r, g, b, a);
    }

    public static void SetSecondaryColor(int col) {
        Secondary = col;
    }

    public static void SetTrackColor(float r, float g, float b, float a) {
        Track = ImColor.floatToColor(r, g, b, a);
    }

    public static void SetTrackColor(int col) {
        Track = col;
    }

}
