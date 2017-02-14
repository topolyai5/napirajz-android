package hu.napirajz.android

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.AppBarLayout
import android.util.AttributeSet
import android.view.View
import android.content.res.TypedArray




class FabScrollBehavior(val context: Context, val attrs: AttributeSet) : CoordinatorLayout.Behavior<FloatingActionButton>(context, attrs) {

    var toolbarHeight: Int


    override fun layoutDependsOn(parent: CoordinatorLayout, fab: FloatingActionButton, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, fab: FloatingActionButton, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            val lp = fab.layoutParams as CoordinatorLayout.LayoutParams
            val fabBottomMargin = lp.bottomMargin
            val distanceToScroll = fab.height + fabBottomMargin
            val ratio = dependency.getY() as Float / toolbarHeight.toFloat()
            fab.translationY = -distanceToScroll * ratio
        }
        return true
    }

    fun getToolbarHeignt(): Int {
        val styledAttributes = context.getTheme().obtainStyledAttributes(
                intArrayOf(R.attr.actionBarSize))
        val toolbarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()

        return toolbarHeight
    }

    init {
        toolbarHeight = getToolbarHeignt()
    }
}