package com.example.elso;

public class UserPackage {
    private String userUid;
    private String packageName;
    private String info;
    private String price;
    private float ratedInfo;
    private int imageResource;

    public UserPackage() {
        // Üres konstruktor szükséges Firestore-hoz
    }

    public UserPackage(String userUid, String packageName, String info, String price, float ratedInfo, int imageResource) {
        this.userUid = userUid;
        this.packageName = packageName;
        this.info = info;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.imageResource = imageResource;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRatedInfo() {
        return ratedInfo;
    }

    public void setRatedInfo(float ratedInfo) {
        this.ratedInfo = ratedInfo;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
