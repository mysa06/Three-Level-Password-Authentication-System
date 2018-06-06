package ysnyldrm.com.mysa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.krtkush.lineartimer.LinearTimer;
import io.github.krtkush.lineartimer.LinearTimerView;

public class OtpActivity extends AppCompatActivity {

    SqliteHelper sqliteHelper;
    String Phone;
    String randomNumber;
    private TextView time;
    Button send;
    EditText edt;
    String usersCode;
    String validationNumber;
    int counter = 5;

    private SmsVerifyCatcher smsVerifyCatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        send = (Button) findViewById(R.id.reSend);
        edt = (EditText) findViewById(R.id.editTextNum);

        LinearTimerView linearTimerView = (LinearTimerView)
                findViewById(R.id.linearTimer);
        sqliteHelper = new SqliteHelper(this);
        Phone = "0" + sqliteHelper.getPhoneNumber();
        randomNumberGenerator();
        refreshTimer();
        sendOTP();

        final EditText etCode = (EditText) findViewById(R.id.editTextNum);

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                etCode.setText(code);//set code in edit text
                //then you can send verification code to server
                otoValidateNextOTG();
            }
        });


    }

    public void sendOTP(){
        try {

            send.setVisibility(View.GONE);
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(Phone, null, randomNumber, null, null);


            Toast.makeText(OtpActivity.this, "One-Time Password is  sended  ", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(OtpActivity.this, "One-Time Password cannot sended ! ", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendOTPButton(View view){
        try {

            refreshTimer();
            randomNumberGenerator();
            send.setVisibility(View.INVISIBLE);
            // randomNumber
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(Phone, null, randomNumber, null, null);


            Toast.makeText(OtpActivity.this, "One-Time Password is succesfully sended ! ", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(OtpActivity.this, "One-Time Password cannot sended ! ", Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshTimer(){

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

                send.setVisibility(View.VISIBLE);

            }
        }.start();

    }

    public void randomNumberGenerator(){
        Random rnd = new Random();
        int n = 100000 + rnd.nextInt(900000);
        randomNumber = Integer.toString(n);
        validationNumber = randomNumber;
        randomNumber = "Please don't share this password. Your verification code is: " + randomNumber ;
    }

    public void otoValidateNextOTG(){
        usersCode = edt.getText().toString();

        if(validationNumber.matches(usersCode)){


            final ProgressDialog progressDialog = new ProgressDialog(this,
                    R.style.Theme_AppCompat_DayNight_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Validation is succesful , you are redirecting to next step, please plug-in your OTG device...");
            progressDialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(OtpActivity.this,OtgRegister.class);
                    startActivity(intent);

                }

            }, 4000);



        }
        else{

            Toast.makeText(OtpActivity.this, " Validation is unsuccesful , please  wait the timer and re-send OTP !", Toast.LENGTH_LONG).show();
            counter--;
            if(counter == 0){
                Toast.makeText(OtpActivity.this, " Login attemp is failed, you are redirecting to login page !", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this,FingerprintActivity.class);
                startActivity(intent);
            }
        }
    }

    public void validate(View view){

        usersCode = edt.getText().toString();

        if(validationNumber.matches(usersCode)){


        /*    final ProgressDialog progressDialog = new ProgressDialog(this,
                    R.style.Theme_AppCompat_DayNight_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Validation is succesful , you are redirecting to next step, please plug-in your OTG device...");
            progressDialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {



                }

            }, 4000); */



            Intent intent = new Intent(OtpActivity.this,OtgRegister.class);
            startActivity(intent);

        }
        else{

            Toast.makeText(OtpActivity.this, " Validation is unsuccesful , please  wait the timer and re-send OTP !", Toast.LENGTH_LONG).show();
            counter--;
            if(counter == 0){
                Toast.makeText(OtpActivity.this, " Login attemp is failed, you are redirecting to login page !", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this,FingerprintActivity.class);
                startActivity(intent);
            }
        }
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