package com.ks.freecoupon.module.bean;

/**
 * author：康少
 * date：2019/4/12
 * description：首页Banner对象
 */
public class BannerBean {
    private String image;
    private String url;

    public BannerBean(String image, String url) {
        this.image = image;
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
