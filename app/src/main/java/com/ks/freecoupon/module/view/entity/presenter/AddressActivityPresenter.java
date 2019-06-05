package com.ks.freecoupon.module.view.entity.presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.ks_interface.OnCountDownTimerListener;
import com.ks.freecoupon.module.bean.JsonBean;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.module.contract.entity.AddressContract;
import com.ks.freecoupon.module.view.account.activity.ResetPasswordActivity;
import com.ks.freecoupon.module.view.entity.activity.AddressActivity;
import com.ks.freecoupon.override.MyCountDownTimer;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.GetJsonDataUtil;
import com.ks.freecoupon.utils.KeybordS;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Date:2019/3/25
 * Author：康少
 * Description：收货地址——逻辑层
 */
public class AddressActivityPresenter implements AddressContract.IAddressActivityPresenter {
    private AddressActivity mAddressActivity;
    /*控件对象*/
    private TextView tv_city;//省、市、区
    private EditText et_linkman;//收货联系人
    private EditText et_phone;//联系方式
    private EditText et_address;//详细地址
    private TextView tv_phone;//手机号
    private TextView tv_sendYZM;//发送验证码
    private EditText et_yzm;//验证码
    /*参数*/
    private List<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Handler mHandler;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private Thread thread;
    private static boolean isLoaded = false;
    //new倒计时对象,总共的时间,每隔多少秒更新一次时间
    private MyCountDownTimer myCountDownTimer;

