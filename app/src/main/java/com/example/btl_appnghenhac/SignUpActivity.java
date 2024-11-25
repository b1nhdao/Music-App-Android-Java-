package com.example.btl_appnghenhac;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_appnghenhac.Object.User;
import com.example.btl_appnghenhac.db.dbManager;

public class SignUpActivity extends AppCompatActivity {


    EditText edt_username, edt_password, edt_password_confirm;
    Button btn_create;
    TextView tv_toSignUp;
    dbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getViews();

        dbManager = new dbManager(SignUpActivity.this);
        dbManager.open();

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_username.getText().toString().isEmpty() || edt_password.getText().toString().isEmpty() ||
                        edt_password_confirm.getText().toString().isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Hãy điền đầy đủ thông tin !", Toast.LENGTH_SHORT).show();
                }
                else if (!(edt_password.getText().toString().equals(edt_password_confirm.getText().toString())))
                    Toast.makeText(SignUpActivity.this, "Mật khẩu xác nhận không khớp, hãy nhập lại", Toast.LENGTH_SHORT).show();

                else {
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    dbManager.insertUser(getUser());
                    dbManager.close();
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    //default
    private User createUserDefault(){
        return new User(
                "admin",
                "admin",
                1);
    }

    private User getUser(){
        return new User(
                edt_username.getText().toString(),
                edt_password.getText().toString(),
                2);
    }

    private void getViews(){
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        btn_create = findViewById(R.id.btn_create);
        tv_toSignUp = findViewById(R.id.tv_toSignUp);
        edt_password_confirm = findViewById(R.id.edt_password_confirm);
    }
}