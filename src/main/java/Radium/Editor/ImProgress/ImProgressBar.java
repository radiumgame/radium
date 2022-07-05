package Radium.Editor.ImProgress;

import javax.swing.*;

public class ImProgressBar {

    public String name;
    public Runnable action;

    public float progress;
    private SwingWorker worker;

    private boolean shouldRender = true;

    public ImProgressBar(String name, Runnable action) {
        this.name = name;
        this.action = action;

        this.progress = 0f;
    }

    public void execute() {
        progress = 0.0f;
        worker = new SwingWorker() {
            @Override
            public Object doInBackground() throws Exception {
                action.run();

                return null;
            }

            @Override
            protected void done()
            {
                progress = 1.0f;
                shouldRender = false;
            }
        };

        worker.execute();
    }

    public void render(float width) {
        ImProgress.renderProgressBar(this, width);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        if (this.progress > 1.0f) {
            this.progress = 1.0f;
        }
    }

    public void addProgress(float progress) {
        this.progress += progress;
        if (this.progress > 1.0f) {
            this.progress = 1.0f;
        }
    }

}
