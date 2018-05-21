package ysnyldrm.com.mysa;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqliteHelper = new SqliteHelper(this);

        String phoneNumber = sqliteHelper.getPhoneNumber();

        if(phoneNumber != null){

            Intent intent = new Intent(this,FingerprintActivity.class);
            startActivity(intent);

        }

        initViews();
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    String UserName = editTextUserName.getText().toString();
                    String Email = editTextEmail.getText().toString();
                    String Password = editTextPassword.getText().toString();
                    String PhoneNumber = editTextPhoneNumber.getText().toString();

                    //Check in the database is there any user associated with  this email
                    if (!sqliteHelper.isEmailExists(Email)) {

                        //Email does not exist now add new user to database
                        sqliteHelper.addUser(new User(null, UserName, Email, Password, PhoneNumber));
                        Snackbar.make(buttonRegister, "User created successfully! Please Login ", Snackbar.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, LoginTypeActivity.class);
                                startActivity(intent);
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
            if (UserName.length() > 5) {
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


}







    /*   public void CreateAccount(View view ){

        boolean flag = true;

        String nameString = name.getText().toString();
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        String phoneNumberString = phoneNumber.getText().toString();

        if (TextUtils.isEmpty(nameString)) {
            name.setError("This field is required !");
            flag = false;
        } else if (TextUtils.isEmpty(emailString)) {
            email.setError("This field is required !");
            flag = false;

        } else if (TextUtils.isEmpty(passwordString)) {
            password.setError("This field is required !");
            flag = false;
        }
          else if(TextUtils.isEmpty(phoneNumberString)){
            phoneNumber.setError("This field is required !");
        }
          else if(flag == true){

            final ProgressDialog progressDialog = new ProgressDialog(this,
                    R.style.Theme_AppCompat_DayNight_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {



                }

            }, 1000);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    final Intent mainIntent = new Intent(MainActivity.this, LoginTypeActivity.class);
                    MainActivity.this.startActivity(mainIntent);
                    MainActivity.this.finish();
                    progressDialog.dismiss();
                }

            }, 3000);
        }



    } */

