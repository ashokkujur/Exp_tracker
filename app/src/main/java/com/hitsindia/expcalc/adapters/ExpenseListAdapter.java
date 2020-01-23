package com.hitsindia.expcalc.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hitsindia.expcalc.MainActivity;
import com.hitsindia.expcalc.R;
import com.hitsindia.expcalc.helper.SqliteHelper;
import com.hitsindia.expcalc.listner.OnDateChangedListner;
import com.hitsindia.expcalc.listner.OnSelectDataFromSpinner;
import com.hitsindia.expcalc.model.ExpModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import  android.os.StrictMode;
public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ExpenseListViewHolder> {

    private ArrayList<ExpModel> models = new ArrayList<>();
    private Context context;
    public ExpenseListAdapter( ArrayList<ExpModel> models,Context context) {
        this.models = models;
        this.context = context;
    }
    public ExpModel getModel(int intPosition){
        return models.get(intPosition);
    }
    public  ExpenseListAdapter removeModelDatas(){
        models.clear();
        notifyDataSetChanged();
        return this;
    }
    public  ExpenseListAdapter setModels(ArrayList<ExpModel> models){
            this.models.addAll(models);
            notifyDataSetChanged();
            return this;
    }
    public  ArrayList<ExpModel> getAllModel(){
        return models;
    }
    @NonNull
    @Override
    public ExpenseListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_adapter,parent,false);
        return new ExpenseListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ExpenseListViewHolder holder, final int position) {
        try {
            holder.tvExpLstAdptrAmnt.setText("₹"+Long.toString( models.get(position).getLngAmount()));
            Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(models.get(position).getDtInvest());
            SimpleDateFormat formatter = new SimpleDateFormat("d-MMM");
            String strDate = formatter.format(date1);
            holder.tvExpLstAdptrCategory.setText(models.get(position).getStrCategory());
            holder.tvExpLstAdptrDesc.setText(models.get(position).getStrDesc());
            holder.tvExpLstAdptrSlNo.setText(strDate.replace("-","\n"));

         
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(holder.itemView.getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                    dialog.setContentView(R.layout.add_new_expense_activity);
                    dialog.show();

                    final TextView tvAdd  = dialog.findViewById(R.id.tvAdd);
                    final ImageView ivAttachment  = dialog.findViewById(R.id.ivAttachment);
                    final TextView tvAddExpTitle = dialog.findViewById(R.id.tvAddExpTitle);
                    final TextView tvSelectFile = dialog.findViewById(R.id.tvSelectFile);
                    tvAddExpTitle.setText("Update Expense");
                    tvSelectFile.setText(models.get(position).getStrAttachUrl());


                    MimeTypeMap myMime ;
                    String mimeType = "None";

                    if(models.get(position).getStrAttachUrl() != null){
                         myMime = MimeTypeMap.getSingleton();
                         mimeType = myMime.getMimeTypeFromExtension(fileExt(models.get(position).getStrAttachUrl()));
                    }
                    if(mimeType.equals("application/pdf")){
                        ivAttachment.setBackgroundResource(R.drawable.pdf);
                    }
                    else if(mimeType.equals("application/xls") || mimeType.equals("application/xlsx")){
                        ivAttachment.setBackgroundResource(R.drawable.xls);
                    }
                    else if(mimeType.equals("application/doc")){
                        ivAttachment.setBackgroundResource(R.drawable.doc);
                    }
                    else if(mimeType.equals("image/png") || mimeType.equals("image/jpeg")){
                        try {
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());

                            ivAttachment.setBackground(new BitmapDrawable(context.getResources(), MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(new java.io.File(models.get(position).getStrAttachUrl())))));
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    ivAttachment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());

                            MimeTypeMap myMime = MimeTypeMap.getSingleton();
                            Intent newIntent = new Intent(Intent.ACTION_VIEW);
                            String mimeType = myMime.getMimeTypeFromExtension(fileExt(models.get(position).getStrAttachUrl()));
                            newIntent.setDataAndType(Uri.fromFile(new java.io.File(models.get(position).getStrAttachUrl())),mimeType);
                            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                context.startActivity(newIntent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    final EditText etAmount = dialog.findViewById(R.id.etAmount);
                    AutoCompleteTextView etDesc = (AutoCompleteTextView) dialog.findViewById(R.id.etDesc);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(holder.itemView.getContext(),R.layout.support_simple_spinner_dropdown_item,new String[]{"ashok","kujur"});
                    etDesc.setAdapter(arrayAdapter);
                    etDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            etDesc.showDropDown();
                        }
                    });

                    final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                    tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    etAmount.requestFocus();
                    etAmount.setText(Long.toString(models.get(position).getLngAmount()));
                    etDesc.setText(models.get(position).getStrDesc());

                    final TextView tvSpinnerSelected = dialog.findViewById(R.id.tvSpinnerSelected);
                    tvSpinnerSelected.setText(models.get(position).getStrCategory());

                    final com.hitsindia.expcalc.helper.Spinner spCategoryList = dialog.findViewById(R.id.spCategoryList);
                    ArrayList<String> list= SqliteHelper.getInstanciate(holder.itemView.getContext()).getCategory();

                    SpinnerAdapter spinnerAdapter = new SpinnerAdapter(list,holder.itemView.getContext() );
                    spinnerAdapter.setOnDataListner(new OnSelectDataFromSpinner() {
                        @Override
                        public void onSelected(String strCategory) {
                            spCategoryList.dismiss();
                            tvSpinnerSelected.setText(strCategory);
                        }
                    });
                    spCategoryList .setAdapter(spinnerAdapter);

                    tvAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if( SqliteHelper.getInstanciate(holder.itemView.getContext()).updateExp(new ExpModel(models.get(position).getIntPrimaryKey(),etDesc.getText().toString(),Long.parseLong(etAmount.getText().toString()),models.get(position).getDtInvest(),tvSpinnerSelected.getText().toString(),models.get(position).getStrAttachUrl())) != -1) {
                                SqliteHelper.getInstanciate(holder.itemView.getContext()).getnewRecordEntryLisnter().onSaved(0);
                                Toast.makeText(holder.itemView.getContext() ,"record update successfully!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                ExpenseListAdapter.this.notifyDataSetChanged();
                            }
                        }
                    });
                    ExpenseListAdapter.this.notifyDataSetChanged();

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        if(models.get(position).getStrAttachUrl() != null){
            holder.tvAttachment.setVisibility(View.VISIBLE);
        } else{
                holder.tvAttachment.setVisibility(View.GONE);
        }
        holder.tvAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                String mimeType = myMime.getMimeTypeFromExtension(fileExt(models.get(position).getStrAttachUrl()));
                newIntent.setDataAndType(Uri.fromFile(new java.io.File(models.get(position).getStrAttachUrl())),mimeType);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    context.startActivity(newIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static String getDate(Date date, String dateFormat) {
        DateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public  static class ExpenseListViewHolder extends RecyclerView.ViewHolder{
        private TextView tvExpLstAdptrSlNo, tvExpLstAdptrCategory, tvExpLstAdptrDesc, tvExpLstAdptrAmnt,tvAttachment;
        private ImageView ivAttachment;
        public ExpenseListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExpLstAdptrSlNo = itemView.findViewById(R.id.tvExpLstAdptrSlNo);
            tvExpLstAdptrCategory = itemView.findViewById(R.id.tvExpLstAdptrCategory);
            tvExpLstAdptrDesc = itemView.findViewById(R.id.tvExpLstAdptrDesc);
            tvExpLstAdptrAmnt = itemView.findViewById(R.id.tvExpLstAdptrAmnt);
            ivAttachment = itemView.findViewById(R.id.ivAttachment);
            tvAttachment = itemView.findViewById(R.id.tvAttachment);
        }
    }
    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

}
