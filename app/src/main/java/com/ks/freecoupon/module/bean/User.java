package com.ks.freecoupon.module.bean;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

/**
 * author：康少
 * date：2019/3/23
 * description：
 */
public class User implements Serializable{
    public static User mInstance;
    private int id = 0;//id
    private String mobile = "";//手机号
    private String avatar = "http:\\/\\/card.benwunet.com\\/static\\/images\\/default_avatar.png";//头像
    private String nickname = "未设置";//昵称
    private int point = 0;//积分数
    private int coupon = 0;//优惠券数量

    private String ship_name = "";//收货人姓名
    private String ship_mobile = "";//收货人电话
    private String ship_area = "";//省市区
    private String ship_address = "";//详细地址

    public static User getInstance() {
        if (mInstance == null) {
            synchronized (User.class) {
                if (mInstance == null) {
                    mInstance = new User();
                }
            }
        }
        return mInstance;
    }

    /**
     * Description：清空用户
     */
    public void emptyUser() {
        mInstance.setId(0);
        mInstance.setMobile("");
        mInstance.setAvatar("http://card.benwunet.com/static/images/default_avatar.png");
        mInstance.setNickname("未设置");
        mInstance.setShip_name("");
        mInstance.setShip_mobile("");
        mInstance.setShip_area("");
        mInstance.setShip_address("");
        mInstance.setPoint(0);
        mInstance.setCoupon(0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getCoupon() {
        return coupon;
    }

    public void setCoupon(int coupon) {
        this.coupon = coupon;
    }

    public String getShip_name() {
        return ship_name;
    }

    public void setShip_name(String ship_name) {
        this.ship_name = ship_name;
    }

    public String getShip_mobile() {
        return ship_mobile;
    }

    public void setShip_mobile(String ship_mobile) {
        this.ship_mobile = ship_mobile;
    }

    public String getShip_address() {
        return ship_address;
    }

    public void setShip_address(String ship_address) {
        this.ship_address = ship_address;
    }

    public String getShip_area() {
        return ship_area;
    }

    public void setShip_area(String ship_area) {
        this.ship_area = ship_area;
    }
}
