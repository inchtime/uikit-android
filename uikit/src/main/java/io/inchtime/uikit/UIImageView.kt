package io.inchtime.uikit

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView

open class UIImageView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ImageView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private var size: Float = 0.0f
    private var radius: Float = 0.0f
    private val paint: Paint = Paint()
    private val localMatrix = Matrix()

    init {
        paint.isAntiAlias = true

        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.UIImageView)

        radius = styleAttrs.getDimension(R.styleable.UIImageView_radius, 0.0f)

        styleAttrs.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = Math.min(measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    private fun bitmapShader(): BitmapShader {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val scale = size / Math.min(bitmap.height, bitmap.width)
        localMatrix.setScale(scale, scale)
        bitmapShader.setLocalMatrix(localMatrix)
        return bitmapShader
    }

    override fun onDraw(canvas: Canvas?) {
        paint.shader = bitmapShader()
        canvas?.drawRoundRect(0.0f, 0.0f, measuredWidth.toFloat(), measuredHeight.toFloat(), radius, radius, paint)
    }
}