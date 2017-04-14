package com.vitwale.vitwale.SignUpSignIn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stephentuso.welcome.WelcomeHelper;
import com.vitwale.vitwale.IntroSlider.MyWelcomeActivity;
import com.vitwale.vitwale.MainActivity;
import com.vitwale.vitwale.R;

public class SignUpSignIn extends AppCompatActivity {

    private TextView tvSignupInvoker;
    private LinearLayout llSignup;
    private TextView tvSigninInvoker;
    private LinearLayout llSignin;
    private Button btnSignup;
    private Button btnSignin;
    private WelcomeHelper welcomeScreen;
    private TextView memailSignup, mPasswordSignup, mMobileSignup;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupsignin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        welcomeScreen = new WelcomeHelper(this, MyWelcomeActivity.class);
        welcomeScreen.show(savedInstanceState);

        tvSignupInvoker = (TextView) findViewById(R.id.tvSignupInvoker);
        tvSigninInvoker = (TextView) findViewById(R.id.tvSigninInvoker);

        memailSignup = (TextView) findViewById(R.id.email_signup_field);
        mPasswordSignup = (TextView) findViewById(R.id.password_signup_field);
        mMobileSignup = (TextView) findViewById(R.id.mobile_signup_filed);
        btnSignup= (Button) findViewById(R.id.SignupBtn);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);


        btnSignin= (Button) findViewById(R.id.btnSignin);

        llSignup = (LinearLayout) findViewById(R.id.llSignup);
        llSignin = (LinearLayout) findViewById(R.id.llSignin);

        tvSignupInvoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignupForm();
            }
        });
        showSignupForm();

        tvSigninInvoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSigninForm();
            }
        });
        // showSigninForm();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_right_to_left);
                btnSignup.startAnimation(clockwise);
                startRegister();
            }
        });
    }

    private void showSignupForm() {
        PercentRelativeLayout.LayoutParams paramsLogin = (PercentRelativeLayout.LayoutParams) llSignin.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoLogin = paramsLogin.getPercentLayoutInfo();
        infoLogin.widthPercent = 0.15f;
        llSignin.requestLayout();


        PercentRelativeLayout.LayoutParams paramsSignup = (PercentRelativeLayout.LayoutParams) llSignup.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoSignup = paramsSignup.getPercentLayoutInfo();
        infoSignup.widthPercent = 0.85f;
        llSignup.requestLayout();

        tvSignupInvoker.setVisibility(View.GONE);
        tvSigninInvoker.setVisibility(View.VISIBLE);
        Animation translate= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.translate_right_to_left);
        llSignup.startAnimation(translate);

        Animation clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_right_to_left);
        btnSignup.startAnimation(clockwise);

    }

    private void showSigninForm() {
        PercentRelativeLayout.LayoutParams paramsLogin = (PercentRelativeLayout.LayoutParams) llSignin.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoLogin = paramsLogin.getPercentLayoutInfo();
        infoLogin.widthPercent = 0.85f;
        llSignin.requestLayout();


        PercentRelativeLayout.LayoutParams paramsSignup = (PercentRelativeLayout.LayoutParams) llSignup.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoSignup = paramsSignup.getPercentLayoutInfo();
        infoSignup.widthPercent = 0.15f;
        llSignup.requestLayout();

        Animation translate= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.translate_left_to_right);
        llSignin.startAnimation(translate);

        tvSignupInvoker.setVisibility(View.VISIBLE);
        tvSigninInvoker.setVisibility(View.GONE);
        Animation clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_left_to_right);
        btnSignin.startAnimation(clockwise);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        welcomeScreen.onSaveInstanceState(outState);
    }

    private void startRegister() {

        final String mobile = mMobileSignup.getText().toString().trim();
        final String email = memailSignup.getText().toString().trim();
        final String password = mPasswordSignup.getText().toString().trim();

        if(!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgressDialog.setMessage("Sigining up...");
            mProgressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("mobile").setValue(mobile);
                        current_user_db.child("email").setValue(email);
                        current_user_db.child("password").setValue(password);
                        mProgressDialog.dismiss();

                        Intent mainIntent = new Intent(SignUpSignIn.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                    }

                }
            });

        }

    }


}
