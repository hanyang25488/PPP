package com.kevin.wraprecyclerview.sample.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kevin.jsontool.JsonTool;
import com.kevin.loopview.AdLoopView;
import com.kevin.wraprecyclerview.BaseRecyclerAdapter;
import com.kevin.wraprecyclerview.WrapAdapter;
import com.kevin.wraprecyclerview.WrapRecyclerView;
import com.kevin.wraprecyclerview.sample.R;
import com.kevin.wraprecyclerview.sample.adapter.PictureAdapter;
import com.kevin.wraprecyclerview.sample.bean.PictureData;
import com.kevin.wraprecyclerview.sample.utils.LocalFileUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zhouwk on 2015/12/25 0025.
 */
public class WarpRecyclerViewActivity extends AppCompatActivity {

    private WrapRecyclerView mWrapRecyclerView;
    private MaterialRefreshLayout layout;
    public static long mExitTime;//退出时间
    private String refreshDate, addHeader;
    private boolean isFirstData = true;
    private PictureAdapter mAdapter;
    private PictureData pictureData;
    private WrapAdapter mWrapAdapter;
    private ArrayList<PictureData.Picture> allList = new ArrayList<>();
    private ArrayList<PictureData.Picture> list = new ArrayList<>();
    int k = 0;
    int j = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrap_recycler);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        refreshDate = getString(R.string.menu_refresh_data);
        addHeader = getString(R.string.menu_add_header);
        initViews();
        addHeaderView();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        layout = (MaterialRefreshLayout) this.findViewById(R.id.refresh);
        mWrapRecyclerView = (WrapRecyclerView) this.findViewById(R.id.wrap_recycler_act_recycler_view);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWrapRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // 创建数据适配器
        mAdapter = new PictureAdapter(this);
        mWrapRecyclerView.setAdapter(mAdapter);
        // 获取包装类适配器，因为要用它去刷新数据
        mWrapAdapter = mWrapRecyclerView.getAdapter();
        initData();
        mAdapter.setOnRecyclerViewListener(new BaseRecyclerAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (list.size() > 0) {

                    Log.e("ppp", position + "");
                    Intent intent = new Intent(WarpRecyclerViewActivity.this, SpaceImageDetailActivity.class);
                    intent.putExtra("images", (Serializable) allList.get(position));
                    intent.putExtra("position", position);
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    intent.putExtra("locationX", location[0]);
                    intent.putExtra("locationY", location[1]);
                    intent.putExtra("width", view.getWidth());
                    intent.putExtra("height", view.getHeight());
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else {
                    Toast.makeText(WarpRecyclerViewActivity.this, "没图!,骚年~", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        layout.setLoadMore(true);
        layout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                Log.e("ref", "ref");
                initData();
                layout.finishRefresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                Log.e("more", "more");
                ref();
                layout.finishRefreshLoadMore();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(refreshDate);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equals(refreshDate)) {
            initData();
        }
        return true;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        final ProgressDialog dialog = new ProgressDialog(this);
        HttpUtils httpUtils = new HttpUtils(60000);

        httpUtils.send(HttpRequest.HttpMethod.GET, "http://www.xdhtxt.com/DBANK/hello.html", new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String response = responseInfo.result;
                pictureData = JsonTool.toBean(response, PictureData.class);
                allList = (ArrayList<PictureData.Picture>) pictureData.list;
                for (int i = 0; i <10; i++) {
                    list.add(pictureData.list.get(i));
                    Log.e("P", i + "");
                }
                mAdapter.setItemLists(list); // 数据适配器设置数据
                mWrapAdapter.notifyDataSetChanged();// 包装类适配器刷新数据
                dialog.dismiss();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(WarpRecyclerViewActivity.this, "没网!,骚年~", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        isFirstData = !isFirstData;

    }

    private void ref() {
        if (j <= allList.size()) {
            for (int i = 0; i <= 10; i++) {
                list.add(allList.get(i + j));
                Log.e("P", i + j + "");
                mAdapter.setItemLists(list); // 数据适配器设置数据
                mWrapAdapter.notifyDataSetChanged();// 包装类适配器刷新数据
            }
        } else {
            Toast.makeText(WarpRecyclerViewActivity.this, "到底了!,骚年~", Toast.LENGTH_SHORT).show();
        }
        j = j + 11;
    }

    /**
     * 添加头部View LoopView
     * <p/>
     * 这里使用的是LoopView开源项目，项目地址：https://github.com/xuehuayous/Android-LoopView
     *
     * @return void
     */
    private void addHeaderView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.recycler_header, null);
        AdLoopView mAdLoopView = (AdLoopView) layout.findViewById(R.id.home_frag_rotate_vp);
        mWrapAdapter.addHeaderView(layout);

        // 初始化LoopView数据
        String json = LocalFileUtils.getStringFormAsset(this, "loopview.json");
        mAdLoopView.refreshData(json);
        mAdLoopView.startAutoLoop();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Toast.makeText(WarpRecyclerViewActivity.this, "确定不看了? 再按一次,骚年~", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                mExitTime = 0;
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
