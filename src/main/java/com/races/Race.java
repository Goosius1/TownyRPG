package com.races;

import java.io.File;

public class Race {

    private String name;  //user friendly name
    private String id;      //lowercase, no spaces
    private String description;
    private String iconFileName;
    private File iconFile;  //Todo - do we need to store this?.  It seems unused

    public Race() {
        name = "Dwarf";
        id = "dwarf";
        iconFileName = "races/dwarf/dwarf.png";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getIconFile() {
        return iconFile;
    }

    public void setIconFile(File iconFile) {
        this.iconFile = iconFile;
    }

    public String getIconFileName() {
        return iconFileName;
    }

    public void setIconFileName(String iconFileName) {
        this.iconFileName = iconFileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
