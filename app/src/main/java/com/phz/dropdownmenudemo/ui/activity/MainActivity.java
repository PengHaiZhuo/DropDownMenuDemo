package com.phz.dropdownmenudemo.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.PopupWindow;

import com.phz.dropdownmenudemo.MyApplication;
import com.phz.dropdownmenudemo.R;
import com.phz.dropdownmenudemo.adapter.AccountFlowMenuListAdapter;
import com.phz.dropdownmenudemo.databinding.ActivityMainBinding;
import com.phz.dropdownmenudemo.ui.window.popup.PopupWindowAccountFlowFee;
import com.phz.dropdownmenudemo.ui.window.popup.PopupWindowAccountFlowTime;
import com.phz.dropdownmenudemo.ui.window.popup.PopupWindowAccountFlowType;
import com.phz.dropdownmenudemo.util.AppManager;
import com.phz.dropdownmenudemo.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    private List<PopupWindow> popupWindowList;
    private PopupWindowAccountFlowType popupWindowBusinessType;
    private PopupWindowAccountFlowFee popupWindowFeeProject;
    private PopupWindowAccountFlowTime popupWindowAccountFlowTime;

    private AccountFlowMenuListAdapter businessTypeAdapter;
    private AccountFlowMenuListAdapter feeProjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);
        binding.setOnClickListener(this);
        AppManager.getInstance().addActivity(this);

        popupWindowList = new ArrayList<>();
        String[] strings = MyApplication.getInstance().getResources().getStringArray(R.array.biil_list);
        businessTypeAdapter = new AccountFlowMenuListAdapter(mContext, strings);
        popupWindowBusinessType = new PopupWindowAccountFlowType(mContext, businessTypeAdapter);
        popupWindowList.add(popupWindowBusinessType);

        String[] stringsNew = MyApplication.getInstance().getResources().getStringArray(R.array.service_list);
        feeProjectAdapter = new AccountFlowMenuListAdapter(mContext, stringsNew);
        popupWindowFeeProject = new PopupWindowAccountFlowFee(mContext, feeProjectAdapter);
        popupWindowList.add(popupWindowFeeProject);

        popupWindowAccountFlowTime = new PopupWindowAccountFlowTime(mContext, (stringBeginDate, stringEndDate) -> {

        });
        popupWindowList.add(popupWindowAccountFlowTime);

        popupWindowBusinessType.setOnDismissListener(() -> resetMenu());
        popupWindowFeeProject.setOnDismissListener(() -> resetMenu());
        popupWindowAccountFlowTime.setOnDismissListener(() -> resetMenu());
    }

    /**
     * 复位顶部菜单
     */
    private void resetMenu() {
        viewModel.getSelectMenu().set(0);
        binding.viewLightTransparent.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cl_type:
                viewModel.getSelectMenu().set(1);
                popupWindowBusinessType.showAsDropDown(v);
                SystemClock.sleep(50);
                binding.viewLightTransparent.setVisibility(View.VISIBLE);
                break;
            case R.id.cl_project:
                viewModel.getSelectMenu().set(2);
                popupWindowFeeProject.showAsDropDown(v);
                SystemClock.sleep(50);
                binding.viewLightTransparent.setVisibility(View.VISIBLE);
                break;
            case R.id.cl_time:
                viewModel.getSelectMenu().set(3);
                popupWindowAccountFlowTime.showAsDropDown(v);
                SystemClock.sleep(50);
                binding.viewLightTransparent.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().removeActivity(this);
    }
}
