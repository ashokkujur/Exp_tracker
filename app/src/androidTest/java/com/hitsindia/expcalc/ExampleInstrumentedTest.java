package com.hitsindia.expcalc;

import android.content.Context;
import android.database.Cursor;
import android.icu.lang.UScript;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.hitsindia.expcalc.adapters.BudgetAdapter;
import com.hitsindia.expcalc.helper.SqliteHelper;
import com.hitsindia.expcalc.model.BudgetModel;
import com.hitsindia.expcalc.model.ExpModel;
import com.hitsindia.expcalc.model.SuggestModel;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.PortUnreachableException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.hitsindia.expcalc", appContext.getPackageName());
    }
    @Test
    public void addExp(){

       /* Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ExpModel  expModel = new ExpModel(null,144,new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        SqliteHelper.getInstanciate(appContext).addExp(expModel);
        List<ExpModel> expModels= SqliteHelper.getInstanciate(appContext).getAllExp();
        System.out.println(expModels.stream());
        System.out.println(SqliteHelper.getInstanciate(appContext).getExpOfThisMonth("0").size());
        System.out.println(SqliteHelper.getInstanciate(appContext).getTodayExp().size());*/

    }

    @Test
    public void  sqlitreQuery(){
        List<ExpModel> models = new ArrayList<>();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Cursor cursor = SqliteHelper.getInstanciate(appContext).getDb().rawQuery("SELECT * FROM TBL_EXP",null);
        if (cursor.moveToFirst()) {
            do {
                String test = cursor.getString(2);
                ExpModel expModel = new ExpModel();
                expModel.setIntPrimaryKey(Integer.parseInt(cursor.getString(0)));
                expModel.setStrDesc(cursor.getString(1));
                expModel.setLngAmount(cursor.getInt(2));
                expModel.setDtInvest(cursor.getString(3));
                models.add(expModel);
            } while (cursor.moveToNext());
        }
        for(ExpModel model: models){
            System.out.println(model);
        }
    }
    @Test
    public void inserData(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
       // SqliteHelper.getInstanciate(appContext).addExp(new ExpModel("this is test" , 4000 ,"15/02/2020"));
    }

    @Test
    public void insetCategory(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SqliteHelper.getInstanciate(appContext);
    }
    @Test
    public void getNoOfCategory(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        System.out.println("No of category:"+SqliteHelper.getInstanciate(appContext).getNoOfCategory());
    }

    @Test
    public void insertBudget(){
        /*Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SqliteHelper.getInstanciate(appContext).updateBudget(new BudgetModel(100,"llkk","Jan-2020"));*/
    }
    @Test
    public void getBudget(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ArrayList<BudgetModel>models =   SqliteHelper.getInstanciate(appContext).getBudget("Jan-2020");
        for(BudgetModel model:models)
            System.out.println(model.getStrBudegetAmount());
    }

    @Test
    public  void insetSuggest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
      //  SqliteHelper.getInstanciate(appContext).insertSuggest(new SuggestModel(0,"ok",0));
    }
    @Test
    public void getSuggest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ArrayList<SuggestModel> models= SqliteHelper.getInstanciate(appContext).getSugest();
        for(SuggestModel model: models){
            System.out.println(model.getStrSuggest());
        }
    }
    @Test
    public  void checkSuggest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SuggestModel model = SqliteHelper.getInstanciate(appContext).getSuggest("chai");
        System.out.println(model.getStrSuggest());
    }

    public void observer(){
        // observable
        Observable<String> animalsObservable = getAnimalsObservable();

        // observer
        Observer<String> animalsObserver = getAnimalsObserver();

        // observer subscribing to observable
        animalsObservable
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(animalsObserver);
    }
    @Test
    private Observer<String> getAnimalsObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "Name: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }

    private Observable<String> getAnimalsObservable() {
        return Observable.just("Ant", "Bee", "Cat", "Dog", "Fox");
    }
}
