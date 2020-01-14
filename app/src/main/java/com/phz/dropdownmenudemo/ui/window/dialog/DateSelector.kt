package com.phz.dropdownmenudemo.ui.window.dialog

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.TextView
import com.phz.dropdownmenudemo.R
import com.phz.dropdownmenudemo.ui.view.PickerView
import com.phz.dropdownmenudemo.util.TimeUtil
import com.phz.dropdownmenudemo.util.Util
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author 彭海卓 on 2019/11/14
 * @introduction 日期选择器
 */
class DateSelector {
    /**
     * 暂时只支持年月日
     */
    private val FORMAT_DATE = "yyyy-MM-dd"
    /**
     * 最大月份
     */
    var MAX_MONTH:Int=12
    /**
     * 动画延迟时间
     */
    var ANIMATION_DELAY:Long=200
    /**
     * 发送日期变化消息延迟时间
     */
    var CHANGE_DELAY:Long=90
    /**
     * 上下文对象
     */
    private var context: Context? = null
    private var handler: ResultHandler? = null

    private var tv_title: TextView? = null
    private var tv_select: TextView? = null
    private var tv_cancle: TextView? = null
    private var year_pv: PickerView? = null
    private var month_pv: PickerView? = null
    private var day_pv: PickerView? = null

    private var yearList: ArrayList<String> = ArrayList()
    private var monthList:ArrayList<String> = ArrayList()
    private var dayList:ArrayList<String> = ArrayList()
    private var startYear: Int = 0
    private var startMonth:Int = 0
    private var startDay:Int = 0
    private var endYear:Int = 0
    private var endMonth:Int = 0
    private var endDay:Int = 0
    /**
     * 选择年 是否不固定
     */
    private var spanYear: Boolean = true
    /**
     * 选择月 是否不固定
     */
    private var spanMon:Boolean = true
    /**
     * 选择日 是否不固定
     */
    private var spanDay:Boolean = true

    private var selectedCalender: Calendar = Calendar.getInstance()
    private var startCalendar: Calendar = Calendar.getInstance()
    private var endCalendar: Calendar = Calendar.getInstance()
    private lateinit var seletorDialog: Dialog

    constructor(context: Context?, handler: ResultHandler?,startDate: String,endDate: String,selectDate: String) {
        this.context = context
        this.handler = handler
        selectedCalender.time = TimeUtil.format(selectDate,FORMAT_DATE)
        startCalendar.time =TimeUtil.format(startDate,FORMAT_DATE)
        endCalendar.time =TimeUtil.format(endDate,FORMAT_DATE)
        initDialog()
        initView()
    }

    /** kotlin没有static关键字，但是有伴生对象，可以实现Java中静态方法调用和静态变量修改，静态方法使用@JvmStatic关键字实现*/
    companion object{
        var MAX_MONTH:Int=12
        interface ResultHandler {
            fun handle(time: String)
        }
    }

    /**
     * 初始化对话框
     */
    fun initDialog(){
            seletorDialog=Dialog(context!!, R.style.DateSelectorDialog)
            seletorDialog.setCancelable(false)
            seletorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            seletorDialog.setContentView(R.layout.dialog_date)
            var window:Window= seletorDialog.window!!
            //只设置对话框的宽度，高度随他
            window.attributes.width= Util.getScreenWidth(context!!)
            //显示在底部，可以自由设置，比如顶部，或者默认(居中)
            window.setGravity(Gravity.BOTTOM)
    }

    /**
     * 初始化控件
     */
    fun initView(){
        year_pv=seletorDialog.findViewById(R.id.year_pv)
        month_pv=seletorDialog.findViewById(R.id.month_pv)
        day_pv=seletorDialog.findViewById(R.id.day_pv)
        tv_title=seletorDialog.findViewById(R.id.tv_title)
        tv_select=seletorDialog.findViewById(R.id.tv_select)
        tv_cancle=seletorDialog.findViewById(R.id.tv_cancle)
        tv_cancle!!.setOnClickListener {
            seletorDialog.dismiss()
        }
        tv_select!!.setOnClickListener {
            handler?.handle(TimeUtil.format(selectedCalender.time,FORMAT_DATE))
            seletorDialog.dismiss()
        }
    }

