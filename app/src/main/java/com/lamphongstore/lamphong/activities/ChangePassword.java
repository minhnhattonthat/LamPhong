package com.lamphongstore.lamphong.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.data.UserManager;
import com.lamphongstore.lamphong.material.LPEditText;
import com.lamphongstore.lamphong.model.LPError;

import org.json.JSONException;
import org.json.JSONObject;

import static com.lamphongstore.lamphong.ApiLamPhong.API_CHANGE_PASSWORD;
import static com.lamphongstore.lamphong.activities.abstracted.AbstractBaseActivity.handleError;
import static com.lamphongstore.lamphong.activities.abstracted.AbstractBaseFormActivity.isValidPassword;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {
    private static final String AUTHORIZATION = "Authorization";
    private static final String OLD_PASSWORD = "old_password";
    private static final String NEW_PASSWORD = "new_password";
    private LPEditText inputOldPassWord, inputNewPassWord;
    private TextInputLayout inputLayoutOldPassword, inputLayoutNewPassWord;
    private Button confirmButton;
    private ProgressBar progressBar;
    private ImageView backButton;
    private UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        inputLayoutOldPassword = (TextInputLayout) findViewById(R.id.layout_input_old_pass);
        inputLayoutNewPassWord = (TextInputLayout) findViewById(R.id.layout_input_new_pass);
        inputOldPassWord = (LPEditText) findViewById(R.id.input_old_password);
        inputNewPassWord = (LPEditText) findViewById(R.id.input_new_password);

        confirmButton = (Button) findViewById(R.id.button_confirm_change_password);
        progressBar = (ProgressBar) findViewById(R.id.progress_change);
        backButton = (ImageView) findViewById(R.id.button_back_to_profile);

        mUserManager = UserManager.getInstance();

        backButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);

    }

    private void changePassword() {

        inputOldPassWord.setError(null);
        inputNewPassWord.setError(null);

        String oldPassword = inputOldPassWord.getText().toString();
        String newPassword = inputNewPassWord.getText().toString();

        boolean checkError = false;
        View focusView = null;

        if (!isValidPassword(oldPassword)) {
            inputLayoutOldPassword.setEnabled(true);
            inputLayoutOldPassword.setError(getString(R.string.err_msg_password));
            focusView = inputOldPassWord;
            checkError = true;
        }

        if (newPassword.equals(oldPassword)) {
            inputLayoutNewPassWord.setEnabled(true);
            inputLayoutNewPassWord.setError(getString(R.string.error_same_old_password));
            focusView = inputNewPassWord;
            checkError = true;
        }

        if (!isValidPassword(newPassword)) {
            inputLayoutNewPassWord.setEnabled(true);
            inputLayoutNewPassWord.setError(getString(R.string.err_msg_password));
            focusView = inputOldPassWord;
            checkError = true;
        }

        if (checkError) {
            focusView.requestFocus();
        } else {
            showLoading();
            AndroidNetworking.post(API_CHANGE_PASSWORD)
                    .addHeaders(AUTHORIZATION, mUserManager.getUser().getToken())
                    .addBodyParameter(OLD_PASSWORD, oldPassword)
                    .addBodyParameter(NEW_PASSWORD, newPassword)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("success")) {
                                    Toast.makeText(ChangePassword.this, "change success", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                hideLoading();
                                e.printStackTrace();
                                Toast.makeText(ChangePassword.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            hideLoading();
                            handleError(new LPError(anError), ChangePassword.this);
                        }
                    });
        }
    }

    private void showLoading() {
        hideView(inputNewPassWord);
        hideView(inputOldPassWord);
        hideView(backButton);
        hideView(confirmButton);

        showView(progressBar);
    }

    private void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void hideView(View view) {
        view.setVisibility(View.GONE);
    }

    private void hideLoading() {
        hideView(progressBar);
        showView(inputOldPassWord);
        showView(inputNewPassWord);
        showView(confirmButton);
        showView(backButton);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm_change_password:
                changePassword();
                break;
            case R.id.button_back_to_profile:
                onBackPressed();
                break;
        }
    }
}
