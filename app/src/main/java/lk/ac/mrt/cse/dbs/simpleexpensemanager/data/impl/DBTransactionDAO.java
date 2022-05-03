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

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {

    private Context context;
    private static final String DATABASE_NAME = "lab_190478E.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "transaction_";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ACC_NO = "acc_no";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TYPE = "expenseType";
    private static final String COLUMN_AMOUNT = "amount";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DBTransactionDAO(@Nullable Context context) {
        super(context , DATABASE_NAME , null , DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " ("+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_ACC_NO + " TEXT, "+
                COLUMN_DATE + " DATETIME, " +
                COLUMN_TYPE + " TEXT, "+
                COLUMN_AMOUNT + " REAL);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;

        db.execSQL(query);
        onCreate(db);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        SQLiteDatabase DB = this.getWritableDatabase();

        DB.beginTransaction();
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put("acc_no", accountNo);

            contentValues.put("date",  dateFormat.format(date));
            contentValues.put("type", String.valueOf(expenseType));
            contentValues.put("amount", amount);

            DB.insert(TABLE_NAME , null , contentValues);

            DB.setTransactionSuccessful();

        } finally {
            DB.endTransaction();
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+ TABLE_NAME , null);

        if(cursor.getCount()>0){

            List<Transaction> transactions = new ArrayList<>();

            while (cursor.moveToNext()){
                String acc_no = cursor.getString(cursor.getColumnIndex(COLUMN_ACC_NO));
                String dateStr = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
                double amount = cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT));

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
        SQLiteDatabase DB = this.getWritableDatabase();

        List<Transaction> transactions = new ArrayList<>();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+ TABLE_NAME+ " OFFSET "+limit+";" , null);

        if(cursor.getCount()>0){

            while (cursor.moveToNext()){
                String acc_no = cursor.getString(cursor.getColumnIndex(COLUMN_ACC_NO));
                String dateStr = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
                double amount = cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT));

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
            cursor = DB.rawQuery("SELECT * FROM "+ TABLE_NAME , null);

            if(cursor.getCount()>0){

                while (cursor.moveToNext()){
                    String acc_no = cursor.getString(cursor.getColumnIndex(COLUMN_ACC_NO));
                    String dateStr = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                    String type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
                    double amount = cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT));

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
