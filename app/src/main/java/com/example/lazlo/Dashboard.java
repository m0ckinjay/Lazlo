package com.example.lazlo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;

import com.example.lazlo.Sql.DBHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import android.widget.Button;
import android.database.Cursor;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Dashboard extends AppCompatActivity {
    TextInputEditText startDuration_choice,endDuration_choice;
    String selectedStart_duration,selectedEnd_duration;
    DatePickerDialog datePickerDialog,datePickerDialog2;
    Button btnshowPredictedSpending;
    DBHelper dbHelper = new DBHelper(this);
    LocalDate selectedStart_duration_String,selectedEnd_duration_String;
    TextView sumTotalView;
    SimpleCursorAdapter simpleCursorAdapter;
    ListView showSpendingListView;


    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), FinalPage.class);
        startActivity(intent);
        //Dashboard.this.finish();
    }
    public static LocalDate getDateFromString(String string, DateTimeFormatter dateTimeFormatter){
        LocalDate date = LocalDate.parse(string, dateTimeFormatter);
        return date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //process dates

        startDuration_choice = (TextInputEditText) findViewById(R.id.startDateInput);
        endDuration_choice = (TextInputEditText) findViewById(R.id.endDateInput);
        sumTotalView = (TextView) findViewById(R.id.SumTotalView);
        showSpendingListView = findViewById(R.id.showSpendingListView);

        final Calendar calendar = Calendar.getInstance();
        int sYear = calendar.get(Calendar.YEAR);
        int sMonth = calendar.get(Calendar.MONTH);
        int sDay = calendar.get(Calendar.DAY_OF_MONTH);
        int eYear = calendar.get(Calendar.YEAR);
        int eMonth = calendar.get(Calendar.MONTH);
        int eDay = calendar.get(Calendar.DAY_OF_MONTH);

        startDuration_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set startDatePicker dialog
                datePickerDialog = new DatePickerDialog(Dashboard.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        startDuration_choice.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                },sYear,sMonth,sDay);
                datePickerDialog.show();

            }
        });
        endDuration_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set endDatePicker dialog
                datePickerDialog2 = new DatePickerDialog(Dashboard.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        endDuration_choice.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                },eYear,eMonth,eDay);
                datePickerDialog2.show();
            }
        });

        //show sum
        btnshowPredictedSpending = (Button) findViewById(R.id.btnShowPredictedSpending);

        btnshowPredictedSpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/L/yyyy");
                try {
                    selectedStart_duration = startDuration_choice.getText().toString().trim();
                    selectedEnd_duration = endDuration_choice.getText().toString().trim();
                    selectedStart_duration_String = getDateFromString(selectedStart_duration,dateTimeFormatter);
                    selectedEnd_duration_String = getDateFromString(selectedEnd_duration,dateTimeFormatter);
                }catch (IllegalArgumentException e){
                    System.out.println("Exception" + e);
                }

                try {
                    Cursor cursor = dbHelper.getSum(selectedStart_duration_String, selectedEnd_duration_String);
                    Cursor cursor1 = dbHelper.getSpendingDetails(selectedStart_duration_String, selectedEnd_duration_String);
                    spendingListViewPopulate(cursor1);
                    if (cursor.getCount() == 0){
                        sumTotalView.setText("No entries found");
                    }
                    if (cursor.moveToFirst()){
                        sumTotalView.setText("Kshs " + cursor.getString(cursor.getColumnIndexOrThrow("sumTotal")));
                    }
                    cursor.close();
                }catch (Exception e){
                    System.out.println("Error occurred: " + e);
                }

            }
        });



    }
    private void spendingListViewPopulate(Cursor cursor){
        if (simpleCursorAdapter == null){
            simpleCursorAdapter = new SimpleCursorAdapter(this,R.layout.spending_listview,cursor,new String[]{"TaskTitle","TaskAssociatedPrice"},new int[]{R.id.spendingViewTitle_textView,R.id.spendingViewPrice_textView},0);
            showSpendingListView.setAdapter(simpleCursorAdapter);
        }
    }
}