package com.hitsindia.expcalc;

import android.content.Context;
import android.util.Log;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest  {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void stringToDate(){
        try {
            String date = "31/12/2019";
            Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);

            System.out.println(date1.getDate());
            System.out.println( new SimpleDateFormat("dd-MMM").format(date1));
            Calendar calendar = Calendar.getInstance();
            System.out.println(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        catch (ParseException e){
            e.printStackTrace();
        }
    }
    @Test
    public void test(){
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(new String[]{}));
        arrayList.add("Asok");
        for (String s:arrayList){
            System.out.println(s);
        }
        new test(){

        };

    }
    interface RC
    {
        void Run();
        void Turn(Boolean leftRight);
        void Reverse();
        void Stop();
        abstract void Launch();
    }
    abstract class test{
        void test(String s){

        }
           void  test(){

        }
    }
    String result;
    String hello = "Hello";

}