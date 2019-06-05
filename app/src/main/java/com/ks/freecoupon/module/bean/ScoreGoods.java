package com.ks.freecoupon.module.bean;

import java.util.List;

/**
 * author：康少
 * date：2019/4/4
 * description：积分商品
 */
public class ScoreGoods {
    private String goodsId;//货物Id、订单Id
    private String goodsImage;//货物图片
    private List<String> imageList;//货物图片集合
    private String goodsDesc;//货物抬头
    private String scorePrice;//货物价值（积分价值）
    private String stock;//库存
    private String is_physical;//1为虚拟商品 0 为实物商品
    private String goodsDetail;//商品详情介绍

    public ScoreGoods(List<String> imageList, String goodsDesc, String scorePrice, String is_physical, String goodsDetail) {
        this.imageList = imageList;
        this.goodsDesc = goodsDesc;
        this.scorePrice = scorePrice;
        this.is_physical = is_physical;
        this.goodsDetail = goodsDetail;
    }

    public ScoreGoods(String goodsId, String goodsImage, String goodsDesc, String scorePrice, String stock, String is_physical) {
        this.goodsId = goodsId;
        this.goodsImage = goodsImage;
        this.goodsDesc = goodsDesc;
        this.scorePrice = scorePrice;
        this.stock = stock;
        this.is_physical = is_physical;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public String getScorePrice() {
        return scorePrice;
    }

    public void setScorePrice(String scorePrice) {
        this.scorePrice = scorePrice;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getIs_physical() {
        return is_physical;
    }

    public void setIs_physical(String is_physical) {
        this.is_physical = is_physical;
    }

    public String getGoodsDetail() {
        return goodsDetail;
    }

    public void setGoodsDetail(String goodsDetail) {
        this.goodsDetail = goodsDetail;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }
}
