package com.ks.freecoupon.override;

/**
 * author：康少
 * date：2018/10/13
 * description：PopWindow的item模型
 */
public class CustomPopWindowItem {
    private String index;//控件名称
    private int icon;//图标,0:没有图标
    private String textName;//图标后面的名称

    public CustomPopWindowItem(String index, int icon, String textName) {
        this.index = index;
        this.icon = icon;
        this.textName = textName;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTextName() {
        return textName;
    }

    public void setTextName(String textName) {
        this.textName = textName;
    }
}
