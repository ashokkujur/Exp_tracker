package com.hitsindia.expcalc.ui.home;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.hitsindia.expcalc.R;
import com.hitsindia.expcalc.adapters.DatesAdapter;
import com.hitsindia.expcalc.adapters.ExpenseListAdapter;
import com.hitsindia.expcalc.helper.RecyclerItemTouchHelper;
import com.hitsindia.expcalc.helper.SqliteHelper;
import com.hitsindia.expcalc.listner.OnDataRefreshListner;
import com.hitsindia.expcalc.listner.OnDateChangedListner;
import com.hitsindia.expcalc.listner.OnScrollChangedListner;
import com.hitsindia.expcalc.model.ExpModel;

import java.nio.file.attribute.GroupPrincipal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpensesListFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    private RecyclerView rvDates,rvExpenses;
    private ExpenseListAdapter expenseListAdapter;
    private DatesAdapter datesAdapter;
    private TextView tvFilterText, tvYear, tvFilterTextTitle, tvNoDataFound, tvAll;
    private String strFilterDate = "";
    private int intDay, intMonth, intYear;
    private ArrayList<String> date = new ArrayList<>();
    private Date date1;
    private String dateWithLetterMonth = "";
    private int intDateThisMonth ;
    private LinearLayout llDatePicker;
    private Long lngTotalMonth = 0l;
    private Long lngTotalToday = 0l;
    private Long lngTotalLastSeven = 0l;
    private ArrayList NoOfEmp = new ArrayList();
    private OnDateChangedListner dateChangedListner;
    private ImageView ivLeftScrollButton, ivRightScrollButton;

    public ExpensesListFragment setMonthYearListner(OnDateChangedListner dateChangedListner){
        this.dateChangedListner = dateChangedListner;
        return this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.expenses_list_fragment, container, false);

        final Calendar cal = Calendar.getInstance();
        intYear = cal.get(Calendar.YEAR);
        intMonth = cal.get(Calendar.MONTH);
        intDay = cal.get(Calendar.DAY_OF_MONTH);

        intDateThisMonth = intMonth;
        tvYear = root.findViewById(R.id.tvYear);

        ivLeftScrollButton = root.findViewById(R.id.ivLeftScrollButton);
        ivRightScrollButton = root.findViewById(R.id.ivRightScrollButton);

        tvFilterText = root.findViewById(R.id.tvFilterText);
        tvFilterTextTitle = root.findViewById(R.id.tvFilterTextTitle);
        tvNoDataFound = root.findViewById(R.id.tvNoDataFound);

        llDatePicker = root.findViewById(R.id.llDatePicker);
        rvDates = root.findViewById(R.id.rvDates);

        llDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(ExpensesListFragment.this.getContext(),R.style.DatePickerCustomTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                intDay = dayOfMonth;
                                intMonth = monthOfYear;
                                intYear = year;

                                Calendar calendar = Calendar.getInstance();
                                Date newDate = new Date();
                                newDate.setDate(dayOfMonth);
                                newDate.setMonth(monthOfYear);
                                newDate.setYear(year);
                                calendar.setTime(newDate);

                                datesAdapter.setNewDate((Integer.toString(dayOfMonth).length() ==1 ?"0"+Integer.toString(dayOfMonth):Integer.toString(dayOfMonth)), Integer.toString(monthOfYear+1).length() == 1?"0"+Integer.toString(monthOfYear+1):Integer.toString(monthOfYear+1),Integer.toString(year));

                                int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                                String pattern = "yyyy-MMM-dd";
                                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                ExpensesListFragment.this.date.clear();
                                for(int i = 1; i <= days; i++)
                                    ExpensesListFragment.this.date.add(Integer.toString(i) +"-"+ simpleDateFormat.format(newDate).split("-")[1]);
                                datesAdapter.setDate(ExpensesListFragment.this.date);

                                try{
                                    date1 = new SimpleDateFormat("dd/MM/yyyy").parse(((Integer.toString(dayOfMonth).length() == 1?"0"+Integer.toString(dayOfMonth):Integer.toString(dayOfMonth))+"/"
                                            + (Integer.toString(monthOfYear+1).length() == 1? "0"+Integer.toString(monthOfYear+1):Integer.toString(monthOfYear+1))+"/"+Integer.toString(year)));
                                    dateWithLetterMonth  = new SimpleDateFormat("dd MMM").format(date1);
                                }
                                catch (ParseException e){
                                    e.printStackTrace();
                                }
                                tvYear.setText(new SimpleDateFormat("MMM-yyyy").format(date1));
                                datesAdapter.getDateChangedListner().onDateChanged(Integer.toString(dayOfMonth));
                                dateChangedListner.onDateChanged(new SimpleDateFormat("MMM-yyyy").format(date1));
                            }
                        }, intYear, intMonth, intDay);
                datePickerDialog.show();
            }

        });

        rvDates = root.findViewById(R.id.rvDates);
        Calendar calendar = Calendar.getInstance();

        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String pattern = "yyyy-MMM-dd";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        for(int i = 1; i <= days; i++)
            date.add(Integer.toString(i));

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        strFilterDate = formatter.format(currentDate);
        tvAll = root.findViewById(R.id.tvAll);
        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    expenseListAdapter.removeModelDatas();
                    expenseListAdapter.notifyDataSetChanged();
                    lngTotalToday = 0l;
                    expenseListAdapter.setModels( SqliteHelper.getInstanciate(ExpensesListFragment.this.getContext()).getExpOfThisMonth(Integer.toString(date1.getMonth()).length() == 1? "0"+Integer.toString(date1.getMonth()).length():Integer.toString(date1.getMonth())+"/"+Integer.toString(date1.getYear())));
                    for (ExpModel model :expenseListAdapter.getAllModel()) {
                        lngTotalToday += model.getLngAmount();
                    }
                    if (intDateThisMonth == intMonth){
                        tvFilterTextTitle.setText("This month");
                        tvFilterText.setText("(₹" + Long.toString(lngTotalToday) + ")");
                    } else
                        try {
                            tvFilterTextTitle.setText(new SimpleDateFormat("MMM").format(new SimpleDateFormat(Integer.toString(date1.getMonth()).length() == 1? "0"+Integer.toString(date1.getMonth()):Integer.toString(date1.getMonth())+"/"+Integer.toString(date1.getYear()))));
                            tvFilterText.setText("(₹" + Long.toString(lngTotalToday) + ")");
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                tvNoDataFound.setVisibility(View.GONE);
                rvExpenses.setVisibility(View.VISIBLE);
                if(expenseListAdapter.getAllModel().size() == 0){
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    rvExpenses.setVisibility(View.GONE);
                }
                }
        });
        datesAdapter = new DatesAdapter(date,formatter.format(currentDate).split("/")[0],formatter.format(currentDate).split("/")[1],formatter.format(currentDate).split("/")[2],rvDates,this)
                .setDateChangedListner(new OnDateChangedListner() {
                    @Override
                    public void onDateChanged(String date) {
                        PopupWindow popupWindow = customProgressBar(root);
                        try{
                            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                            dateWithLetterMonth  = new SimpleDateFormat("dd MMM").format(date1);
                            tvYear.setText(new SimpleDateFormat("MMM-yyyy").format(new SimpleDateFormat("dd/MM/yyyy").parse(date)).replace("-","\n").toUpperCase());

                        } catch (ParseException e){
                            e.printStackTrace();
                        }

                        if(!date.split("/")[0].equals("All")) {

                            expenseListAdapter.removeModelDatas();
                            expenseListAdapter.setModels(SqliteHelper.getInstanciate(ExpensesListFragment.this.getContext()).getExpByDate(date));
                            lngTotalToday= 0l;
                            for(ExpModel model: SqliteHelper.getInstanciate(ExpensesListFragment.this.getContext()).getExpByDate(date)){
                                lngTotalToday += model.getLngAmount();
                            }

                            try {

                                if (dateWithLetterMonth.equals(new SimpleDateFormat("dd MMM").format(new Date()))){
                                    tvFilterText.setText("₹"+Long.toString(lngTotalToday));
                                    tvFilterTextTitle.setText("Today:");

                                }

                                else{
                                    tvFilterText.setText( "₹" + Long.toString(lngTotalToday));
                                    tvFilterTextTitle.setText(Integer.toString(Integer.parseInt(dateWithLetterMonth.split(" ")[0]))+" "+dateWithLetterMonth.split(" ")[1]+":");
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                        tvNoDataFound.setVisibility(View.GONE);
                        rvExpenses.setVisibility(View.VISIBLE);
                        if(expenseListAdapter.getAllModel().size() == 0){
                            tvNoDataFound.setVisibility(View.VISIBLE);
                            rvExpenses.setVisibility(View.GONE);
                        }
                        popupWindow.dismiss();
                    }
                });

        rvDates.setHasFixedSize(false);
        rvDates.setLayoutManager(new LinearLayoutManager(this.getContext(),RecyclerView.HORIZONTAL,false));
        rvDates.setAdapter(datesAdapter);
        datesAdapter.setOnScrollChangedListner(new OnScrollChangedListner() {
            @Override
            public void onSrolled(int intPosition) {
                if(intPosition == date.size()-1){
                    ivRightScrollButton.setVisibility(View.GONE);
                    ivLeftScrollButton.setVisibility(View.VISIBLE);
                }else if(intPosition == 0){
                    ivRightScrollButton.setVisibility(View.VISIBLE);
                    ivLeftScrollButton.setVisibility(View.GONE);
                }
                else {
                    ivLeftScrollButton.setVisibility(View.VISIBLE);
                    ivRightScrollButton.setVisibility(View.VISIBLE);
                }
            }
        });
        rvExpenses = root.findViewById(R.id.rvExpenses);
        tvFilterText = root.findViewById(R.id.tvFilterText);
        expenseListAdapter = new ExpenseListAdapter(SqliteHelper.getInstanciate(ExpensesListFragment.this.getContext()).getExpByDate(strFilterDate),getContext());
        rvExpenses.setHasFixedSize(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        rvExpenses.setLayoutManager(linearLayoutManager);

        rvExpenses.setAdapter(expenseListAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallbackdelet = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallbackdelet).attachToRecyclerView(rvExpenses);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallbackEdit = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallbackEdit).attachToRecyclerView(rvExpenses);

        dateWithLetterMonth  = new SimpleDateFormat("dd MMM").format(new Date());
        getAllData(root.getContext(),strFilterDate.split("/")[1]+"/"+strFilterDate.split("/")[2]);

        SqliteHelper.getInstanciate(root.getContext()).setRefreshListner(new OnDataRefreshListner() {
            @Override
            public void refreshData() {
                NoOfEmp.clear();
                lngTotalMonth = 0L;
                lngTotalToday = 0L;
                lngTotalLastSeven = 0L;
                SqliteHelper.getInstanciate(ExpensesListFragment.this.getContext()).getExpByDate(strFilterDate);
                getAllData(root.getContext(),strFilterDate.split("/")[1]+"/"+strFilterDate.split("/")[2]);
                expenseListAdapter.notifyDataSetChanged();
                if(expenseListAdapter.getAllModel().size()>0) {
                    tvNoDataFound.setVisibility(View.GONE);
                    rvExpenses.setVisibility(View.VISIBLE);
                }
            }
        });
        return root;
    }

    public void getAllData(Context context, String strMonth){

        List<ExpModel> expModels = SqliteHelper.getInstanciate(context).getExpOfThisMonth(strMonth);
        for(int i = 0;i<expModels.size();i++){
            NoOfEmp.add(new BarEntry(i , expModels.get(i).getLngAmount()));
        }

        for(ExpModel model : SqliteHelper.getInstanciate(context).getTodayExp()){
            lngTotalToday += model.getLngAmount();
        }
        for(ExpModel model :SqliteHelper.getInstanciate(context).getExpOfThisMonth(strFilterDate.split("/")[1]+"/"+strFilterDate.split("/")[2]) ){
            lngTotalMonth += model.getLngAmount();
        }
        int i = 0;
        for(ExpModel model : SqliteHelper.getInstanciate(context).getExpOfThisMonth(strFilterDate.split("/")[1]+"/"+strFilterDate.split("/")[2])){
            lngTotalLastSeven += model.getLngAmount();
            if(i == 7) break;
        }
        tvFilterText.setText("(₹"+Long.toString(lngTotalToday)+")");
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        SqliteHelper.getInstanciate(this.getContext()).deleteExp(expenseListAdapter.getModel(position));
    }
    private PopupWindow customProgressBar(View view){

        ProgressBar progressBar;
        LayoutInflater layoutInflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.custom_dialog_progressbar,null);

        progressBar = (ProgressBar)customView.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new FadingCircle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        return popupWindow;
    }
}
