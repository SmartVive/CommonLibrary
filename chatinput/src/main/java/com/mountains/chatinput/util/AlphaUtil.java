package com.mountains.chatinput.util;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.WindowManager;

public class AlphaUtil {

    public  static void setAlpha(final Activity activity, float bgAlpha) {
        ValueAnimator valueAnimator = null;
        final WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();

        valueAnimator = ValueAnimator.ofFloat(layoutParams.alpha, bgAlpha);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.alpha = (float) animation.getAnimatedValue(); //0.0-1.0
                activity.getWindow().setAttributes(layoutParams);
            }
        });
        valueAnimator.start();
    }
}
