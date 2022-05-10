/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.PersistentExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.MainActivity;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

public class ApplicationTest {

    private ExpenseManager expenseManager;

    private String accountNo = "12345";
    private String bankName = "ABC";
    private String accountHolderName = "David";

    @Before
    public void DBSetup(){
        expenseManager = new PersistentExpenseManager(ApplicationProvider.getApplicationContext());

        double balance = 1000;

        expenseManager.addAccount(accountNo , bankName , accountHolderName , balance);
    }

    @Test
    public void addAccountTest(){

        List<String> accountNumList = expenseManager.getAccountNumbersList();

        assertTrue(accountNumList.contains(accountNo));
    }

    @Test
    public void addTransactionTest1() throws InvalidAccountException {
        int day = 1;
        int month = 1;
        int year = 2022;
        ExpenseType expenseType = ExpenseType.INCOME;
        String amount = "500";

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date transactionDate = calendar.getTime();

        int transactionsOldSize = expenseManager.getTransactionLogs().size();

        expenseManager.updateAccountBalance(accountNo , day , month , year , expenseType , amount);

        List<Transaction> transactions = expenseManager.getTransactionLogs();

        boolean success = false;

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        for (Transaction transaction:transactions) {
            boolean dateMatched = fmt.format(transactionDate).equals(fmt.format(transaction.getDate()));
            if(transaction.getAccountNo().equals(accountNo) && dateMatched && transaction.getExpenseType()==expenseType && transaction.getAmount()==Double.valueOf(amount)){
                success = true;
                break;
            }
        }

        assertTrue(transactions.size()>transactionsOldSize);
    }

//    @Test
//    public void addTransactionTest2() {
//        int day = 1;
//        int month = 1;
//        int year = 2022;
//        String accountNo = "11111";
//        ExpenseType expenseType = ExpenseType.INCOME;
//        String amount = "500";
//
//        List<String> accountNumList = expenseManager.getAccountNumbersList();
//
//        while(accountNumList.contains(accountNo)){
//            byte[] array = new byte[5]; // length is bounded by 7
//            new Random().nextBytes(array);
//            accountNo = new String(array, Charset.forName("UTF-8"));
//        }
//
//        String finalAccountNo = accountNo;
//        assertThrows(InvalidAccountException.class,  expenseManager.updateAccountBalance(finalAccountNo, day , month , year , expenseType , amount));
//
//    }
}
