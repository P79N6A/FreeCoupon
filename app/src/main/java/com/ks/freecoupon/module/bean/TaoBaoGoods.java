package com.ks.freecoupon.module.bean;

/**
 * author：康少
 * date：2019/3/13
 * description：淘宝商品
 */
public class TaoBaoGoods {
    private String id;
    private String image;//图片
    private String name;//名称
    private String price;//优惠额度
    private String yuanjia;//原价
    private String discountPrice;//现价
    private String coupon_pwd;//淘口令

    public TaoBaoGoods(String id, String image, String name, String price, String yuanjia, String discountPrice, String coupon_pwd) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.price = price;
        this.yuanjia = yuanjia;
        this.discountPrice = discountPrice;
        this.coupon_pwd = coupon_pwd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getYuanjia() {
        return yuanjia;
    }

    public void setYuanjia(String yuanjia) {
        this.yuanjia = yuanjia;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getCoupon_pwd() {
        return coupon_pwd;
    }

    public void setCoupon_pwd(String coupon_pwd) {
        this.coupon_pwd = coupon_pwd;
    }
}