    public AddressActivityPresenter(AddressActivity mAddressActivity) {
        this.mAddressActivity = mAddressActivity;
        mAddressActivity.setPresenter(this);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void start() {
        initObject();//初始化对象
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_LOAD_DATA:
                        if (thread == null) {//如果已创建就不再重新创建子线程了
                            LogUtils.d("开始解析地区数据");
                            thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // 子线程中解析省市区数据
                                    initJsonData();
                                }
                            });
                            thread.start();
                        }
                        break;
                    case MSG_LOAD_SUCCESS:
                        LogUtils.d("数据解析成功");
                        isLoaded = true;
                        break;
                    case MSG_LOAD_FAILED:
                        LogUtils.d("数据解析失败");
                        break;
                }
            }
        };
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
        initView();
    }

    private void initObject() {
        tv_city = mAddressActivity.getTv_city();
        et_linkman = mAddressActivity.getEt_linkman();
        et_phone = mAddressActivity.getEt_phone();
        et_address = mAddressActivity.getEt_address();
        tv_phone = mAddressActivity.getTv_phone();
        et_yzm = mAddressActivity.getEt_yzm();
        tv_sendYZM = mAddressActivity.getTv_sendYZM();
        Button btn_confirm = mAddressActivity.getBtn_confirm();

        tv_sendYZM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendYZM();
            }
        });

        tv_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCityClick();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmClick();
            }
        });

        myCountDownTimer = new MyCountDownTimer(60000, 1000);
        myCountDownTimer.setOnCountDownTimerListener(new OnCountDownTimerListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                //防止计时过程中重复点击
                tv_sendYZM.setClickable(false);
                tv_sendYZM.setText("重新发送" + l / 1000 + "s");
            }
            @Override
            public void onFinish() {
                //重新给Button设置文字
                tv_sendYZM.setText("获取验证码");
                //设置可点击
                tv_sendYZM.setClickable(true);
            }
        });
    }

    /**
     * Description：初始化视图
     */
    private void initView() {
        if (!User.getInstance().getNickname().equals("")) {
            et_linkman.setText(User.getInstance().getShip_name());
        }
        if (!User.getInstance().getMobile().equals("")) {
            et_phone.setText(User.getInstance().getShip_mobile());
        }
        if (!User.getInstance().getShip_area().equals("")) {
            tv_city.setText(User.getInstance().getShip_area());
        }
        if (!User.getInstance().getShip_address().equals("")) {
            et_address.setText(User.getInstance().getShip_address());
        }
        String mobile = User.getInstance().getMobile();
        String first = mobile.substring(0, 3);
        String end = mobile.substring(7, 11);
        String phone = first + "****" + end;
        tv_phone.setText(phone);
    }

    /**
     * Description：解析地址数据
     */
    private void initJsonData() {
        /*
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         */
        String JsonData = new GetJsonDataUtil().getJson(mAddressActivity, "province.json");//获取assets目录下的json文件数据
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /*
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);//添加城市

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getShip_area() == null
                        || jsonBean.get(i).getCityList().get(c).getShip_area().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getShip_area());
                }*/
                ArrayList<String> city_AreaList = new ArrayList<>(jsonBean.get(i).getCityList().get(c).getArea());
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }
            /*
             * 添加城市数据
             */
            options2Items.add(cityList);
            /*
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }
        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
    }

    /**
     * Description：解析json数据
     */
    private ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    private void showPickerView() {//条件选择器初始化
        OptionsPickerView pvOptions = new OptionsPickerBuilder(mAddressActivity, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getPickerViewText() : "";

                String opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";

                String opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : "";

                String tx = opt1tx + " " + opt2tx + " " + opt3tx;
                tv_city.setText(tx);
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    /**
     * Description：省市选择器
     */
    private void onCityClick() {
        if (isLoaded) {
            boolean softInputShow = KeybordS.isSoftInputShow(mAddressActivity);
            if (softInputShow) {
                KeybordS.closeKeybord(et_linkman, mAddressActivity);
                KeybordS.closeKeybord(et_phone, mAddressActivity);
                KeybordS.closeKeybord(et_address, mAddressActivity);
            }
            showPickerView();
        } else {
            ToastKs.show(mAddressActivity, "正在解析地区数据，请稍后");
        }
    }

    /**
     * Description：提交点击事件
     */
    private void onConfirmClick() {
        if (isEditTextEmpty()) {
            ToastKs.show(mAddressActivity,"请填写相关内容");
            return;
        }
        updateAddress();
    }

    /**
     * Description：更新收货地址
     */
    private void updateAddress() {
        // 更新收货地址
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mAddressActivity, "token", ""));
        map.put("nickname", User.getInstance().getNickname());
        map.put("code", et_yzm.getText().toString());
        map.put("ship_name", et_linkman.getText().toString());
        map.put("ship_mobile", et_phone.getText().toString());
        map.put("ship_area", tv_city.getText().toString());
        map.put("ship_address", et_address.getText().toString());
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    User user = User.getInstance();
                    user.setShip_name(et_linkman.getText().toString());
                    user.setShip_mobile(et_phone.getText().toString());
                    user.setShip_area(tv_city.getText().toString());
                    user.setShip_address(et_address.getText().toString());
                    ToastKs.show(mAddressActivity, "保存成功");
                    //通知 ConformOrderActivity提交订单 更新收货人信息
                    EventBus.getDefault().post(user);
                    mAddressActivity.finish();
                } else {
                    ToastUtil.show(mAddressActivity, JSONUtil.getString(response, "msg", ""));
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
            }
        });
        okHttpUtil.requestCall(mAddressActivity, "user.editinfo", list);
    }

        /**
     * Description：发送验证码
     */
    private void sendYZM() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", User.getInstance().getMobile());
        map.put("type", "change_ship");// 类型
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    ToastKs.show(mAddressActivity, "发送成功！");
                    //获取验证码倒计时
                    myCountDownTimer.start();
                } else {
                    ToastUtil.show(mAddressActivity, JSONUtil.getString(response, "msg", ""));
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {

            }
        });
        okHttpUtil.requestCall(mAddressActivity, "user.sms", list);
    }

    /**
     * Description：输入框是否为空
     */
    private boolean isEditTextEmpty() {
        if (et_linkman.getText().toString().equals("")) {
            return true;
        }
        if (et_phone.getText().toString().equals("")) {
            return true;
        }
        if (tv_city.getText().toString().equals("")) {
            return true;
        }
        if (et_address.getText().toString().equals("")) {
            return true;
        }
        if (et_yzm.getText().toString().equals("")) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
