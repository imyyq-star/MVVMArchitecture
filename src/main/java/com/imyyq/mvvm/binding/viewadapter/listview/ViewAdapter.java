package com.imyyq.mvvm.binding.viewadapter.listview;

import android.widget.AbsListView;
import android.widget.ListView;

import androidx.databinding.BindingAdapter;

import com.imyyq.mvvm.binding.command.BindingCommand;

public final class ViewAdapter {

    @BindingAdapter(value = {"onScrollChangeCommand", "onScrollStateChangedCommand"}, requireAll = false)
    public static void onScrollChangeCommand(final ListView listView,
                                             final BindingCommand<ListViewScrollDataWrapper> onScrollChangeCommand,
                                             final BindingCommand<Integer> onScrollStateChangedCommand) {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int scrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.scrollState = scrollState;
                if (onScrollStateChangedCommand != null) {
                    onScrollStateChangedCommand.execute(scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (onScrollChangeCommand != null) {
                    onScrollChangeCommand.execute(new ListViewScrollDataWrapper(scrollState, firstVisibleItem, visibleItemCount, totalItemCount));
                }
            }
        });

    }


    @BindingAdapter(value = {"onItemClickCommand"}, requireAll = false)
    public static void onItemClickCommand(final ListView listView, final BindingCommand<Integer> onItemClickCommand) {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (onItemClickCommand != null) {
                onItemClickCommand.execute(position);
            }
        });
    }

    public static class ListViewScrollDataWrapper {
        public final int firstVisibleItem;
        public final int visibleItemCount;
        public final int totalItemCount;
        public final int scrollState;

        public ListViewScrollDataWrapper(int scrollState, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            this.firstVisibleItem = firstVisibleItem;
            this.visibleItemCount = visibleItemCount;
            this.totalItemCount = totalItemCount;
            this.scrollState = scrollState;
        }
    }
}
