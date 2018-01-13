package com.lamphongstore.lamphong.material;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.model.LPError;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.lamphongstore.lamphong.ApiLamPhong.API_RESET_PASSWORD;
import static com.lamphongstore.lamphong.activities.abstracted.AbstractBaseActivity.handleError;
import static com.lamphongstore.lamphong.activities.abstracted.AbstractBaseFormActivity.isValidEmail;

/**
 * Created by Norvia on 24/03/2017.
 */

public class ForgotPasswordDialog extends DialogFragment {

    private TextInputLayout inputLayoutEmail;
    private LPEditText inputEmail;
    private Vibrator vib;
    private Animation animShake;

    public ForgotPasswordDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_forgot_password, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        vib = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);

        inputLayoutEmail = (TextInputLayout) view.findViewById(R.id.input_layout_email);
        inputEmail = (LPEditText) view.findViewById(R.id.input_email);
        Button confirmButton = (Button) view.findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputs()) {
                    contactServer();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog.getWindow()!=null) {
            dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        }
    }

    private boolean checkInputs() {
        if (!checkEmail()) {
            inputEmail.setAnimation(animShake);
            inputEmail.startAnimation(animShake);
            vib.vibrate(200);
            return false;
        }
        inputLayoutEmail.setErrorEnabled(false);
        return true;
    }

    private boolean checkEmail() {
        String email = inputEmail.getText().toString();

        if (!email.trim().isEmpty() || isValidEmail(email)) {
            inputLayoutEmail.setErrorEnabled(false);
            return true;
        } else {
            inputLayoutEmail.setErrorEnabled(true);
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        }
    }

    private void contactServer() {

        AndroidNetworking.post(API_RESET_PASSWORD)
                .addBodyParameter("email", inputEmail.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(getActivity(), response.getString("data"), Toast.LENGTH_SHORT).show();
                                ForgotPasswordDialog.this.getDialog().cancel();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        handleError(new LPError(anError), getActivity());
                    }
                });
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
