package com.endumedia.core.ui.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.endumedia.core.R;
import com.endumedia.core.utils.UiUtil;

import java.util.List;

public class HintAdapter<T> extends ArrayAdapter<T> {
    private static final int DEFAULT_LAYOUT_RESOURCE = 17367049;
    private static final String TAG = "HintAdapter";
    private Context context;
    private String hintResource;
    private boolean isView;
    private final LayoutInflater layoutInflater;
    private int selectedBottomPosition;
    private int selectedTopPosition;

    public HintAdapter(Context context, int i, List<T> list) {
        this(context, DEFAULT_LAYOUT_RESOURCE, context.getString(i), list);
        this.context = context;
    }

    public HintAdapter(Context context, String str, List<T> list) {
        this(context, DEFAULT_LAYOUT_RESOURCE, str, list);
        this.context = context;
    }

    public HintAdapter(Context context, int i, int i2, List<T> list, boolean z) {
        this(context, i, context.getString(i2), list);
        this.context = context;
        this.isView = z;
    }

    public HintAdapter(Context context, int i, String str, List<T> list) {
        super(context, i, list);
        this.selectedTopPosition = 0;
        this.selectedBottomPosition = 3;
        this.hintResource = str;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int i, View view, ViewGroup viewGroup) {
        View view2 = view;
        FrameLayout frameLayout = null;
        switch (i) {
            case 0:
                view2 = (RelativeLayout) inflateLayout(R.layout.product_list_view_row, viewGroup, false);
                frameLayout = (FrameLayout) view2.findViewById(R.id.background_selection);
                if (this.isView) {
                    setMargin(frameLayout, true);
                    break;
                }
                break;
            case 1:
                if (this.isView) {
                    view2 = (RelativeLayout) inflateLayout(R.layout.product_list_view_row_with_divider, viewGroup, false);
                } else {
                    view2 = (RelativeLayout) inflateLayout(R.layout.product_list_view_row, viewGroup, false);
                }
                frameLayout = (FrameLayout) view2.findViewById(R.id.background_selection);
                if (this.isView) {
                    setMargin(frameLayout, false);
                    break;
                }
                break;
            case 2:
                view2 = (RelativeLayout) inflateLayout(R.layout.product_list_view_row, viewGroup, false);
                frameLayout = (FrameLayout) view2.findViewById(R.id.background_selection);
                if (this.isView) {
                    setMargin(frameLayout, true);
                    break;
                }
                break;
            case 3:
                view2 = (RelativeLayout) inflateLayout(R.layout.product_list_view_row, viewGroup, false);
                frameLayout = (FrameLayout) view2.findViewById(R.id.background_selection);
                if (this.isView) {
                    setMargin(frameLayout, false);
                    break;
                }
                break;
            default:
                view2 = (RelativeLayout) inflateLayout(R.layout.product_list_view_row, viewGroup, false);
                break;
        }
        TextView textView = (TextView) view2.findViewById(R.id.title);
        textView.setText(getItem(i).toString());
        textView.setHint("");
        if (i == this.selectedTopPosition || i == this.selectedBottomPosition) {
            if (this.isView) {
                ((ImageView) view2.findViewById(R.id.tick)).setVisibility(View.VISIBLE);
            }
            frameLayout.setBackgroundColor(ContextCompat.getColor(this.context, R.color.lightestgrey));
        }
        return view2;
    }

    private View inflateDefaultLayout(ViewGroup viewGroup) {
        return inflateLayout(DEFAULT_LAYOUT_RESOURCE, viewGroup, false);
    }

    private View inflateLayout(int i, ViewGroup viewGroup, boolean z) {
        return this.layoutInflater.inflate(i, viewGroup, z);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (i == getHintPosition()) {
            return getDefaultView(viewGroup);
        }
        return getCustomView(i, view, viewGroup);
    }

    private View getDefaultView(ViewGroup viewGroup) {
        View inflateDefaultLayout = inflateDefaultLayout(viewGroup);
        TextView textView = (TextView) inflateDefaultLayout.findViewById(android.R.id.text1);
        textView.setText("");
        textView.setHint(this.hintResource);
        textView.setPadding(UiUtil.convertDpToPixels(this.context, (int) this.context.getResources().getDimension(R.dimen.screen_padding)) / 2, 0, 0, 0);
        textView.setHintTextColor(ViewCompat.MEASURED_STATE_MASK);
        return inflateDefaultLayout;
    }

    public int getHintPosition() {
        int count = getCount();
        return count > 0 ? count + 1 : count;
    }

    public void setSelectedTopPosition(int i) {
        this.selectedTopPosition = i;
    }

    public void setSelectedBottomPosition(int i) {
        this.selectedBottomPosition = i;
    }

    private void setMargin(FrameLayout frameLayout, boolean z) {
        LayoutParams layoutParams = (LayoutParams) frameLayout.getLayoutParams();
        int convertDpToPixels = UiUtil.convertDpToPixels(this.context, 5);
        if (z) {
            layoutParams.setMargins(0, convertDpToPixels, 0, 0);
        } else {
            layoutParams.setMargins(0, 0, 0, convertDpToPixels);
        }
        frameLayout.setLayoutParams(layoutParams);
    }
}
