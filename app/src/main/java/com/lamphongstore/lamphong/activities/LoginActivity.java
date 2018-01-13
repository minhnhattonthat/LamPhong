package com.lamphongstore.lamphong.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.activities.abstracted.AbstractBaseFormActivity;
import com.lamphongstore.lamphong.material.ForgotPasswordDialog;
import com.lamphongstore.lamphong.material.LPEditText;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AbstractBaseFormActivity {

    private static final int TO_SIGN_UP = 12;

    private TextInputLayout inputLayoutPhone, inputLayoutPassword;
    private LPEditText inputPhone, inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        needsDrawer = false;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);

        inputPhone = (LPEditText) findViewById(R.id.input_phone);
        inputPassword = (LPEditText) findViewById(R.id.input_password);

        final Button logInButton = (Button) findViewById(R.id.login_button);
        TextView forgotPasswordText = (TextView) findViewById(R.id.login_forgot_password);
        TextView signUpText = (TextView) findViewById(R.id.signup_text_clickable);

        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    logInButton.performClick();
                    return true;
                }
                return false;
            }
        });

        logInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInputs())
                    contactServer();
            }
        });

        forgotPasswordText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });

        signUpText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(intent, TO_SIGN_UP);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TO_SIGN_UP && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }

    }

    @Override
    protected boolean checkInputs() {
        if (!checkPhone(inputPhone, inputLayoutPhone)) {
            inputPhone.setAnimation(animShake);
            inputPhone.startAnimation(animShake);
            vib.vibrate(200);
            return false;
        }

        if (!checkPassword(inputPassword, inputLayoutPassword)) {
            inputPassword.setAnimation(animShake);
            inputPassword.startAnimation(animShake);
            vib.vibrate(200);
            return false;
        }

        inputLayoutPhone.setErrorEnabled(false);
        inputLayoutPassword.setErrorEnabled(false);
        return true;
    }

    @Override
    protected void contactServer() {
        super.contactServer();

        Map<String, Object> params = new HashMap<>();
        params.put("phone", inputPhone.getText().toString());
        params.put("password", inputPassword.getText().toString());
        getUserDataStore().logIn(params);
    }

    private void showForgotPasswordDialog() {
        ForgotPasswordDialog dialog;
        dialog = new ForgotPasswordDialog();
        dialog.show(getFragmentManager(), "forgot_password");

    }

}

