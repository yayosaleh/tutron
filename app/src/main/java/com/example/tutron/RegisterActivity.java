package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.FrameLayout;

import java.util.List;

public class RegisterActivity extends AppCompatActivity  implements View.OnClickListener{
    private EditText accountEditText;
    private EditText passwordEditText;
    private EditText nickNameEditText;
    private RadioGroup roleRadioGroup;
    private RadioButton studentRadioButton;
    private RadioButton tutorRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }
    private void initView() {
        accountEditText = findViewById(R.id.ed_account);
        passwordEditText = findViewById(R.id.ed_password);
        nickNameEditText = findViewById(R.id.ed_nickname);
        roleRadioGroup  = findViewById(R.id.radioGroup_role);
        studentRadioButton  = findViewById(R.id.radioButton_student);
        tutorRadioButton  = findViewById(R.id.radioButton_tutor);

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                finish();
                break;
            case R.id.btn_register:
                regist();
                break;
            default:
                break;
        }
    }
    private void regist(){
        String account = accountEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String nickname = nickNameEditText.getText().toString();
        String role = "0";
        if (roleRadioGroup.getCheckedRadioButtonId() == R.id.radioButton_student){
            role = "1";
        }else{
            role = "2";
        }
        if (TextUtils.isEmpty(account)){
            toast("account is empty!");
            return;
        }
        if (TextUtils.isEmpty(password)){
            toast("password is empty!");
            return;
        }
        if (TextUtils.isEmpty(nickname)){
            toast("nickname is empty!");
            return;
        }

        List<UserInfo> infos = UserInfo.getUserInfos();
        for (int i = 0; i < infos.size(); i++) {
            UserInfo user = infos.get(i);
            if (account.equals(user.getAccount())){
                toast("account is exits!");
                return;
            }
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setAccount(account);
        userInfo.setPassword(password);
        userInfo.setNickname(nickname);
        userInfo.setRole(role);
        UserInfo.getUserInfos().add(userInfo);
        toast("register success!");

        accountEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });

            }
        },1000);


    }
    private void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
}
