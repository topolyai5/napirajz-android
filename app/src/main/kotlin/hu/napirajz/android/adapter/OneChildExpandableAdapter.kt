package hu.napirajz.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import hu.napirajz.android.R

class OneChildExpandableAdapter(val context: Context, val text: String) : BaseExpandableListAdapter() {

    override fun getGroup(pos: Int): Any {
        val maxLength = 33
        if (text.length < maxLength) {
            return text
        }
        return text.substring(0, maxLength - 3) + "..."
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val textview = inflater.inflate(R.layout.sample_text, null) as TextView
        textview.text = getGroup(0) as String
        return textview
    }

    override fun getChildrenCount(p0: Int): Int {
        return 1
    }

    override fun getChild(p0: Int, p1: Int): Any {
        return text
    }

    override fun getGroupId(p0: Int): Long {
        return 1
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val textview = inflater.inflate(R.layout.intro_text, null) as TextView
        textview.text = getChild(1, 1) as String
        return textview
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return 1
    }

    override fun getGroupCount(): Int {
        return 1
    }

}