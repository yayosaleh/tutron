package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener  {
    private EditText accountEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        accountEditText = findViewById(R.id.ed_account);
        passwordEditText = findViewById(R.id.ed_password);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                Intent intent = new Intent(this,RegisterActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    private void login(){
        String account = accountEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(account)){
            toast("account is empty!");
            return;
        }
        if (TextUtils.isEmpty(password)){
            toast("password is empty!");
            return;
        }
        UserInfo userInfo = null;
        List<UserInfo> infos = UserInfo.getUserInfos();
        for (int i = 0; i < infos.size(); i++) {
            UserInfo user = infos.get(i);
            if (account.equals(user.getAccount())){
                userInfo = user;
                break;
            }
        }
        if (userInfo == null){
            toast("account is not exists,please register!");
            return;
        }
        if (!userInfo.getPassword().equals(password)){
            toast("password is not right!");
            return;
        }
        toast("login success!");


        UserInfo finalUserInfo = userInfo;
        accountEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        accountEditText.setText("");
                        passwordEditText.setText("");
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra("user", finalUserInfo);
                        startActivity(intent);
                    }
                });

            }
        },500);

    }
    private void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
}
