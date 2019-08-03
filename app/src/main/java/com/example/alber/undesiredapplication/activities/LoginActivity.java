package com.example.alber.undesiredapplication.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alber.undesiredapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar Toolbar;
    private EditText LoginEmail;
    private EditText LoginPassword;
    private Button Login_btn;
    private ProgressDialog LoginProgress;
    private FirebaseAuth Auth;
    private DatabaseReference UserDatabase;
    private Button Signin_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Auth = FirebaseAuth.getInstance();


        LoginProgress = new ProgressDialog(this);

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        LoginEmail = (EditText) findViewById(R.id.email);
        LoginPassword = (EditText) findViewById(R.id.password);
        Login_btn = (Button) findViewById(R.id.log_btn);
        Signin_btn = (Button) findViewById(R.id.sing_btn);


        Signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), RegisterActivity.class);
                view.getContext().startActivity(intent);

            }
            });



        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = LoginEmail.getText().toString();
                String password = LoginPassword.getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    LoginProgress.setTitle("Logging In");
                    LoginProgress.setMessage("Please wait");
                    LoginProgress.setCanceledOnTouchOutside(false);
                    LoginProgress.show();

                    loginUser(email, password);

                }

            }
        });


    }



    private void loginUser(String email, String password) {


        Auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    LoginProgress.dismiss();

                    String current_user_id = Auth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();     //device token needed to implement notifications (coming soon)

                    UserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            //this flag will cause any existing task that would be associated with the activity to be cleared before the activity is started
                            //guys at developer.android.com said it's useful so...
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(mainIntent);
                            finish();
                        }
                    });

                } else {

                    String error = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        error = "Invalid Email!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        error = "Invalid Password!";
                    } catch (Exception e) {
                        error = "Default error!";
                        e.printStackTrace();
                    }

                    LoginProgress.hide();
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();

                }

            }
        });

    }
}
