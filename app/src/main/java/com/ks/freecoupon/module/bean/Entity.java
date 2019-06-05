package com.ks.freecoupon.module.bean;

import java.io.Serializable;
import java.util.List;

/**
 * author：康少
 * date：2019/3/25
 * description：实物模型
 */
public class Entity implements Serializable{
    private String id;
    private String title;
    private String num;
    private String desc;
    private List<String> imageList;

    public Entity(String id, String title, String num, String desc, List<String> imageList) {
        this.id = id;
        this.title = title;
        this.num = num;
        this.desc = desc;
        this.imageList = imageList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }
}
