package com.hitsindia.expcalc.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hitsindia.expcalc.R;
import com.hitsindia.expcalc.adapters.BudgetAdapter;
import com.hitsindia.expcalc.helper.SqliteHelper;
import com.hitsindia.expcalc.listner.OnDataRefreshListner;
import com.hitsindia.expcalc.listner.OnDateChangedListner;
import com.hitsindia.expcalc.listner.OnNewRecordEntryLisnter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BudgetListFragment extends Fragment{
    private RecyclerView rvBudget;
    private BudgetAdapter adapter;
    private TextView tvAdvanceTotalAmount,tvTotalBudget,tvYear;
    private OnDateChangedListner dateChangedListner;
    private OnNewRecordEntryLisnter newRecordEntryLisnter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
       @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.budget_list_fragment, container, false);

        tvAdvanceTotalAmount =  root.findViewById(R.id.tvAdvanceTotalAmount);
        tvTotalBudget =  root.findViewById(R.id.tvTotalBudget);
        tvYear =  root.findViewById(R.id.tvYear);
        tvYear.setText(new SimpleDateFormat("MMM-yyyy").format(new Date()).replace("-","\n").toUpperCase());
        tvTotalBudget.setText("Total:");

        rvBudget =  root.findViewById(R.id.rvBudget);
        adapter = new BudgetAdapter(SqliteHelper.getInstanciate(this.getContext()).getBudget( new SimpleDateFormat("MMM-yyyy").format(new Date())))
                    .setNewRecordEntryLisnter(newRecordEntryLisnter);
        rvBudget.setHasFixedSize(false);
        rvBudget.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvBudget.setAdapter(adapter);
        tvAdvanceTotalAmount.setText("₹"+Long.toString(adapter.getTotalAmount()));
        SqliteHelper.getInstanciate(root.getContext()).setONBudgetUpdateListner(new OnDataRefreshListner() {
            @Override
            public void refreshData() {
                adapter.notifyDataSetChanged();
                tvAdvanceTotalAmount.setText("₹"+Long.toString(adapter.getTotalAmount()));
            }
        });
        dateChangedListner = new OnDateChangedListner() {
            @Override
            public void onDateChanged(String date) {
                SqliteHelper.getInstanciate(getContext()).getBudget(date);
                adapter.setSelectedMonth(date);
                adapter.notifyDataSetChanged();
                tvAdvanceTotalAmount.setText("₹"+Long.toString(adapter.getTotalAmount()));
                tvYear.setText(date.replace("-","\n").toUpperCase());
            }
        };

        return root;
    }
    public OnDateChangedListner getGetMonthYeareListner(){
        return dateChangedListner;
    }
    public BudgetListFragment setNewRecordEntryLisnter(OnNewRecordEntryLisnter newRecordEntryLisnter){
        this.newRecordEntryLisnter = newRecordEntryLisnter;
        return this;
    }
}
