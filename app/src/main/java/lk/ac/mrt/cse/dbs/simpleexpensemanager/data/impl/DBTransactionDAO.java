package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.config.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBTransactionDAO implements TransactionDAO {


    private Context context;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DBTransactionDAO(@Nullable Context context) {
        this.context = context;
    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String query = "CREATE TABLE " + TABLE_NAME +
//                " ("+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
//                COLUMN_ACC_NO + " TEXT, "+
//                COLUMN_DATE + " DATETIME, " +
//                COLUMN_TYPE + " TEXT, "+
//                COLUMN_AMOUNT + " REAL);";
//        db.execSQL(query);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
//        String query = "DROP TABLE IF EXISTS " + TABLE_NAME+";";
//
//        db.execSQL(query);
//        onCreate(db);
//    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        DBHelper DBH = DBHelper.getInstanceDB(context);

        SQLiteDatabase DB = DBH.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("acc_no", accountNo);

        contentValues.put(DBHelper.COLUMN_DATE,  dateFormat.format(date));
        contentValues.put(DBHelper.COLUMN_TYPE, String.valueOf(expenseType));
        contentValues.put(DBHelper.COLUMN_AMOUNT, amount);

        DB.insert(DBHelper.TABLE_NAME2 , null , contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        DBHelper DBH = DBHelper.getInstanceDB(context);

        SQLiteDatabase DB = DBH.getWritableDatabase();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+ DBHelper.TABLE_NAME2+";" , null);

        if(cursor.getCount()>0){

            List<Transaction> transactions = new ArrayList<>();

            while (cursor.moveToNext()){
                String acc_no = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ACC_NO));
                String dateStr = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATE));
                String type = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TYPE));
                double amount = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_AMOUNT));

                ExpenseType expenseType = null;

                if(ExpenseType.EXPENSE.name().equals(type)){
                    expenseType = ExpenseType.EXPENSE;
                }else expenseType = ExpenseType.INCOME;

                try{
                    Date date = dateFormat.parse(dateStr);
                    transactions.add(new Transaction(date , acc_no , expenseType , amount));
                } catch (ParseException e){
                    e.printStackTrace();
                }

            }
            return transactions;

        }else {
            return new ArrayList<Transaction>();
        }
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        DBHelper DBH = DBHelper.getInstanceDB(context);

        SQLiteDatabase DB = DBH.getWritableDatabase();

        List<Transaction> transactions = new ArrayList<>();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+ DBHelper.TABLE_NAME2+ " LIMIT "+limit+";" , null);

        if(cursor.getCount()>0){

            while (cursor.moveToNext()){
                String acc_no = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ACC_NO));
                String dateStr = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATE));
                String type = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TYPE));
                double amount = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_AMOUNT));

                ExpenseType expenseType = ExpenseType.valueOf(type);

//                ExpenseType.valueOf()
//
//                if(ExpenseType.EXPENSE.name().equals(type)){
//                    expenseType = ExpenseType.EXPENSE;
//                }else expenseType = ExpenseType.INCOME;

                try{
                    Date date = dateFormat.parse(dateStr);
                    transactions.add(new Transaction(date , acc_no , expenseType , amount));
                } catch (ParseException e){
                    e.printStackTrace();
                }

            }
            return transactions;

        }else {
            cursor = DB.rawQuery("SELECT * FROM "+ DBHelper.TABLE_NAME2+";" , null);

            if(cursor.getCount()>0){

                while (cursor.moveToNext()){
                    String acc_no = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ACC_NO));
                    String dateStr = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATE));
                    String type = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TYPE));
                    double amount = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_AMOUNT));

                    ExpenseType expenseType = null;

                    if(ExpenseType.EXPENSE.name().equals(type)){
                        expenseType = ExpenseType.EXPENSE;
                    }else expenseType = ExpenseType.INCOME;

                    try{
                        Date date = dateFormat.parse(dateStr);
                        transactions.add(new Transaction(date , acc_no , expenseType , amount));
                    } catch (ParseException e){
                        e.printStackTrace();
                    }

                }

            }

            return transactions;
        }
    }
}
