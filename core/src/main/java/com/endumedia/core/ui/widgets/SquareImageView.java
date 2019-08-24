package com.endumedia.core.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.endumedia.core.R;

public class SquareImageView extends AppCompatImageView {
    public SquareImageView(Context context) {
        super(context);
        commonInit();
    }

    public SquareImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        commonInit();
    }

    public SquareImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        commonInit();
    }

    private void commonInit() {
        setBackgroundColor(getResources().getColor(R.color.windowBackground));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        doOnMeasure(i, i2);
    }

    protected void doOnMeasure(int i, int i2) {
        i = getMeasuredWidth();
        setMeasuredDimension(i, i);
    }
}
