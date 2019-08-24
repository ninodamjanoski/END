package com.endumedia.core.utils;

import android.content.Context;
import android.graphics.Path;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

public class UiUtil {
    private static Path sBulletPath;

    public interface OnSpanClickListener {
        void onClick(String str);
    }

    public static void getRidOfInitialSelectionTabLayout(TabLayout tabLayout) {
        View childAt = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
        if (childAt != null) {
            childAt.setSelected(false);
        }
    }

    public static int convertDpToPixels(Context context, int i) {
        return (int) TypedValue.applyDimension(1, (float) i, context.getResources().getDisplayMetrics());
    }

    public static void clearViews(View... viewArr) {
        for (View view : viewArr) {
            if (view instanceof TextView) {
                ((TextView) view).setText("");
            } else if (view instanceof ImageView) {
                ((ImageView) view).setImageDrawable(null);
            } else if (view instanceof ViewGroup) {
                ((ViewGroup) view).removeAllViews();
            }
        }
    }

    public static void goneViews(View... viewArr) {
        for (View visibility : viewArr) {
            visibility.setVisibility(View.GONE);
        }
    }
}
