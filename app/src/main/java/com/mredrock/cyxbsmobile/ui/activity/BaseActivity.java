package com.mredrock.cyxbsmobile.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.mredrock.cyxbsmobile.R;
import com.mredrock.cyxbsmobile.util.KeyboardUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {
    private static final int HEADER_HIDE_ANIM_DURATION = 300;

    private boolean mActionBarAutoHideEnbale = false;
    private boolean mActionBarShown;
    private int mActionBarAutoHideMinY = 0;
    private int mActionBarAutoHideSensitivity = 0;
    private int mActionBarAutoHideSingnal = 0;

    private ArrayList<View> mHideableHeaderViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            KeyboardUtils.autoHideInput(v, ev);
            return super.dispatchTouchEvent(ev);
        }
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    protected void enbaleActionBarAutoHide(RecyclerView recyclerView) {
        setupActionBarAutoHide();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private Map<Integer, Integer> heights = new HashMap<>();
            private int lastCurrentScrollY = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                View firstVisibleItemView = layoutManager.getChildAt(0);
                if (firstVisibleItemView == null) {
                    return;
                }

                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                heights.put(firstVisibleItemPosition, firstVisibleItemView.getHeight());

                int previousItemsHeight = 0;
                for (int i = 0; i < firstVisibleItemPosition; i++) {
                    previousItemsHeight += heights.get(i) != null ? heights.get(i) : 0;
                }
                int currentScrollY = previousItemsHeight - firstVisibleItemView.getTop()
                        + firstVisibleItemView.getPaddingTop();
                onHomeContentScrolled(currentScrollY, currentScrollY - lastCurrentScrollY);
                lastCurrentScrollY = currentScrollY;
            }
        });
    }

    private void setupActionBarAutoHide() {
        mActionBarAutoHideEnbale = true;
        mActionBarAutoHideMinY =
                getResources().getDimensionPixelOffset(R.dimen.action_bar_auto_hide_min_y);
        mActionBarAutoHideSensitivity =
                getResources().getDimensionPixelOffset(R.dimen.action_bar_auto_hide_sensitivity);
    }

    private void onHomeContentScrolled(int currentY, int deltaY) {
        if (deltaY > mActionBarAutoHideSensitivity) {
            deltaY = mActionBarAutoHideSensitivity;
        } else if (deltaY < -mActionBarAutoHideSensitivity) {
            deltaY = -mActionBarAutoHideSensitivity;
        }

        if (Math.signum(deltaY) * Math.signum(mActionBarAutoHideSensitivity) < 0) {
            mActionBarAutoHideSingnal = deltaY;
        } else {
            mActionBarAutoHideSingnal += deltaY;
        }

        boolean shouldShow = currentY < mActionBarAutoHideMinY ||
                (mActionBarAutoHideSingnal <= -mActionBarAutoHideSensitivity);
        autoShowOrHideActionBar(shouldShow);
    }

    private void autoShowOrHideActionBar(boolean show) {
        if (show == mActionBarShown) {
            return;
        }

        mActionBarShown = show;
        onActionBarAutoShowOrHide(show);
    }

    private void onActionBarAutoShowOrHide(boolean shown) {
        updateSwipeRefreshProgressBarTop();

        for (final View view : mHideableHeaderViews) {
            if (shown) {
                ViewCompat.animate(view)
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .withLayer();
            } else {
                ViewCompat.animate(view)
                        .translationY(-view.getBottom())
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator())
                        .withLayer();
            }
        }
    }

    private void updateSwipeRefreshProgressBarTop() {

    }

    protected void registerHideableHeaderView(View hideableHeaderView) {
        if (mHideableHeaderViews == null) {
            mHideableHeaderViews = new ArrayList<>();
        }
        if (!mHideableHeaderViews.contains(hideableHeaderView)) {
            mHideableHeaderViews.add(hideableHeaderView);
        }
    }

    protected void deregisterHideableHeaderView(View hideableHeaderView) {
        if (mHideableHeaderViews != null && mHideableHeaderViews.contains(hideableHeaderView)) {
            mHideableHeaderViews.remove(hideableHeaderView);
        }
    }
}
