package com.example.iseeproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class listActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ListView expenselistview;
    dbHandler dbhandler;
    String usr = "";
    User userr;
    String username;
    SearchView sv;
    String selectedCategory;
    private ImageButton menuBtn;
    menuHandler MenuHandler;
    EditText datepickFrom, datepickTo;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date1,date2;

    Spinner spinner12;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        dbhandler = new dbHandler(this);
        Bundle b = getIntent().getExtras();

        if (b != null) {
            usr = b.getString("username");
        }

        userr = dbhandler.getUser(usr);
        MenuHandler = new menuHandler(listActivity.this, usr);
        menuBtn  = (ImageButton) findViewById(R.id.menuLines);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                PopupMenu popup = new PopupMenu(listActivity.this, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return MenuHandler.onMenuItemClick(item);
                    }
                });
                popup.inflate(R.menu.drawermenu);
                popup.show();
            }
        });

        expenselistview = findViewById(R.id.expenseLV);
        dbhandler = new dbHandler(this);

        Button back = (Button) findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(listActivity.this, homePage.class);
                Bundle b = new Bundle();
                b.putString("username",usr);

                myIntent.putExtras(b); //Put your id to your next Intent
                startActivity(myIntent);
            }

        });

        dbhandler = new dbHandler(this);
        Set<String> cats1 = dbhandler.getThresholds(usr).keySet();
        //we add all of the categories but first the option to choose to view every expense
        final List<String> categories1= new ArrayList<String>();
        categories1.add("All");
        categories1.addAll(cats1);

        spinner12 = (Spinner)findViewById(R.id.spinner123);
        ArrayAdapter<String> datAdapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, categories1);
        datAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner12.setAdapter(datAdapter1);
//        spinner12.setSelected(false);
//        spinner12.setSelection(0,true);
        spinner12.setOnItemSelectedListener(this);

//we set the calendar view in the ui
        myCalendar = Calendar.getInstance();

        datepickFrom = (EditText) findViewById(R.id.selectDate1);
        datepickTo = (EditText) findViewById(R.id.selectDate2);
        date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(datepickFrom);
            }

        };

        date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(datepickTo);
            }

        };

        datepickFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 // TODO Auto-generated method stub
                 DatePickerDialog mDatePicker = new DatePickerDialog(listActivity.this, date1, myCalendar
                         .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                 mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                 mDatePicker.show();
            }
        }

        );

        datepickTo.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  // TODO Auto-generated method stub
                   DatePickerDialog mDatePicker = new DatePickerDialog(listActivity.this, date2, myCalendar
                     .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                   mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                   mDatePicker.show();
              }
        }

        );

        Button go = (Button) findViewById(R.id.goBtn);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategory = spinner12.getSelectedItem().toString();
                dbhandler = new dbHandler(getApplicationContext());
                ArrayList<Expenses>  cateList;
                if (selectedCategory.equals("All")) {
                    cateList = dbhandler.getAllExpenses(userr);
                }
                else {
                    cateList = dbhandler.getSortedCategory(userr, selectedCategory);
                }
                //now we check the date and filter the list
                String dateFrom = datepickFrom.getText().toString();
                String dateTo = datepickTo.getText().toString();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate dFrom = null , dTo = null;
                String strDateRegEx = "\\d{2}-\\d{2}-\\d{4}";
                //we check if user has selected a date from the calendar
                if(dateFrom.matches(strDateRegEx)) {
                   dFrom = LocalDate.parse(dateFrom, formatter);
                }
                if(dateTo.matches(strDateRegEx)) {
                    dTo = LocalDate.parse(dateTo, formatter);
                }
                for(Expenses exp : cateList) {
                    LocalDate expDate = LocalDate.parse(exp.getExpenseTime(), formatter);
                    //if it is before the earliest date the user chooses
                    if (dFrom != null && (expDate.isBefore(dFrom) || expDate.isEqual(dFrom)) ) {
                        cateList.remove(exp);
                    }
                    //if it is after the latest day the user chooses
                    if (dTo != null && (expDate.isAfter(dTo)|| expDate.isEqual(dTo))) {
                        cateList.remove(exp);
                    }
                    //if it is inside the date boundaries, keep it in the list
                }
                //if in the end none of the expenses fit the selected dates or categories
                if (cateList.isEmpty()) {
                    MenuHandler.showToast("No results for the selected criteria");
                }
                else {
                    filteredExpenses(cateList);
                }
            }
        });

    }

    private void updateLabel(EditText datepick) {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMANY);

        datepick.setText(sdf.format(myCalendar.getTime()));
    }

    public void filteredExpenses(final ArrayList<Expenses> explist){

        ArrayList<HashMap<String,String>> myMapList = new ArrayList<>();

        for(int i=0; i<explist.size();i++){
            HashMap<String,String> myMap = new HashMap<>();
            myMap.put("Date",explist.get(i).getExpenseTime());
            myMap.put("Amount",Double.toString(explist.get(i).getPrice()));
            myMap.put("Category",explist.get(i).getCategory());
            myMap.put("Payment_Method",explist.get(i).getPaymentMethod());

            myMapList.add(myMap);
        }

        final ListAdapter adapter = new SimpleAdapter(listActivity.this,myMapList,R.layout.row,
                new String[]{"Date","Amount","Category","Payment_Method"},
                new int[]{R.id.textviewdate,R.id.textviewamount,R.id.textviewcategory,R.id.textviewpaymenttype});
        expenselistview.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawermenu, menu);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner123) {
            // On selecting a spinner item
            String item = parent.getItemAtPosition(position).toString();
            // Showing selected spinner item
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast t = Toast.makeText(listActivity.this,
                "Select one Category", Toast.LENGTH_LONG);
        t.show();
    }
}