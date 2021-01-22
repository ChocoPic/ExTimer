package com.tlatla.extimer;

import android.view.View;

public interface OnItemClickListener {
    public void onItemClick(Adapter.Holder holder, View view, int position);
    public boolean onLongItemClick(Adapter.Holder holder, View view, int position);
}
