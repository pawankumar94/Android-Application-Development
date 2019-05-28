package com.example.iseeproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java. util. Date;
import java. sql. Timestamp;

public class enterExpenses extends AppCompatActivity {

    int Income ;
    String username;
    int Rent ;
    int Bills ;
    int Insurance ;
    String famous_cat = "Not Defined";
    int extra_exp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_expenses);
        peopleDB = new DBHandler(this);

        Bundle b = getIntent().getExtras();
        //we pass all the arguments cause they need to be passed again on the homepage
        //in order to be displayed
        username = b.getString("username");
        User usr = peopleDB.getUser(username);
        Button button1 = (Button) findViewById(R.id.submitBtn);

        Button button2 = (Button) findViewById(R.id.backBtn);

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                double leis = 0, food = 0, misc = 0, extra_expen = 0;
                String famous_categ = "";
                EditText LeisureEdit = (EditText)findViewById(R.id.LeisureEdit);

                Date date= new Date();
                long time = date.getTime();
                Timestamp ts = new Timestamp(time);

                if (LeisureEdit.getText().toString() != "") {
                    leis = Double.parseDouble(LeisureEdit.getText().toString());
                    Expenses exp = new Expenses(username, leis, ts,"LEISURE");

                    peopleDB.addExpenses(exp);

                    extra_expen+= leis;
                }

                EditText FoodEdit   = (EditText)findViewById(R.id.FoodEdit);
                if (FoodEdit.getText().toString() != "") {
                    food = Integer.parseInt(FoodEdit.getText().toString());
                    Expenses exp = new Expenses(username, food, "Food")
                    peopleDB.addExpenses(exp);
                    extra_expen+= food;
                }

                EditText MiscEdit   = (EditText)findViewById(R.id.MiscEdit);
                if (MiscEdit.getText().toString() != "") {
                    misc = Integer.parseInt(LeisureEdit.getText().toString());
                    Expenses exp = new Expenses(usr, misc, "Misclleaneous")
                    peopleDB.addExpenses(exp);
                    extra_expen+= misc;
                }
                //now finding out for which category we have the most expenses so far
                //we can only find current value of category because we don't have previous data
                if ((leis > food) && (leis > misc)) {
                    famous_categ = "Leisure";
                }
                else if(food > misc) {
                    famous_categ = "Food";
                }
                else if (misc > food) {
                    famous_categ = "Miscelaneous";
                }
                else {
                    famous_categ = "Equal Category Expenses";
                }

                //in case of same values in a category, the string remails the same
                // with the last time that we got it from the bundle

                if (Income < extra_exp + extra_expen + Rent + Bills + Insurance) {
                    Toast t = Toast.makeText(enterExpenses.this,
                            "Expenses entered overcome savings", Toast.LENGTH_LONG);
                    t.show();
                }
                else {
                    // Start NewActivity.class
                    Intent myIntent = new Intent(enterExpenses.this,
                            HomePage.class);
                    Bundle b = new Bundle();
                    b.putInt("income", Income);
                    b.putInt("rent", Rent);
                    b.putInt("bills", Bills);
                    b.putInt("ins", Insurance);
                    b.putInt("extra_exp", (extra_exp + extra_expen)); //adding previous and new expenses
                    b.putString("famous_cat", famous_categ);
                    myIntent.putExtras(b); //Put your id to your next Intent
                    startActivity(myIntent);
                    finish();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(enterExpenses.this,
                        HomePage.class);
                Bundle b = new Bundle();
                b.putInt("income", Income);
                b.putInt("rent", Rent);
                b.putInt("bills", Bills);
                b.putInt("ins", Insurance);
                b.putInt("extra_exp", extra_exp);
                b.putString("famous_cat", famous_cat);
                myIntent.putExtras(b);
                startActivity(myIntent);
                finish();
            }
        });
    }
}
