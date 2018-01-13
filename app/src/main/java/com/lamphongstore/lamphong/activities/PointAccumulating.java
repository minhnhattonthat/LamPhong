package com.lamphongstore.lamphong.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.lamphongstore.lamphong.ApiLamPhong;
import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.data.UserManager;
import com.lamphongstore.lamphong.material.CustomSweetAlertDialog;
import com.lamphongstore.lamphong.model.LPError;
import com.lamphongstore.lamphong.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PointAccumulating extends AppCompatActivity {
    private final String TAG = PointAccumulating.class.getSimpleName();
    CustomSweetAlertDialog pDialog;
    private TextView customerName;
    private TextView viewPoint;
    private TextView viewCustomerRank;
    private TextInputLayout input_layout_money_amount;
    private TextInputLayout input_layout_introducer_code;
    private TextInputEditText txtIntroducerCode, txtMoneyAmount;
    private Button btnSave;
    private Toast toast;
    private User userAddPoint;
    private User userCurrent;
    private Vibrator vib;
    private Animation animShake;
    private Dialog confirmDialog;
    //----Dialog

    TextView titleDialog;
    TextView contentDialog;
    Button confirmButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_accumulating);

        Toolbar toolbar = (Toolbar) findViewById(R.id.point_accumulating_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        initViews();

        if (getIntent() != null && getIntent().getSerializableExtra("USERSCAN") != null) {

            userCurrent = UserManager.getInstance().getUser();
            userAddPoint = (User) getIntent().getSerializableExtra("USERSCAN");
            setInfoToLayout(userAddPoint);
            setOnclickButtonSave();
            setOnTouchLayout();

            if (userCurrent == null) {
                gotoLoginActivity();
                finish();
            }
        } else {
            displayToast("No user Code ,error getting data . Redirecting back to Main ");
            Intent intent = new Intent(PointAccumulating.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }

    private void gotoLoginActivity() {
        Intent intent = new Intent(PointAccumulating.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void initViews() {
        customerName = (TextView) findViewById(R.id.customerName);
        viewPoint = (TextView) findViewById(R.id.viewPoint);
        viewCustomerRank = (TextView) findViewById(R.id.viewCustomerRank);
        //------
        input_layout_money_amount = (TextInputLayout) findViewById(R.id.input_layout_money_amount);
        input_layout_introducer_code = (TextInputLayout) findViewById(R.id.input_layout_introducer_code);
        //------
        txtMoneyAmount = (TextInputEditText) findViewById(R.id.txtMoneyAmount);
        txtIntroducerCode = (TextInputEditText) findViewById(R.id.txtIntroducerCode);
        //-------
        btnSave = (Button) findViewById(R.id.btnSave);
        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        Typeface sfNS = Typeface.createFromAsset(getAssets(), "fonts/SF-UI-Text-Regular.otf");
        Typeface sfNSbold = Typeface.createFromAsset(getAssets(), "fonts/SF-UI-Text-Bold.otf");
        Typeface sfNSMedium = Typeface.createFromAsset(getAssets(), "fonts/SF-UI-Text-Medium.otf");
        customerName.setTypeface(sfNSMedium);
        viewPoint.setTypeface(sfNSbold);
        txtMoneyAmount.setTypeface(sfNS);
        txtMoneyAmount.setRawInputType(Configuration.KEYBOARD_QWERTY);
        txtMoneyAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtIntroducerCode.setTypeface(sfNS);
        txtIntroducerCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
//        viewMoneyAmount.setTypeface(sfNS);
//        viewIntroducerCode.setTypeface(sfNS);
        viewCustomerRank.setTypeface(sfNS);
        btnSave.setTypeface(sfNS);
        //Set when number is over 1000 , then add comma
        txtMoneyAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                txtMoneyAmount.removeTextChangedListener(this);

                try {
                    String givenstring = editable.toString();
                    Long longval;
                    if (givenstring.contains(",")) {
                        givenstring = givenstring.replaceAll(",", "");
                    }
                    longval = Long.parseLong(givenstring);
                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    String formattedString = formatter.format(longval);
                    txtMoneyAmount.setText(formattedString);
                    txtMoneyAmount.setSelection(txtMoneyAmount.getText().length());
                    // to place the cursor at the end of text
                } catch (Exception e) {
                    e.printStackTrace();
                }

                txtMoneyAmount.addTextChangedListener(this);
            }
        });
        //---
        confirmDialog = new Dialog(PointAccumulating.this);
        confirmDialog.setContentView(R.layout.dialog_confirm_layout);
        if (confirmDialog.getWindow() != null)
            confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        titleDialog = (TextView) confirmDialog.findViewById(R.id.title_dialog_confirm);
        contentDialog = (TextView) confirmDialog.findViewById(R.id.content_dialog_confirm);
        confirmButton = (Button) confirmDialog.findViewById(R.id.confirm_button_dialog);
        cancelButton = (Button) confirmDialog.findViewById(R.id.cancel_button_dialog);

        if (Build.VERSION.SDK_INT >= 21) {
            pDialog = new CustomSweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Đang xử lý");
            pDialog.setCancelable(false);
            pDialog.setConfirmClickListener(new CustomSweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(CustomSweetAlertDialog sweetAlertDialog) {
                    pDialog.dismiss();
                }
            });
        }

    }


    //Click outside to turn off keyboard
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void addMorePoint(String invitation_code) {
        HashMap<String,String> postRequest = new HashMap<String, String>();
        postRequest.put("user_id",String.valueOf(userAddPoint.getId()));
        postRequest.put("total_money", String.valueOf(txtMoneyAmount.getText().toString().replaceAll(",", "")));
        if(!invitation_code.trim().isEmpty()){
            postRequest.put("invitation_code",invitation_code);
        }
        AndroidNetworking.post(ApiLamPhong.API_ADD_POINT)
                .setContentType("application/x-www-form-urlencoded")
                .addHeaders("Authorization", userCurrent.getToken())
                .addBodyParameter(postRequest)
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (!success) {

                                displayToast("Update point failed");
                            } else {
                                JSONObject data = response.getJSONObject("data");
                                JSONObject pointInfo = data.getJSONObject("point_info");
                                String userAddPointString = "Chúc mừng thành viên " + userAddPoint.getFullname() + " đã tích lũy được " + pointInfo.getInt("point") + " điểm";
                                if (Build.VERSION.SDK_INT >= 21){
                                    pDialog.changeAlertType(CustomSweetAlertDialog.SUCCESS_TYPE);
                                    pDialog.setTitleText("Thành công");
                                    pDialog.setContentText(userAddPointString);
                                    pDialog.show();
                                } else {
                                    displayToast(userAddPointString);
                                }

                                setNewPointToDisplayAfterAddPoint(pointInfo);
                            }
                        } catch (JSONException e) {
                            displayToast("Lỗi lấy dữ liệu : " + e.getMessage());
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        pDialog.cancel();

                        LPError errpr = new LPError(anError);
                        displayToast("Lỗi xảy ra : " + errpr.getShow());
                    }
                });


    }

    public void setNewPointToDisplayAfterAddPoint(JSONObject data) throws JSONException {

        int newPoint = data.getInt("point");
        viewPoint.setText(String.valueOf(newPoint));

    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void setOnTouchLayout() {
//        txtMoneyAmount.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                input_layout_money_amount.setError("");
//                input_layout_introducer_code.setError("");
//                txtMoneyAmount.setText("");
//                return false;
//            }
//        });
    }

    public int checkRequiredField() {
        // 0 success , 1 invalid money , 2 invalid introducer code
        int money;
        try {
            money = Integer.parseInt(txtMoneyAmount.getText().toString().replaceAll(",", ""));
        } catch (NumberFormatException e) {
            return 1;
        }

        if (money < 0) {
            return 1;
        }
        if (txtIntroducerCode.getText().toString().trim().length() > 0 && txtIntroducerCode.getText().toString().trim().length() < 6) {

            return 2;
        }
        return 0;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void initDialog(){
        titleDialog.setText("Xác Nhận!!!");
        contentDialog.setText("Bạn muốn nhập " + txtMoneyAmount.getText().toString() +  " cho khách hàng ?");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog.dismiss();
            }
        });

    }
    public void setOnclickButtonSave() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDialog();
                hideKeyboard();
                if (checkRequiredField() == 1) {
                    input_layout_money_amount.setError("Số tiền không hợp lệ");
                    input_layout_money_amount.setAnimation(animShake);
                    input_layout_money_amount.startAnimation(animShake);
                    vib.vibrate(200);
                    requestFocus(input_layout_money_amount);
                } else if (checkRequiredField() == 2) {
                    input_layout_introducer_code.setError("Mã khách hàng không hợp lệ");
                    input_layout_introducer_code.setAnimation(animShake);
                    input_layout_introducer_code.startAnimation(animShake);
                    requestFocus(input_layout_introducer_code);
                    vib.vibrate(200);
                } else {
                    if (checkRequiredField() == 0) {
                        if (txtIntroducerCode.getText().toString().trim().length() > 0) {

                            confirmButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pDialog.show();
                                    AndroidNetworking.post(ApiLamPhong.API_CHANGE_PROFILE)
                                            .addHeaders("Authorization", userCurrent.getToken())
                                            .setContentType("application/x-www-form-urlencoded")
                                            .addBodyParameter("user_code", txtIntroducerCode.getText().toString().trim())
                                            .setTag(this)
                                            .setPriority(Priority.LOW)
                                            .build()
                                            .getAsJSONObject(new JSONObjectRequestListener() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {

                                                        boolean success = response.getBoolean("success");
                                                        if (!success) {
                                                            Log.e(TAG, " success false");
                                                            displayToast("Mã khách hàng không tìm thấy");
                                                            pDialog.dismiss();
                                                            confirmDialog.dismiss();

                                                        } else {
                                                            input_layout_introducer_code.setErrorEnabled(false);
                                                            addMorePoint(txtIntroducerCode.getText().toString().trim());
                                                            input_layout_introducer_code.setErrorEnabled(true);
                                                            confirmDialog.dismiss();
                                                        }
                                                    } catch (JSONException e) {
                                                        displayToast("Lỗi lấy dữ liệu " + e.getMessage());
                                                        confirmDialog.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onError(ANError anError) {
                                                    input_layout_introducer_code.setError("Không tìm thấy mã giới thiệu này !");
                                                    input_layout_introducer_code.setAnimation(animShake);
                                                    input_layout_introducer_code.startAnimation(animShake);
                                                    vib.vibrate(200);
                                                    requestFocus(input_layout_introducer_code);
                                                    pDialog.dismiss();
                                                    confirmDialog.dismiss();
                                                }
                                            });
                                }
                            });
                            confirmDialog.show();
                        } else {
                            confirmButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pDialog.show();
                                    addMorePoint("");
                                    confirmDialog.dismiss();
                                }
                            });
                            confirmDialog.show();
                        }
                    }
                }

            }
        });
    }

    public void displayToast(String message) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void setInfoToLayout(User user) {
        customerName.setText(user.getFullname());
        viewPoint.setText(String.valueOf(user.getPointInfo().getPoint()));
        viewCustomerRank.setText("Thành viên " + user.getMemberType());
        txtIntroducerCode.setText(user.getInvitationCode(), TextView.BufferType.EDITABLE);
    }
}