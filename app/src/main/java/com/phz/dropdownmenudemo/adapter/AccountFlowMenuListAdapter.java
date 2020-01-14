package com.phz.dropdownmenudemo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.phz.base.BaseListAdapter;
import com.phz.dropdownmenudemo.R;
import com.phz.dropdownmenudemo.util.ResUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author haizhuo
 * @introduction 账户流水  普通菜单菜单列表填充器
 */
public class AccountFlowMenuListAdapter extends BaseListAdapter<String, AccountFlowMenuListAdapter.ViewHolder> {

    public AccountFlowMenuListAdapter(Context context, String[] data) {
        super(context, data);
    }

    @Override
    protected AccountFlowMenuListAdapter.ViewHolder newViewHolder(View convertView) {
        return new AccountFlowMenuListAdapter.ViewHolder(convertView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_popup_list_account_flow;
    }

    @Override
    protected void convert(AccountFlowMenuListAdapter.ViewHolder holder, String item, int position) {
        holder.tv.setText(item);
        if (mSelectPosition != -1) {
            if (position == mSelectPosition) {
                holder.tv.setSelected(true);
                holder.tv.setCompoundDrawablesWithIntrinsicBounds(null, null, ResUtil.getVectorDrawable(holder.tv.getContext(), R.drawable.ic_checked_right), null);
            } else {
                holder.tv.setSelected(false);
                holder.tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        }
    }

    class ViewHolder {
        @BindView(R.id.tv)
        TextView tv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

