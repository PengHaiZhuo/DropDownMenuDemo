package com.phz.dropdownmenudemo.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.phz.dropdownmenudemo.R
import com.phz.dropdownmenudemo.util.Util.parabola
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * @author 彭海卓 on 2019/11/22
 * @introduction
 */
@SuppressWarnings("all")
class PickerView :View{
    /**
     * text之间间距和minTextSize之比
     */
    val MARGIN_ALPHA = 2.8f
    /**
     * 自动回滚到中间的速度
     */
    val SPEED = 10f
    /**
     * 控制是否首尾相接循环显示 默认为循环显示
     */
    private var loop = true
    /**
     * 控制是否可以滚动
     */
    private var canScroll=false
    /**
     * 数据集
     */
    private var mDataList: ArrayList<String> = ArrayList()
    /**
     * 选中的位置，这个位置是mDataList的中心位置，一直不变
     */
    private var mCurrentSelected: Int = 0

    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var nPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mMaxTextSize = 80f
    private var mMinTextSize = 40f

    private val mMaxTextAlpha = 255f
    private val mMinTextAlpha = 120f

    private var mViewHeight: Int = 0
    private var mViewWidth: Int = 0

    /**
     * 文字颜色 浅灰
     */
    private val mColorText = 0x666666


    private var mLastDownY: Float = 0.toFloat()
    /**
     * 滑动的距离
     */
    private var mMoveLen = 0f
    private var isInit = false
    private var mSelectListener: OnSelectListener? = null
    private var scheduledExecutorService: ScheduledExecutorService= ScheduledThreadPoolExecutor(5)
    private var mTask: MyTimerTask? = null
    private var updateHandler:Handler=Handler(){
        if (Math.abs(mMoveLen) < SPEED) {
            mMoveLen = 0f
            if (mTask != null) {
                mTask!!.cancel()
                mTask = null
                performSelect()
            }
        } else {
            // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
            mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED
        }
        invalidate()
        false
    }

