package com.example.lazlo;

import androidx.appcompat.app.AppCompatActivity;

/* added code */

import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lazlo.Sql.DBHelper;

import android.os.Bundle;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button btnSubmitLoginCredentials;
    TextView createAccount;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean e=false, p=false;
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);
        btnSubmitLoginCredentials = (Button) findViewById(R.id.btnSubmit_login);
        dbHelper = new DBHelper(this);
        btnSubmitLoginCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailCheck = email.getText().toString().trim();
                String passCheck = password.getText().toString().trim();
                Cursor cursor = dbHelper.getData();
                if (cursor.getCount() == 0){
                    Toast.makeText(Login.this, "No entries Exists", Toast.LENGTH_LONG).show();
                }
                if (loginCheck(cursor, emailCheck, passCheck)){
                    Intent intent = new Intent(Login.this, FinalPage.class);
                    intent.putExtra("email",emailCheck);
                    email.setText("");
                    password.setText("");
                    startActivity(intent);
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setCancelable(true);
                    builder.setTitle("Wrong credentials");
                    builder.setMessage("Wrong credentials");
                    builder.show();
                }
                dbHelper.close();
            }
        });
        createAccount = findViewById(R.id.createAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
    public static boolean loginCheck(Cursor cursor, String emailCheck, String passCheck){
        while (cursor.moveToNext()){
            if (cursor.getString(0).equals(emailCheck)){
                if(cursor.getString(2).equals(passCheck)){
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}