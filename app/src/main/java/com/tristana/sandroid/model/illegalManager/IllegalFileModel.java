package com.tristana.sandroid.model.illegalManager;

public class IllegalFileModel {

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    private String cover;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    private String file;

    public IllegalFileModel(String cover, String file, String content) {
        this.cover = cover;
        this.file = file;
        this.content = content;
    }
}
