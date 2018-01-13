package com.lamphongstore.lamphong.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.activities.abstracted.AbstractBaseFormActivity;
import com.lamphongstore.lamphong.material.LPEditText;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AbstractBaseFormActivity {

    private TextInputLayout inputLayoutPassword;
    private LPEditText inputPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        needsDrawer = false;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initViews() {
        super.initViews();
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputPassword = (LPEditText) findViewById(R.id.input_password);

        Button confirmButton = (Button) findViewById(R.id.signup_button);

        TextView logInText = (TextView) findViewById(R.id.login_text_clickable);

        logInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                if (checkInputs() && checkOptionalInputs()) {
                    contactServer();
                } else {
                    showProgress(false);
                }
            }
        });
    }

    @Override
    protected void contactServer() {
        super.contactServer();

        Map<String, String> params = new HashMap<>();
        params.put("phone", inputPhone.getText().toString());
        params.put("password", inputPassword.getText().toString());
        params.put("fullname", inputName.getText().toString());
        params.put("email", inputEmail.getText().toString());
        params.put("birthday", getBirthDateFormatted());
        params.put("address", inputAddress.getText().toString());
        if (inputGender.getCheckedRadioButtonId() == R.id.input_gender_male) {
            params.put("gender", "male");
        } else {
            params.put("gender", "female");
        }
        params.put("invitaion_code", inputRefCode.getText().toString().toUpperCase());

        getUserDataStore().register(params);
    }

    private boolean checkOptionalInputs() {

        if (!checkPassword(inputPassword, inputLayoutPassword)) {
            inputPassword.setAnimation(animShake);
            inputPassword.startAnimation(animShake);
            vib.vibrate(200);
            return false;
        }
        inputLayoutPassword.setErrorEnabled(false);
        return true;
    }

}