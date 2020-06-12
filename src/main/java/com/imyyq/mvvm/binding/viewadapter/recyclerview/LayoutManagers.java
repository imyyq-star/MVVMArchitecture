package com.imyyq.mvvm.binding.viewadapter.recyclerview;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LayoutManagers {
    protected LayoutManagers() {
    }

    public interface LayoutManagerFactory {
        RecyclerView.LayoutManager create(RecyclerView recyclerView);
    }

    /**
     * A {@link LinearLayoutManager}.
     */
    public static LayoutManagerFactory linear() {
        return recyclerView -> new LinearLayoutManager(recyclerView.getContext());
    }

    /**
     * A {@link LinearLayoutManager} with the given orientation and reverseLayout.
     */
    public static LayoutManagerFactory linear(@Orientation final int orientation, final boolean reverseLayout) {
        return recyclerView -> new LinearLayoutManager(recyclerView.getContext(), orientation, reverseLayout);
    }

    /**
     * A {@link GridLayoutManager} with the given spanCount.
     */
    public static LayoutManagerFactory grid(final int spanCount) {
        return recyclerView -> new GridLayoutManager(recyclerView.getContext(), spanCount);
    }

    public static LayoutManagerFactory grid(final int spanCount, final GridLayoutManager.SpanSizeLookup lookup) {
        return recyclerView -> {
            GridLayoutManager manager = new GridLayoutManager(recyclerView.getContext(), spanCount);
            manager.setSpanSizeLookup(lookup);
            return manager;
        };
    }

    /**
     * A {@link GridLayoutManager} with the given spanCount, orientation and reverseLayout.
     **/
    public static LayoutManagerFactory grid(final int spanCount, @Orientation final int orientation, final boolean reverseLayout) {
        return recyclerView -> new GridLayoutManager(recyclerView.getContext(), spanCount, orientation, reverseLayout);
    }

    /**
     * A {@link StaggeredGridLayoutManager} with the given spanCount and orientation.
     */
    public static LayoutManagerFactory staggeredGrid(final int spanCount, @Orientation final int orientation) {
        return recyclerView -> new StaggeredGridLayoutManager(spanCount, orientation);
    }

    @IntDef({LinearLayoutManager.HORIZONTAL, LinearLayoutManager.VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {
    }
}