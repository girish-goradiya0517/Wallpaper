package com.dsquare.wallzee.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class CustomTabLayout extends SmartTabLayout {

    public CustomTabLayout(Context context) {
        super(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected TextView createDefaultTabView(CharSequence title) {
        TextView textView = super.createDefaultTabView(title);

        Typeface typeface = ResourcesCompat.getFont(getContext(), com.ymg.ads.sdk.R.font.custom_font);
        textView.setTypeface(typeface,Typeface.BOLD);
        return textView;
    }

}