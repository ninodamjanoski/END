package com.endumedia.core.ui.widgets;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class HintSpinner<T> {
    private static final String TAG = "HintSpinner";
    private final HintAdapter<T> adapter;
    private final Callback<T> callback;
    private final Spinner spinner;

    public interface Callback<T> {
        void onItemSelected(int i, T t);
    }

    public HintSpinner(Spinner spinner, HintAdapter<T> hintAdapter, Callback<T> callback) {
        this.spinner = spinner;
        this.adapter = hintAdapter;
        this.callback = callback;
    }

    public void init() {
        this.spinner.setAdapter(this.adapter);
        this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                String a = HintSpinner.TAG;
                StringBuilder stringBuilder = new StringBuilder("position selected: ");
                stringBuilder.append(i);
                Log.d(a, stringBuilder.toString());
                if (HintSpinner.this.callback == null) {
                } else if (!HintSpinner.this.isHintPosition(i)) {
                    HintSpinner.this.callback.onItemSelected(i, (T) HintSpinner.this.spinner.getItemAtPosition(i));
                    return;
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(HintSpinner.TAG, "Nothing selected");
            }
        });
        selectHint();
    }

    private boolean isHintPosition(int i) {
        return i == this.adapter.getHintPosition();
    }

    public void selectHint() {
        this.spinner.setSelection(this.adapter.getHintPosition());
    }
}
