package com.hitsindia.expcalc.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.hitsindia.expcalc.R;
import com.hitsindia.expcalc.listner.OnDateChangedListner;
import com.hitsindia.expcalc.listner.OnScrollChangedListner;

import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.stream.Stream;


public class DatesAdapter extends RecyclerView.Adapter<DatesAdapter.DatesViewHolder> {
    ArrayList<String> data = new ArrayList<>();
    private Fragment context;
    private ArrayList<DatesViewHolder> datesViewHolders = new ArrayList<>();
    private OnDateChangedListner dateChangedListner;
    private String strMonth = null;
    private String strYear = null;
    private String strDate = null;
    private RecyclerView recyclerView;
    private boolean isFirstTimeScroll = true;
    private OnScrollChangedListner onScrollChangedListner;

    public DatesAdapter(ArrayList<String> data,String strDate, String strMonth,String  strYear, RecyclerView recyclerView,Fragment context) {
        this.data.addAll(data);
        this.context = context;
        this.strDate = strDate;
        this.strMonth = strMonth;
        this.strYear = strYear;
        this.recyclerView = recyclerView;
    }
    public  DatesAdapter removeAllModelData(){
        data.clear();
        return this;
    }
    public void setNewDate(String strDate, String strMonth, String strYear){
        this.strDate = strDate;
        this.strMonth = strMonth;
        this.strYear =  strYear;
    }
    public DatesAdapter setDate(ArrayList<String> data){
        this.data.clear();
        this.data.add("All");
        this.data.addAll(data);
        this.notifyDataSetChanged();
        return this;
    }

    public DatesAdapter setDateChangedListner(OnDateChangedListner dateChangedListner){
        this.dateChangedListner = dateChangedListner;
        return this;
    }
    public void setOnScrollChangedListner(OnScrollChangedListner onScrollChangedListner){
        this.onScrollChangedListner = onScrollChangedListner;
    }
    public OnDateChangedListner getDateChangedListner(){
        return  new OnDateChangedListner() {
            @Override
            public void onDateChanged(String date) {
                strDate = date;
                for(DatesViewHolder datesViewHolder:datesViewHolders){
                    datesViewHolder.tvAdapterDate.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    datesViewHolder.tvAdapterDate.setBackgroundResource(R.drawable.date_adapter_date_backgroud);
                }
                try{
                    ((DatesViewHolder)recyclerView.findViewHolderForAdapterPosition(Integer.parseInt(date))).tvAdapterDate.setTextColor(Color.WHITE);
                    ((DatesViewHolder)recyclerView.findViewHolderForAdapterPosition(Integer.parseInt(date))).tvAdapterDate.setBackgroundResource(R.drawable.date_adapter_date_selected);
                    datesViewHolders.add( ((DatesViewHolder)recyclerView.findViewHolderForAdapterPosition(Integer.parseInt(date))));
                    ((DatesViewHolder)recyclerView.findViewHolderForAdapterPosition(Integer.parseInt(date))).itemView.setSelected(true);
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                    recyclerView.scrollToPosition(Integer.parseInt(date));
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            ((DatesViewHolder)DatesAdapter.this.recyclerView.findViewHolderForAdapterPosition(Integer.parseInt(date))).tvAdapterDate.setTextColor(Color.WHITE);
                            ((DatesViewHolder)DatesAdapter.this.recyclerView.findViewHolderForAdapterPosition(Integer.parseInt(date))).tvAdapterDate.setBackgroundResource(R.drawable.date_adapter_date_selected);
                            datesViewHolders.add( ((DatesViewHolder)DatesAdapter.this.recyclerView.findViewHolderForAdapterPosition(Integer.parseInt(date))));
                            ((DatesViewHolder)DatesAdapter.this.recyclerView.findViewHolderForAdapterPosition(Integer.parseInt(date))).itemView.setSelected(true);
                            recyclerView.removeOnScrollListener(this);
                        }
                    });
                }

            }
        };
    }
    @NonNull
    @Override
    public DatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dates_adapter, parent, false);
        return new DatesViewHolder(view);
    }

    @Override
    public void onViewRecycled(@NonNull DatesViewHolder holder) {
        super.onViewRecycled(holder);
        holder.tvAdapterDate.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        holder.tvAdapterDate.setBackgroundResource(R.drawable.date_adapter_date_backgroud);
    }

    @Override
    public void onBindViewHolder(@NonNull final DatesViewHolder holder, final int position) {
        try {
            onScrollChangedListner.onSrolled(position);
           if(Integer.parseInt(data.get(position).split("-")[0]) == Integer.parseInt(strDate)) {
               dateChangedListner.onDateChanged((data.get(position).split("-")[0].length() == 1? "0"+data.get(position).split("-")[0]:data.get(position).split("-")[0]) + "/" + strMonth + "/" + strYear);
               holder.tvAdapterDate.setTextColor(Color.WHITE);
               holder.tvAdapterDate.setBackgroundResource(R.drawable.date_adapter_date_selected);
               datesViewHolders.add(holder);
               holder.itemView.setSelected(true);

           }

           if (isFirstTimeScroll) {
               recyclerView.getLayoutManager().scrollToPosition(Integer.parseInt(strDate));
               isFirstTimeScroll = false;
            }
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }

        holder.tvAdapterDate.setText(data.get(position));
        holder.tvAdapterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(position);
                strDate = holder.tvAdapterDate.getText().toString().split("-")[0].length() == 1? "0"+holder.tvAdapterDate.getText().toString().split("-")[0]:holder.tvAdapterDate.getText().toString().split("-")[0];
                dateChangedListner.onDateChanged(strDate+"/"+strMonth+"/"+strYear);
                for(DatesViewHolder datesViewHolder:datesViewHolders){
                     datesViewHolder.tvAdapterDate.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                     datesViewHolder.tvAdapterDate.setBackgroundResource(R.drawable.date_adapter_date_backgroud);
                }
                holder.tvAdapterDate.setTextColor(Color.WHITE);
                holder.tvAdapterDate.setBackgroundResource(R.drawable.date_adapter_date_selected);
                datesViewHolders.add(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class DatesViewHolder extends RecyclerView.ViewHolder{
         TextView tvAdapterDate;
        public DatesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAdapterDate = itemView.findViewById(R.id.tvAdapterDate);
        }
    }
}
