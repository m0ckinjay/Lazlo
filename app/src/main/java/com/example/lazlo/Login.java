package com.example.lazlo;

import androidx.appcompat.app.AppCompatActivity;

/* added code */

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;
import com.example.lazlo.Sql.DBHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import android.os.Bundle;

public class Login extends AppCompatActivity {
    String uname;
    EditText username, password;
    MaterialButton btnSubmitLoginCredentials;
    TextView createAccount;
    DBHelper dbHelper;
    SharedPreferences sharedPreferences;
    TextInputLayout loginUserName_inputLayout,loginPassword_inputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.loginUserName_input);
        password = findViewById(R.id.loginPassword_input);
        btnSubmitLoginCredentials = findViewById(R.id.btnSubmit_login);
        dbHelper = new DBHelper(this);
        Intent intent = new Intent(Login.this, FinalPage.class);
        loginUserName_inputLayout = (TextInputLayout) findViewById(R.id.loginUserName_inputLayout);
        loginPassword_inputLayout = (TextInputLayout) findViewById(R.id.loginPassword_inputLayout);
        loginUserName_inputLayout.requestFocus();
        //shared preferences are used to store variables persistently, even
        //after uses closes the app. Only cleared when they logout.
        //preferred to global variables as global variables are lost when user closes the app

        sharedPreferences = getSharedPreferences("user_details",MODE_PRIVATE);
        if (sharedPreferences.contains("username") && sharedPreferences.contains("password")){
            startActivity(intent);
        }
        btnSubmitLoginCredentials.setOnClickListener(view -> {
            String unameCheck = username.getText().toString().trim();
            String passCheck = password.getText().toString().trim();

            Cursor cursor = dbHelper.getData(unameCheck);
                if (!unameCheck.isEmpty()){
                    if (!passCheck.isEmpty()){
                        if (cursor.getCount() != 0){
                            if (loginCheck(cursor, unameCheck, passCheck)){
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username", uname);
                                editor.apply();
                                username.setText("");
                                password.setText("");
                                startActivity(intent);
                            }/*else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                            builder.setCancelable(true);
                            builder.setTitle("Wrong credentials");
                            builder.setMessage("Username or password not found");
                            builder.setPositiveButton("Login again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent logIn = new Intent(getApplicationContext(), Login.class);
                                    startActivity(logIn);
                                }
                            });
                            builder.setNegativeButton("Sign up", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent signUp = new Intent(getApplicationContext(), SignUp.class);
                                    startActivity(signUp);
                                }
                            });
                            builder.show();
                        }*/
                        }else{
                            loginUserName_inputLayout.setErrorEnabled(true);
                            loginUserName_inputLayout.setError("Invalid username");
                            loginPassword_inputLayout.setErrorEnabled(false);
                        }
                    }else{
                        loginPassword_inputLayout.setErrorEnabled(true);
                        loginPassword_inputLayout.setError("No blank password");
                        loginUserName_inputLayout.setErrorEnabled(false);
                    }
                }else{
                    loginUserName_inputLayout.setErrorEnabled(true);
                    loginUserName_inputLayout.setError("No blank username");
                    loginPassword_inputLayout.setErrorEnabled(false);
                }
            dbHelper.close();
        });
        createAccount = findViewById(R.id.createAccount);
        createAccount.setOnClickListener(view -> {
            Intent intent1 = new Intent(Login.this, SignUp.class);
            startActivity(intent1);
        });
    }
    public boolean loginCheck(Cursor cursor, String unameCheck, String passCheck){
        while (cursor.moveToNext()){
            if (cursor.getString(cursor.getColumnIndexOrThrow("userName")).equals(unameCheck)){
                if(cursor.getString(cursor.getColumnIndexOrThrow("password")).equals(passCheck)){
                    uname = cursor.getString(cursor.getColumnIndexOrThrow("userName"));
                        return true;
                }else{
                    loginUserName_inputLayout = (TextInputLayout) findViewById(R.id.loginUserName_inputLayout);
                    loginUserName_inputLayout.setErrorEnabled(false);
                    loginPassword_inputLayout = (TextInputLayout) findViewById(R.id.loginPassword_inputLayout);
                    loginPassword_inputLayout.setErrorEnabled(true);
                    loginPassword_inputLayout.setError("Wrong password");
                    //Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
                }
                return false;
            }else {
                Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

}