package com.ks.freecoupon.module.bean;

/**
 * author：康少
 * date：2019/4/26
 * description：首页——热门商品
 */
public class HotGoods {
    private String coupon_id;//优惠券编号
    private String title;//称呼
    private String img;//图片
    private String type;//1：附近，2：免费，3：淘宝；4：京东
    private String price;//优惠额度
    /*京东*/
    private String quota;//满减金额
    private String link;//领券链接
    /*淘宝*/
    private String yuanjia;//原价
    private String discountPrice;//现价
    private String coupon_pwd;//淘口令
    /*附近*/
    private String mkt_price;//满减价格
    private String address;//地址
    private String tel;//地址
    private String coupon_content;//优惠券文本
    private String stime;//开始领取时间
    private String etime;//结束领取时间

    /**
     * Description：附近
     */
    public HotGoods(String coupon_id, String title, String img, String mkt_price, String address, String tel, String coupon_content, String stime, String etime) {
        this.coupon_id = coupon_id;
        this.title = title;
        this.img = img;
        this.mkt_price = mkt_price;
        this.address = address;
        this.tel = tel;
        this.coupon_content = coupon_content;
        this.stime = stime;
        this.etime = etime;
        this.type = "1";
    }

    /**
     * Description：免费实物
     */
    public HotGoods(String title, String img) {
        this.title = title;
        this.img = img;
        this.type = "2";
    }

    /**
     * Description：淘宝
     */
    public HotGoods(String title, String img, String yuanjia, String price, String discountPrice, String coupon_pwd) {
        this.title = title;
        this.img = img;
        this.price = price;
        this.yuanjia = yuanjia;
        this.discountPrice = discountPrice;
        this.coupon_pwd = coupon_pwd;
        this.type = "3";
    }


    /**
     * Description：京东
     */
    public HotGoods(String title, String img, String price, String quota, String link) {
        this.title = title;
        this.img = img;
        this.price = price;
        this.quota = quota;
        this.link = link;
        this.type = "4";
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
