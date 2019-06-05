package com.ks.freecoupon.module.bean;

/**
 * author：康少
 * date：2019/5/9
 * description：抽奖记录
 */
public class RecordBean {
    private String ctime;
    private String gift_name;

    public RecordBean(String ctime, String gift_name) {
        this.ctime = ctime;
        this.gift_name = gift_name;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getGift_name() {
        return gift_name;
    }

    public void setGift_name(String gift_name) {
        this.gift_name = gift_name;
    }
}
