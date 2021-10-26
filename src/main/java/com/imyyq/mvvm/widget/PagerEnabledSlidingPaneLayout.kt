package com.imyyq.mvvm.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.core.view.MotionEventCompat
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import kotlin.math.roundToInt

/**
 * 界面的侧滑关键类，侧滑的效果就是用它实现的
 *
 * @author imyyq.star@gmail.com
 */
internal class PagerEnabledSlidingPaneLayout constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : SlidingPaneLayout(context, attrs, defStyle) {
    private var mInitialMotionX = 0f
    private val mEdgeSlop: Float
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (MotionEventCompat.getActionMasked(ev)) {
            MotionEvent.ACTION_DOWN -> {
                mInitialMotionX = ev.x
            }
            MotionEvent.ACTION_MOVE -> {
                val x = ev.x
                val y = ev.y
                // The user should always be able to "commonx_close" the pane, so we only check
                // for child scrollability if the pane is currently closed.
                if (mInitialMotionX > mEdgeSlop && !isOpen && canScroll(
                        this,
                        false,
                        (x - mInitialMotionX).roundToInt(),
                        x.roundToInt(),
                        y.roundToInt()
                    )
                ) {

                    // How do we set super.mIsUnableToDrag = true?

                    // send the parent a cancel event
                    val cancelEvent = MotionEvent.obtain(ev)
                    cancelEvent.action = MotionEvent.ACTION_CANCEL
                    return super.onInterceptTouchEvent(cancelEvent)
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    init {
        val config = ViewConfiguration.get(context)
        mEdgeSlop = config.scaledEdgeSlop.toFloat()
    }
}