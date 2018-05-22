package ysnyldrm.com.mysa;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.krtkush.lineartimer.LinearTimer;
import io.github.krtkush.lineartimer.LinearTimerView;

public class OtpActivity extends AppCompatActivity  {

    private final String TAG  = "OTP";

    private CountDownTimer countDownTimer;
    private SmsVerifyCatcher smsVerifyCatcher;
    EditText editTextPhone, editTextCode;

    SqliteHelper sqliteHelper;

    private TextView time;

    private FirebaseAuth mAuth;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks getmCallbacks;
    String verification_code;

    String codeSent;

    String phoneNumber;

    private Button otg;

    //Timer:


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        sqliteHelper = new SqliteHelper(this);

        phoneNumber = sqliteHelper.getPhoneNumber();

        mAuth = FirebaseAuth.getInstance();

        editTextCode = findViewById(R.id.editTextCode);

        otg = findViewById(R.id.nextOTG);

        otg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OTGActivity.class));
            }
        });

        getmCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verification_code = s;
                Toast.makeText(getApplicationContext(),"code sent to number",Toast.LENGTH_SHORT).show();


            }
        };

        send_Sms();

        //sendVerificationCode();



        LinearTimerView linearTimerView = (LinearTimerView)
                findViewById(R.id.linearTimer);

        final LinearTimer linearTimer = new LinearTimer.Builder()
                .linearTimerView(linearTimerView)
                .duration(120*1000)
                .build();
        linearTimer.startTimer();
        time = (TextView) findViewById(R.id.counttimer);


        new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                time.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {

                time.setText("done!");
            }
        }.start();

        // Start the timer.

        final EditText etCode = (EditText) findViewById(R.id.editTextCode);

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                etCode.setText(code);//set code in edit text
                //then you can send verification code to server
            }
        });

    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }
/*
    private void verifyLoginCode(){
        String code = editTextCode.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
        //FirebaseAuth.getInstance().signOut();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //here you can open new activity
                            Toast.makeText(getApplicationContext(),
                                    "Login Successfull", Toast.LENGTH_LONG).show();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),
                                        "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
*/

    public void send_Sms(){
        //String number = phoneNumber;

        Log.d(TAG,"phone number : : : : " + phoneNumber);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+905301180330",60,TimeUnit.SECONDS,this,getmCallbacks
        );
    }

    public void signInWithPhone(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "User signed in Successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void Verify(View view){

        startActivity(new Intent(getApplicationContext(), OTGActivity.class));

        /*String input_code = editTextCode.getText().toString();
        //if (verification_code.equals("")){
            verifyPhoneNumber(verification_code,input_code);
       // }*/
    }

    public void verifyPhoneNumber(String verifyCode, String inputCode){

        Log.d(TAG," verify code :  " + verifyCode);
        PhoneAuthCredential credential =  PhoneAuthProvider.getCredential(verifyCode,inputCode);
        signInWithPhone(credential);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure for exit the application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", null).show();
    }

}

