package ysnyldrm.com.mysa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.multidots.fingerprintauth.AuthErrorCodes;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;
import com.multidots.fingerprintauth.FingerPrintUtils;

public class FingerprintActivity extends AppCompatActivity implements FingerPrintAuthCallback {

    private TextView mAuthMsgTv;
    private ViewSwitcher mSwitcher;
    private Button mGoToSettingsBtn;
    private Button mGoToPasswordBtn;
    private FingerPrintAuthHelper mFingerPrintAuthHelper;
    SqliteHelper2 sqliteHelper2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        sqliteHelper2 = new SqliteHelper2(this);

        Toast.makeText(getApplicationContext(), sqliteHelper2.getRandomStr(), Toast.LENGTH_LONG).show();

        mGoToSettingsBtn = (Button) findViewById(R.id.go_to_settings_btn);
        mGoToPasswordBtn = (Button) findViewById(R.id.go_to_use_password);

        mGoToPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PasswordActivity.class));
            }
        });

        mGoToSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FingerPrintUtils.openSecuritySettings(getApplicationContext());
            }
        });

        mSwitcher = (ViewSwitcher) findViewById(R.id.main_switcher);
        mAuthMsgTv = (TextView) findViewById(R.id.auth_message_tv);


        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoToSettingsBtn.setVisibility(View.GONE);

        mAuthMsgTv.setText("Scan your finger");

        //start finger print authentication
        mFingerPrintAuthHelper.startAuth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFingerPrintAuthHelper.stopAuth();
    }

    @Override
    public void onNoFingerPrintHardwareFound() {
        //mAuthMsgTv.setText("Your device does not have finger print scanner. Please type 1234 to authenticate.");
        //mSwitcher.showNext();
        //Password activity ye geçiş
        startActivity(new Intent(getApplicationContext(), PasswordActivity.class));
    }

    @Override
    public void onNoFingerPrintRegistered() {
        mAuthMsgTv.setText("There are no finger prints registered on this device. Please register your finger from settings.");
        mGoToSettingsBtn.setVisibility(View.VISIBLE);
        //butonu düzenle
    }

    @Override
    public void onBelowMarshmallow() {
        //mAuthMsgTv.setText("You are running older version of android that does not support finger print authentication. Please type 1234 to authenticate.");
        //mSwitcher.showNext();
        //Password activity ye geçiş
        startActivity(new Intent(getApplicationContext(), PasswordActivity.class));
    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        Toast.makeText(FingerprintActivity.this, "Authentication succeeded.", Toast.LENGTH_SHORT).show();
        //OTP aşamasına geçiş
        startActivity(new Intent(getApplicationContext(), OtpActivity.class));
    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {
        switch (errorCode) {
            case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
                mAuthMsgTv.setText("Cannot recognize your finger print. Please try again.");
                break;
            case AuthErrorCodes.NON_RECOVERABLE_ERROR:
                //mAuthMsgTv.setText("Cannot initialize finger print authentication. Please type 1234 to authenticate.");
                //mSwitcher.showNext();
                //passworda gidecek
                startActivity(new Intent(getApplicationContext(), PasswordActivity.class));
                break;
            case AuthErrorCodes.RECOVERABLE_ERROR:
                mAuthMsgTv.setText(errorMessage);
                break;
        }
    }
}