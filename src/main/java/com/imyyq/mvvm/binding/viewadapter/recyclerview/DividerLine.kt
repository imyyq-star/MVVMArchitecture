package com.imyyq.mvvm.binding.viewadapter.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.imyyq.mvvm.utils.DensityUtil.dp2px

/**
 * 分隔线
 */
class DividerLine(context: Context) : ItemDecoration() {
    //divider对应的drawable
    private val mDividerDrawable: Drawable?
    private var mDividerSize = 0

    //默认为null
    private var mMode: Int = LINE_VERTICAL

    constructor(context: Context, mode: Int) : this(context) {
        this.mMode = mode
    }

    constructor(context: Context, dividerSize: Int, mode: Int) : this(
        context,
        mode
    ) {
        this.mDividerSize = dividerSize
    }

    init {
        //获取样式中对应的属性值
        val attrArray = context.obtainStyledAttributes(ATTRS)
        mDividerDrawable = attrArray.getDrawable(0)
        attrArray.recycle()
    }

    /**
     * Item绘制完毕之后绘制分隔线
     * 根据不同的模式绘制不同的分隔线
     */
    override fun onDrawOver(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.onDrawOver(c, parent, state)
        when (mMode) {
            LINE_VERTICAL -> drawVertical(c, parent, state)
            LINE_HORIZONTAL -> drawHorizontal(c, parent, state)
            LINE_BOTH -> {
                drawHorizontal(c, parent, state)
                drawVertical(c, parent, state)
            }
            else -> throw IllegalStateException("Unexpected value: $mMode")
        }
    }

    /**
     * 绘制垂直分隔线
     */
    private fun drawVertical(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin
            val left = child.right + params.rightMargin
            val right =
                if (mDividerSize == 0) left + dp2px(DEFAULT_DIVIDER_SIZE.toFloat()) else left + mDividerSize
            mDividerDrawable!!.setBounds(left, top, right, bottom)
            mDividerDrawable.draw(c)
        }
    }

    /**
     * 绘制水平分隔线
     */
    private fun drawHorizontal(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            //分别为每个item绘制分隔线,首先要计算出item的边缘在哪里,给分隔线定位,定界
            val child = parent.getChildAt(i)
            //RecyclerView的LayoutManager继承自ViewGroup,支持了margin
            val params =
                child.layoutParams as RecyclerView.LayoutParams
            //child的左边缘(也是分隔线的左边)
            val left = child.left - params.leftMargin
            //child的底边缘(恰好是分隔线的顶边)
            val top = child.bottom + params.topMargin
            //child的右边(也是分隔线的右边)
            val right = child.right - params.rightMargin
            //分隔线的底边所在的位置(那就是分隔线的顶边加上分隔线的高度)
            val bottom =
                if (mDividerSize == 0) top + dp2px(DEFAULT_DIVIDER_SIZE.toFloat()) else top + mDividerSize
            mDividerDrawable!!.setBounds(left, top, right, bottom)
            //画上去
            mDividerDrawable.draw(c)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        //        outRect.bottom = getDividerSize() == 0 ? dip2px(mContext, DEFAULT_DIVIDER_SIZE) : getDividerSize();
    }

    companion object {
        //默认分隔线厚度为2dp
        private const val DEFAULT_DIVIDER_SIZE = 1

        //控制分隔线的属性,值为一个drawable
        private val ATTRS = intArrayOf(android.R.attr.listDivider)

        const val LINE_HORIZONTAL = 0
        const val LINE_VERTICAL = 1
        const val LINE_BOTH = 2
    }
}