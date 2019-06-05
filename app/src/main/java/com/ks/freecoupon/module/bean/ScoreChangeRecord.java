package com.ks.freecoupon.module.bean;

/**
 * author：康少
 * date：2019/4/17
 * description：积分兑换记录
 */
public class ScoreChangeRecord {
    private String order_id;//单号
    private String name;//兑换的商品名称
    private String ctime;//创建时间
    private String point;//积分数量
    private String mark;//虚拟物品发货说明
    private String is_physical;//1=虚拟 0=实物
    private String delivery_code;//物流单号
    private String delivery_name;//物流名称

    /**
     * Description：虚物构造方法
     */
    public ScoreChangeRecord(String order_id,String name, String ctime, String point, String mark, String is_physical) {
        this.name = name;
        this.order_id = order_id;
        this.ctime = ctime;
        this.point = point;
        this.mark = mark;
        this.is_physical = is_physical;
    }

    /**
     * Description：实物构造方法
     */
    public ScoreChangeRecord(String order_id, String name, String ctime, String point, String is_physical, String delivery_code, String delivery_name) {
        this.name = name;
        this.order_id = order_id;
        this.ctime = ctime;
        this.point = point;
        this.is_physical = is_physical;
        this.delivery_code = delivery_code;
        this.delivery_name = delivery_name;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getIs_physical() {
        return is_physical;
    }

    public void setIs_physical(String is_physical) {
        this.is_physical = is_physical;
    }

    public String getDelivery_code() {
        return delivery_code;
    }

    public void setDelivery_code(String delivery_code) {
        this.delivery_code = delivery_code;
    }

    public String getDelivery_name() {
        return delivery_name;
    }

    public void setDelivery_name(String delivery_name) {
        this.delivery_name = delivery_name;
    }
}
