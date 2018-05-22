package com.example.rouge.a6120hjemmeeksamen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    public static final String UserEmail = "";
    EditText Email, Password;
    Button LogIn;
    String PasswordHolder, EmailHolder;
    String finalResult;
    // Lokal filsti for emulator :
    String HttpURL = "http://10.0.2.2/hobbyhuset/UserLogin.php";
    // String HttpURL = "http://192.168.10.171/hobbyhuset/UserLogin.php";
    Boolean CheckEditText;
    ProgressDialog progressDialog;
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    SharedPreferences pref, prefStore;
    SharedPreferences.Editor editor, prefStoreEditor;
    CheckBox rememberMe;

    boolean checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        LogIn = findViewById(R.id.Login);
        rememberMe = findViewById(R.id.huskMeg);
        rememberMe.setOnCheckedChangeListener(this);
        checked = rememberMe.isChecked();

        pref = getSharedPreferences("login.conf", Context.MODE_PRIVATE);
        /** TODO Bruk en annen sharedPreferences-fil for å lagre innloggin hvis brukeren ikke huker av for å bli husket
         //prefStore = getSharedPreferences("storeLogin.conf", Context.MODE_PRIVATE);
         //prefStoreEditor = prefStore.edit(); */
        editor = pref.edit();

        String prefUname = pref.getString("username", "");
        String prefPass = pref.getString("password", "");

        if (!(prefUname.equals("") && prefPass.equals(""))) {
            UserLoginFunction(prefUname, prefPass);
        }

        // midlertidig fix for direkte login
        Intent login = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(login);

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckEditTextIsEmptyOrNot();

                if (CheckEditText) {

                    if(checked) {
                        editor.putString("username", Email.getText().toString());
                        editor.putString("password", Password.getText().toString());
                        editor.apply();
                    } /** Dette legger innloggingen i storeLogin.conf hvis det skal legges inn som en funksjon i "Settings"-menyen
                     else {
                     prefStoreEditor.putString("username", Email.getText().toString());
                     prefStoreEditor.putString("password", Password.getText().toString());
                     prefStoreEditor.apply();

                     }*/

                    UserLoginFunction(EmailHolder, PasswordHolder);

                } else {

                    Toast.makeText(LoginActivity.this, "Fyll ut alle feltene", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    public void CheckEditTextIsEmptyOrNot() {

        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();

        if (TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder)) {
            CheckEditText = false;
        } else {

            CheckEditText = true;
        }
    }

    public void UserLoginFunction(final String email, final String password) {
        // Når klassen extender AsyncTask, genereres onPreExecute, onPostExecute og doInBackground automatisk
        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(LoginActivity.this, "Laster Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                if (httpResponseMsg.equalsIgnoreCase("Bruker Funnet")) {

                    finish();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(UserEmail, email);

                    Toast.makeText(LoginActivity.this, "Du er logget inn", Toast.LENGTH_SHORT).show();

                    startActivity(intent);


                } else {

                    Toast.makeText(LoginActivity.this, httpResponseMsg, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("email", params[0]);

                hashMap.put("password", params[1]);

                finalResult = httpParse.postRequest(hashMap, HttpURL,"POST");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(email, password);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checked = isChecked;
    }

}
