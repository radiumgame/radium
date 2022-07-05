package Radium.Integration.Project;

import java.io.File;

public interface AssetsListener {

    void OnFileCreated(File file);
    void OnFileDeleted(File file);
    void OnFileChanged(File file);

}
