package com.ziwenwen.onekeychat.view;

import android.content.Context;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;


/**
 * Created by WenZiwen on 2015/6/29.
 * 宽高等比的ImageView
 */
public class SquareImageView extends AppCompatImageView {
    private boolean mAsHeight = false;
    /**
     * 宽高比
     */
    private float mRatio = 1.0f;
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!mAsHeight) {
            int width = getMeasuredWidth();
            setMeasuredDimension(width, (int) (width * mRatio));
        } else {
            int height = getMeasuredHeight();
            setMeasuredDimension((int) (height / mRatio), height);
        }
    }

    public void setRatio(float value) {
        mRatio = value;
    }

    public void setmAsHeight(boolean mAsHeight) {
        this.mAsHeight = mAsHeight;
    }
}
