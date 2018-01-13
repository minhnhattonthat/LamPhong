package com.lamphongstore.lamphong.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.lamphongstore.lamphong.ApiLamPhong;
import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.data.UserManager;
import com.lamphongstore.lamphong.material.BarcodeTrackerFactory;
import com.lamphongstore.lamphong.material.CameraSourcePreview;
import com.lamphongstore.lamphong.material.CustomSweetAlertDialog;
import com.lamphongstore.lamphong.material.GraphicOverlay;
import com.lamphongstore.lamphong.material.GraphicTracker;
import com.lamphongstore.lamphong.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class ScanQRActivity extends AppCompatActivity {

    private static final int RC_HANDLE_GMS = 9001;
    private final String TAG = "SCANQRACTIVITY";
    Toast toast;
    GraphicOverlay mGraphicOverlay;
    CameraSourcePreview mPreview;
    CameraSource cameraSource;
    BarcodeDetector detector;
    //-------
    Button btnInsertPoint;
    ImageView btnBack;
    TextInputEditText txtCustomerCodeManual;
    TextInputLayout customerCodeWrapper;
    RelativeLayout layout;
    //-----
    UserManager mUserManager;
    //-----
    CustomSweetAlertDialog pDialog;
    boolean checkCaptureQRCode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 200);
            onStop();
        }
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.graphicOverlay);

        if (Build.VERSION.SDK_INT >= 21) {
            setUpDialog();
        }

        mappingObject();
        layout.setVisibility(View.VISIBLE);
        setUpCameraSource();
        setButtonInsertPoint();
        setOnTouchLayout();

        checkCaptureQRCode = false;
        mUserManager = UserManager.getInstance();
    }

    private void setUpDialog() {
        pDialog = new CustomSweetAlertDialog(this, CustomSweetAlertDialog.ERROR_TYPE);

        pDialog.setConfirmClickListener(new CustomSweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(CustomSweetAlertDialog sweetAlertDialog) {
                pDialog.dismiss();
            }
        });
        pDialog.setTitleText("Kh!");
        pDialog.setContentText("Mã thành viên không tồn tại");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayToast("Kết nối camera thành công");
                    // permission was granted, yay! do the
                    // calendar task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(findViewById(android.R.id.content), "Hãy vào settings để cấp quyền !",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();
                }
            }
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private boolean doesUserHavePermission() {
        int result = this.checkCallingOrSelfPermission(Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void setOnBackClick() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void mappingObject() {
        btnInsertPoint = (Button) findViewById(R.id.btnInsertPoint);
        txtCustomerCodeManual = (TextInputEditText) findViewById(R.id.txtCustomerCodeManual);
        customerCodeWrapper = (TextInputLayout) findViewById(R.id.input_layout_customer_code_manual);
        btnBack = (ImageView) findViewById(R.id.btnBackScanQr);
        setOnBackClick();
        //---Force all caps
        txtCustomerCodeManual.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        layout = (RelativeLayout) findViewById(R.id.allScanRelativeLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (doesUserHavePermission()) {
            startCameraSource();
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

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public int checkRequiredField() {
        //0 is success , 1 is empty field , 2 is code < 6 , 3 cannot found on server
        if (txtCustomerCodeManual.getText().toString().trim().isEmpty()) {
            return 1;
        }
        if (txtCustomerCodeManual.getText().toString().length() < 6) {
            return 2;
        }
        return 0;
    }

    public void setOnTouchLayout() {
//        txtCustomerCodeManual.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                customerCodeWrapper.setError("");
//                txtCustomerCodeManual.setText("");
//                return false;
//            }
//        });
    }

    public void setButtonInsertPoint() {
        btnInsertPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                if (checkRequiredField() == 1) {
                    customerCodeWrapper.setError("Bạn chưa nhập ");
                } else if (checkRequiredField() == 2) {
                    customerCodeWrapper.setError("Độ dài mã khách hàng không đủ 6 ký tự");

                } else if (checkRequiredField() == 0) {
                    AndroidNetworking.post(ApiLamPhong.API_CURRENT_USER)
                            .addHeaders("Authorization", mUserManager.getUser().getToken())
                            .setContentType("application/x-www-form-urlencoded")
                            .addBodyParameter("user_code", txtCustomerCodeManual.getText().toString().trim())
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
                                            displayToast("Không tìm thấy mã khách hàng !");
                                        } else {
                                            customerCodeWrapper.setErrorEnabled(false);
                                            User userGetFromServer = new User(response);
                                            Intent intent = new Intent(ScanQRActivity.this, PointAccumulating.class);
                                            intent.putExtra("USERSCAN", userGetFromServer);
                                            startActivity(intent);
                                            customerCodeWrapper.setErrorEnabled(true);

                                        }

                                    } catch (JSONException e) {
                                        displayToast("Lỗi lấy dữ liệu " + e.getMessage());

                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    handleError(anError);
                                }
                            });
                }
            }
        });

    }

    public void handleError(ANError anError) {
        try {
            JSONObject fromString = new JSONObject(anError.getErrorBody());
            JSONObject error = fromString.getJSONObject("error");
            String message = error.getString("show");
            customerCodeWrapper.setError(message);
        } catch (JSONException e) {
            displayToast("Lỗi lấy dự liệu");
        }

    }

    public void startCameraSource() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null) {
            try {
                mPreview.start(cameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Không thế kết nối camera nguồn", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    public void displayToast(String message) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void setUpCameraSource() {
        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();
        if (!detector.isOperational()) {
            Snackbar.make(findViewById(android.R.id.content), "Không thế kết nối camera",
                    Snackbar.LENGTH_INDEFINITE).setAction("Trở lại",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }).show();
            return;
        }

        cameraSource = new CameraSource
                .Builder(this, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .setAutoFocusEnabled(true)
                .build();

        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, new GraphicTracker.Callback() {
            @Override
            public void onFound(final String barcodeValue) {
                if (checkCaptureQRCode) {
                    return;
                }
                //01284907915
                checkCaptureQRCode = true;
                Log.d(TAG, "Barcode in Multitracker = " + barcodeValue);
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                AndroidNetworking.post(ApiLamPhong.API_CHANGE_PROFILE)
                        .addHeaders("Authorization", mUserManager.getUser().getToken())
                        .setContentType("application/x-www-form-urlencoded")
                        .addBodyParameter("user_code", barcodeValue)
                        .setTag(this)
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean success = response.getBoolean("success");
                                    if (success) {
                                        User userGetFromServer = new User(response);
                                        Intent intent = new Intent(ScanQRActivity.this, PointAccumulating.class);
                                        intent.putExtra("USERSCAN", userGetFromServer);
                                        startActivity(intent);
                                    }
                                } catch (JSONException e) {
                                    displayToast("Lỗi lấy dự liệu " + e.getMessage());

                                }
                            }

                            @Override
                            public void onError(ANError anError) {
//                                 handleError(anError);
                                if (Build.VERSION.SDK_INT >= 21) {
                                    pDialog.show();
                                } else {
                                    displayToast("Mã thành viên không tồn tại");
                                }

                                Runnable r = new Runnable() {
                                    @Override
                                    public void run() {
                                        startCameraSource();
                                        checkCaptureQRCode = false;
                                    }
                                };

                                Handler h = new Handler();
                                h.postDelayed(r, 3000);
                                mPreview.stop();

                            }
                        });
            }
        });

        MultiProcessor<Barcode> barcodeMultiProcessor = new MultiProcessor.Builder<>(barcodeFactory).build();
        detector.setProcessor(barcodeMultiProcessor);
    }

}