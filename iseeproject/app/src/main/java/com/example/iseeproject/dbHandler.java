package com.example.iseeproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class dbHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SmartBucksData";

    // Contacts table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_EXPENSES = "expenses";
    private static final String TABLE_CATEGORIES = "categories";

    // User Table Columns names
    private static final String KEY_USN = "username";
    private static final String KEY_PWD = "password";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL= "email";
    private static final String KEY_INCOME = "income";
    private static final String KEY_BUDGET= "budget";
    private static final String KEY_RENT = "rent";
    private static final String KEY_BILLS = "bills";
    private static final String KEY_INSURANCE = "insurance";

    // Expenses Table Columns names
    private static final String KEY_ADDTIME = "addition_time";
    private static final String KEY_REALTIME = "expense_time";
    private static final String KEY_PRICE = "price";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_PAYMENT = "payment_method";

    private static final String KEY_CAT1 = "Leisure";
    private static final String KEY_CAT2 = "Food";
    private static final String KEY_CAT3 = "Bills";
    private static final String KEY_CAT4 = "Miscellaneous";

    public dbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USN + " TEXT PRIMARY KEY," + KEY_PWD + " TEXT ," + KEY_NAME + " TEXT,"
                +  KEY_EMAIL + " TEXT," + KEY_INCOME + " REAL, " +  KEY_BUDGET + " REAL," + KEY_RENT + " REAL, " +
                KEY_BILLS + " REAL, " + KEY_INSURANCE + " REAL " + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + KEY_ADDTIME + " TEXT PRIMARY KEY, "+ KEY_REALTIME + " TEXT," + KEY_USN + " TEXT REFERENCES " + TABLE_USER +" (" + KEY_USN + ") , " + KEY_PRICE + " REAL,"
                + KEY_CATEGORY + " TEXT," + KEY_PAYMENT + " TEXT )";
        db.execSQL(CREATE_EXPENSES_TABLE);

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + KEY_USN + " TEXT PRIMARY KEY, "+ KEY_CAT1 + " REAL," + KEY_CAT2 + " REAL," + KEY_CAT3 + " REAL,"
                + KEY_CAT4 + " REAL )";
        db.execSQL(CREATE_CATEGORIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        // Creating tables again
        onCreate(db);
    }

    //TODO convert-md5-back-to-normal method

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public boolean addUser(User usr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USN, usr.getUsername());
        values.put(KEY_PWD, usr.getPwd());
        values.put(KEY_NAME , usr.getName());
        values.put(KEY_EMAIL , usr.getEmail());
        values.put(KEY_INCOME , usr.getIncome());
        values.put(KEY_BUDGET , usr.getBudget());
        values.put(KEY_RENT, usr.getRent());
        values.put(KEY_BILLS , usr.getBills());
        values.put(KEY_INSURANCE , usr.getInsurance());

        long result = db.insert(TABLE_USER,null,values);
        if (result == -1){
            return false;
        }
        else
        {
            return true;
        }

    }

    public void addExpenses(Expenses exp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //why comment addtime and username?
        values.put(KEY_ADDTIME , exp.getAdditionTime());
        values.put(KEY_REALTIME ,exp.getExpenseTime());
        values.put(KEY_USN, exp.getUsername());
        values.put(KEY_PRICE, exp.getPrice());
        values.put(KEY_CATEGORY, exp.getCategory());
        values.put(KEY_PAYMENT,exp.getPaymentMethod());

        db.insert(TABLE_EXPENSES, null, values);
        db.close(); // Closing database connection
    }

    public void addCatThresholds(String username, Map<String,Double> thresholds) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USN, username);
        for (Map.Entry<String,Double> entry : thresholds.entrySet()) {
            //key of the map is the column name and value is the threshold set
            values.put(entry.getKey(), entry.getValue());
        }
        db.insert(TABLE_CATEGORIES, null, values);
        db.close(); // Closing database connection
    }

    //method to add a new expenses category
    public boolean updateCategories(String[] newCat) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] cols = getCategoriesNames();
        for (int i= 0;i<= newCat.length;i++) {
            db.execSQL("ALTER TABLE " + TABLE_CATEGORIES + " ADD COLUMN " + newCat[i] + "REAL ;");
        }
        boolean booly;
        if (cols.length < (getCategoriesNames()).length) {
            //succesful addition to the database
            //String username, Map<String,Double> thresholds
            booly=true;
        }
        else{
            booly = false;
        }
        db.close();
        return booly;
    }

    public String[] getCategoriesNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, null);
        String[] colNames = cursor.getColumnNames();
        for (String name : colNames) {
            //check if specific column contains a threshold set from the user

        }
        cursor.close();
        db.close();
        return colNames;
    }

    //public Map<String,Double> getThresholds(String username) {

      //  return
    //}

    public User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[] {KEY_USN , KEY_PWD , KEY_NAME
                        , KEY_EMAIL, KEY_INCOME , KEY_BUDGET, KEY_RENT , KEY_BILLS,KEY_INSURANCE}, KEY_USN + "=?",
                new String[] { username }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        User user = new User(cursor.getString(0), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), Double.parseDouble(cursor.getString(4)),
                Double.parseDouble(cursor.getString(5)), Double.parseDouble(cursor.getString(6)),
                Double.parseDouble(cursor.getString(7)), Double.parseDouble(cursor.getString(8)));

        cursor.close();
        return user;

    }

    public boolean isUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[] {KEY_USN , KEY_PWD , KEY_NAME
                        ,KEY_EMAIL, KEY_INCOME , KEY_BUDGET, KEY_RENT , KEY_BILLS,KEY_INSURANCE}, KEY_USN + "=?",
                new String[] { username }, null, null, null, null);
        if (cursor!=null && cursor.getCount()>0) {
            cursor.close();
            return true;
        }
        else{
            return false;
        }


    }


    public int updateUser(User usr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USN, usr.getUsername());
        values.put(KEY_PWD, usr.getPwd());
        values.put(KEY_NAME , usr.getName());
        values.put(KEY_EMAIL , usr.getEmail());
        values.put(KEY_INCOME , usr.getIncome());
        values.put(KEY_BUDGET , usr.getBudget());
        values.put(KEY_RENT, usr.getRent());
        values.put(KEY_BILLS , usr.getBills());
        values.put(KEY_INSURANCE , usr.getInsurance());

