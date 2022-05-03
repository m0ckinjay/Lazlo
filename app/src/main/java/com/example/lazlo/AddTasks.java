package com.example.lazlo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.lazlo.Sql.DBHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class AddTasks extends AppCompatActivity {
    TextInputEditText task_title,task_description,priceAutocompleteView;
    AutoCompleteTextView select_date;
    DatePickerDialog datePickerDialog;
    ImageButton btn_saveTasks, btn_cancelTaskCreation;
    DBHelper dbHelper;
    SharedPreferences tasks_sharedPrefs;
    LocalDate selected_date,date_now;
    AutoCompleteTextView tasksCategories;
    String selected_category;
    Double Price;
    TextInputLayout taskTitle_TextLayout,taskDescription_TextLayout,tasksCategoryTextLayout,
            price_TextLayout,selectedDate_TextInputLayout;
    boolean b;

//method to parse date input from adding task

    public static LocalDate getDateFromString(String string,DateTimeFormatter dateTimeFormatter){
        return LocalDate.parse(string, dateTimeFormatter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tasks);

        //===================================================Variables===============================================================
        task_title = (TextInputEditText) findViewById(R.id.taskTitleAutoCompleteView);
        task_description = (TextInputEditText) findViewById(R.id.taskDescriptionAutoCompleteView);
        select_date = (AutoCompleteTextView) findViewById(R.id.selectDate_AutocompleteView);
        priceAutocompleteView = (TextInputEditText) findViewById(R.id.priceAutoCompleteView);
        tasksCategories = (AutoCompleteTextView) findViewById(R.id.tasksAutoCompleteView);

        taskTitle_TextLayout = findViewById(R.id.taskTitle_TextLayout);
        taskDescription_TextLayout = findViewById(R.id.taskDescription_TextLayout);
        tasksCategoryTextLayout = findViewById(R.id.tasksCategoryTextLayout);
        price_TextLayout = findViewById(R.id.price_TextLayout);
        selectedDate_TextInputLayout = findViewById(R.id.selectedDate_TextInputLayout);

        btn_saveTasks = (ImageButton) findViewById(R.id.btn_saveTask);
        dbHelper = new DBHelper(this);
        // get the string username broadcast from login to stand in as the
        //determiner of who enters tasks. Should be replaced by the username or userId
        tasks_sharedPrefs = getSharedPreferences("user_details",MODE_PRIVATE);


        //===================================================process dropdown=========================================================
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.categories, android.R.layout.simple_dropdown_item_1line);
        tasksCategories.setAdapter(adapter);
        tasksCategories.setOnItemClickListener((adapterView, view, i, l) -> selected_category = (String) adapterView.getItemAtPosition(i));


        //======================================================process date picker=======================================================
        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //date picker dialog
                datePickerDialog = new DatePickerDialog(AddTasks.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        select_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear,mMonth, mDay);
                datePickerDialog.show();
            }
        });
        //================================================save inputs=======================================================
        btn_saveTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get inputs to insert to db
                String USERNAME = tasks_sharedPrefs.getString("username",null);
                String taskTitle_String = task_title.getText().toString().trim();
                String taskDescription_String = task_description.getText().toString().trim();
                String selectedDate_String = select_date.getText().toString().trim();
                String TaskAssociatedPrice =  priceAutocompleteView.getText().toString().trim();
                String selectedCategory_string = tasksCategories.getText().toString().trim();


                //process inputs
                if (!taskTitle_String.isEmpty()){
                    if (!taskDescription_String.isEmpty()){
                        if (!selectedCategory_string.isEmpty()){
                            if (!selectedDate_String.isEmpty() && willDateFormat(selectedDate_String)){
                                if (willPriceFormat(TaskAssociatedPrice)){
                                    date_now = LocalDate.now();
                                    if (selected_date.compareTo(date_now) > 0 || selected_date.compareTo(date_now) == 0) {
                                        try {
                                            //insert task to db if dates are cool
                                            b = dbHelper.insertTasks(USERNAME, taskTitle_String, taskDescription_String, selected_category, Price, selected_date);

                                        }catch(Exception e){
                                            System.out.println("Db insertion error: " + e);
                                        }
                                        if (b){
                                            Toast.makeText(getApplicationContext(), "Task inserted successfully", Toast.LENGTH_LONG).show();
                                            finish();
                                        }else {
                                            Toast.makeText(getApplicationContext(), "Task insert failure", Toast.LENGTH_LONG).show();
                                        }
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Choose another date", Toast.LENGTH_LONG).show();
                                        select_date.setText("");
                                    }
                                }else{
                                    price_TextLayout.setErrorEnabled(true);
                                    price_TextLayout.setError("Wrong price");
                                    taskTitle_TextLayout.setErrorEnabled(false);
                                    taskDescription_TextLayout.setErrorEnabled(false);
                                    price_TextLayout.setErrorEnabled(false);
                                    tasksCategoryTextLayout.setErrorEnabled(false);
                                    selectedDate_TextInputLayout.setErrorEnabled(false);
                                }
                            }else{
                                selectedDate_TextInputLayout.setErrorEnabled(true);
                                selectedDate_TextInputLayout.setError("Blank deadline");
                                taskTitle_TextLayout.setErrorEnabled(false);
                                taskDescription_TextLayout.setErrorEnabled(false);
                                price_TextLayout.setErrorEnabled(false);
                                tasksCategoryTextLayout.setErrorEnabled(false);
                            }

                        }else{
                            tasksCategoryTextLayout.setErrorEnabled(true);
                            tasksCategoryTextLayout.setError("Blank category");
                            taskTitle_TextLayout.setErrorEnabled(false);
                            taskDescription_TextLayout.setErrorEnabled(false);
                            price_TextLayout.setErrorEnabled(false);
                            selectedDate_TextInputLayout.setErrorEnabled(false);
                        }
                    }else{
                        taskDescription_TextLayout.setErrorEnabled(true);
                        taskDescription_TextLayout.setError("Blank description");
                        taskTitle_TextLayout.setErrorEnabled(false);
                        tasksCategoryTextLayout.setErrorEnabled(false);
                        price_TextLayout.setErrorEnabled(false);
                        selectedDate_TextInputLayout.setErrorEnabled(false);
                    }

                }else{
                    taskTitle_TextLayout.setErrorEnabled(true);
                    taskTitle_TextLayout.setError("Blank title");
                    taskDescription_TextLayout.setErrorEnabled(false);
                    tasksCategoryTextLayout.setErrorEnabled(false);
                    price_TextLayout.setErrorEnabled(false);
                    selectedDate_TextInputLayout.setErrorEnabled(false);
                    }




            }
        });

    }
    private boolean willDateFormat(String selectedDate){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/L/yyyy");
        try {
            selected_date = getDateFromString(selectedDate, dateTimeFormatter);
            return true;
        }catch (IllegalArgumentException e){
            System.out.println("Date Exception" + e);
            return false;
        }
    }
    private boolean willPriceFormat(String priceToParse){
            try {
                if (!priceToParse.isEmpty()){
                    Price = Double.parseDouble(priceToParse);
                }else {
                    Price = 0.0;
                }
                return true;
            }catch(Exception e){
                System.out.println("Price Exception" + e);
                return false;
            }

    }


    }
