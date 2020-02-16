package com.general.files;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

import com.app85taxi.passenger.R;


/**
 * Created by Admin on 04-03-2017.
 */
/*public class MyClickableSpan extends ClickableSpan {// extend ClickableSpan

    String clicked;

    public MyClickableSpan(String string) {
        super();
        clicked = string;
    }

    public void onClick(View tv) {

    }

    public void updateDrawState(TextPaint ds) {// override updateDrawState
        ds.setUnderlineText(false); // set to false to remove underline
    }
}*/

public abstract class MyClickableSpan extends ClickableSpan {
    Context mContext;

    public MyClickableSpan(Context mContext) {
        this.mContext = mContext;
    }

    public void updateDrawState(TextPaint ds) {
        ds.setColor(mContext.getResources().getColor(R.color.appThemeColor_1));
        ds.setTextSize(mContext.getResources().getDimension(R.dimen.txt_size_16));
        ds.setUnderlineText(false);
    }
}