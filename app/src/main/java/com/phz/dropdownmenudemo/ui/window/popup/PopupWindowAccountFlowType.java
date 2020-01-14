package com.phz.dropdownmenudemo.ui.window.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.phz.dropdownmenudemo.R;
import com.phz.dropdownmenudemo.adapter.AccountFlowMenuListAdapter;
import com.phz.dropdownmenudemo.util.UIUtil;

/**
 * @author haizhuo
 * @introduction 账户流水 菜单弹出框
 */
public class PopupWindowAccountFlowType extends PopupWindow {
    private View rootView;
    private ListView listView;

    public PopupWindowAccountFlowType(Context context, AccountFlowMenuListAdapter myAdapter){
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView=inflater.inflate(R.layout.popup_account_flow, null);
        listView=rootView.findViewById(R.id.lv);
        this.setWidth(UIUtil.getScreenWidth());
        this.setHeight(UIUtil.getScreenHeight()/3);
        this.setContentView(rootView);
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //让点击外面区域时隐藏popup
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            myAdapter.setSelectPosition(position);
//            PopupWindowAccountFlowType.this.dismiss();
        });
    }

}
