package com.lamphongstore.lamphong.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.data.UserManager;
import com.lamphongstore.lamphong.material.ShareHelper;

public class GenerateQRActivity extends AppCompatActivity {
    TextView viewScanQRCode, viewCustomerInstruction, viewEmployeeInstruction, viewCustomerCode;
    ImageView qrCode, back,shareIcon;
    Toast toast;
    ShareHelper shareHelper;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);
        initViews();
        setOnClickButtonBack();
        UserManager mUserManager = UserManager.getInstance();
        if (mUserManager != null) {
            generateQRCode(mUserManager.getUser().getUser_code());
            viewCustomerCode.setText(mUserManager.getUser().getUser_code());
        } else {
            displayToast("Lỗi tạo hình, có gì đó đã xảy ra!");
            gotoLoginActivity();
            finish();
        }

    }


    private void gotoLoginActivity() {
        Intent intent = new Intent(GenerateQRActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void setOnClickButtonBack() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GenerateQRActivity.super.onBackPressed();
            }
        });
    }

    public void displayToast(String message) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void initViews() {
        Typeface sfNS = Typeface.createFromAsset(getAssets(), "fonts/SF-UI-Text-Regular.otf");
        Typeface sfNSbold = Typeface.createFromAsset(getAssets(), "fonts/SF-UI-Text-Bold.otf");
        viewScanQRCode = (TextView) findViewById(R.id.viewScanQRCode);
        viewCustomerCode = (TextView) findViewById(R.id.viewCustomerCode);
        viewCustomerInstruction = (TextView) findViewById(R.id.viewCustomerInstruction);
        viewEmployeeInstruction = (TextView) findViewById(R.id.viewEmployeeInstruction);
        shareIcon = (ImageView) findViewById(R.id.share_preference_code_image);
        viewCustomerInstruction.setTypeface(sfNS);
        viewCustomerCode.setTypeface(sfNSbold);
        viewEmployeeInstruction.setTypeface(sfNS);
        viewScanQRCode.setTypeface(sfNSbold);
        qrCode = (ImageView) findViewById(R.id.qrCode);
        back = (ImageView) findViewById(R.id.btnBack);
        //Facebook -----------------------------
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        String shareBody = "Mã giới thiệu của tôi là " + UserManager.getInstance().getUser().getUser_code();
        shareHelper = new ShareHelper(this, "Chia sẻ mã giới thiệu", shareBody, shareDialog);
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareHelper.share();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    public void generateQRCode(String s) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(s, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bmp = barcodeEncoder.createBitmap(bitMatrix);
            qrCode.setImageBitmap(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
