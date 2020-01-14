package com.phz.dropdownmenudemo.viewmodel;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

/**
 * @author haizhuo
 * @introduction MainActivity对应的ViewModel
 */
public class MainViewModel extends ViewModel {
    private ObservableInt selectMenu=new ObservableInt();
    {
        //默认没有显示任何菜单项
        selectMenu.set(0);
    }

    public ObservableInt getSelectMenu() {
        return selectMenu;
    }
}
