package com.phz.dropdownmenudemo.ui.window.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.phz.dropdownmenudemo.R;
import com.phz.dropdownmenudemo.config.Constract;
import com.phz.dropdownmenudemo.ui.window.dialog.DateSelector;
import com.phz.dropdownmenudemo.util.AppManager;
import com.phz.dropdownmenudemo.util.TimeUtil;
import com.phz.dropdownmenudemo.util.UIUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author haizhuo
 * @introduction 账户流水 菜单弹出框
 */
public class PopupWindowAccountFlowTime extends PopupWindow {
    @BindView(R.id.tv_time_start)
    TextView tvTimeStart;
    @BindView(R.id.tv_time_end)
    TextView tvTimeEnd;
    @BindView(R.id.bt_cancel)
    Button btCancel;
    @BindView(R.id.bt_sure)
    Button btSure;
    private View rootView;

    private SelectTimeFinishListener selectTimeFinishListener;

    private Context mContext;
    //开始日期
    private String stringBeginDate;
    //结束日期
    private String stringEndDate;
    /**
     * 开始日期选择器
     */
    private DateSelector dateSelectorBegin;
    /**
     * 结束日期选择器
     */
    private DateSelector dateSelectorEnd;

    public PopupWindowAccountFlowTime(Context context, SelectTimeFinishListener selectTimeFinishListener) {
        super(context);
        this.mContext=context;
        this.selectTimeFinishListener=selectTimeFinishListener;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.popup_account_flow_time, null);
        this.setWidth(UIUtil.getScreenWidth());
        this.setHeight(UIUtil.getScreenHeight() / 3);
        this.setContentView(rootView);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        ButterKnife.bind(this,rootView);
    }

    @OnClick({R.id.tv_time_start, R.id.tv_time_end, R.id.bt_cancel, R.id.bt_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_time_start:
                dateSelectorBegin = new DateSelector(AppManager.getInstance().currentActivity(), time -> {
                    tvTimeStart.setText(time);
                    stringBeginDate = time;
                }, Constract.StartTime, Constract.EndTime,TimeUtil.format(new Date(),"yyyy-MM-dd"));
                if (TextUtils.isEmpty(stringBeginDate)) {
                    dateSelectorBegin.show(TimeUtil.format(new Date(),"yyyy-MM-dd"));
                } else {
                    dateSelectorBegin.show(stringBeginDate);
                }
                break;
            case R.id.tv_time_end:
                dateSelectorEnd = new DateSelector(AppManager.getInstance().currentActivity(), time -> {
                    tvTimeEnd.setText(time);
                    stringEndDate = time;
                }, Constract.StartTime, Constract.EndTime,TimeUtil.format(new Date(),"yyyy-MM-dd"));
                if (TextUtils.isEmpty(stringEndDate)) {
                    dateSelectorEnd.show(TimeUtil.format(new Date(),"yyyy-MM-dd"));
                } else {
                    dateSelectorEnd.show(stringEndDate);
                }
                break;
            case R.id.bt_cancel:
                this.dismiss();
                break;
            case R.id.bt_sure:
                if (TextUtils.isEmpty(tvTimeStart.getText().toString().trim())){
                    Toast.makeText(mContext,"请选择开始日期",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(tvTimeEnd.getText().toString().trim())){
                    Toast.makeText(mContext,"请选择结束日期",Toast.LENGTH_LONG).show();
                }else {
                    selectTimeFinishListener.time(stringBeginDate,stringEndDate);
                    this.dismiss();
                }
                break;
            default:
                break;
        }
    }

    public interface SelectTimeFinishListener{
        void time(String stringBeginDate, String stringEndDate);
    }
}
