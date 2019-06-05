package com.ks.freecoupon.override;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.ks.freecoupon.R;
import com.ks.freecoupon.ks_interface.OnPopWindowItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * author：康少
 * date：2018/10/12
 * description：点击后的弹出框
 */
public class CustomPopWindow extends PopupWindow {
    private Context context;
    private RecyclerView rv_list;
    private CustomPopWindowAdapter customPopWindowAdapter;
    private List<CustomPopWindowItem> popWindowItemList;

    private OnPopWindowItemClickListener onPopWindowItemClickListener;

    public CustomPopWindow(Context context, List<CustomPopWindowItem> list, OnPopWindowItemClickListener onPopWindowItemClickListener) {
        super(context);
        this.context = context;
        if (popWindowItemList == null) {
            popWindowItemList = new ArrayList<>();
        } else {
            popWindowItemList.clear();
        }
        popWindowItemList.addAll(list);
        this.onPopWindowItemClickListener = onPopWindowItemClickListener;
        initObject();
    }

    private void initObject() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_popupwindow, null);
        rv_list = view.findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new GridLayoutManager(context, 1));

        customPopWindowAdapter = new CustomPopWindowAdapter(context, this);
        if (onPopWindowItemClickListener != null) {
            customPopWindowAdapter.setOnPopWindowItemClickListener(onPopWindowItemClickListener);
        }
        rv_list.setAdapter(customPopWindowAdapter);
        customPopWindowAdapter.addList(popWindowItemList);
        setContentView(view);
        initWindow();
    }

    private void initWindow() {
        DisplayMetrics d = context.getResources().getDisplayMetrics();
//        this.setWidth((int) (d.widthPixels * 0.4));
        this.setWidth(LayoutParams.WRAP_CONTENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
//        backgroundAlpha((Activity) context, 0.8f);//0.0-1.0
//        this.setOnDismissListener(new OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                backgroundAlpha((Activity) context, 1f);
//            }
//        });
    }

    //设置添加屏幕的背景透明度
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void showAtBottom(View view) {
        //弹窗位置设置
        showAsDropDown(view, Math.abs((view.getWidth() - getWidth()) / 2), 10);
        //showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 10, 110);//有偏差
    }

    public void showAtBottom(View view, int xOff, int yOff) {
        //弹窗位置设置
        showAsDropDown(view, xOff, yOff);
    }

    public void showAtTop(View view) {
        showAsDropDown(view, Math.abs((view.getWidth() - getWidth()) / 2), -(view.getHeight() / 2));
    }

    @Override
    public void showAsDropDown(View anchor) {
        showAsDropDown(anchor, 0, 0);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT == 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor, xoff, yoff);
    }
}
