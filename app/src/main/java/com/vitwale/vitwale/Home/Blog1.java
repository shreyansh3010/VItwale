package com.vitwale.vitwale.Home;


public class Blog1 {
    private String desc;
    private String image;
    private String title;

    public Blog1(){

    }

    public Blog1(String title, String desc, String image){

        this.title = title;
        this.desc = desc;
        this.image = image;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
