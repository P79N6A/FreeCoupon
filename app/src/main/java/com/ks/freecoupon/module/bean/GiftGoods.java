package com.ks.freecoupon.module.bean;

/**
 * author：康少
 * date：2019/4/20
 * description：
 */
public class GiftGoods {


    private String id;
    private String name;

    public GiftGoods(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
