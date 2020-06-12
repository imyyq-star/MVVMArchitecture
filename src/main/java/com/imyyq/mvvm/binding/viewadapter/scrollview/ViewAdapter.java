package com.imyyq.mvvm.binding.viewadapter.scrollview;

import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;
import androidx.databinding.BindingAdapter;

import com.imyyq.mvvm.binding.command.BindingCommand;

public final class ViewAdapter {

    @BindingAdapter({"onScrollChangeCommand"})
    public static void onScrollChangeCommand(final NestedScrollView nestedScrollView, final BindingCommand<NestScrollDataWrapper> onScrollChangeCommand) {
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (onScrollChangeCommand != null) {
                onScrollChangeCommand.execute(new NestScrollDataWrapper(scrollX, scrollY, oldScrollX, oldScrollY));
            }
        });
    }

    @BindingAdapter({"onScrollChangeCommand"})
    public static void onScrollChangeCommand(final ScrollView scrollView, final BindingCommand<ScrollDataWrapper> onScrollChangeCommand) {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (onScrollChangeCommand != null) {
                onScrollChangeCommand.execute(new ScrollDataWrapper(scrollView.getScrollX(), scrollView.getScrollY()));
            }
        });
    }

    public static class ScrollDataWrapper {
        public float scrollX;
        public float scrollY;

        public ScrollDataWrapper(float scrollX, float scrollY) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
        }
    }

    public static class NestScrollDataWrapper {
        public int scrollX;
        public int scrollY;
        public int oldScrollX;
        public int oldScrollY;

        public NestScrollDataWrapper(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.oldScrollX = oldScrollX;
            this.oldScrollY = oldScrollY;
        }
    }
}
