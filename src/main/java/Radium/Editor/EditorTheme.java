package Radium.Editor;

import imgui.ImColor;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDir;

/**
 * Different themes and appearances for the editor
 */
public class EditorTheme {

    protected EditorTheme() {}

    public static void MonoChrome() {
        ImGuiStyle style = ImGui.getStyle();
        style.setAlpha(1.0f);
        style.setChildRounding(3);
        style.setWindowRounding(3);
        style.setGrabRounding(1);
        style.setGrabMinSize(20);

        style.setColor(ImGuiCol.Text, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 1.00f));
        style.setColor(ImGuiCol.TextDisabled, ImColor.floatToColor(0.00f, 0.40f, 0.41f, 1.00f));
        style.setColor(ImGuiCol.WindowBg, ImColor.floatToColor(0.00f, 0.00f, 0.00f, 1.00f));
        style.setColor(ImGuiCol.ChildBg, ImColor.floatToColor(0.00f, 0.00f, 0.00f, 0.00f));
        style.setColor(ImGuiCol.Border, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.65f));
        style.setColor(ImGuiCol.BorderShadow, ImColor.floatToColor(0.00f, 0.00f, 0.00f, 0.00f));
        style.setColor(ImGuiCol.FrameBg, ImColor.floatToColor(0.44f, 0.80f, 0.80f, 0.18f));
        style.setColor(ImGuiCol.FrameBgHovered, ImColor.floatToColor(0.44f, 0.80f, 0.80f, 0.27f));
        style.setColor(ImGuiCol.FrameBgActive, ImColor.floatToColor(0.44f, 0.81f, 0.86f, 0.66f));
        style.setColor(ImGuiCol.TitleBg, ImColor.floatToColor(0.14f, 0.18f, 0.21f, 0.73f));
        style.setColor(ImGuiCol.TitleBgCollapsed, ImColor.floatToColor(0.00f, 0.00f, 0.00f, 0.54f));
        style.setColor(ImGuiCol.TitleBgActive, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.27f));
        style.setColor(ImGuiCol.MenuBarBg, ImColor.floatToColor(0.00f, 0.00f, 0.00f, 0.20f));
        style.setColor(ImGuiCol.ScrollbarBg, ImColor.floatToColor(0.22f, 0.29f, 0.30f, 0.71f));
        style.setColor(ImGuiCol.ScrollbarGrab, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.44f));
        style.setColor(ImGuiCol.ScrollbarGrabHovered, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.74f));
        style.setColor(ImGuiCol.ScrollbarGrabActive, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 1.00f));
        style.setColor(ImGuiCol.CheckMark, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.68f));
        style.setColor(ImGuiCol.SliderGrab, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.36f));
        style.setColor(ImGuiCol.SliderGrabActive, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.76f));
        style.setColor(ImGuiCol.Button, ImColor.floatToColor(0.00f, 0.65f, 0.65f, 0.46f));
        style.setColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(0.01f, 1.00f, 1.00f, 0.43f));
        style.setColor(ImGuiCol.ButtonActive, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.62f));
        style.setColor(ImGuiCol.Header, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.33f));
        style.setColor(ImGuiCol.HeaderHovered, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.42f));
        style.setColor(ImGuiCol.HeaderActive, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.54f));
        style.setColor(ImGuiCol.ResizeGrip, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.54f));
        style.setColor(ImGuiCol.ResizeGripHovered, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.74f));
        style.setColor(ImGuiCol.ResizeGripActive, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 1.00f));
        style.setColor(ImGuiCol.Button, ImColor.floatToColor(0.00f, 0.78f, 0.78f, 0.35f));
        style.setColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(0.00f, 0.78f, 0.78f, 0.47f));
        style.setColor(ImGuiCol.ButtonActive, ImColor.floatToColor(0.00f, 0.78f, 0.78f, 1.00f));
        style.setColor(ImGuiCol.PlotLines, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 1.00f));
        style.setColor(ImGuiCol.PlotLinesHovered, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 1.00f));
        style.setColor(ImGuiCol.PlotHistogram, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 1.00f));
        style.setColor(ImGuiCol.PlotHistogramHovered, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 1.00f));
        style.setColor(ImGuiCol.TextSelectedBg, ImColor.floatToColor(0.00f, 1.00f, 1.00f, 0.22f));
        style.setColor(ImGuiCol.ModalWindowDimBg, ImColor.floatToColor(0.04f, 0.10f, 0.09f, 0.51f));
    }

    public static void Dark() {
        ImGuiStyle style = ImGui.getStyle();

        style.setFrameRounding(4.0f);
        style.setGrabRounding(4.0f);
        style.setColor(ImGuiCol.Text, ImColor.floatToColor(0.95f, 0.96f, 0.98f, 1.00f));
        style.setColor(ImGuiCol.TextDisabled, ImColor.floatToColor(0.36f, 0.42f, 0.47f, 1.00f));
        style.setColor(ImGuiCol.WindowBg, ImColor.floatToColor(0.11f, 0.15f, 0.17f, 1.00f));
        style.setColor(ImGuiCol.ChildBg, ImColor.floatToColor(0.15f, 0.18f, 0.22f, 1.00f));
        style.setColor(ImGuiCol.PopupBg, ImColor.floatToColor(0.08f, 0.08f, 0.08f, 0.94f));
        style.setColor(ImGuiCol.Border, ImColor.floatToColor(0.08f, 0.10f, 0.12f, 1.00f));
        style.setColor(ImGuiCol.BorderShadow, ImColor.floatToColor(0.00f, 0.00f, 0.00f, 0.00f));
        style.setColor(ImGuiCol.FrameBg, ImColor.floatToColor(0.20f, 0.25f, 0.29f, 1.00f));
        style.setColor(ImGuiCol.FrameBgHovered, ImColor.floatToColor(0.12f, 0.20f, 0.28f, 1.00f));
        style.setColor(ImGuiCol.FrameBgActive, ImColor.floatToColor(0.09f, 0.12f, 0.14f, 1.00f));
        style.setColor(ImGuiCol.TitleBg, ImColor.floatToColor(0.09f, 0.12f, 0.14f, 0.65f));
        style.setColor(ImGuiCol.TitleBgActive, ImColor.floatToColor(0.08f, 0.10f, 0.12f, 1.00f));
        style.setColor(ImGuiCol.TitleBgCollapsed, ImColor.floatToColor(0.00f, 0.00f, 0.00f, 0.51f));
        style.setColor(ImGuiCol.MenuBarBg, ImColor.floatToColor(0.15f, 0.18f, 0.22f, 1.00f));
        style.setColor(ImGuiCol.ScrollbarBg, ImColor.floatToColor(0.02f, 0.02f, 0.02f, 0.39f));
        style.setColor(ImGuiCol.ScrollbarGrab, ImColor.floatToColor(0.20f, 0.25f, 0.29f, 1.00f));
        style.setColor(ImGuiCol.ScrollbarGrabHovered, ImColor.floatToColor(0.18f, 0.22f, 0.25f, 1.00f));
        style.setColor(ImGuiCol.ScrollbarGrabActive, ImColor.floatToColor(0.09f, 0.21f, 0.31f, 1.00f));
        style.setColor(ImGuiCol.CheckMark, ImColor.floatToColor(0.28f, 0.56f, 1.00f, 1.00f));
        style.setColor(ImGuiCol.SliderGrab, ImColor.floatToColor(0.28f, 0.56f, 1.00f, 1.00f));
        style.setColor(ImGuiCol.SliderGrabActive, ImColor.floatToColor(0.37f, 0.61f, 1.00f, 1.00f));
        style.setColor(ImGuiCol.Button, ImColor.floatToColor(0.20f, 0.25f, 0.29f, 1.00f));
        style.setColor(ImGuiCol.ButtonHovered, ImColor.floatToColor(0.28f, 0.56f, 1.00f, 1.00f));
        style.setColor(ImGuiCol.ButtonActive, ImColor.floatToColor(0.06f, 0.53f, 0.98f, 1.00f));
        style.setColor(ImGuiCol.Header, ImColor.floatToColor(0.20f, 0.25f, 0.29f, 0.55f));
        style.setColor(ImGuiCol.HeaderHovered, ImColor.floatToColor(0.26f, 0.59f, 0.98f, 0.80f));
        style.setColor(ImGuiCol.HeaderActive, ImColor.floatToColor(0.26f, 0.59f, 0.98f, 1.00f));
        style.setColor(ImGuiCol.Separator, ImColor.floatToColor(0.20f, 0.25f, 0.29f, 1.00f));
        style.setColor(ImGuiCol.SeparatorHovered, ImColor.floatToColor(0.10f, 0.40f, 0.75f, 0.78f));
        style.setColor(ImGuiCol.SeparatorActive, ImColor.floatToColor(0.10f, 0.40f, 0.75f, 1.00f));
        style.setColor(ImGuiCol.ResizeGrip, ImColor.floatToColor(0.26f, 0.59f, 0.98f, 0.25f));
        style.setColor(ImGuiCol.ResizeGripHovered, ImColor.floatToColor(0.26f, 0.59f, 0.98f, 0.67f));
        style.setColor(ImGuiCol.ResizeGripActive, ImColor.floatToColor(0.26f, 0.59f, 0.98f, 0.95f));
        style.setColor(ImGuiCol.Tab, ImColor.floatToColor(0.11f, 0.15f, 0.17f, 1.00f));
        style.setColor(ImGuiCol.TabHovered, ImColor.floatToColor(0.26f, 0.59f, 0.98f, 0.80f));
        style.setColor(ImGuiCol.TabActive, ImColor.floatToColor(0.20f, 0.25f, 0.29f, 1.00f));
        style.setColor(ImGuiCol.TabUnfocused, ImColor.floatToColor(0.11f, 0.15f, 0.17f, 1.00f));
        style.setColor(ImGuiCol.TabUnfocusedActive, ImColor.floatToColor(0.11f, 0.15f, 0.17f, 1.00f));
        style.setColor(ImGuiCol.PlotLines, ImColor.floatToColor(0.61f, 0.61f, 0.61f, 1.00f));
        style.setColor(ImGuiCol.PlotLinesHovered, ImColor.floatToColor(1.00f, 0.43f, 0.35f, 1.00f));
        style.setColor(ImGuiCol.PlotHistogram, ImColor.floatToColor(0.90f, 0.70f, 0.00f, 1.00f));
        style.setColor(ImGuiCol.PlotHistogramHovered, ImColor.floatToColor(1.00f, 0.60f, 0.00f, 1.00f));
        style.setColor(ImGuiCol.TextSelectedBg, ImColor.floatToColor(0.26f, 0.59f, 0.98f, 0.35f));
        style.setColor(ImGuiCol.DragDropTarget, ImColor.floatToColor(1.00f, 1.00f, 0.00f, 0.90f));
        style.setColor(ImGuiCol.NavHighlight, ImColor.floatToColor(0.26f, 0.59f, 0.98f, 1.00f));
        style.setColor(ImGuiCol.NavWindowingHighlight, ImColor.floatToColor(1.00f, 1.00f, 1.00f, 0.70f));
        style.setColor(ImGuiCol.NavWindowingDimBg, ImColor.floatToColor(0.80f, 0.80f, 0.80f, 0.20f));
        style.setColor(ImGuiCol.ModalWindowDimBg, ImColor.floatToColor(0.80f, 0.80f, 0.80f, 0.35f));

    }

    public static void ModernDark() {
        ImGuiStyle style = ImGui.getStyle();

        style.setAntiAliasedLines(true);
        style.setAntiAliasedFill(true);

        style.setChildRounding(0);
        style.setGrabRounding(0);
        style.setFrameRounding(2);
        style.setPopupRounding(0);
        style.setScrollbarRounding(0);
        style.setTabRounding(2);
        style.setWindowRounding(0);
        style.setFrameRounding(4);

        style.setWindowTitleAlign(0, 0.5f);
        style.setColorButtonPosition(ImGuiDir.Left);
        
        style.setColor(ImGuiCol.Text, ImColor.floatToColor( 1.0f, 1.0f, 1.0f, 1.00f));				//
        style.setColor(ImGuiCol.TextDisabled, ImColor.floatToColor( 0.25f, 0.25f, 0.25f, 1.00f));		//
        style.setColor(ImGuiCol.WindowBg, ImColor.floatToColor( 0.09f, 0.09f, 0.09f, 0.94f));			//
        style.setColor(ImGuiCol.ChildBg, ImColor.floatToColor( 0.11f, 0.11f, 0.11f, 1.00f));			//
        style.setColor(ImGuiCol.PopupBg, ImColor.floatToColor( 0.11f, 0.11f, 0.11f, 0.94f));			//
        style.setColor(ImGuiCol.Border, ImColor.floatToColor( 0.07f, 0.08f, 0.08f, 1.00f));
        style.setColor(ImGuiCol.BorderShadow, ImColor.floatToColor( 0.00f, 0.00f, 0.00f, 0.00f));
        style.setColor(ImGuiCol.FrameBg, ImColor.floatToColor( 0.35f, 0.35f, 0.35f, 0.54f));			//
        style.setColor(ImGuiCol.FrameBgHovered, ImColor.floatToColor( 0.31f, 0.29f, 0.27f, 1.00f));
        style.setColor(ImGuiCol.FrameBgActive, ImColor.floatToColor( 0.40f, 0.36f, 0.33f, 0.67f));
        style.setColor(ImGuiCol.TitleBg, ImColor.floatToColor( 0.1f, 0.1f, 0.1f, 1.00f));
        style.setColor(ImGuiCol.TitleBgActive, ImColor.floatToColor( 0.3f, 0.3f, 0.3f, 1.00f));		//
        style.setColor(ImGuiCol.TitleBgCollapsed, ImColor.floatToColor( 0.0f, 0.0f, 0.0f, 0.61f));
        style.setColor(ImGuiCol.MenuBarBg, ImColor.floatToColor( 0.18f, 0.18f, 0.18f, 0.94f));		//
        style.setColor(ImGuiCol.ScrollbarBg, ImColor.floatToColor( 0.00f, 0.00f, 0.00f, 0.16f));
        style.setColor(ImGuiCol.ScrollbarGrab, ImColor.floatToColor( 0.24f, 0.22f, 0.21f, 1.00f));
        style.setColor(ImGuiCol.ScrollbarGrabHovered, ImColor.floatToColor( 0.31f, 0.29f, 0.27f, 1.00f));
        style.setColor(ImGuiCol.ScrollbarGrabActive, ImColor.floatToColor( 0.40f, 0.36f, 0.33f, 1.00f));
        style.setColor(ImGuiCol.CheckMark, ImColor.floatToColor( 0.84f, 0.84f, 0.84f, 1.0f));			//
        style.setColor(ImGuiCol.SliderGrab, ImColor.floatToColor( 0.8f, 0.8f, 0.8f, 1.0f));			//
        style.setColor(ImGuiCol.SliderGrabActive, ImColor.floatToColor( 0.55f, 0.55f, 0.55f, 1.00f)); //
        style.setColor(ImGuiCol.Button, ImColor.floatToColor( 0.55f, 0.55f, 0.55f, 0.40f));			//
        style.setColor(ImGuiCol.ButtonHovered, ImColor.floatToColor( 0.15f, 0.15f, 0.15f, 0.62f));	//
        style.setColor(ImGuiCol.ButtonActive, ImColor.floatToColor( 0.60f, 0.60f, 0.60f, 1.00f));		//
        style.setColor(ImGuiCol.Header, ImColor.floatToColor( 0.84f, 0.36f, 0.05f, 0.0f));			//
        style.setColor(ImGuiCol.HeaderHovered, ImColor.floatToColor( 0.25f, 0.25f, 0.25f, 0.80f));	//
        style.setColor(ImGuiCol.HeaderActive, ImColor.floatToColor( 0.42f, 0.42f, 0.42f, 1.00f));
        style.setColor(ImGuiCol.Separator, ImColor.floatToColor( 0.35f, 0.35f, 0.35f, 0.50f));		//
        style.setColor(ImGuiCol.SeparatorHovered, ImColor.floatToColor( 0.31f, 0.29f, 0.27f, 0.78f));
        style.setColor(ImGuiCol.SeparatorActive, ImColor.floatToColor( 0.40f, 0.36f, 0.33f, 1.00f));
        style.setColor(ImGuiCol.ResizeGrip, ImColor.floatToColor( 1.0f, 1.0f, 1.0f, 0.25f));			//
        style.setColor(ImGuiCol.ResizeGripHovered, ImColor.floatToColor( 1.00f, 1.0f, 1.0f, 0.4f));	//
        style.setColor(ImGuiCol.ResizeGripActive, ImColor.floatToColor( 1.00f, 1.00f, 1.0f, 0.95f));	//
        style.setColor(ImGuiCol.Tab, ImColor.floatToColor( 0.18f, 0.18f, 0.18f, 1.0f));				//
        style.setColor(ImGuiCol.TabHovered, ImColor.floatToColor( 0.58f, 0.58f, 0.58f, 0.80f));		//
        style.setColor(ImGuiCol.TabActive, ImColor.floatToColor( 0.6f, 0.60f, 0.60f, 1.00f));
        style.setColor(ImGuiCol.TabUnfocused, ImColor.floatToColor( 0.07f, 0.10f, 0.15f, 0.97f));
        style.setColor(ImGuiCol.TabUnfocusedActive, ImColor.floatToColor( 0.14f, 0.26f, 0.42f, 1.00f));
        style.setColor(ImGuiCol.PlotLines, ImColor.floatToColor( 0.66f, 0.60f, 0.52f, 1.00f));
        style.setColor(ImGuiCol.PlotLinesHovered, ImColor.floatToColor( 0.98f, 0.29f, 0.20f, 1.00f));
        style.setColor(ImGuiCol.PlotHistogram, ImColor.floatToColor( 0.60f, 0.59f, 0.10f, 1.00f));
        style.setColor(ImGuiCol.PlotHistogramHovered, ImColor.floatToColor( 0.72f, 0.73f, 0.15f, 1.00f));
        style.setColor(ImGuiCol.TextSelectedBg, ImColor.floatToColor( 0.27f, 0.52f, 0.53f, 0.35f));
        style.setColor(ImGuiCol.DragDropTarget, ImColor.floatToColor( 0.60f, 0.59f, 0.10f, 0.90f));
        style.setColor(ImGuiCol.NavHighlight, ImColor.floatToColor( 0.51f, 0.65f, 0.60f, 1.00f));
        style.setColor(ImGuiCol.NavWindowingHighlight, ImColor.floatToColor( 1.00f, 1.00f, 1.00f, 0.70f));
        style.setColor(ImGuiCol.NavWindowingDimBg, ImColor.floatToColor( 0.80f, 0.80f, 0.80f, 0.20f));
        style.setColor(ImGuiCol.ModalWindowDimBg, ImColor.floatToColor( 0.11f, 0.13f, 0.13f, 0.35f));

        style.setColor(ImGuiCol.TabUnfocusedActive, ImColor.floatToColor(0.275f, 0.275f, 0.275f, 1));
        style.setColor(ImGuiCol.TabActive, ImColor.floatToColor(0.3f, 0.3f, 0.3f, 1));
        style.setColor(ImGuiCol.TitleBgActive, ImColor.floatToColor(0.2f, 0.2f, 0.2f, 1));
    }

    public static void SetStyle(Theme theme) {
        ImGuiStyle style = ImGui.getStyle();

        style.setColor(ImGuiCol.Text, ImColor.floatToColor( theme.textColor.r, theme.textColor.g, theme.textColor.b, 1.00f));
        style.setColor(ImGuiCol.TextDisabled, ImColor.floatToColor( theme.textColor.r, theme.textColor.g, theme.textColor.b, 0.58f));
        style.setColor(ImGuiCol.WindowBg, ImColor.floatToColor( theme.bodyColor.r, theme.bodyColor.g, theme.bodyColor.b, 0.95f));
        style.setColor(ImGuiCol.ChildBg, ImColor.floatToColor( theme.areaColor.r, theme.areaColor.g, theme.areaColor.b, 0.58f));
        style.setColor(ImGuiCol.Border, ImColor.floatToColor( theme.bodyColor.r, theme.bodyColor.g, theme.bodyColor.b, 0.00f));
        style.setColor(ImGuiCol.BorderShadow, ImColor.floatToColor( theme.bodyColor.r, theme.bodyColor.g, theme.bodyColor.b, 0.00f));
        style.setColor(ImGuiCol.FrameBg, ImColor.floatToColor( theme.areaColor.r, theme.areaColor.g, theme.areaColor.b, 1.00f));
        style.setColor(ImGuiCol.FrameBgHovered, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 0.78f));
        style.setColor(ImGuiCol.FrameBgActive, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 1.00f));
        style.setColor(ImGuiCol.TitleBg, ImColor.floatToColor( theme.areaColor.r, theme.areaColor.g, theme.areaColor.b, 1.00f));
        style.setColor(ImGuiCol.TitleBgCollapsed, ImColor.floatToColor( theme.areaColor.r, theme.areaColor.g, theme.areaColor.b, 0.75f));
        style.setColor(ImGuiCol.TitleBgActive, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 1.00f));
        style.setColor(ImGuiCol.MenuBarBg, ImColor.floatToColor( theme.areaColor.r, theme.areaColor.g, theme.areaColor.b, 0.47f));
        style.setColor(ImGuiCol.ScrollbarBg, ImColor.floatToColor( theme.areaColor.r, theme.areaColor.g, theme.areaColor.b, 1.00f));
        style.setColor(ImGuiCol.ScrollbarGrab, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 0.21f));
        style.setColor(ImGuiCol.ScrollbarGrabHovered, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 0.78f));
        style.setColor(ImGuiCol.ScrollbarGrabActive, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 1.00f));
        style.setColor(ImGuiCol.CheckMark, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 0.80f));
        style.setColor(ImGuiCol.SliderGrab, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 0.50f));
        style.setColor(ImGuiCol.SliderGrabActive, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 1.00f));
        style.setColor(ImGuiCol.Button, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 0.50f));
        style.setColor(ImGuiCol.ButtonHovered, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 0.86f));
        style.setColor(ImGuiCol.ButtonActive, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 1.00f));
        style.setColor(ImGuiCol.Header, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 0.76f));
        style.setColor(ImGuiCol.HeaderHovered, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 0.86f));
        style.setColor(ImGuiCol.HeaderActive, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 1.00f));
        style.setColor(ImGuiCol.ResizeGrip, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 0.15f));
        style.setColor(ImGuiCol.ResizeGripHovered, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 0.78f));
        style.setColor(ImGuiCol.ResizeGripActive, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 1.00f));
        style.setColor(ImGuiCol.PlotLines, ImColor.floatToColor( theme.textColor.r, theme.textColor.g, theme.textColor.b, 0.63f));
        style.setColor(ImGuiCol.PlotLinesHovered, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 1.00f));
        style.setColor(ImGuiCol.PlotHistogram, ImColor.floatToColor( theme.textColor.r, theme.textColor.g, theme.textColor.b, 0.63f));
        style.setColor(ImGuiCol.PlotHistogramHovered, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 1.00f));
        style.setColor(ImGuiCol.TextSelectedBg, ImColor.floatToColor( theme.headerColor.r, theme.headerColor.g, theme.headerColor.b, 0.43f));
        style.setColor(ImGuiCol.PopupBg, ImColor.floatToColor( theme.popupColor.r, theme.popupColor.g, theme.popupColor.b, 0.92f));
        style.setColor(ImGuiCol.ModalWindowDimBg, ImColor.floatToColor( theme.areaColor.r, theme.areaColor.g, theme.areaColor.b, 0.73f));
        style.setColor(ImGuiCol.Tab, ImColor.floatToColor(theme.tabColor.r, theme.tabColor.g, theme.tabColor.b, 0.50f));
        style.setColor(ImGuiCol.TabHovered, ImColor.floatToColor(theme.tabColor.r, theme.tabColor.g, theme.tabColor.b, 0.86f));
        style.setColor(ImGuiCol.TabActive, ImColor.floatToColor(theme.tabColor.r, theme.tabColor.g, theme.tabColor.b, 1.00f));
        style.setColor(ImGuiCol.TabUnfocused, ImColor.floatToColor(theme.tabColor.r, theme.tabColor.g, theme.tabColor.b, 0.50f));
        style.setColor(ImGuiCol.TabUnfocusedActive, ImColor.floatToColor(theme.tabColor.r, theme.tabColor.g, theme.tabColor.b, 0.86f));
    }

}
