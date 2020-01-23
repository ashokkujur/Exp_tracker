package com.hitsindia.expcalc.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.hitsindia.expcalc.R;
import com.hitsindia.expcalc.helper.SqliteHelper;
import com.hitsindia.expcalc.listner.OnDataRefreshListner;
import com.hitsindia.expcalc.listner.OnDateChangedListner;

public class HomeFragment extends Fragment {

    private TextView tvToday,tvThisWeek,tvThisMonth,tvBudget,tvYear;

    private int intDay, intMonth, intYear;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppBarLayout toolbar_layout;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       final View root = inflater.inflate(R.layout.fragment_home, container, false);
        tvToday = root.findViewById(R.id.tvToday);
        tvYear = root.findViewById(R.id.tvYear);
        tvThisWeek = root.findViewById(R.id.tvThisWeek);
        tvThisMonth = root.findViewById(R.id.tvThisMonth);

        toolbar_layout = root.findViewById(R.id.app_bar);


        viewPager = (ViewPager) root.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout)root.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tvThisMonth = root.findViewById(R.id.tvThisMonth);
        tvThisWeek = root.findViewById(R.id.tvThisWeek);

        tvBudget = root.findViewById(R.id.tvBudget);

        setOverViewData();
        SqliteHelper.getInstanciate(getContext()).setOnOverViewListner(new OnDataRefreshListner() {
            @Override
            public void refreshData() {
                setOverViewData();
            }
        });
        return root;
    }
    public void setOverViewData(){
        tvThisMonth.setTextColor(Color.BLACK);
        tvToday.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvToday.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        tvToday.setText("Today: ₹"+Long.toString(SqliteHelper.getInstanciate(getContext()).getTodayTotalExp()));
        tvThisWeek.setText(Long.toString(SqliteHelper.getInstanciate(getContext()).getLastSevenExp()));
        tvThisMonth.setText("₹"+Long.toString(SqliteHelper.getInstanciate(getContext()).getThisMonthTotalExp()));
        if(SqliteHelper.getInstanciate(getContext()).getThisMonthTotalExp() > SqliteHelper.getInstanciate(getContext()).getTotalBudget(new SimpleDateFormat("MMM-yyyy").format(new Date()))
            && SqliteHelper.getInstanciate(getContext()).getThisMonthTotalExp() != SqliteHelper.getInstanciate(getContext()).getTotalBudget(new SimpleDateFormat("MMM-yyyy").format(new Date()))){
            //tvThisMonth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.warning_24, 0, 0, 0);
            tvThisMonth.setTextColor(Color.RED);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        BudgetListFragment budgetListFragment = new BudgetListFragment()
                                                .setNewRecordEntryLisnter(newData -> {
                                                        tvThisMonth.setTextColor(Color.BLACK);
                                                    tvThisMonth.setText("₹"+Long.toString(SqliteHelper.getInstanciate(getContext()).getThisMonthTotalExp()));
                                                    if(SqliteHelper.getInstanciate(getContext()).getThisMonthTotalExp() > SqliteHelper.getInstanciate(getContext()).getTotalBudget(new SimpleDateFormat("MMM-yyyy").format(new Date()))
                                                            && SqliteHelper.getInstanciate(getContext()).getThisMonthTotalExp() != SqliteHelper.getInstanciate(getContext()).getTotalBudget(new SimpleDateFormat("MMM-yyyy").format(new Date()))){
                                                        //tvThisMonth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.warning_24, 0, 0, 0);
                                                        tvThisMonth.setTextColor(Color.RED);
                                                    }
                                                });

        adapter.addFragment(new ExpensesListFragment()
                .setMonthYearListner(new OnDateChangedListner() {
                    @Override
                    public void onDateChanged(String date) {
                        budgetListFragment.getGetMonthYeareListner().onDateChanged(date);
                    }
                }), "Expenses");
        adapter.addFragment(budgetListFragment, "Budget");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
