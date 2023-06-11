package com.example.tutron;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    UserInfo userInfo;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userInfo = (UserInfo) getIntent().getSerializableExtra("user");
        TextView textView = findViewById(R.id.welcome);
        String[] roles = {"Administrator","Student","Tutor"};
        String msg = "Welcome! You are logged in as " + roles[Integer.parseInt(userInfo.getRole())];
        textView.setText(msg);
        findViewById(R.id.btn_logoff).setOnClickListener(view -> confirmAlert());

    }
    private void confirmAlert() {
        builder = new AlertDialog.Builder(this);
        alert = builder.setTitle("tip").setMessage("are you log off?").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        }).setPositiveButton("confirm", (dialog, which) -> finish()).create();             //create AlertDialog object
        alert.show();                    //show dialog box
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_logoff:
                finish();
                break;
            default:
                break;
        }
    }
}