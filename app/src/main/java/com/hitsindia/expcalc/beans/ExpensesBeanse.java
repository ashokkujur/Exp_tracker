package com.hitsindia.expcalc.beans;

import com.hitsindia.expcalc.model.ExpModel;
import com.hitsindia.expcalc.model.OverviewModel;

import java.util.ArrayList;

public class ExpensesBeanse {
    private ExpModel expModel;
    private OverviewModel overviewModel;
    private ArrayList<String> arrBudget;

    public ExpensesBeanse() {
    }

    public ExpensesBeanse(ExpModel expModel, OverviewModel overviewModel, ArrayList<String> arrBudget) {
        this.expModel = expModel;
        this.overviewModel = overviewModel;
        this.arrBudget = arrBudget;
    }

    public ExpModel getExpModel() {
        return expModel;
    }

    public void setExpModel(ExpModel expModel) {
        this.expModel = expModel;
    }

    public OverviewModel getOverviewModel() {
        return overviewModel;
    }

    public void setOverviewModel(OverviewModel overviewModel) {
        this.overviewModel = overviewModel;
    }

    public ArrayList<String> getArrBudget() {
        return arrBudget;
    }

    public void setArrBudget(ArrayList<String> arrBudget) {
        this.arrBudget = arrBudget;
    }
}
