package com.ks.freecoupon.module.bean;

/**
 * author：康少
 * date：2019/3/26
 * description：搜索词
 */
public class Word {
    private String id;
    private String name;

    public Word() {
    }

    public Word(String id, String name) {
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
