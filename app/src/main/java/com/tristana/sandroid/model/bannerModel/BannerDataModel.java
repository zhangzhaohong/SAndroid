package com.tristana.sandroid.model.bannerModel;

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/12 18:17
 * @description
 */
public class BannerDataModel {
    private final String imagePath;

    public BannerDataModel(String imagePath, String directionPath) {
        this.imagePath = imagePath;
        this.directionPath = directionPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDirectionPath() {
        return directionPath;
    }

    private final String directionPath;
}
