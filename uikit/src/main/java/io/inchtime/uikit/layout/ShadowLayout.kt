package io.inchtime.uikit.layout

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import io.inchtime.uikit.R

class ShadowLayout constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private var shadowColor: Int = Color.TRANSPARENT
    private var shadowRadius: Float = 0.0f
    private var cornerRadius: Float = 0.0f
    private var shadowDx: Float = 0.0f
    private var shadowDy: Float = 0.0f

    private var invalidateShadowOnSizeChanged = true
    private var forceInvalidateShadow = false

    init {

        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout)

        try {
            cornerRadius = styleAttrs.getDimension(R.styleable.ShadowLayout_cornerRadius, 0.0f)
            shadowRadius = styleAttrs.getDimension(R.styleable.ShadowLayout_shadowRadius, 0.0f)
            shadowColor = styleAttrs.getColor(R.styleable.ShadowLayout_shadowColor, Color.TRANSPARENT)
            shadowDx = styleAttrs.getDimension(R.styleable.ShadowLayout_shadowDx, 0f)
            shadowDy = styleAttrs.getDimension(R.styleable.ShadowLayout_shadowDy, 0f)
        } finally {
            styleAttrs.recycle()
        }

        val xPadding = (shadowRadius + Math.abs(shadowDx)).toInt()
        val yPadding = (shadowRadius + Math.abs(shadowDy)).toInt()
        setPadding(xPadding, yPadding, xPadding, yPadding)
    }

    override fun getSuggestedMinimumWidth(): Int {
        return 0
    }

    override fun getSuggestedMinimumHeight(): Int {
        return 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0 && (background == null || invalidateShadowOnSizeChanged || forceInvalidateShadow)) {
            forceInvalidateShadow = false
            setBackgroundCompat(w, h)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (forceInvalidateShadow) {
            forceInvalidateShadow = false
            setBackgroundCompat(right - left, bottom - top)
        }
    }

    fun setInvalidateShadowOnSizeChanged(invalidateShadowOnSizeChanged: Boolean) {
        this.invalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged
    }

    fun invalidateShadow() {
        forceInvalidateShadow = true
        requestLayout()
        invalidate()
    }

    private fun setBackgroundCompat(w: Int, h: Int) {
        val bitmap = createShadowBitmap(w, h, cornerRadius, shadowRadius, shadowDx, shadowDy, shadowColor, Color.TRANSPARENT)
        val drawable = BitmapDrawable(resources, bitmap)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable)
        } else {
            background = drawable
        }
    }

    private fun createShadowBitmap(
        shadowWidth: Int, shadowHeight: Int, cornerRadius: Float, shadowRadius: Float,
        dx: Float, dy: Float, shadowColor: Int, foreColor: Int
    ): Bitmap {

        val output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val shadowRect = RectF(
            shadowRadius,
            shadowRadius,
            shadowWidth - shadowRadius,
            shadowHeight - shadowRadius
        )

        if (dy > 0) {
            shadowRect.top += dy
            shadowRect.bottom -= dy
        } else if (dy < 0) {
            shadowRect.top += Math.abs(dy)
            shadowRect.bottom -= Math.abs(dy)
        }

        if (dx > 0) {
            shadowRect.left += dx
            shadowRect.right -= dx
        } else if (dx < 0) {
            shadowRect.left += Math.abs(dx)
            shadowRect.right -= Math.abs(dx)
        }

        val shadowPaint = Paint()
        shadowPaint.isAntiAlias = true
        shadowPaint.color = foreColor
        shadowPaint.style = Paint.Style.FILL
        shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor)

        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint)

        return output
    }

}