    /**
     * 显示对话框，并且选中日期selectDate
     */
    fun show(selectDate: String){
        startYear=startCalendar.get(Calendar.YEAR)
        //显示1-12月，所以+1
        startMonth=startCalendar.get(Calendar.MONTH)+1
        startDay=startCalendar.get(Calendar.DAY_OF_MONTH)
        endYear=endCalendar.get(Calendar.YEAR)
        endMonth=endCalendar.get(Calendar.MONTH)+1
        endDay=endCalendar.get(Calendar.DAY_OF_MONTH)
        selectedCalender.time = TimeUtil.format(selectDate,FORMAT_DATE)

        yearList.clear()
        monthList.clear()
        dayList.clear()
        //初始化年月日对应list列表数据
        if (spanYear){
            for (index in startYear..endYear){
                yearList.add(index.toString())
            }
            for (index in startMonth..endMonth){
                monthList.add(fomatTimeUnit(index))
            }
            for (index in startDay..endDay){
                dayList.add(fomatTimeUnit(index))
            }
        }else if (spanMon){
            yearList.add(startYear.toString())
            for (index in startMonth..endMonth){
                monthList.add(fomatTimeUnit(index))
            }
            for (index in startDay..endDay){
                dayList.add(fomatTimeUnit(index))
            }
        }else if (spanDay){
            yearList.add(startYear.toString())
            monthList.add(startMonth.toString())
            for (index in startDay..endDay){
                dayList.add(fomatTimeUnit(index))
            }
        }
        year_pv!!.setData(yearList)
        month_pv!!.setData(monthList)
        day_pv!!.setData(dayList)
        year_pv!!.setSelected(TimeUtil.getYear(TimeUtil.format(selectDate,FORMAT_DATE)))
        month_pv!!.setSelected(TimeUtil.getMonth(TimeUtil.format(selectDate,FORMAT_DATE)))
        day_pv!!.setSelected(TimeUtil.getDay(TimeUtil.format(selectDate,FORMAT_DATE)))
        year_pv!!.setCanScroll(yearList.size > 1)
        month_pv!!.setCanScroll(monthList.size > 1)
        day_pv!!.setCanScroll(dayList.size > 1)

        year_pv!!.setOnSelectListener(object :PickerView.OnSelectListener{
            override fun onSelect(text: String) {
                selectedCalender.set(Calendar.YEAR, Integer.parseInt(text))
                monthChange()
            }
        })
        month_pv!!.setOnSelectListener(object :PickerView.OnSelectListener{
            override fun onSelect(text: String) {
                selectedCalender.set(Calendar.DAY_OF_MONTH, 1)
                selectedCalender.set(Calendar.MONTH, Integer.parseInt(text) - 1)
                dayChange()
            }
        })
        day_pv!!.setOnSelectListener(object :PickerView.OnSelectListener{
            override fun onSelect(text: String) {
                selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(text))
            }
        })
        seletorDialog.show()
    }

    private fun monthChange() {
        monthList.clear()
        val selectedYear = selectedCalender.get(Calendar.YEAR)
        if (selectedYear == startYear) {
            for (i in startMonth..MAX_MONTH) {
                monthList.add(fomatTimeUnit(i))
            }
        } else if (selectedYear == endYear) {
            for (i in 1..endMonth) {
                monthList.add(fomatTimeUnit(i))
            }
        } else {
            for (i in 1..MAX_MONTH) {
                monthList.add(fomatTimeUnit(i))
            }
        }
        selectedCalender.set(Calendar.MONTH, Integer.parseInt(monthList.get(0)) - 1)
        month_pv!!.setData(monthList)
        month_pv!!.setSelected(0)
        excuteAnimator(ANIMATION_DELAY, month_pv!!)

        month_pv!!.postDelayed({ dayChange() }, CHANGE_DELAY)

    }

    private fun dayChange() {
        dayList.clear()
        val selectedYear = selectedCalender.get(Calendar.YEAR)
        val selectedMonth = selectedCalender.get(Calendar.MONTH) + 1
        if (selectedYear == startYear && selectedMonth == startMonth) {
            for (i in startDay..selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                dayList.add(fomatTimeUnit(i))
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth) {

            for (i in 1..endDay) {
                dayList.add(fomatTimeUnit(i))
            }
        } else {
            for (i in 1..selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                dayList.add(fomatTimeUnit(i))
            }
        }
        selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayList.get(0)))
        day_pv!!.setData(dayList)
        day_pv!!.setSelected(0)
        excuteAnimator(ANIMATION_DELAY, day_pv!!)
    }

    /**
     * 判断是否小于10，小于10返回字段加0处理
     */
    private fun fomatTimeUnit(unit: Int): String {
        return if (unit < 10) "0$unit" else unit.toString()
    }

    private fun excuteAnimator(ANIMATORDELAY: Long, view: View) {
        val pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f)
        val pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f)
        val pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f)
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(ANIMATORDELAY).start()
    }

}