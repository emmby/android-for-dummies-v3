package com.dummies.tasks.tv;

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;

import com.dummies.tasks.R;
import com.dummies.tasks.provider.TaskProvider;
import com.squareup.picasso.Picasso;

public class CardPresenter extends Presenter {
    private static int CARD_WIDTH = 313;
    private static int CARD_HEIGHT = 176;

    static class ViewHolder extends Presenter.ViewHolder {
        ImageCardView cardView;

        public ViewHolder(View view) {
            super(view);
            cardView = (ImageCardView) view;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        ImageCardView cardView = new ImageCardView(context);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setBackgroundResource(R.color.window_background);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Task task = (Task)item;

        // Update card
        ViewHolder vh = (ViewHolder) viewHolder;
        ImageCardView cardView = vh.cardView;
        cardView.setTitleText(task.title);
        cardView.setContentText(task.notes);
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);

        Context context= cardView.getContext();
        Picasso.with(context)
                .load(TaskProvider.getImageUrlForTask(task.id))
                .resize(CARD_WIDTH, CARD_HEIGHT)
                .centerCrop()
                .into(cardView.getMainImageView());
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
    }

}
