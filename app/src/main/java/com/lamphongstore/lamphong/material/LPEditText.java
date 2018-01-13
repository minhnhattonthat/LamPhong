package com.lamphongstore.lamphong.material;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;

/**
 * Created by Norvia on 09/04/2017.
 */

public class LPEditText extends TextInputEditText {

    private Context context;
    private AttributeSet attrs;
    private int defStyleAttr;

    public LPEditText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public LPEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public LPEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.defStyleAttr = defStyleAttr;
        init();
    }

    private void init() {
        Typeface font=Typeface.createFromAsset(getContext().getAssets(), "fonts/SF-UI-Text-Regular.otf");
        this.setTypeface(font);
    }
    @Override
    public void setTypeface(Typeface tf, int style) {
        tf=Typeface.createFromAsset(getContext().getAssets(), "fonts/SF-UI-Text-Regular.otf");
        super.setTypeface(tf, style);
    }

    @Override
    public void setTypeface(Typeface tf) {
        tf=Typeface.createFromAsset(getContext().getAssets(), "fonts/SF-UI-Text-Regular.otf");
        super.setTypeface(tf);
    }
}
