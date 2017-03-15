package com.dl.swipelistviewdemo.modle;

/**
 * Created by hp on 2017/3/15.
 */

public class Info {
    private String title;
    private String content;

    public Info(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