// updating row
        return db.update(TABLE_USER, values, KEY_USN + " = ?",
                new String[]{String.valueOf(usr.getUsername())});
    }

    public boolean expensesExist(User user) {
        //String selectQuery = "SELECT * FROM " + TABLE_EXPENSES + " WHERE " + KEY_USN + " = " + user.getUsername();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_EXPENSES, new String[] {KEY_ADDTIME, KEY_REALTIME,KEY_USN ,KEY_PRICE ,KEY_CATEGORY ,KEY_PAYMENT}, KEY_USN + "=?",
                new String[] { user.getUsername() }, null, null, null, null);
        //Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor!=null && cursor.getCount()>0) {
            cursor.close();
            return true;
        }
        else{
            return false;
        }
    }

    /*public ArrayList<Expenses> getExpenses(){
        SQLiteDatabase ab = this.getWritableDatabase();
        ArrayList<Expenses> expenselist = new ArrayList<>();
        String expensequery = "SELECT expense_time,price,category,payment_method FROM " +TABLE_EXPENSES;
        Cursor cursor= ab.rawQuery(expensequery,null);

        while(cursor.moveToNext()){
            Expenses exp = new Expenses();
            exp.setExpenseTime(cursor.getString(cursor.getColumnIndex(KEY_REALTIME)));
            exp.setPrice(cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)));
            exp.setCategory(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
            exp.setPaymentMethod(cursor.getString(cursor.getColumnIndex(KEY_PAYMENT)));
            expenselist.add(exp);

        }
        return expenselist;
    }*/


    public ArrayList<Expenses> getAllExpenses(User user) {
        ArrayList<Expenses> expList = new ArrayList<Expenses>();
// Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        //String selectQuery = "SELECT * FROM " + TABLE_EXPENSES + " WHERE " + KEY_USN + " = " + user.getUsername();
        Cursor cursor = db.query(TABLE_EXPENSES, new String[] {KEY_ADDTIME, KEY_REALTIME,KEY_USN ,KEY_PRICE ,KEY_CATEGORY ,KEY_PAYMENT}, KEY_USN + "=?",
                new String[] { user.getUsername() }, null, null, null, null);

        //Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Expenses exp = new Expenses(cursor.getString(0),cursor.getString(1),cursor.getString(2),Double.parseDouble(cursor.getString(3)), cursor.getString(4),cursor.getString(5));
                Expenses exp = new Expenses(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), Double.parseDouble(cursor.getString(3)),
                        cursor.getString(4),  cursor.getString(5));
                exp.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USN)));
                //exp.setUsername(cursor.getString(0));
                expList.add(exp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return expList;
    }

}
