package com.ks.freecoupon.module.bean;

import java.io.Serializable;

/**
 * author：康少
 * date：2019/4/12
 * description：附近优惠券Bean
 */
public class NearGoods implements Serializable{
    private int id;
    private String coupon_code;//优惠券编号
    private String name;//称呼
    private String img;//图片
    private String cost_price;//现价
    private String mkt_price;//满减价格
    private String address;//地址
    private String tel;//电话
    private String coupon_content;//优惠券文本
    private String stime;//开始领取时间
    private String etime;//结束领取时间
    private String lng;//经度
    private String lat;//纬度

    /**
     * Description：附近优惠券
     */
    public NearGoods(int id, String name, String img, String cost_price, String mkt_price, String address,String tel, String coupon_content, String stime, String etime, String lng, String lat) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.cost_price = cost_price;
        this.mkt_price = mkt_price;
        this.address = address;
        this.tel = tel;
        this.coupon_content = coupon_content;
        this.stime = stime;
        this.etime = etime;
        this.lng = lng;
        this.lat = lat;
    }

    /**
     * Description：我的优惠券
     */
    public NearGoods(String coupon_code, String name, String img, String mkt_price, String address, String coupon_content, String stime, String etime) {
        this.coupon_code = coupon_code;
        this.name = name;
        this.img = img;
        this.mkt_price = mkt_price;
        this.address = address;
        this.coupon_content = coupon_content;
        this.stime = stime;
        this.etime = etime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCost_price() {
        return cost_price;
    }

    public void setCost_price(String cost_price) {
        this.cost_price = cost_price;
    }

    public String getMkt_price() {
        return mkt_price;
    }

    public void setMkt_price(String mkt_price) {
        this.mkt_price = mkt_price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCoupon_content() {
        return coupon_content;
    }

    public void setCoupon_content(String coupon_content) {
        this.coupon_content = coupon_content;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }
}
