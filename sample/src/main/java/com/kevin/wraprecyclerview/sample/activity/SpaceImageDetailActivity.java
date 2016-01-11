package com.kevin.wraprecyclerview.sample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gc.materialdesign.views.CheckBox;
import com.kevin.wraprecyclerview.sample.R;
import com.kevin.wraprecyclerview.sample.bean.PictureData;
import com.kevin.wraprecyclerview.sample.utils.SmoothImageView;
import com.nostra13.universalimageloader.core.ImageLoader;


public class SpaceImageDetailActivity extends Activity {

    private PictureData.Picture mDatas;
    private int mPosition;
    private int mLocationX;
    private int mLocationY;
    private int mWidth;
    private int mHeight;
    SmoothImageView imageView = null;
    private CheckBox checkBox;

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
        checkBox= (CheckBox) findViewById(R.id.checkboxs);
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
        checkBox.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(CheckBox view, boolean check) {
                if (check) {
                    Toast.makeText(SpaceImageDetailActivity.this, "已赞!,骚年~", Toast.LENGTH_SHORT).show();
                }
            }
        });
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


}
