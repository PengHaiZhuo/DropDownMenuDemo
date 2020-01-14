package com.phz.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author haizhuo
 * @introduction 一个带选中的列表适配器
 */
public abstract class BaseListAdapter<T,H> extends BaseAdapter {
    private final List<T> mData = new ArrayList<>();
    private Context mContext;
    protected View convertView;

    public BaseListAdapter(Context context) {
        mContext = context;
    }

    public BaseListAdapter(Context context, List<T> data) {
        mContext = context;
        setData(data);
    }

    public BaseListAdapter(Context context, T[] data) {
        mContext = context;
        setData(data);
    }

    /**
     * 当前点击的条目
     */
    protected int mSelectPosition = -1;

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return checkPosition(position) ? mData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        H holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), getLayoutId(), null);
            holder = newViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (H) convertView.getTag();
        }

        T item = getItem(position);
        if (item != null) {
            convert(holder, item, position);
        }
        this.convertView=convertView;
        return convertView;
    }

    public Context getContext() {
        return mContext;
    }

    private boolean checkPosition(int position) {
        return position >= 0 && position <= mData.size() - 1;
    }

    public void setData(List<T> data) {
        if (data != null) {
            mData.clear();
            mData.addAll(data);
            mSelectPosition = -1;
            notifyDataSetChanged();
        }
    }

    public void setData(T[] data) {
        if (data != null && data.length > 0) {
            setData(Arrays.asList(data));
        }
    }

    public void removeElement(T element) {
        if (mData.contains(element)) {
            mData.remove(element);
            notifyDataSetChanged();
        }
    }

    public void removeElement(int position) {
        if (mData.size() > position) {
            mData.remove(position);
            notifyDataSetChanged();
        }
    }

    public void removeElements(List<T> elements) {
        if (elements != null && elements.size() > 0 && mData.size() >= elements.size()) {
            for (T element : elements) {
                if (mData.contains(element)) {
                    mData.remove(element);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void removeElements(T[] elements) {
        if (elements != null && elements.length > 0) {
            removeElements(Arrays.asList(elements));
        }
    }

    public void updateElement(T element, int position) {
        if (checkPosition(position)) {
            mData.set(position, element);
            notifyDataSetChanged();
        }
    }

    public void addElement(T element) {
        if (element != null) {
            mData.add(element);
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        mData.clear();
        mSelectPosition = -1;
        notifyDataSetChanged();
    }

    public void clearNotNotify() {
        mData.clear();
        mSelectPosition = -1;
    }

    /**
     * 创建ViewHolder
     * @param convertView
     * @return
     */
    protected abstract H newViewHolder(View convertView);

    /**
     * 获取适配的布局ID
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 转换布局
     * @param holder
     * @param item
     * @param position
     */
    protected abstract void convert(H holder, T item, int position);

    /**
     * @return 当前列表的选中项
     */
    public int getSelectPosition() {
        return mSelectPosition;
    }

    /**
     * 设置当前列表的选中项
     *
     * @param selectPosition
     * @return
     */
    public BaseListAdapter setSelectPosition(int selectPosition) {
        mSelectPosition = selectPosition;
        notifyDataSetChanged();
        return this;
    }

    /**
     * 获取当前列表选中项
     *
     * @return 当前列表选中项
     */
    public T getSelectItem() {
        return getItem(mSelectPosition);
    }
}
