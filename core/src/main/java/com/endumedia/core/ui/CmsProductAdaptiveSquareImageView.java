package com.endumedia.core.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import androidx.appcompat.widget.AppCompatImageView;

public class CmsProductAdaptiveSquareImageView extends AppCompatImageView {
    public CmsProductAdaptiveSquareImageView(Context context) {
        super(context);
    }

    public CmsProductAdaptiveSquareImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CmsProductAdaptiveSquareImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        doOnMeasure(i, i2);
    }

    protected void doOnMeasure(int i, int i2) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        i = Math.round(((float) ((int) ((((float) Math.round(((float) displayMetrics.widthPixels) /
                (Resources.getSystem().getDisplayMetrics().xdpi / 160.0f))) / 397.0f) * 125.0f))) * (Resources.getSystem().getDisplayMetrics().xdpi / 160.0f));
        setMeasuredDimension(i, i);
    }
}
