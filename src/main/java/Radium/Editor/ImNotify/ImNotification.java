package Radium.Editor.ImNotify;

import Radium.Engine.Graphics.Texture;
import Radium.Engine.Time;

public class ImNotification {

    public String title;
    public String content;

    public int icon;
    public float life;
    public float opacity = 0.0f;

    public float[] backgroundColor = new float[] { 0.3f, 0.3f, 0.3f };
    public float[] textColor = new float[] { 1.0f, 1.0f, 1.0f };

    public ImNotification(String title, String content, float lifetime) {
        this.title = title;
        this.content = content;
        this.life = lifetime;

        icon = new Texture("EngineAssets/Editor/Notification/log.png", true).GetTextureID();
    }

    private boolean fading = false;
    public void update() {
        life -= Time.deltaTime;
        if (!fading) opacity += Time.deltaTime * 4.0f;
        if (opacity >= 1) opacity = 1.0f;

        if (life <= 0.5f) {
            fading = true;
            opacity -= Time.deltaTime;
        }
        if (opacity <= 0) opacity = 0.0f;

        if (life <= 0) {
            ImNotify.deleteNotification(this);
        }
    }

}
