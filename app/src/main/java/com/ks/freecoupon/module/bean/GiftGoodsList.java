package com.ks.freecoupon.module.bean;

import java.util.List;

/**
 * author：康少
 * date：2019/4/20
 * description：
 */
public class GiftGoodsList {

    public static GiftGoodsList mInstance;

    private List<GiftGoods> giftGoodsList;
    private String[] nameList;

    public static GiftGoodsList getInstance() {
        if (mInstance == null) {
            synchronized (GiftGoodsList.class) {
                if (mInstance == null) {
                    mInstance = new GiftGoodsList();
                }
            }
        }
        return mInstance;
    }

    public List<GiftGoods> getGiftGoodsList() {
        return giftGoodsList;
    }
    public void setGiftGoodsList(List<GiftGoods> giftGoodsList) {
        this.giftGoodsList = giftGoodsList;
    }

    public String[] getNameList() {
        return nameList;
    }

    public void setNameList(String[] nameList) {
        this.nameList = nameList;
    }
}
