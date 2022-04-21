package RadiumEditor.Clipboard;

import imgui.ImGui;

public class Clipboard {

    private static Object clipboard = null;

    protected Clipboard() {}

    public static void SetClipboard(Object obj) {
        clipboard = obj;
    }

    public static <T> T GetClipboardAs(Class<T> type) {
        if (clipboard == null) {
            return null;
        }
        if (type.isAssignableFrom(clipboard.getClass())) {
            return type.cast(clipboard);
        }

        return null;
    }

    public static void ClearClipboard() {
        clipboard = null;
    }

    public static void OpenCopyPasteMenu() {
        ImGui.openPopup("CopyPasteMenu");
    }

    public static void CopyPasteMenu(Object copy, Runnable paste) {
        if (ImGui.beginPopup("CopyPasteMenu")) {
            if (ImGui.menuItem("Copy")) {
                SetClipboard(copy);
            }
            if (ImGui.menuItem("Paste")) {
                paste.run();
            }

            ImGui.endPopup();
        }
    }

}
