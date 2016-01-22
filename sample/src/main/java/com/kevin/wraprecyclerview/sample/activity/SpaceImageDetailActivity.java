package com.kevin.wraprecyclerview.sample.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.kevin.wraprecyclerview.sample.R;
import com.kevin.wraprecyclerview.sample.bean.PictureData;
import com.kevin.wraprecyclerview.sample.progressbutton.CircularProgressButton;
import com.kevin.wraprecyclerview.sample.utils.SmoothImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


public class SpaceImageDetailActivity extends Activity {

    private PictureData.Picture mDatas;
    private int mPosition;
    private int mLocationX;
    private int mLocationY;
    private int mWidth;
    private int mHeight;
    SmoothImageView imageView = null;
    private CircularProgressButton buttonflat_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatas = (PictureData.Picture) getIntent().getSerializableExtra("images");
        mPosition = getIntent().getIntExtra("position", 0);
        mLocationX = getIntent().getIntExtra("locationX", 0);
        mLocationY = getIntent().getIntExtra("locationY", 0);
        mWidth = getIntent().getIntExtra("width", 0);
        mHeight = getIntent().getIntExtra("height", 0);

        setContentView(R.layout.image_detail);
        imageView= (SmoothImageView) findViewById(R.id.imagesss);
        buttonflat_set= (CircularProgressButton) findViewById(R.id.buttonflat_set);
        imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
        imageView.transformIn();
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(-1,-1));
        imageView.setScaleType(ScaleType.FIT_CENTER);
        ImageLoader.getInstance().displayImage(mDatas.IMAGE_URL, imageView);
//        Glide.with(this)
//                .load(mDatas.IMAGE_URL)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .error(R.drawable.error)
//                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonflat_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set();
            }
        });
        findViewById(R.id.shareText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
    }


    private void set(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(SpaceImageDetailActivity.this);
                Bitmap bitmap= ImageLoader.getInstance().loadImageSync(mDatas.IMAGE_URL);
                try
                {
                    wallpaperManager.setBitmap(bitmap);

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (buttonflat_set.getProgress() == 0) {
                    simulateSuccessProgress(buttonflat_set);
                } else {
                    buttonflat_set.setProgress(0);
                }

            }
        }.execute();
    }



    /**
     * 成功
     */
    private void simulateSuccessProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 100);
        widthAnimation.setDuration(1000);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        button.setProgress(value);
                    }
                });
        widthAnimation.start();
    }

    /**
     * 失败
     */
    private void simulateErrorProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 99);
        widthAnimation.setDuration(1500);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        button.setProgress(value);
                        if (value == 99) {
                            button.setProgress(-1);
                        }
                    }
                });
        widthAnimation.start();
    }


    @Override
    public void onBackPressed() {
        imageView.setOnTransformListener(new SmoothImageView.TransformListener() {
            @Override
            public void onTransformComplete(int mode) {
                if (mode == 2) {
                    finish();
                }
            }
        });
        imageView.transformOut();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
    private void showShare(){
        ShareSDK.initSDK(getBaseContext());
        OnekeyShare oks = new OnekeyShare();
        // 分享时Notification的图标和文字
//        oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        oks.setText("硬了么-分享");
        oks.setUrl(mDatas.IMAGE_URL);
        oks.setImageUrl(mDatas.IMAGE_URL);
        // 启动分享GUI
        oks.show(getBaseContext());
    }

}
