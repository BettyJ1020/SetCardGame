package tw.edu.ncku.iim.yhjiang.setcardgame

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View


/**
 * TODO: document your custom view class.
 */

var cardIds = mutableListOf<Int>() // to store each card's Id
var setsIds = mutableListOf<Int>() // temp save each set's Id
var selectedIds = mutableListOf<Int>()// to store selected card's Id
class SetCardView : View {
//    var cardId = intent.getStringExtra("cardIds")
    enum class Shape {
        OVAL, DIAMOND, WORM
    }


    enum class Shading {
        EMPTY, SOLID, STRIP
    }

    public var number:Int = 1
        set(value) {
            if (value >= 1 && value <= 3) {
                field = value
                invalidate()
            }
        }

    public var shape = Shape.OVAL
        set(value) {
            field = value
            invalidate()
        }

    public var color = Color.BLUE
        set(value) {
            field = value
            invalidate()
        }

    public var shading = Shading.EMPTY
        set(value) {
            field = value
            invalidate()
        }

    public var cardSelected: Boolean = false // init to unselected
        set(value) {
            field = value
            invalidate()
        }



    fun addCardIds(id: Int) {
        cardIds.add(id)
        Log.i("SetCardView", "Card ID added: $id")
        Log.i("SetCardView", "Card IDs: $cardIds")
    }
    fun addSetIds(id: Int) {
        setsIds.add(id)
    }


    companion object SetCardConstants {
        const val CARD_STANDARD_HEIGHT =  240.0f
        const val CORNER_RADIUS = 12.0f
        const val SYMBOL_WIDTH_SCALE_FACTOR = 0.6f
        const val SYMBOL_HEIGHT_SCALE_FACTOR = 0.125f
        const val STRIP_DISTANCE_SCALE_FACTOR = 0.05f
    }

    private val cornerScaleFactor: Float
        get() {
            return height / CARD_STANDARD_HEIGHT
        }

    private val cornerRadius: Float
        get() {
            return CORNER_RADIUS * cornerScaleFactor
        }

    private val mPaint = Paint() // For drawing border and face images
    private val mTextPaint = TextPaint() // for drawing pips (text)
    interface SetCardClickListener {
        fun onSetCardClick(selectedIds: List<Int>)
    }

    private var setCardClickListener: SetCardClickListener? = null


    fun setCardClickListener(listener: SetCardClickListener) {
        setCardClickListener = listener
    }

    fun refreshSelected() {
        if (!cardSelected) {
            selectedIds.remove(id)
        }
    }




    private fun init(context: Context, attrs: AttributeSet? = null) {
        mPaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);

