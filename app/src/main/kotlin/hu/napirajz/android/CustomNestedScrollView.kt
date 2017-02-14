package hu.napirajz.android

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.MotionEvent



class CustomNestedScrollView : NestedScrollView {

    private var y: Int = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    fun dispatchHandlerScroll(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                y = e.y.toInt()
                startNestedScroll(2)
            }
            MotionEvent.ACTION_MOVE -> {
                val dY = y - e.y.toInt()
                dispatchNestedPreScroll(0, dY, null, null)
                dispatchNestedScroll(0, 0, 0, dY, null)
            }
            MotionEvent.ACTION_UP -> stopNestedScroll()
        }
        return true
    }

}