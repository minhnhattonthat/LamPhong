package com.lamphongstore.lamphong.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.activities.abstracted.AbstractBaseFormActivity;
import com.lamphongstore.lamphong.data.UserManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AbstractBaseFormActivity {

    private RadioButton maleButton;
    private RadioButton femaleButton;

    private Map<String, String> params;

    private boolean profileChanged;
    private AlertDialog saveDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateUserInformation();
    }

    @Override
    protected void initViews() {
        super.initViews();

        setToolbarTitle("Quản lý tài khoản");

        initViewsDrawerLayout();

        maleButton = (RadioButton) findViewById(R.id.input_gender_male);
        femaleButton = (RadioButton) findViewById(R.id.input_gender_female);

        Button changePasswordButton = (Button) findViewById(R.id.button_change_pasword);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChangePassword();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Lưu thay đổi?")
                .setNegativeButton("Huỷ bỏ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        setResult(RESULT_CANCELED);
                        if (drawerLayout.isDrawerOpen(Gravity.START)) {
                            saveDialog.dismiss();
                        } else {
                            finish();
                        }

                    }
                })
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        contactServer();
                    }
                });

        saveDialog = builder.create();

    }

    @Override
    protected void initViewsDrawerLayout() {
        super.initViewsDrawerLayout();
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                drawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(Gravity.START);

                if (position == 1) {
                    drawerLayout.closeDrawer(GravityCompat.START);

                } else if (position == 2){
                    gotoShareCode();

                } else if (checkProfileChanged() || getInstanceBitmap() != null) {
                    saveDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    switch (position) {
                                        case 0:
                                            finish();
                                            break;
                                        case 3:
                                            gotoLamPhongStore();
                                            finish();
                                            break;
                                        case 4:
                                            gotoPoint();
                                            finish();
                                            break;
                                        case 5:
                                            gotoPromotion();
                                            finish();
                                            break;
                                        case 6:
                                            logOutDialog.show();
                                            break;
                                    }
                                }
                            }, DELAY_TIME);
                        }
                    });
                    saveDialog.show();

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switch (position) {
                                case 0:
                                    finish();
                                    break;
                                case 3:
                                    gotoLamPhongStore();
                                    finish();
                                    break;
                                case 4:
                                    gotoPoint();
                                    finish();
                                    break;
                                case 5:
                                    gotoPromotion();
                                    finish();
                                    break;
                                case 6:
                                    logOutDialog.show();
                                    break;
                            }
                        }
                    }, DELAY_TIME);
                }
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }

    private void populateUserInformation() {

        if (UserManager.getInstance().getAvatar() != null) {
            getInputAvatar().setImageBitmap(UserManager.getInstance().getAvatar());
            drawerUserAvatar.setImageBitmap(UserManager.getInstance().getAvatar());
        }
        drawerFullName.setText(UserManager.getInstance().getUser().getFullname());
        inputName.setText(UserManager.getInstance().getUser().getFullname());
        inputEmail.setText(UserManager.getInstance().getUser().getEmail());
        inputPhone.setText(UserManager.getInstance().getUser().getPhone());
        inputBirthday.setText(UserManager.getInstance().getUser().getBirthday());
        inputAddress.setText(UserManager.getInstance().getUser().getAddress());
        if (Objects.equals(UserManager.getInstance().getUser().getGender(), "male")) {
            maleButton.setChecked(true);
        } else {
            femaleButton.setChecked(true);
        }
        if (UserManager.getInstance().getUser().getInvitationCode() != null) {
            inputRefCode.setText(UserManager.getInstance().getUser().getInvitationCode());
        }
    }

    private void gotoChangePassword() {
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }

    @Override
    protected void contactServer() {
        super.contactServer();

        if (profileChanged) {
            getUserDataStore().changeProfile(params);

        } else if (getInstanceBitmap() != null) {
            try {

                File file = File.createTempFile("profile", ".jpg", this.getCacheDir());
                FileOutputStream outputStream = new FileOutputStream(file);
                getInstanceBitmap().compress(Bitmap.CompressFormat.JPEG, 35, outputStream);

                getUserDataStore().uploadImage(file);
                clearInstaceBitmap();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error", e.getMessage());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                showProgress(true);
                if (checkProfileChanged() || getInstanceBitmap() != null) {
                    contactServer();
                } else {
                    onBackPressed();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    private boolean checkProfileChanged() {

        params = new HashMap<>();

        String fullname = inputName.getText().toString();
        String phone = inputPhone.getText().toString();
        String email = inputEmail.getText().toString();
        String birthday = inputBirthday.getText().toString();
        String address = inputAddress.getText().toString();
        String refCode = inputRefCode.getText().toString();

        if (!Objects.equals(fullname, UserManager.getInstance().getUser().getFullname())) {
            params.put("fullname", fullname);
            profileChanged = true;
        }
        if (!Objects.equals(email, UserManager.getInstance().getUser().getEmail())) {
            params.put("email", email);
            profileChanged = true;
        }
        if (!Objects.equals(phone, UserManager.getInstance().getUser().getPhone())) {
            params.put("phone", phone);
            profileChanged = true;
        }
        if (!Objects.equals(birthday, UserManager.getInstance().getUser().getBirthday())) {
            params.put("birthday", birthday);
            profileChanged = true;
        }
        if (!Objects.equals(address, UserManager.getInstance().getUser().getAddress())) {
            params.put("address", address);
            profileChanged = true;
        }
        if (maleButton.isChecked() && Objects.equals(UserManager.getInstance().getUser().getGender(), "female")) {
            params.put("gender", "male");
            profileChanged = true;
        }
        if (femaleButton.isChecked() && Objects.equals(UserManager.getInstance().getUser().getGender(), "male")) {
            params.put("gender", "female");
            profileChanged = true;
        }
        if (!Objects.equals(refCode, UserManager.getInstance().getUser().getInvitationCode())) {
            params.put("invitation_code", refCode);
            profileChanged = true;
        }
        return profileChanged;
    }

    @Override
    public void onBackPressed() {
        if (checkProfileChanged() || getInstanceBitmap() != null) {
            saveDialog.show();

        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onContactServerSuccess() {
        if (getInstanceBitmap() != null) {
            try {
                File file = File.createTempFile("profile", ".jpg", this.getCacheDir());
                FileOutputStream outputStream = new FileOutputStream(file);
                getInstanceBitmap().compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream);

                getUserDataStore().uploadImage(file);
                clearInstaceBitmap();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error", e.getMessage());
            }
        }

        setResult(RESULT_OK);
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            saveDialog.dismiss();
        } else {
            finish();
        }

    }
}
