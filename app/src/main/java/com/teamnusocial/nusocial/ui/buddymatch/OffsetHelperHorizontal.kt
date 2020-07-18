package com.teamnusocial.nusocial.ui.buddymatch

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal class OffsetHelperHorizontal(private val mBottomOffset: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val dataSize = state.itemCount
        val position = parent.getChildAdapterPosition(view)
        if (dataSize > 1 && position == dataSize - 1) {
            outRect.set(0, 0, mBottomOffset, 0)
        } else if(dataSize > 1 && position == 0) {
            outRect.set(mBottomOffset, 0, 0, 0)
        }
        else if(dataSize == 1){
            outRect.set(mBottomOffset, 0, 0, 0)
        } else {
            outRect.set(0, 0, 0, 0)
        }
    }

}