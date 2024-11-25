package com.example.btl_appnghenhac;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import android.widget.Toast;

import com.example.btl_appnghenhac.db.dbManager;


public class LoginActivity extends AppCompatActivity {

    EditText edt_username, edt_password;
    Button btn_login;
    TextView tv_toSignUp;
    dbManager dbManager;
    CheckBox checkBox;
    public static final String fName = "account.xml";
    SharedPreferences preferences;

    //dinh nghia int mode:
    public static final int MODE = Activity.MODE_PRIVATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getViews();

        dbManager = new dbManager(LoginActivity.this);
        dbManager.open();


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edt_username.getText().toString().trim();
                String password = edt_password.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin đăng nhập!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check login credentials
                if (dbManager.checkLogin(username, password)) {
                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);

                    // Check for admin role
                    if (username.equals("admin")) {
                        bundle.putInt("role", 1); // Admin role
                    } else {
                        bundle.putInt("role", 2); // Normal user role
                    }

                    saveAccount();

                    intent1.putExtras(bundle);
                    startActivity(intent1);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Thông tin đăng nhập không chính xác, hãy thử lại!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        preferences = getSharedPreferences(fName, MODE);
        readAccount();
    }

    private void saveAccount(){
        //luu dl vao file
        //lay ra doi tuong Editor de luu du lieu vao file
        if(checkBox.isChecked()){
            SharedPreferences.Editor editor = preferences.edit();

            //put du lieu vao file xml
            editor.putBoolean("save", checkBox.isChecked());
            editor.putString("username", edt_username.getText().toString());
            editor.putString("password", edt_password.getText().toString());
            //hoan thanh viec luu du lieu
            editor.commit();
        }
    }

    private void readAccount(){
        boolean isSave = preferences.getBoolean("save", false);
        if(isSave){
            //doc tiep du lieu va data bind vao views (cac edittext)
            String username = preferences.getString("username", null);
            String password = preferences.getString("password", null);
            edt_username.setText(username);
            edt_password.setText(password);
        }
    }

    protected void getViews(){
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_login);
        tv_toSignUp = findViewById(R.id.tv_toSignUp);
        checkBox = findViewById(R.id.checkBox);
    }
}