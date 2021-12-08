package Integration.Discord;

import Radium.SceneManagement.SceneManager;
import Radium.Variables;
import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public final class DiscordStatus {

    public static boolean UseDiscordRichPresence = false;
    private static DiscordRichPresence drp;

    protected DiscordStatus() {}

    public static void EnableRPC() {
        Variables.Settings.UseDiscord = true;
        Variables.Settings.Save("EngineAssets/editor.settings");

        DiscordRPC.INSTANCE.Discord_Initialize("912490707359522877", new DiscordEventHandlers(), true, "");
        drp = new DiscordRichPresence();
        drp.details = "Editing " + SceneManager.GetCurrentScene().file.getName();
        drp.state = "https://bit.ly/3xgabpC";
        drp.startTimestamp = System.currentTimeMillis() + 5 * 60;
        drp.largeImageKey = "icon";

        DiscordRPC.INSTANCE.Discord_UpdatePresence(drp);
    }

    public static void DisableRPC() {
        Variables.Settings.UseDiscord = false;
        Variables.Settings.Save("EngineAssets/editor.settings");

        DiscordRPC.INSTANCE.Discord_Shutdown();
    }

    public static void UpdateScene() {
        drp.details = "Editing " + SceneManager.GetCurrentScene().file.getName();
        DiscordRPC.INSTANCE.Discord_UpdatePresence(drp);
    }

}
