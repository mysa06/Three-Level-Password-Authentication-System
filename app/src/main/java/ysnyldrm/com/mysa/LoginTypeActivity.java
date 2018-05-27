package ysnyldrm.com.mysa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LoginTypeActivity extends AppCompatActivity {

    Button buttonFingerprint;
    Button buttonPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_type);



        buttonFingerprint = (Button) findViewById(R.id.btn_choosefingerprint);
        buttonPassword = (Button) findViewById(R.id.btn_choosepassword);
        buttonPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginTypeActivity.this,PasswordActivity.class);
                startActivity(intent);
            }
        });

        buttonFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginTypeActivity.this,FingerprintActivity.class);
                startActivity(intent);
            }
        });

    }

}