package com.hitsindia.expcalc.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SpinnerAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

public class Spinner extends AppCompatSpinner {
    public Spinner(Context context) {
        super(context);
    }

    public Spinner(Context context, int mode) {
        super(context, mode);
    }

    public Spinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Spinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Spinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        super.setOnItemLongClickListener(listener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
    public void dismiss(){
        onDetachedFromWindow();
    }
}
