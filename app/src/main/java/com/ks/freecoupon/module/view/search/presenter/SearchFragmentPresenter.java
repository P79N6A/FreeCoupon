package com.ks.freecoupon.module.view.search.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ks.basictools.overView.CommonAdapter;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.Word;
import com.ks.freecoupon.module.contract.search.SearchFragmentContract;
import com.ks.freecoupon.module.view.MainActivity;
import com.ks.freecoupon.module.view.search.activity.SearchResultActivity;
import com.ks.freecoupon.module.view.search.fragment.SearchFragment;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.KeybordS;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * author：康少
 * date：2019/3/21
 * description：搜素——逻辑层
 */
public class SearchFragmentPresenter implements SearchFragmentContract.ISearchFragmentPresenter {
    private SearchFragment mSearchFragment;
    private Context mContext;
    /*控件对象*/
    private EditText etSearchClazz;
    private RecyclerView rvHotSearch;
    private RecyclerView rvSearchHistory;

    /*参数*/
    private Handler mHandler;
    private List<Word> hotWordList;
    private List<String> historyWordList;
    private CommonAdapter mAdapterSearchHistory;
    private final String SEARCH_HISTORY = "searchHistoryList";

    public SearchFragmentPresenter(SearchFragment mSearchFragment) {
        this.mSearchFragment = mSearchFragment;
        mSearchFragment.setPresenter(this);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void start() {
        initObject();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        applyView();
                        break;
                }
            }
        };
        getHotWord();
        showHistory();
    }

    private void initObject() {
        etSearchClazz = mSearchFragment.getEtSearchClazz();
        rvHotSearch = mSearchFragment.getRvHotSearch();
        rvSearchHistory = mSearchFragment.getRvSearchHistory();
        mContext = mSearchFragment.getmContext();

        hotWordList = new ArrayList<>();
        historyWordList = new ArrayList<>();
        rvHotSearch.setLayoutManager(new GridLayoutManager(mContext, 5));
        rvSearchHistory.setLayoutManager(new GridLayoutManager(mContext, 1));
    }

    /**
     * Description：获取热门搜索数据
     */
    private void getHotWord() {
        Word beef = new Word("1", "牛肉");
        Word yellowTea = new Word("2", "黄茶");
        Word blackTea = new Word("3", "黑茶");
        Word redTea = new Word("4", "红茶");
        Word whiteTea = new Word("5", "白茶");
        Word mango = new Word("6", "芒果");
        Word orange = new Word("7", "香橙");
        hotWordList.clear();
        hotWordList.add(beef);
        hotWordList.add(yellowTea);
        hotWordList.add(blackTea);
        hotWordList.add(redTea);
        hotWordList.add(whiteTea);
        hotWordList.add(mango);
        hotWordList.add(orange);
        mHandler.sendEmptyMessage(1);
    }

    /**
     * Description：显示搜索历史
     */
    private void showHistory() {
        historyWordList = SharedPreferencesUtils.getArrayList(mContext, SEARCH_HISTORY);
    }

    /**
     * Description：渲染视图
     */
    private void applyView() {
        /*加载搜索热词*/
        CommonAdapter mAdapterHotSearch = new CommonAdapter<Word>(mContext, R.layout.item_hotword, hotWordList) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void convert(final ViewHolder holder, final Word word, int position) {
                ((TextView) holder.getView(R.id.tv_id)).setText(word.getId());
                ((TextView) holder.getView(R.id.tv_name)).setText(word.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转搜索结果页
                        Intent intent = new Intent(mContext, SearchResultActivity.class);
                        intent.putExtra("keyWord", word.getName());
                        mContext.startActivity(intent);
                        addHistory(word.getName());
                    }
                });
            }
        };
        rvHotSearch.setAdapter(mAdapterHotSearch);
        /*加载搜索历史*/
        mAdapterSearchHistory = new CommonAdapter<String>(mContext, R.layout.item_searchhistory, historyWordList) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void convert(final ViewHolder holder, final String word, final int position) {
                ((TextView) holder.getView(R.id.tv_word)).setText(word);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转搜索结果页
                        Intent intent = new Intent(mContext, SearchResultActivity.class);
                        intent.putExtra("keyWord", word);
                        mContext.startActivity(intent);
                    }
                });
                holder.getView(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除操作
                        SharedPreferencesUtils.removeListItemByKeyword(mContext, SEARCH_HISTORY, word);
                        historyWordList.clear();
                        mAdapterSearchHistory.notifyDataSetChanged();
                        historyWordList.addAll(SharedPreferencesUtils.getArrayList(mContext, SEARCH_HISTORY));
                        mAdapterSearchHistory.notifyItemChanged(position);
                    }
                });
            }
        };
        rvSearchHistory.setAdapter(mAdapterSearchHistory);
    }

    /**
     * Description：添加到历史
     */
    private void addHistory(String word) {
        List<String> list = SharedPreferencesUtils.getArrayList(mContext, SEARCH_HISTORY);
        boolean flag = true;//如果搜索历史列表存在此关键词，则不添加到搜索历史列表中
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(word)) {
                    flag = false;
                }
            }
            if (flag) {
                list.add(0, word);
                historyWordList.clear();
                mAdapterSearchHistory.notifyDataSetChanged();
                historyWordList.addAll(list);
                mAdapterSearchHistory.notifyDataSetChanged();
                SharedPreferencesUtils.removeAllArrayList(mContext, SEARCH_HISTORY);
                SharedPreferencesUtils.setArrayList(mContext, SEARCH_HISTORY, list);
            }
        }else{
            List<String> wordList = new ArrayList<>();
            wordList.add(word);
            historyWordList.clear();
            mAdapterSearchHistory.notifyDataSetChanged();
            historyWordList.addAll(wordList);
            mAdapterSearchHistory.notifyDataSetChanged();
            SharedPreferencesUtils.setArrayList(mContext, SEARCH_HISTORY, wordList);
        }
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                String word = etSearchClazz.getText().toString();
                if (word.equals("")) {
                    ToastKs.show((MainActivity) mContext, "搜索关键词为空");
                } else {
                    //跳转搜索结果页
                    Intent intent = new Intent(mContext, SearchResultActivity.class);
                    intent.putExtra("keyWord", word);
                    mContext.startActivity(intent);
                }
                //收起键盘
                if (KeybordS.isSoftInputShow((MainActivity) mContext)) {
                    KeybordS.closeKeybord(etSearchClazz, mContext);
                }
                break;
            case R.id.tv_clearHistory:
                SharedPreferencesUtils.removeAllArrayList(mContext, SEARCH_HISTORY);
                historyWordList.clear();
                mAdapterSearchHistory.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