        setOnClickListener {
            cardSelected = !cardSelected // change between selected / unselected
            if (cardSelected) {
                selectedIds.add(id)
                Log.i("SetCardView", "Card ID selected: $id")
                Log.i("SetCardView", "Selected IDs: $selectedIds")

            } else {
                selectedIds.remove(id)
                Log.i("SetCardView", "Card ID deselected: $id")
                Log.i("SetCardView", "Selected IDs: $selectedIds")
            }
            setCardClickListener?.onSetCardClick(selectedIds) // Notify the click event
        }

    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context, attrs,defStyle) {
        init(context, attrs)
    }




    private fun drawShapeWithVerticalOffset(canvas: Canvas, voffset: Float) { // shape
        val path = Path()
        val width = width * SYMBOL_WIDTH_SCALE_FACTOR
        val height = height * SYMBOL_HEIGHT_SCALE_FACTOR

        if (shape == Shape.OVAL) { // OVAL
            // to accommodate the entire oval shape
            val bitmapWidth = (width + 2 * mPaint.strokeWidth).toInt()
            val bitmapHeight = (height + 2 * mPaint.strokeWidth).toInt()

            val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
            val bitmapCanvas = Canvas(bitmap)
            // to center
            val centerX = canvas.width / 2f
            val centerY = canvas.height / 2f + voffset

            val ovalRect = RectF(
                centerX - width / 2f,
                centerY - height / 2f,
                centerX + width / 2f,
                centerY + height / 2f
            )

            bitmapCanvas.drawOval(ovalRect, mPaint)
            canvas.drawBitmap(bitmap, centerX - bitmap.width / 2f, centerY - bitmap.height / 2f, null)
            path.addOval(ovalRect, Path.Direction.CW)

        } else if (shape == Shape.DIAMOND) { // DIAMOND
            val centerX = canvas.width / 2f
            val centerY = canvas.height / 2f + voffset

            val halfWidth = width / 2f
            val halfHeight = height / 2f

            path.moveTo(centerX, centerY - halfHeight) // Top
            path.lineTo(centerX + halfWidth, centerY) // Right
            path.lineTo(centerX, centerY + halfHeight) // Bottom
            path.lineTo(centerX - halfWidth, centerY) // Left
            path.close() // Connect back to the top

            canvas.drawPath(path, mPaint)
        } else { // WORM
            val center = PointF((getWidth() / 2).toFloat(), getHeight() / 2 + voffset)
            path.moveTo(center.x - width / 2, center.y + height / 2)

            val cp1 = PointF(center.x - width / 4, center.y - height * 1.5f)
            val cp2 = PointF(center.x + width / 4, center.y)
            val dst = PointF(center.x + width / 2, center.y - height / 2)
            path.cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, dst.x, dst.y)

            cp1.x = center.x + width / 2
            cp1.y = center.y + height * 2
            cp2.x = center.x - width / 2
            cp2.y = center.y

            dst.x = center.x - width / 2
            dst.y = center.y + height / 2

            path.cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, dst.x, dst.y)
            canvas.drawPath(path, mPaint)
        }

        // shading
        drawShadingInPath(canvas, path);
    }



    private fun drawShapes(canvas: Canvas) {
        mPaint.color = color
        for (cardId in cardIds) {
            if (id == cardId) {
                if (number == 1) {
                    drawShapeWithVerticalOffset(canvas, 0f)
                } else if (number == 2) {
                    val h = height / 4
                    drawShapeWithVerticalOffset(canvas, -(h - h / 4).toFloat())
                    drawShapeWithVerticalOffset(canvas, (h - h / 4).toFloat())
                } else {
                    val h = height / 3
                    drawShapeWithVerticalOffset(canvas, -(h - h / 4).toFloat())
                    drawShapeWithVerticalOffset(canvas, 0f)
                    drawShapeWithVerticalOffset(canvas, (h - h / 4).toFloat())
                }
            }
        }

        for (setId in setsIds) { // history set of cards
            if (number == 1) {
                drawShapeWithVerticalOffset(canvas, 0f)
            } else if (number == 2) {
                val h = height / 4
                drawShapeWithVerticalOffset(canvas, -(h - h / 4).toFloat())
                drawShapeWithVerticalOffset(canvas, (h - h / 4).toFloat())
            } else {
                val h = height / 3
                drawShapeWithVerticalOffset(canvas, -(h - h / 4).toFloat())
                drawShapeWithVerticalOffset(canvas, 0f)
                drawShapeWithVerticalOffset(canvas, (h - h / 4).toFloat())
            }

        }


    }

    private fun drawShadingInPath(canvas: Canvas, path: Path) { // Shading
        canvas.save();

        if (shading == Shading.SOLID) {
            mPaint.style = Paint.Style.FILL
            canvas.drawPath(path, mPaint)
        } else if (shading == Shading.STRIP) {
            canvas.clipPath(path);
            val path = Path();

            val strip_distance = width * STRIP_DISTANCE_SCALE_FACTOR;
            var x = 0f
            while (x < width) {
                path.moveTo(x, 0f);
                path.lineTo(x, height.toFloat());
                x += strip_distance
            }
            canvas.drawPath(path, mPaint);
        }// else, EMPTY

        // ensure border exists
        mPaint.style = Paint.Style.STROKE
        canvas.drawPath(path, mPaint)


        canvas.restore();
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // draw card
        val path = Path()
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
        // Intersect the current clip with the specified path
        canvas.clipPath(path)

        // fill
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.WHITE
        canvas.drawPath(path, mPaint)
        // border
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 3.0f
        mPaint.color = Color.BLACK
        canvas.drawPath(path, mPaint)

        drawShapes(canvas)
    }
}
