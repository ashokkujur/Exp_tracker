package com.hitsindia.expcalc.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import com.hitsindia.expcalc.listner.OnDataRefreshListner;
import com.hitsindia.expcalc.listner.OnNewRecordEntryLisnter;
import com.hitsindia.expcalc.model.BudgetModel;
import com.hitsindia.expcalc.model.ExpModel;
import com.hitsindia.expcalc.model.SuggestModel;

import java.sql.SQLInvalidAuthorizationSpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public  class SqliteHelper extends SQLiteOpenHelper {

    private static  final String SQLITE_DB= "DB_EXP_CAL";

    /*--------- Expense table constants  -----------------*/
    private static final String SQLITE_TABLE_EXPENSES = "TBL_EXP";
    private static final int    DB_VERSION = 784455;
    private static final String KEY_PRIMARY = "id";
    private static final String KEY_DESC= "exp_descr";
    private static final String KEY_AMOUNT = "exp_amount";
    private static final String KEY_DATE = "exp_date";
    private static final String KEY_EXP_CATEGORY = "exp_category";
    private static final String KEY_EXP_ATTACHMENT = "attachment";
    /*---------------- Expenses budget table constants ------------------------*/
    private static final String SQLITE_TABLE_BUDGET = "TBL_EXP_BUDGET";
    private static final String KEY_BUDGET_PRIMARY_KEY = "id";
    private static final String KEY_BUDGET = "budget_amount";
    private static final String KEY_BUDGET_CATEGORY = "budget_category";
    private static final String KEY_BUDGET_MONTH = "budget_month";

    /*------------------ Expenses category table constant --------------*/
    private static final String SQLITE_TABLE_CATEGORY = "TBLE_CATEGORY";
    private static final String KEY_CATEGORY_PRIMARY_KEY = "id";
    private static final String KEY_CATEGORY = "category";
    private static final String []categories = {"Housing","Clothing","Education","Healthcare","Transport"};

    /*----------------- Expenses category suggest list ----------------------*/
    private static final String SQLITE_TABLE_SUGGEST_LIST = "TABLE_CATEGORY_HINT";
    private static final String KEY_SUGGEST_PRIMARY_KEY = "id";
    private static final String KEY_SUGGEST = "hint";
    private static final String KEY_SUGGEST_NO_OF_HITS = "no_of_hits";

    private static SqliteHelper sqliteHelper;
    private SQLiteDatabase db;
    private ArrayList<ExpModel> expModelsToday = new ArrayList<ExpModel>();
    private ArrayList<ExpModel> expModelsOfThisMonth = new ArrayList<ExpModel>();
    private ArrayList<ExpModel> expModelsAll = new ArrayList<ExpModel>();
    private OnDataRefreshListner newDataRefreshListner;
    private OnDataRefreshListner budegetataRefreshListner;
    private OnDataRefreshListner overViewDataRefreshListner;
    private OnNewRecordEntryLisnter newRecordEntryLisnter;
    private  ArrayList<String> category = new ArrayList<>();
    private ArrayList<BudgetModel> budgets = new ArrayList<>();


    public static  SqliteHelper getInstanciate(Context context){
        SqliteHelper sqliteHelper  = null;

        /* This loop run for insert first time for insert category into SQLITE_TABLE_CATEGORY table. */
        if(SqliteHelper.sqliteHelper == null){
            sqliteHelper = SqliteHelper.sqliteHelper = new SqliteHelper(context,null,null,0);

            /*This 'if case' use for check default category available or not in SQLITE_TABLE_CATEGORY table.*/
            if(sqliteHelper.getNoOfCategory() == 0)
                sqliteHelper.insertCategory(categories);

            return sqliteHelper;
        }
        else
            return sqliteHelper = SqliteHelper.sqliteHelper;

    }

    public SqliteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {

        super(context,SQLITE_DB, factory, DB_VERSION);

        /* This listner user of update of or add into any table that time helps to change the model data for update on ui. */
        newRecordEntryLisnter = new OnNewRecordEntryLisnter(){
            @Override
            public void onSaved(int refreshCode) {
                newDataRefreshListner.refreshData();
            }
        };
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        /**@param   CREATE_EXP_TABLE
        * This query used for create new expenses table .
        **/
        String CREATE_EXP_TABLE = "CREATE TABLE " + SQLITE_TABLE_EXPENSES+ "("
                + KEY_PRIMARY + " INTEGER PRIMARY KEY," + KEY_DESC + " TEXT,"
                + KEY_AMOUNT + " BIGINT," + KEY_DATE+ " TEXT," +  KEY_EXP_CATEGORY+ " TEXT," +  KEY_EXP_ATTACHMENT+ " TEXT" +")";
        db.execSQL(CREATE_EXP_TABLE);

        /**@param CREATE_EXP_BUDGET_TABLE
         * This query used for create budget table.
         * */
        String CREATE_EXP_BUDGET_TABLE = "CREATE TABLE " + SQLITE_TABLE_BUDGET+ "("
                + KEY_BUDGET_PRIMARY_KEY+ " INTEGER PRIMARY KEY,"  +  KEY_BUDGET+ " BIGINT,"
                + KEY_BUDGET_CATEGORY + " TEXT,"   +  KEY_BUDGET_MONTH + " TEXT"+")";
        db.execSQL(CREATE_EXP_BUDGET_TABLE);

        /**@param CREATE_EXP_CATEGORY_TABLE
        * This query used for create category table
        * */
        String CREATE_EXP_CATEGORY_TABLE = "CREATE TABLE " + SQLITE_TABLE_CATEGORY+ "("
                + KEY_CATEGORY_PRIMARY_KEY+ " INTEGER PRIMARY KEY," +  KEY_CATEGORY+ " TEXT"+")";
        db.execSQL(CREATE_EXP_CATEGORY_TABLE);

        /**@param CREAT_EXP_CATEGORY_HINT_TABLE
         * This query used for create category hints table
         * */
        String CREAT_EXP_CATEGORY_HINT_TABLE = "CREATE TABLE " + SQLITE_TABLE_SUGGEST_LIST + "("
                        + KEY_SUGGEST_PRIMARY_KEY + " INTEGER PRIMARY KEY," +KEY_SUGGEST + " TEXT,"
                        + KEY_SUGGEST_NO_OF_HITS + " INTEGER" + ")";
        db.execSQL(CREAT_EXP_CATEGORY_HINT_TABLE);
    }

    public SQLiteDatabase getDb(){
        return  this.getWritableDatabase();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE_EXPENSES);
        onCreate(db);
    }

    public boolean addExp(ExpModel expModel) {
        boolean blnerr = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DESC, expModel.getStrDesc());
        values.put(KEY_AMOUNT, expModel.getLngAmount());
        values.put(KEY_DATE, expModel.getDtInvest());
        values.put(KEY_EXP_CATEGORY, expModel.getStrCategory());
        values.put(KEY_EXP_ATTACHMENT, expModel.getStrAttachUrl());
        db.insert(SQLITE_TABLE_EXPENSES, null, values);
        db.close();
        overViewDataRefreshListner.refreshData();
        blnerr = true;
        return blnerr;
    }

    public  ExpModel getExp(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SQLITE_TABLE_EXPENSES, new String[] { KEY_PRIMARY,
                        KEY_DESC, KEY_AMOUNT, KEY_DATE}, KEY_PRIMARY + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        ExpModel expModel = new ExpModel(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),cursor.getInt(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
        return expModel;
    }

    public  ArrayList<ExpModel> getExpByDate(String date) {
        expModelsAll.clear();
        String selectQuery = "SELECT  * FROM " + SQLITE_TABLE_EXPENSES+" WHERE "+KEY_DATE+" = "+"'"+date+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExpModel expModel = new ExpModel();
                expModel.setIntPrimaryKey(Integer.parseInt(cursor.getString(0)));
                expModel.setStrDesc(cursor.getString(1));
                expModel.setLngAmount(cursor.getLong(2));
                expModel.setDtInvest(cursor.getString(3));
                expModel.setStrCategory(cursor.getString(4));
                expModel.setStrAttachUrl(cursor.getString(5));
                expModelsAll.add(expModel);
            } while (cursor.moveToNext());
        }
        return expModelsAll;
    }

    public ArrayList<ExpModel> getAllExp() {
        expModelsAll.clear();
        String selectQuery = "SELECT  * FROM " + SQLITE_TABLE_EXPENSES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExpModel expModel = new ExpModel();
               expModel.setIntPrimaryKey(Integer.parseInt(cursor.getString(0)));
               expModel.setStrDesc(cursor.getString(1));
               expModel.setLngAmount(cursor.getLong(2));
               expModel.setDtInvest(cursor.getString(3));
               expModel.setStrCategory(cursor.getString(4));
               expModel.setStrAttachUrl(cursor.getString(5));
               expModelsAll.add(expModel);
            } while (cursor.moveToNext());
        }
        return expModelsAll;
    }

    public int updateExp(ExpModel expModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DESC, expModel.getStrDesc());
        values.put(KEY_AMOUNT, expModel.getLngAmount());
        values.put(KEY_DATE, expModel.getDtInvest());
        values.put(KEY_EXP_CATEGORY, expModel.getStrCategory());
        values.put(KEY_EXP_ATTACHMENT, expModel.getStrAttachUrl());
        int result =db.update(SQLITE_TABLE_EXPENSES, values, KEY_PRIMARY + " = ?",
                new String[] { String.valueOf(expModel.getIntPrimaryKey()) });
        this.newDataRefreshListner.refreshData();
        this.overViewDataRefreshListner.refreshData();
        return result;
    }

    public void deleteExp(ExpModel expModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SQLITE_TABLE_EXPENSES, KEY_PRIMARY + " = ?",
                new String[] { String.valueOf(expModel.getIntPrimaryKey()) });
        db.close();
        this.newDataRefreshListner.refreshData();
        this.overViewDataRefreshListner.refreshData();
    }

    public int getExpCount() {

        String countQuery = "SELECT  * FROM " + SQLITE_TABLE_EXPENSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    public ArrayList<ExpModel> getExpOfThisMonth(String strMonth){
        expModelsOfThisMonth.clear();
        String selectQuery = "SELECT  * FROM " + SQLITE_TABLE_EXPENSES+" WHERE "+ KEY_DATE + " LIKE " + "'%"+strMonth+"%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                  ExpModel expModel = new ExpModel();
                  expModel.setIntPrimaryKey(Integer.parseInt(cursor.getString(0)));
                  expModel.setStrDesc(cursor.getString(1));
                  expModel.setLngAmount(cursor.getLong(2));
                  expModel.setDtInvest(cursor.getString(3));
                  expModel.setStrCategory(cursor.getString(4));
                  expModel.setStrAttachUrl(cursor.getString(5));
                  expModelsOfThisMonth.add(expModel);

            } while (cursor.moveToNext());
        }
        return expModelsOfThisMonth;
    }

    public  List<ExpModel> getTodayExp() {

        String strTody = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        expModelsToday.clear();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SQLITE_TABLE_EXPENSES, new String[] { KEY_PRIMARY,
                        KEY_DESC, KEY_AMOUNT, KEY_DATE,KEY_EXP_CATEGORY,KEY_EXP_ATTACHMENT}, KEY_DATE + "=?",
                new String[] { String.valueOf(strTody) }, null, null, null, null);
        if (cursor.moveToFirst()) {

            do {
                 ExpModel expModel = new ExpModel();
                 expModel.setIntPrimaryKey(Integer.parseInt(cursor.getString(0)));
                 expModel.setStrDesc(cursor.getString(1));
                 expModel.setLngAmount(cursor.getLong(2));
                 expModel.setDtInvest(cursor.getString(3));
                 expModel.setStrCategory(cursor.getString(4));
                 expModel.setStrAttachUrl(cursor.getString(5));
                expModelsToday.add(expModel);

            } while (cursor.moveToNext());
        }
        return expModelsToday;
    }

    public long getTodayTotalExp(){
        long intTotal = 0;
        for(ExpModel model:getTodayExp()){
            intTotal += model.getLngAmount();
        }
        return intTotal;
    }

    public long getThisMonthTotalExp(){
            long lngTotal = 0;
            for(ExpModel model: getExpOfThisMonth(new SimpleDateFormat("MM/yyyy").format(new Date()))){
                lngTotal += model.getLngAmount();
            }
            return lngTotal;
    }
    public long getLastSevenExp(){
        long lngTotal = 0;
        for(ExpModel model: getExpOfThisMonth(new SimpleDateFormat("MM/yyyy").format(new Date()))){
            if(Integer.parseInt(new SimpleDateFormat("dd/MM/yyyy").format(new Date()).split("/")[0]) - Integer.parseInt(model.getDtInvest().split("/")[0])<7)
                lngTotal += model.getLngAmount();
        }
        return lngTotal;
    }

    public void setRefreshListner(OnDataRefreshListner dataRefreshListner){
        this.newDataRefreshListner = dataRefreshListner;
    }
    public void setOnOverViewListner(OnDataRefreshListner overViewDataRefreshListner){
        this.overViewDataRefreshListner = overViewDataRefreshListner;
    }

    public void setONBudgetUpdateListner(OnDataRefreshListner budegetataRefreshListner) {
        this.budegetataRefreshListner = budegetataRefreshListner;
    }

    public OnNewRecordEntryLisnter getnewRecordEntryLisnter() {
        return newRecordEntryLisnter;
    }


    public ArrayList<BudgetModel> getBudget(String strBudgetDate){
        budgets.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + SQLITE_TABLE_BUDGET +" WHERE "+ KEY_BUDGET_MONTH + " = " +"'"+strBudgetDate+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BudgetModel model = new BudgetModel(cursor.getInt(0),cursor.getInt(1),cursor.getString(2), cursor.getString(3));
                budgets.add(model);
            } while (cursor.moveToNext());
        } else{
            if(budgets.size() == 0){
                ArrayList<String>  Arrcategories = getCategory();
                for (int i = 0; i<Arrcategories .size()-1;i++){
                    updateBudget(new BudgetModel(i,0L,Arrcategories.get(i),strBudgetDate),strBudgetDate);
                }
               getBudget(strBudgetDate);
            }
        }
        return  budgets ;
    }
    public ArrayList<BudgetModel> getBudget(String strBudgetDate,String strCategory){
        budgets.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + SQLITE_TABLE_BUDGET +" WHERE "+ KEY_BUDGET_MONTH + " = " +"'"+strBudgetDate+"'"
                                + " AND " +KEY_BUDGET_CATEGORY +" = "+"'"+strCategory+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BudgetModel model = new BudgetModel(cursor.getInt(0),cursor.getInt(1), cursor.getString(3), cursor.getString(2));
                budgets.add(model);
            } while (cursor.moveToNext());
        }

        return  budgets ;
    }

    public boolean updateBudget(BudgetModel model,String strMonth){
        boolean blnerr = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BUDGET, model.getStrBudegetAmount());
        values.put(KEY_BUDGET_CATEGORY,model.getStrBudegetCategory());
        values.put(KEY_BUDGET_MONTH, model.getStrBudgetMonth());
        if(getBudget(strMonth,model.getStrBudegetCategory()).size() == 0 ){
            db.insert(SQLITE_TABLE_BUDGET, null, values);
        }
        else {
            if(strMonth.equals(new SimpleDateFormat("MMM-yyyy").format(new Date()))) {
                db.update(SQLITE_TABLE_BUDGET, values, KEY_BUDGET_MONTH + " = ?" + " AND " + KEY_BUDGET_CATEGORY + " = ?",
                        new String[]{new SimpleDateFormat("MMM-yyyy").format(new Date()), String.valueOf(model.getStrBudegetCategory())});

            }
        }
        db.close();
        getBudget(model.getStrBudgetMonth());
        try{
            budegetataRefreshListner.refreshData();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        blnerr = true;
        return blnerr;
    }

    public long getTotalBudget(String strMonthYear){
        long lngTotal = 0;
        for(BudgetModel model:getBudget(strMonthYear)){
            lngTotal += model.getStrBudegetAmount() ;
        }
        return lngTotal;
    }
    public void insertCategory(String... strCategories){

        for(String strCategory : strCategories){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_CATEGORY, strCategory);
            db.insert(SQLITE_TABLE_CATEGORY, null, values);
        }
        getCategory();
    }
    public ArrayList<String> getCategory(){
        category.clear();
        String selectQuery = "SELECT  * FROM " + SQLITE_TABLE_CATEGORY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                category.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        category.add("New");
        return category;
    }

    public  int getNoOfCategory(){
        String countQuery = "SELECT  * FROM " + SQLITE_TABLE_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void deleteCategory(String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SQLITE_TABLE_CATEGORY, KEY_CATEGORY + " = ?",
                new String[] { String.valueOf(category) });
        db.close();
        this.newDataRefreshListner.refreshData();
        getCategory();
    }
    public void insertSuggest(String  strSuggest){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_SUGGEST,strSuggest);
            if(!isSuggestContained(strSuggest) ) {
                values.put(KEY_SUGGEST_NO_OF_HITS, 0);
                db.insert(SQLITE_TABLE_SUGGEST_LIST, null, values);
            } else{
                SuggestModel model =getSuggest(strSuggest);
                values.put(KEY_SUGGEST_NO_OF_HITS, model.getIntNoOfHit()+1);
                db.update(SQLITE_TABLE_SUGGEST_LIST,values,KEY_SUGGEST_PRIMARY_KEY + " = ?" ,new String[]{Integer.toString(model.getIntPrimaryKey())});
            }
    }

    public  SuggestModel getSuggest(String strSuggest) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SQLITE_TABLE_SUGGEST_LIST, new String[] { KEY_SUGGEST_PRIMARY_KEY,
                        KEY_SUGGEST, KEY_SUGGEST_NO_OF_HITS}, KEY_SUGGEST + "=?",
                new String[] { String.valueOf(strSuggest) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        SuggestModel model = new SuggestModel(cursor.getInt(0),
                cursor.getString(1),cursor.getInt(2));
        return model;
    }

    public boolean isSuggestContained(String strSuggest){
        String selectQuery = "SELECT  * FROM " + SQLITE_TABLE_SUGGEST_LIST +" WHERE " +KEY_SUGGEST +"=" + "'"+ strSuggest +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToNext())
            return true;
        return false;
    }
    public  ArrayList<SuggestModel> getSugest(){
        ArrayList<SuggestModel> models = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + SQLITE_TABLE_SUGGEST_LIST + " ORDER BY "+ KEY_SUGGEST_NO_OF_HITS +" DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                SuggestModel model =new SuggestModel();
                model.setIntPrimaryKey(cursor.getInt(0));
                model.setStrSuggest(cursor.getString(1));
                model.setIntNoOfHit(cursor.getInt(1));
                models.add(model);
            } while (cursor.moveToNext());
        }
        return models;
    }
}
