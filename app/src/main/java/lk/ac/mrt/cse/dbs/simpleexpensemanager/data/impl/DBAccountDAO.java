package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class DBAccountDAO extends SQLiteOpenHelper implements AccountDAO {

    private Context context;
    private static final String DATABASE_NAME = "lab_190478E.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "account";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ACC_NO = "acc_no";
    private static final String COLUMN_BANK_NAME = "bank_name";
    private static final String COLUMN_ACC_HOLDER = "acc_holder";
    private static final String COLUMN_ACC_BALANCE = "balance";

    public DBAccountDAO(@Nullable Context context) {
        super(context , DATABASE_NAME , null , DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " ("+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_ACC_NO + " TEXT UNIQUE, "+
                COLUMN_BANK_NAME + " TEXT, " +
                COLUMN_ACC_HOLDER + " TEXT, "+
                COLUMN_ACC_BALANCE + " REAL);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;

        db.execSQL(query);
        onCreate(db);
    }

    @Override
    public List<String> getAccountNumbersList() {

        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+ TABLE_NAME , null);

        if(cursor.getCount()>0){

            List<String> accountsNumberList = new ArrayList<>();

            while (cursor.moveToNext()){
                String acc_no = cursor.getString(cursor.getColumnIndex(COLUMN_ACC_NO));

                accountsNumberList.add(acc_no);

            }
            return accountsNumberList;

        }else {
            return new ArrayList<String>();
        }
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+ TABLE_NAME , null);

        if(cursor.getCount()>0){

            List<Account> accounts = new ArrayList<>();

            while (cursor.moveToNext()){
                String acc_no = cursor.getString(cursor.getColumnIndex(COLUMN_ACC_NO));
                String bank_name = cursor.getString(cursor.getColumnIndex(COLUMN_BANK_NAME));
                String acc_holder = cursor.getString(cursor.getColumnIndex(COLUMN_ACC_HOLDER));
                double balance = cursor.getDouble(cursor.getColumnIndex(COLUMN_ACC_BALANCE));

                accounts.add(new Account(acc_no , bank_name , acc_holder , balance));

            }
            return accounts;

        }else {
            return new ArrayList<Account>();
        }
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ACC_NO+" = ?" , new String[] {accountNo});

        if(cursor.getCount()>0){

            Account account = null;

            while (cursor.moveToNext()){
                String acc_no = cursor.getString(cursor.getColumnIndex(COLUMN_ACC_NO));
                String bank_name = cursor.getString(cursor.getColumnIndex(COLUMN_BANK_NAME));
                String acc_holder = cursor.getString(cursor.getColumnIndex(COLUMN_ACC_HOLDER));
                double balance = cursor.getDouble(cursor.getColumnIndex(COLUMN_ACC_BALANCE));

                account = new Account(acc_no , bank_name , acc_holder , balance);
                break;
            }
            return account;

        }else {
            throw new InvalidAccountException(accountNo+" is a invalid account number.");
        }
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("acc_no", account.getAccountNo());
        contentValues.put("bank_name", account.getBankName());
        contentValues.put("acc_holder", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());

        long result = DB.insert(TABLE_NAME , null , contentValues);

//        if(result==-1){
//            throw new Exception("Account Insertion Failed");
//        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ACC_NO+" = ?" , new String[] {accountNo});

        if(cursor.getCount()>0){
            DB.delete(TABLE_NAME , "acc_no=?" , new String[]{accountNo});
        }else {
            throw new InvalidAccountException(accountNo+" is a invalid account number.");
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ACC_NO+" = ?" , new String[] {accountNo});

        if(cursor.getCount()>0){
            double pre_balance = 0;
            while (cursor.moveToNext()){
                pre_balance = cursor.getDouble(cursor.getColumnIndex(COLUMN_ACC_BALANCE));
                break;
            }

            double new_balance = -1;

            switch (expenseType) {
                case EXPENSE:
                    new_balance = pre_balance - amount;
                    break;
                case INCOME:
                    new_balance = pre_balance + amount;
                    break;
            }

            DB.beginTransaction();
            try{
                contentValues.put("balance" , new_balance);
                DB.update(TABLE_NAME , contentValues , "acc_no=?" , new String[]{accountNo});
                DB.setTransactionSuccessful();
            } finally {
                DB.endTransaction();
            }

        }else {
            throw new InvalidAccountException(accountNo+" is a invalid account number.");
        }

    }
}
