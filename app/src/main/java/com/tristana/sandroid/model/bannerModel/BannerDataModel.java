package com.tristana.sandroid.model.bannerModel;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/12 18:17
 * @description
 */
@Data
@AllArgsConstructor
public class BannerDataModel implements Serializable {
    private String imagePath;
    private String directionPath;
}
