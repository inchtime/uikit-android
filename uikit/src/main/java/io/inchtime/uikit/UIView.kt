package io.inchtime.uikit

import android.content.Context
import android.graphics.Outline
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
open class UIView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): View(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    init {

        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.UIView)

        val clipRadius = styleAttrs.getDimension(R.styleable.UIView_clipRadius, 0.0f)

        clipToOutline = styleAttrs.getBoolean(R.styleable.UIView_clipToOutline, false)

        if (clipToOutline) {
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.alpha = 1.0f
                    outline?.setRoundRect(0, 0, view?.width ?: 0, view?.height ?: 0, clipRadius)
                }
            }
        }

        styleAttrs.recycle()

    }

}