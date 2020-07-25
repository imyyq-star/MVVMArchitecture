package com.imyyq.mvvm.binding.viewadapter.listview

import android.widget.AbsListView
import android.widget.ListView
import androidx.databinding.BindingAdapter
import com.imyyq.mvvm.binding.command.BindingConsumer

@BindingAdapter(
    value = ["onScrollChangeCommand", "onScrollStateChangedCommand"],
    requireAll = false
)
fun onScrollChangeCommand(
    listView: ListView,
    onScrollChangeCommand: BindingConsumer<ListViewScrollDataWrapper>?,
    onScrollStateChangedCommand: BindingConsumer<Int>?
) {
    listView.setOnScrollListener(object : AbsListView.OnScrollListener {
        private var scrollState = 0
        private lateinit var wrapper: ListViewScrollDataWrapper
        override fun onScrollStateChanged(
            view: AbsListView,
            scrollState: Int
        ) {
            this.scrollState = scrollState
            onScrollStateChangedCommand?.call(scrollState)
        }

        override fun onScroll(
            view: AbsListView,
            firstVisibleItem: Int,
            visibleItemCount: Int,
            totalItemCount: Int
        ) {
            if (onScrollChangeCommand != null) {
                if (!this::wrapper.isInitialized) {
                    wrapper = ListViewScrollDataWrapper()
                }
                wrapper.firstVisibleItem = firstVisibleItem
                wrapper.visibleItemCount = visibleItemCount
                wrapper.totalItemCount = totalItemCount
                wrapper.scrollState = scrollState
                onScrollChangeCommand.call(wrapper)
            }
        }
    })
}

class ListViewScrollDataWrapper {
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var scrollState = 0
    override fun toString(): String {
        return "ListViewScrollDataWrapper{" +
                "firstVisibleItem=" + firstVisibleItem +
                ", visibleItemCount=" + visibleItemCount +
                ", totalItemCount=" + totalItemCount +
                ", scrollState=" + scrollState +
                '}'
    }
}