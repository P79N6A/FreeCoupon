package com.ks.freecoupon.module.bean;

/**
 * author：康少
 * date：2019/3/13
 * description：京东优惠券
 */
public class JingDongGoods {
    private String image;//图片
    private String title;//名称
    private String price;//优惠额度
    private String quota;//原价
    private String discountPrice;//现价
    private String link;//领券链接

    public JingDongGoods(String image, String title, String price, String quota, String discountPrice, String link) {
        this.image = image;
        this.title = title;
        this.price = price;
        this.quota = quota;
        this.discountPrice = discountPrice;
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}