package com.ks.freecoupon.override;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ks.freecoupon.R;
import com.ks.freecoupon.ks_interface.OnPopWindowItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * author：康少
 * date：2018/10/13
 * description：公共PopWindow的Item
 */
public class CustomPopWindowAdapter extends RecyclerView.Adapter {
    private Context context;
    private PopupWindow popWindow;
    private List<CustomPopWindowItem> mDatas;
    private OnPopWindowItemClickListener onPopWindowItemClickListener;

    public CustomPopWindowAdapter(Context context, PopupWindow popWindow) {
        this.context = context;
        this.popWindow = popWindow;
    }

    public void addList(List<CustomPopWindowItem> list) {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        } else {
            mDatas.clear();
            notifyDataSetChanged();
        }
        mDatas.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnPopWindowItemClickListener(OnPopWindowItemClickListener onPopWindowItemClickListener) {
        this.onPopWindowItemClickListener = onPopWindowItemClickListener;
    }

    class CustomHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_container;
        private ImageView iv_icon;
        private TextView tv_id, tv_textName;

        public CustomHolder(View root) {
            super(root);
            ll_container = root.findViewById(R.id.ll_container);
            tv_id = root.findViewById(R.id.tv_id);
            iv_icon = root.findViewById(R.id.iv_icon);
            tv_textName = root.findViewById(R.id.tv_textName);
        }

        public LinearLayout getLl_container() {
            return ll_container;
        }

        public ImageView getIv_icon() {
            return iv_icon;
        }

        public TextView getTv_id() {
            return tv_id;
        }

        public TextView getTv_textName() {
            return tv_textName;
        }

        public void setLl_container(LinearLayout ll_container) {
            this.ll_container = ll_container;
        }

        public void setIv_icon(ImageView iv_icon) {
            this.iv_icon = iv_icon;
        }

        public void setTv_id(TextView tv_id) {
            this.tv_id = tv_id;
        }

        public void setTv_textName(TextView tv_textName) {
            this.tv_textName = tv_textName;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popupwindow, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CustomHolder tv = (CustomHolder) holder;
        final CustomPopWindowItem dd = mDatas.get(position);
        tv.getTv_id().setText(dd.getIndex());
        if (dd.getIcon() != 0) {
            tv.getIv_icon().setImageResource(dd.getIcon());
        } else {
            tv.getIv_icon().setVisibility(View.GONE);
        }
        tv.getTv_textName().setText(dd.getTextName());
        tv.ll_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPopWindowItemClickListener != null) {
                    onPopWindowItemClickListener.onClick(popWindow, dd.getIndex());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }
}
