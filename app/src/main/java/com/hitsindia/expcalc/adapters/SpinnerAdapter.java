package com.hitsindia.expcalc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.hitsindia.expcalc.R;
import com.hitsindia.expcalc.helper.SqliteHelper;
import com.hitsindia.expcalc.listner.OnSelectDataFromSpinner;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {

    private  ArrayList<String> lstData;
    private Context appCompatActivity;
    private LayoutInflater inflater;

    private OnSelectDataFromSpinner fromSpinner;
    public SpinnerAdapter() {
    }

    public SpinnerAdapter(ArrayList<String> lstData, Context appCompatActivity) {
        this.lstData = lstData;
        this.appCompatActivity = appCompatActivity;
        this.inflater = (LayoutInflater)appCompatActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return lstData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(appCompatActivity).inflate(
                R.layout.category_list_view, parent, false);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        LinearLayout linearLayout = (LinearLayout)view;
        TextView tvCategoryList = linearLayout.findViewById(R.id.tvCategoryList);
        tvCategoryList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showMenu(v,tvCategoryList.getText().toString());
                return false;
            }
        });
        TextView etNewCategory = linearLayout.findViewById(R.id.text_view_name);
        TextView tvCategoryAdd = linearLayout.findViewById(R.id.tvCategoryAdd);
        LinearLayout llCategory = linearLayout.findViewById(R.id.llCategory);
        tvCategoryList.setText(lstData.get(position));
        tvCategoryAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SqliteHelper.getInstanciate(appCompatActivity).insertCategory(etNewCategory.getText().toString());
                fromSpinner.onSelected(etNewCategory.getText().toString());
            }
        });
        tvCategoryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvCategoryList.getText().toString().equals("New")){
                    llCategory.setVisibility(View.VISIBLE);
                    tvCategoryList.setVisibility(View.GONE);
                }
                else{

                    fromSpinner.onSelected(tvCategoryList.getText().toString());
                }
            }
        });
        return view;
    }
    public void setOnDataListner(OnSelectDataFromSpinner fromSpinner){
        this.fromSpinner = fromSpinner;
    }
    public void showMenu(View v,String strCategory) {
        PopupMenu popup = new PopupMenu(appCompatActivity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.popupDelete){
                    SqliteHelper.getInstanciate(appCompatActivity).deleteCategory(strCategory);
                    lstData.remove(strCategory);
                    SpinnerAdapter.this.notifyDataSetChanged();
                }
                return false;
            }
        });
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }
}
