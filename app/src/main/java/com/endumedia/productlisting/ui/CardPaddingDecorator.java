package com.endumedia.productlisting.ui;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.State;

public class CardPaddingDecorator extends ItemDecoration {
    private int headerRows;
    private int paddingBetweenH;
    private int paddingBetweenV;
    private int paddingBottom;
    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingVHeaderBetweenV;
    private int paddingVHeaderBottom;
    private int paddingVHeaderLeft;
    private int paddingVHeaderRight;
    private int paddingVHeaderTop;

    public CardPaddingDecorator(int i) {
        this.paddingLeft = i;
        this.paddingTop = i;
        this.paddingRight = i;
        this.paddingBottom = i;
        this.paddingBetweenH = i;
        this.paddingBetweenV = i;
        this.paddingVHeaderTop = i;
        this.paddingVHeaderBottom = i;
        this.paddingVHeaderBetweenV = i;
        this.paddingVHeaderLeft = i;
        this.paddingVHeaderRight = i;
    }

    public CardPaddingDecorator left(int i) {
        this.paddingLeft = i;
        return this;
    }

    public CardPaddingDecorator right(int i) {
        this.paddingRight = i;
        return this;
    }

    public CardPaddingDecorator top(int i) {
        this.paddingTop = i;
        return this;
    }

    public CardPaddingDecorator bottom(int i) {
        this.paddingBottom = i;
        return this;
    }

    public CardPaddingDecorator betweenHorizontal(int i) {
        this.paddingBetweenH = i;
        return this;
    }

    public CardPaddingDecorator betweenVertical(int i) {
        this.paddingBetweenV = i;
        return this;
    }

    public CardPaddingDecorator headerRowCount(int i) {
        this.headerRows = i;
        return this;
    }

    public CardPaddingDecorator headerBetweenRowsVertical(int i) {
        this.paddingVHeaderBetweenV = i;
        return this;
    }

    public CardPaddingDecorator headerTop(int i) {
        this.paddingVHeaderTop = i;
        return this;
    }

    public CardPaddingDecorator headerBottom(int i) {
        this.paddingVHeaderBottom = i;
        return this;
    }

    public CardPaddingDecorator headerLeft(int i) {
        this.paddingVHeaderTop = this.paddingVHeaderTop;
        return this;
    }

    public CardPaddingDecorator headerRight(int i) {
        this.paddingVHeaderTop = i;
        return this;
    }

    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
        int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
        LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null || !(layoutManager instanceof LinearLayoutManager)) {
            super.getItemOffsets(rect, view, recyclerView, state);
            return;
        }
        Object obj;
        Object obj2;
        Object obj3;
        Object obj4;
        Object obj5;
        Object obj6;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        int i = 1;
        int spanCount = linearLayoutManager instanceof GridLayoutManager ? ((GridLayoutManager) linearLayoutManager).getSpanCount() : 1;
        if (linearLayoutManager.getOrientation() == RecyclerView.VERTICAL) {
            obj = (childAdapterPosition < this.headerRows || (childAdapterPosition - this.headerRows) / spanCount != 0) ? null : 1;
            obj2 = (childAdapterPosition < 0 || childAdapterPosition >= this.headerRows) ? null : 1;
            obj3 = (obj2 == null || childAdapterPosition != 0) ? null : 1;
            obj4 = (obj2 == null || childAdapterPosition != this.headerRows - 1) ? null : 1;
            obj5 = (obj2 != null || (childAdapterPosition - this.headerRows) + spanCount < recyclerView.getAdapter().getItemCount()) ? null : 1;
            obj6 = (obj2 == null && (childAdapterPosition - this.headerRows) % spanCount == 0) ? 1 : null;
            if (!(obj2 == null && (((childAdapterPosition - this.headerRows) + spanCount) - 1) % spanCount == 0)) {
                i = 0;
            }
        } else {
            int i2 = childAdapterPosition % spanCount;
            obj2 = i2 == 0 ? 1 : null;
            obj = i2 == spanCount + -1 ? 1 : null;
            obj6 = childAdapterPosition < spanCount ? 1 : null;
            if (spanCount + childAdapterPosition < recyclerView.getAdapter().getItemCount()) {
                i = 0;
            }
            obj5 = obj;
            obj = obj2;
            obj2 = null;
            obj3 = null;
            obj4 = null;
        }
        if (obj6 != null) {
            rect.left = this.paddingLeft;
        } else if (obj2 != null) {
            rect.left = this.paddingVHeaderLeft;
        } else {
            rect.left = this.paddingBetweenH / 2;
        }
        if (i != 0) {
            rect.right = this.paddingRight;
        } else if (obj2 != null) {
            rect.right = this.paddingVHeaderRight;
        } else {
            rect.right = this.paddingBetweenH / 2;
        }
        if (obj != null) {
            if (this.headerRows == 0) {
                rect.top = this.paddingTop;
            } else {
                rect.top = this.paddingTop / 2;
            }
        } else if (obj3 != null) {
            rect.top = this.paddingVHeaderTop;
        } else if (obj2 != null) {
            rect.top = this.paddingVHeaderBetweenV / 2;
        } else {
            rect.top = this.paddingBetweenV / 2;
        }
        if (obj5 != null) {
            rect.bottom = this.paddingBottom;
        } else if (obj4 != null) {
            rect.bottom = this.paddingVHeaderBottom;
        } else if (obj2 != null) {
            rect.bottom = this.paddingVHeaderBetweenV / 2;
        } else {
            rect.bottom = this.paddingBetweenV / 2;
        }
        StringBuilder stringBuilder = new StringBuilder("position=");
        stringBuilder.append(childAdapterPosition);
        stringBuilder.append(" t=");
        stringBuilder.append(rect.top);
        stringBuilder.append(" l=");
        stringBuilder.append(rect.left);
        stringBuilder.append(" b=");
        stringBuilder.append(rect.bottom);
        stringBuilder.append(" r=");
        stringBuilder.append(rect.right);
//        Timber.d(stringBuilder.toString(), new Object[0]);
    }
}
