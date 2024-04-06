package com.example.elso;

public class Item {
    private String id;
    private String name;
    private String info;
    private String price;
    private float ratedInfo;
    private int imageResource;

    public Item() {
    }

    public Item(String name, String info, String price, float ratedInfo, int imageResource) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getPrice() {
        return price;
    }

    public float getRatedInfo() {
        return ratedInfo;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setId(String id) {
        this.id = id;
    }
}
