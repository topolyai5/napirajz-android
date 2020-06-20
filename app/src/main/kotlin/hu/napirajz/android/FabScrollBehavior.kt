package hu.napirajz.android

import android.content.Context
import com.google.android.material.appbar.AppBarLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.util.AttributeSet
import android.view.View

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