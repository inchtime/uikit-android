package io.inchtime.uikit

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

class UIImageView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatImageView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private val CORNER_NONE = 0
    private val CORNER_TOP_LEFT = 1
    private val CORNER_TOP_RIGHT = 2
    private val CORNER_BOTTOM_RIGHT = 4
    private val CORNER_BOTTOM_LEFT = 8
    private val CORNER_ALL = 15

    private var cornerRect = RectF()
    private var cornerRadius = 0f
    private var roundedCorners: Int = CORNER_TOP_LEFT
    private var size: Float = 0.0f
    private val path = Path()
    private val paint = Paint()
    private val localMatrix = Matrix()
    private val mode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    init {

        setLayerType(LAYER_TYPE_SOFTWARE, null)

        paint.isAntiAlias = true

        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.UIImageView)

        cornerRadius = styleAttrs.getDimension(R.styleable.UIImageView_cornerRadius, 0f)

        roundedCorners = styleAttrs.getInt(R.styleable.UIImageView_roundedCorners, CORNER_NONE)

//        clipToOutline = styleAttrs.getBoolean(R.styleable.UIImageView_clipToOutline, false)
//        clipRadius = styleAttrs.getDimension(R.styleable.UIImageView_clipRadius, 0.0f)
//        clipOutlineAlpha = styleAttrs.getDimension(R.styleable.UIImageView_clipOutlineAlpha, 1.0f)
//
//        if (clipToOutline) {
//            outlineProvider = object : ViewOutlineProvider() {
//                override fun getOutline(view: View?, outline: Outline?) {
//                    outline?.alpha = clipOutlineAlpha
//                    outline?.setRoundRect(0, 0, view?.width ?: 0, view?.height ?: 0, clipRadius)
//                }
//            }
//        }

        styleAttrs.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setPath()
    }

    override fun onDraw(canvas: Canvas) {
        if (!path.isEmpty) {
            val left = 0f
            val top = 0f
            val right = size
            val bottom = size
            paint.shader = bitmapShader()
            canvas.drawPath(path, paint)
            // for anti alias
            paint.xfermode = mode
            canvas.drawRect(left, top, right, bottom, paint)
        } else {
            super.onDraw(canvas)
        }
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


    private fun setPath() {

        path.rewind()

        if (cornerRadius >= 1f && roundedCorners != CORNER_NONE) {
            val width = width
            val height = height
            val twoRadius = cornerRadius * 2
            cornerRect.set(-cornerRadius, -cornerRadius, cornerRadius, cornerRadius)

            if (isRounded(CORNER_TOP_LEFT)) {
                cornerRect.offsetTo(0f, 0f)
                path.arcTo(cornerRect, 180f, 90f)
            } else {
                path.moveTo(0f, 0f)
            }

            if (isRounded(CORNER_TOP_RIGHT)) {
                cornerRect.offsetTo(width - twoRadius, 0f)
                path.arcTo(cornerRect, 270f, 90f)
            } else {
                path.lineTo(width.toFloat(), 0f)
            }

            if (isRounded(CORNER_BOTTOM_RIGHT)) {
                cornerRect.offsetTo(width - twoRadius, height - twoRadius)
                path.arcTo(cornerRect, 0f, 90f)
            } else {
                path.lineTo(width.toFloat(), height.toFloat())
            }

            if (isRounded(CORNER_BOTTOM_LEFT)) {
                cornerRect.offsetTo(0f, height - twoRadius)
                path.arcTo(cornerRect, 90f, 90f)
            } else {
                path.lineTo(0f, height.toFloat())
            }

            path.close()
        }
    }

    private fun isRounded(corner: Int): Boolean {
        return (roundedCorners and corner) == corner
    }


}