    constructor(context: Context):super(context){
        init()
    }
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet){
        val typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.PickerView)
        loop = typedArray.getBoolean(R.styleable.PickerView_isLoop, loop)
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int): super(context, attributeSet, defStyleAttr){
        PickerView(context,attributeSet)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mViewHeight = measuredHeight
        mViewWidth = measuredWidth
        // 按照View的高度计算字体大小
        mMaxTextSize = mViewHeight / 7f
        mMinTextSize = mMaxTextSize / 2.2f
        isInit = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!isInit) return
        // 先绘制选中的text再往上往下绘制其余的text
        val scale = parabola(mViewHeight / 4.0f, mMoveLen)
        val size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize
        mPaint.textSize = size
        mPaint.alpha = ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha).toInt()
        // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
        val x = (mViewWidth / 2.0).toFloat()
        val y = (mViewHeight / 2.0 + mMoveLen).toFloat()
        val fmi = mPaint.fontMetricsInt
        val baseline = (y - (fmi.bottom / 2.0 + fmi.top / 2.0)).toFloat()
        canvas!!.drawText(mDataList[mCurrentSelected], x, baseline, mPaint)
        // 绘制上方data
        run {
            var i = 1
            while (mCurrentSelected - i >= 0) {
                drawOtherText(canvas, i, -1)
                i++
            }
        }
        // 绘制下方data
        var i = 1
        while (mCurrentSelected + i < mDataList.size) {
            drawOtherText(canvas, i, 1)
            i++
        }
    }

    fun setCanScroll(canScroll: Boolean) {
        this.canScroll = canScroll
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        return if(canScroll)super.dispatchTouchEvent(event)else false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.getActionMasked()) {
            MotionEvent.ACTION_DOWN -> doDown(event)
            MotionEvent.ACTION_MOVE -> {
                mMoveLen += event.getY() - mLastDownY

                if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
                    if (!loop && mCurrentSelected == 0) {
                        mLastDownY = event.getY()
                        invalidate()
                        return true
                    }
                    if (!loop) {
                        mCurrentSelected--
                    }
                    // 往下滑超过离开距离
                    moveTailToHead()
                    mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize
                } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
                    if (mCurrentSelected == mDataList.size - 1) {
                        mLastDownY = event.getY()
                        invalidate()
                        return true
                    }
                    if (!loop) {
                        mCurrentSelected++
                    }
                    // 往上滑超过离开距离
                    moveHeadToTail()
                    mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize
                }

                mLastDownY = event.getY()
                invalidate()
            }
            MotionEvent.ACTION_UP -> doUp()
        }
        return true
    }

    private fun doDown(event: MotionEvent) {
        if (mTask != null) {
            mTask!!.cancel()
            mTask = null
        }
        mLastDownY = event.y
    }

    private fun doMove(event: MotionEvent) {

        mMoveLen += event.y - mLastDownY

        if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
            // 往下滑超过离开距离
            moveTailToHead()
            mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize
        } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
            // 往上滑超过离开距离
            moveHeadToTail()
            mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize
        }

        mLastDownY = event.y
        invalidate()
    }

    private fun doUp() {
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (Math.abs(mMoveLen) < 0.0001) {
            mMoveLen = 0f
            return
        }
        if (mTask != null) {
            mTask!!.cancel()
            mTask = null
        }
        mTask = MyTimerTask(updateHandler)
        scheduledExecutorService.scheduleWithFixedDelay(mTask, 0, 10, TimeUnit.MILLISECONDS)
    }

    /**
     * @param canvas
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private fun drawOtherText(canvas: Canvas, position: Int, type: Int) {
        val d = MARGIN_ALPHA * mMinTextSize * position.toFloat() + type * mMoveLen
        val scale = parabola(mViewHeight / 4.0f, d)
        val size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize
        nPaint.textSize = size
        nPaint.alpha = ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha).toInt()
        val y = (mViewHeight / 2.0 + type * d).toFloat()
        val fmi = nPaint.fontMetricsInt
        val baseline = (y - (fmi.bottom / 2.0 + fmi.top / 2.0)).toFloat()
        canvas.drawText(
            mDataList[mCurrentSelected + type * position],
            (mViewWidth / 2.0).toFloat(),
            baseline,
            nPaint
        )
    }

    private fun init() {
        //第一个paint
        mPaint.style=Paint.Style.FILL
        mPaint.textAlign=Paint.Align.CENTER
        mPaint.color=Color.BLACK
        //第二个paint
        nPaint.style=Paint.Style.FILL
        nPaint.textAlign=Paint.Align.CENTER
        nPaint.color=mColorText
    }

    fun setData(datas: List<String>) {
        mDataList = datas as ArrayList<String>
        mCurrentSelected = datas.size / 4
        invalidate()
    }

    /**
     * 选择选中的item的index
     *
     * @param selected
     */
    fun setSelected(selected: Int) {
        mCurrentSelected = selected
        if (loop) {
            val distance = mDataList.size / 2 - mCurrentSelected
            if (distance < 0) {
                for (i in 0 until -distance) {
                    moveHeadToTail()
                    mCurrentSelected--
                }

            } else if (distance > 0) {
                for (i in 0 until distance) {
                    moveTailToHead()
                    mCurrentSelected++
                }

            }
        }
        invalidate()
    }

    /**
     * 选择选中的内容
     *
     * @param mSelectItem
     */
    fun setSelected(mSelectItem: String) {
        for (i in mDataList.indices) {
            if (mDataList[i] == mSelectItem) {
                setSelected(i)
                break
            }
        }
    }


    private fun moveHeadToTail() {
        if (loop) {
            val head = mDataList[0]
            mDataList.removeAt(0)
            mDataList.add(head)
        }
    }

    private fun moveTailToHead() {
        if (loop) {
            val tail = mDataList[mDataList.size - 1]
            mDataList.removeAt(mDataList.size - 1)
            mDataList.add(0, tail)
        }
    }

    private fun performSelect() {
        if (mSelectListener != null) {
            mSelectListener!!.onSelect(mDataList.get(mCurrentSelected))
        }
    }

    fun setOnSelectListener(listener: OnSelectListener) {
        mSelectListener = listener
    }

    interface OnSelectListener {
        fun onSelect(text: String)
    }

    internal inner class MyTimerTask(var handler: Handler) : TimerTask() {

        override fun run() {
            handler.sendMessage(handler.obtainMessage())
        }
    }
}
