package com.dummies.tasks.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.dummies.tasks.R;

/**
 * Our custom CardView used to display the task items in the list.
 * It's also a wrapper for the animations supported by this view.
 */
public class TaskCard extends FrameLayout {

    private static final int ANIMATION_DURATION = 300; // milliseconds

    public TaskCard(Context context) {
        this(context, null);
    }

    public TaskCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.task_card, this);
    }

    /**
     * Animates the CardView up to a specified elevation value, and scales
     * it up by 10% to give the impression of the view being lifted up.
     */
    public void animateCardUp() {
        final float elevation = getContext().getResources().getDimension(
                R.dimen.task_card_lifted_elevation);
        animate().setDuration(ANIMATION_DURATION)
                .setInterpolator(new OvershootInterpolator())
                .translationZ(elevation)
                .scaleX(1.1f)
                .scaleY(1.1f);
    }

    /**
     * Animates the CardView down to it's default elevation and size.
     */
    public void animateCardDown() {
        final float elevation = getContext().getResources().getDimension(
                R.dimen.task_card_default_elevation);
        animate().setDuration(ANIMATION_DURATION)
                .setInterpolator(new OvershootInterpolator())
                .translationZ(elevation)
                .scaleX(1.0f)
                .scaleY(1.0f);
    }
}
