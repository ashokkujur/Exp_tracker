package com.hitsindia.expcalc.adapters;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hitsindia.expcalc.R;
import com.hitsindia.expcalc.helper.SqliteHelper;
import com.hitsindia.expcalc.listner.OnNewRecordEntryLisnter;
import com.hitsindia.expcalc.model.BudgetModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BudgetAdapter  extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private ArrayList<BudgetModel> models = new ArrayList<>();
    private String selectedMonth = null;
    private OnNewRecordEntryLisnter newRecordEntryLisnter;

    public BudgetAdapter(ArrayList<BudgetModel> models) {
        this.models = models;
        this.selectedMonth = new SimpleDateFormat("MMM-yyyy").format(new Date());
    }
    public long getTotalAmount(){
        long totalAmount = 0;
        for(BudgetModel model:models){
            totalAmount += model.getStrBudegetAmount();
        }
        return totalAmount;
    }
    public void setSelectedMonth(String selectedMonth){
        this.selectedMonth = selectedMonth;
    }
    public BudgetAdapter setNewRecordEntryLisnter(OnNewRecordEntryLisnter newRecordEntryLisnter){
        this.newRecordEntryLisnter  = newRecordEntryLisnter;
        return this;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_list_adapter, parent, false);
        return new BudgetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        holder.tvBudgetAdapterCategory.setText(models.get(position).getStrBudegetCategory());
        holder.tvBudgetAdapterAmount.setText("₹"+Long.toString(models.get(position).getStrBudegetAmount()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBudget(holder.itemView.getContext(),position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class BudgetViewHolder extends RecyclerView.ViewHolder{
        TextView tvBudgetAdapterCategory,tvBudgetAdapterAmount;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBudgetAdapterCategory = itemView.findViewById(R.id.tvBudgetAdapterCategory);
            tvBudgetAdapterAmount = itemView.findViewById(R.id.tvBudgetAdapterAmount);
        }
    }

    public void editBudget(Context context, int intPosition){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.budget_update_layout);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView amount = dialog.findViewById(R.id.etAmount);
        amount.setText(Long.toString(models.get(intPosition).getStrBudegetAmount()));
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        TextView tvUpdate = dialog.findViewById(R.id.tvUpdate);
        TextView tvDate = dialog.findViewById(R.id.tvDate);
        tvDate.setText(selectedMonth);
        tvCancel.setOnClickListener(v->dialog.dismiss());

        tvUpdate.setOnClickListener(v->{
            SqliteHelper.getInstanciate(context).updateBudget(new BudgetModel(models.get(intPosition).getIntKey(),Long.parseLong(amount.getText().toString())
                                                ,models.get(intPosition).getStrBudegetCategory()
                                                ,tvDate.getText().toString()),selectedMonth);
            dialog.dismiss();
            newRecordEntryLisnter.onSaved(models.get(intPosition).getIntKey());
        });
        dialog.show();
    }
}
