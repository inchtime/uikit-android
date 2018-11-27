package io.inchtime.uikit.view

import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import io.inchtime.uikit.R

class UIAvatarView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatImageView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    companion object {
        const val CORNER_NONE = 0
        const val CORNER_TOP_LEFT = 1
        const val CORNER_TOP_RIGHT = 2
        const val CORNER_BOTTOM_RIGHT = 4
        const val CORNER_BOTTOM_LEFT = 8
        const val CORNER_ALL = 15
    }

    // if the origin image has transparent colors
    // use this to replace the transparent colors
//    public var bgColor: Int = Color.WHITE

    private var cornerRect = RectF()
    private var cornerRadius = 0f
    private var roundedCorners: Int = CORNER_TOP_LEFT
    private var w: Float = 0.0f
    private var h: Float = 0.0f
    private val path = Path()
    private val bitmapPaint = Paint()
    //    private val localMatrix = Matrix()
    private val mode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    init {

        bitmapPaint.isAntiAlias = true
        bitmapPaint.color = Color.WHITE

        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.UIImageView)

        cornerRadius = styleAttrs.getDimension(R.styleable.UIImageView_cornerRadius, 0f)

        roundedCorners = styleAttrs.getInt(
            R.styleable.UIImageView_roundedCorners,
            CORNER_ALL
        )

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

    private var bitmapSrc: Bitmap? = null
    private var canvasSrc: Canvas? = null
    private var bitmapDest: Bitmap? = null
    private var canvasDest: Canvas? = null

    override fun onDraw(canvas: Canvas) {

        if (!path.isEmpty) {

            // src
            if (bitmapSrc == null || canvasSrc == null) {
                bitmapSrc = Bitmap.createBitmap(w.toInt(), h.toInt(), Bitmap.Config.ARGB_8888)
                bitmapSrc?.setHasAlpha(true)
                canvasSrc = Canvas(bitmapSrc!!)
            }

            // reset bitmap
            resetBitmap(bitmapSrc!!, Color.TRANSPARENT)
//            bitmapPaint.color = bgColor
//            canvasSrc?.drawRect(0f, 0f, w, h, bitmapPaint)
//            bitmapPaint.reset()
            super.onDraw(canvasSrc)

            // dest
            if (bitmapDest == null || canvasDest == null) {
                bitmapDest = Bitmap.createBitmap(w.toInt(), h.toInt(), Bitmap.Config.ARGB_8888)
                bitmapDest?.setHasAlpha(true)
                canvasDest = Canvas(bitmapDest!!)
            }

            canvasDest?.drawPath(path, bitmapPaint)

            val sc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.saveLayer(0f, 0f, w, h, null)
            } else {
                canvas.saveLayer(0f, 0f, w, h, null, Canvas.ALL_SAVE_FLAG)
            }

            canvas.drawBitmap(bitmapDest!!, 0f, 0f, bitmapPaint)
            bitmapPaint.xfermode = mode
            canvas.drawBitmap(bitmapSrc!!, 0f, 0f, bitmapPaint)
            bitmapPaint.xfermode = null
            bitmapPaint.reset()
            canvas.restoreToCount(sc)

        } else {
            super.onDraw(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        w = measuredWidth.toFloat()
        h = measuredHeight.toFloat()
    }

    private fun resetBitmap(src: Bitmap, color: Int) {
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in 0 until width * height) {
            pixels[i] = color
        }
        src.setPixels(pixels, 0, width, 0, 0, width, height)
    }

//    private fun bitmapShader(): BitmapShader {
//        val bitmap = (drawable as BitmapDrawable).bitmap
//        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
//        val scaleX = w / bitmap.width
//        val scaleY = h / bitmap.height
//        localMatrix.setScale(scaleX, scaleY)
//        bitmapShader.setLocalMatrix(localMatrix)
//        return bitmapShader
//    }

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

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        invalidate()
    }

}