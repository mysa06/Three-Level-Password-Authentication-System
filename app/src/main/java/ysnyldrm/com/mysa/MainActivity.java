package ysnyldrm.com.mysa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class MainActivity extends AppCompatActivity {

    private EditText editTextUserName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPhoneNumber;


    TextInputLayout textInputLayoutUserName;
    TextInputLayout textInputLayoutEmail;
    TextInputLayout textInputLayoutPassword;
    TextInputLayout textInputLayoutPhoneNumber;

    //Declaration Button
    Button buttonRegister;

    //Declaration SqliteHelper
    SqliteHelper sqliteHelper;


    final private String TAG = "Main Activity";

    List<String> permissions = new ArrayList<String>(); // Alınmamış izinleri listeye ekleyeceğiz.

    public static String IMEI;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"HardwareIds", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ControlPermissions();

        sqliteHelper = new SqliteHelper(this);


        String phoneNumber = sqliteHelper.getPhoneNumber();
        String imeiNumber = sqliteHelper.getImeiNumber();

        TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);


        if (phoneNumber != null) {

            if (imeiNumber == IMEI) {
                Intent intent = new Intent(this, FingerprintActivity.class);
                startActivity(intent);
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                IMEI = tManager.getImei();
            }
            else{
                IMEI = tManager.getImei();
            }
        }
        else{
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    IMEI = tManager.getImei();

                }
            }
            else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    IMEI = tManager.getImei();
                }
            }
        }



        Log.d(TAG,"imei : " + IMEI);

        initViews();
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    String UserName = editTextUserName.getText().toString();
                    String Email = editTextEmail.getText().toString();
                    String Password = editTextPassword.getText().toString();

                    char[] Pw = Password.toCharArray();
                    byte[] bytePw = hash(Pw);
                    String hashedPassword = new String(bytePw);
                    Password = hashedPassword;

                    String PhoneNumber = editTextPhoneNumber.getText().toString();
                    String Imei = IMEI;

                    //Check in the database is there any user associated with  this email
                    if (!sqliteHelper.isEmailExists(Email)) {

                        //Email does not exist now add new user to database
                        sqliteHelper.addUser(new User(null, UserName, Email, Password, PhoneNumber, Imei));
                        Snackbar.make(buttonRegister, "Hint : If your OTG device is not connected at  any time the program will execute yourself !", Snackbar.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                otgMessage();
                            }
                        }, Snackbar.LENGTH_LONG);
                    } else {

                        //Email exists with email input provided so show error user already exist
                        Snackbar.make(buttonRegister, "User already exists with same email ", Snackbar.LENGTH_LONG).show();
                    }


                }
            }
        });
    }


    private void initViews() {

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutUserName = (TextInputLayout) findViewById(R.id.textInputLayoutUserName);
        textInputLayoutPhoneNumber = (TextInputLayout) findViewById(R.id.textInputLayoutPhoneNumber);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

    }

    public boolean validate() {
        boolean valid = false;


        //Get values from EditText fields
        String UserName = editTextUserName.getText().toString();
        String Email = editTextEmail.getText().toString();
        String Password = editTextPassword.getText().toString();
        String PhoneNumber = editTextPhoneNumber.getText().toString();

        //Handling validation for UserName field
        if (UserName.isEmpty()) {
            valid = false;
            textInputLayoutUserName.setError("Please enter valid username!");
        } else {
            if (UserName.length() > 2) {
                valid = true;
                textInputLayoutUserName.setError(null);
            } else {
                valid = false;
                textInputLayoutUserName.setError("Username is to short!");
            }
        }

        //Handling validation for Email field
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            valid = false;
            textInputLayoutEmail.setError("Please enter valid email!");
        } else {
            valid = true;
            textInputLayoutEmail.setError(null);
        }

        //Handling validation for Password fieldnc
        if (Password.isEmpty()) {
            valid = false;
            textInputLayoutPassword.setError("Please enter valid password!");
        } else {
            if (Password.length() > 5) {
                valid = true;
                textInputLayoutPassword.setError(null);
            } else {
                valid = false;
                textInputLayoutPassword.setError("Password is to short!");
            }
        }

        //Handling validation for Password field
        if (PhoneNumber.isEmpty()) {
            valid = false;
            textInputLayoutPhoneNumber.setError("Please enter valid phoneNumber!");
        } else {
            if (Password.length() > 5) {
                valid = true;
                textInputLayoutPhoneNumber.setError(null);
            } else {
                valid = false;
                textInputLayoutPhoneNumber.setError("PhoneNumber is to short!");
            }
        }


        return valid;
    }

    public void otgMessage() {

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please plug-in your OTG device for registration in 10 Seconds. You are redirecting ...");
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(MainActivity.this,OtgRegister.class);
                startActivity(intent);

            }

        }, 10000);


    }

    public byte[] hash(char[] password) {
        int ITERATIONS = 10000;
        int KEY_LENGTH = 256;
        byte[] salt = "E1F53135E559C253".getBytes();
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }


    public void ControlPermissions(){

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){ // Sms okuma izni verilmemişse listeye ekle
            permissions.add(android.Manifest.permission.READ_SMS);
        }

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){ // Belleğe yazma izni verilmemişse listeye ekle
            permissions.add(android.Manifest.permission.RECEIVE_SMS);
        }

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){ // Belleğe yazma izni verilmemişse listeye ekle
            permissions.add(android.Manifest.permission.SEND_SMS);
        }

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){ // Belleğe yazma izni verilmemişse listeye ekle
            permissions.add(android.Manifest.permission.INTERNET);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.READ_PHONE_STATE);
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        /* İzin listemiz boş değilse artık iznimizi istiyoruz. Burada kullanıcıya bir diyalog kutusu çıkacak ve izni verip vermeyeceği sorulacak. Kullanıcının cevabına göre bize bir result dönecek. Yine bunu 3. parametrede gönderdiğimiz kod ile yakalayacağız. Bu değeri biz belirliyoruz. 2. parametre ise string dizisi alıyor. */

        if(!permissions.isEmpty()){
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), 0);
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), 1);
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), 2);
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), 3);
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), 4);
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), 5);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch ( requestCode ) {
            case 0: {
                for( int i = 0; i < permissions.length; i++ ) { // İstediğimiz izinler dolaşalım
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) { // Eğer izin verilmişse
                        Log.d( "Permissions", "İzin Verildi: " + permissions[i] ); // İsmiyle birlikte izin verildi yazıp log basalım.

                        // İzin verildiği için burada istediğiniz işlemleri yapabilirsiniz. Verilen izne göre sms okuyabilir ve belleğe yazabilirsiniz.
                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) { // Eğer izin reddedildiyse
                        Log.d( "Permissions", "İzin Reddedildi: " + permissions[i] ); // İsmiyle birlikte reddedildi yazıp log basalım.
                        // Burada bir toast mesajı gösterebilirsiniz. Mesela bu işlemi yapabilmek için izin vermeniz gereklidir. gibi..
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
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