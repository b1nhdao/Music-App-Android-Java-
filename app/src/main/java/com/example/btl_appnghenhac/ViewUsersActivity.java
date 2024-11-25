package com.example.btl_appnghenhac;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_appnghenhac.Adapter.UserAdapter;
import com.example.btl_appnghenhac.Object.User;
import com.example.btl_appnghenhac.db.dbManager;

import java.util.ArrayList;

public class ViewUsersActivity extends AppCompatActivity {

    ArrayList<User> userArrayList;
    UserAdapter adapter;
    RecyclerView recyclerView;
    dbManager dbManager;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iv_back = findViewById(R.id.iv_back);
        recyclerView = findViewById(R.id.recyclerView);
        dbManager = new dbManager(ViewUsersActivity.this);
        dbManager.open();
        userArrayList = dbManager.getAllUser();

        adapter = new UserAdapter(ViewUsersActivity.this, userArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewUsersActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}