package Radium.Integration.Discord;

import Radium.Engine.SceneManagement.SceneManager;
import Radium.Engine.Variables;
import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

/**
 * Plugin for showing custom discord status
 */
public final class DiscordStatus {

    public static boolean UseDiscordRichPresence = false;
    private static DiscordRichPresence drp;

    protected DiscordStatus() {}

    /**
     * Enables the RPC for the discord application
     */
    public static void EnableRPC() {
        Variables.Settings.UseDiscord = true;
        Variables.Settings.Save("EngineAssets/editor.settings");

        DiscordRPC.INSTANCE.Discord_Initialize("912490707359522877", new DiscordEventHandlers(), true, "");
        drp = new DiscordRichPresence();
        UpdateScene();
        drp.state = "https://bit.ly/3xgabpC";
        drp.startTimestamp = System.currentTimeMillis() + 5 * 60;
        drp.largeImageKey = "icon";

        DiscordRPC.INSTANCE.Discord_UpdatePresence(drp);
    }

    /**
     * Disables the RPC for the discord application
     */
    public static void DisableRPC() {
        Variables.Settings.UseDiscord = false;
        Variables.Settings.Save("EngineAssets/editor.settings");

        DiscordRPC.INSTANCE.Discord_Shutdown();
    }

    /**
     * Updates the currently playing scene in the status
     */
    public static void UpdateScene() {
        if (drp == null || SceneManager.GetCurrentScene() == null) return;

        drp.details = "Editing " + SceneManager.GetCurrentScene().file.getName();
        DiscordRPC.INSTANCE.Discord_UpdatePresence(drp);
    }

}
