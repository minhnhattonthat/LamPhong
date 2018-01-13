package com.lamphongstore.lamphong.activities.abstracted;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.data.UserDataStore;
import com.lamphongstore.lamphong.material.DatePickerFragment;
import com.lamphongstore.lamphong.material.LPEditText;
import com.lamphongstore.lamphong.model.LPError;
import com.lamphongstore.lamphong.utils.ImgUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.InputType.TYPE_NULL;
import static android.util.Patterns.EMAIL_ADDRESS;

/**
 * Created by Norvia on 19/03/2017.
 */

public abstract class AbstractBaseFormActivity extends AbstractBaseActivity implements
        DatePickerDialog.OnDateSetListener, UserDataStore.OnResponseListener {

    protected static final int IMAGE_QUALITY = 35;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_LIBRARY = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 5;
    private static final int REQUEST_STORAGE_PERMISSION = 6;
    protected Vibrator vib;
    protected Animation animShake;
    protected TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPhone,
            inputLayoutBirthDay, inputLayoutAddress, inputLayoutRefCode;
    protected LPEditText inputName, inputEmail, inputPhone, inputBirthday,
            inputAddress, inputRefCode;
    protected RadioGroup inputGender;
    protected CircleImageView inputAvatar;
    protected Bitmap instanceBitmap;
    private String birthDateFormatted;
    private int mYear;
    private int mMonth;
    private int mDay;
    private DatePickerFragment datePicker;
    private UserDataStore mUserDataStore;
    private String mCurrentPhotoPath;

    public static boolean isValidPhoneNumber(String phone) {
        return (phone.startsWith("01") && phone.length() == 11)
                || ((phone.startsWith("09") || phone.startsWith("08")) && phone.length() == 10);
    }

    public static boolean isValidEmail(String email) {
        return email.contains(" ") && EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return !(password.trim().isEmpty() || password.contains(" ") || password.length() < 6);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        mUserDataStore = new UserDataStore();
        mUserDataStore.setOnResponseListener(this);
    }

    protected void initViews() {
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);
        inputLayoutBirthDay = (TextInputLayout) findViewById(R.id.input_layout_birthday);
        inputLayoutAddress = (TextInputLayout) findViewById(R.id.input_layout_address);

        inputLayoutRefCode = (TextInputLayout) findViewById(R.id.input_layout_refcode);

        inputName = (LPEditText) findViewById(R.id.input_name);
        inputEmail = (LPEditText) findViewById(R.id.input_email);
        inputPhone = (LPEditText) findViewById(R.id.input_phone);
        inputBirthday = (LPEditText) findViewById(R.id.input_birthday);
        inputAddress = (LPEditText) findViewById(R.id.input_address);
        inputGender = (RadioGroup) findViewById(R.id.input_gender);
        inputRefCode = (LPEditText) findViewById(R.id.input_refcode);

        inputAvatar = (CircleImageView) findViewById(R.id.input_avatar);

        registerForContextMenu(inputAvatar);
        inputAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContextMenu(v);
            }
        });

        inputBirthday.setInputType(TYPE_NULL);
        inputBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inputBirthday.hasFocus()) {
                    inputBirthday.requestFocus();
                } else {
                    showDatePickerDialog();
                }

            }
        });

        inputBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog();
                    v.clearFocus();
                }
            }
        });

        TextView genderText = (TextView) findViewById(R.id.gender_text_view);
        genderText.setTypeface(mainFont);
    }

    protected void contactServer() {
        showProgress(true);
    }

    protected boolean checkInputs() {
        if (!checkName()) {
            inputName.setAnimation(animShake);
            inputName.startAnimation(animShake);
            vib.vibrate(200);
            return false;
        }
        if (!checkEmail()) {
            inputEmail.setAnimation(animShake);
            inputEmail.startAnimation(animShake);
            vib.vibrate(200);
            return false;
        }
        if (!checkPhone(inputPhone, inputLayoutPhone)) {
            inputPhone.setAnimation(animShake);
            inputPhone.startAnimation(animShake);
            vib.vibrate(200);
            return false;
        }
        if (!checkBirthDay()) {
            inputBirthday.setAnimation(animShake);
            inputBirthday.startAnimation(animShake);
            vib.vibrate(200);
            return false;
        }
        if (!checkAddress()) {
            inputAddress.setAnimation(animShake);
            inputAddress.startAnimation(animShake);
            vib.vibrate(200);
            return false;
        }
        if (!checkGender()) {
            inputGender.setAnimation(animShake);
            inputGender.startAnimation(animShake);
            vib.vibrate(200);
            return false;
        }
        if (!checkRefCode()) {
            inputRefCode.setAnimation(animShake);
            inputRefCode.startAnimation(animShake);
            vib.vibrate(200);
            return false;
        }

        inputLayoutName.setErrorEnabled(false);
        inputLayoutEmail.setErrorEnabled(false);
        inputLayoutPhone.setErrorEnabled(false);
        inputLayoutBirthDay.setErrorEnabled(false);
        inputLayoutAddress.setErrorEnabled(false);

        inputLayoutRefCode.setErrorEnabled(false);

        return true;
    }

    private boolean checkName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setErrorEnabled(true);
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
            return true;
        }
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

    public boolean checkPhone(LPEditText inputPhone, TextInputLayout inputLayoutPhone) {
        String phone = inputPhone.getText().toString().trim();

        if (isValidPhoneNumber(phone)) {
            inputLayoutPhone.setErrorEnabled(false);
            return true;
        } else {
            inputLayoutPhone.setErrorEnabled(true);
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(inputPhone);
            return false;
        }
    }

    private boolean checkBirthDay() {

        if (inputBirthday.getText().toString().isEmpty()) {
            inputLayoutBirthDay.setErrorEnabled(true);
            inputLayoutBirthDay.setError(getString(R.string.err_msg_birthday));
            requestFocus(inputBirthday);
            return false;
        } else {
            inputLayoutBirthDay.setErrorEnabled(false);
            return true;
        }
    }

    private boolean checkAddress() {
        if (inputAddress.getText().toString().isEmpty()) {
            inputLayoutAddress.setErrorEnabled(true);
            inputLayoutAddress.setError(getString(R.string.err_msg_address));
            requestFocus(inputAddress);
            return false;
        } else {
            inputLayoutAddress.setErrorEnabled(false);
            return true;
        }
    }

    private boolean checkGender() {
        return inputGender.getCheckedRadioButtonId() != -1;
    }

    private boolean checkRefCode() {
        String refCode = inputRefCode.getText().toString();
        if (!refCode.isEmpty() && refCode.length() != 6) {
            inputLayoutRefCode.setErrorEnabled(true);
            inputLayoutRefCode.setError(getString(R.string.err_msg_inviationcode));
            requestFocus(inputRefCode);
            return false;
        } else {
            inputLayoutRefCode.setErrorEnabled(false);
            return true;
        }
    }

    public boolean checkPassword(LPEditText inputPassword, TextInputLayout inputLayoutPassword) {
        String password = inputPassword.getText().toString();
        if (isValidPassword(password)) {
            inputLayoutPassword.setErrorEnabled(false);
            return true;
        } else {
            inputLayoutPassword.setErrorEnabled(true);
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.avatar_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_camera:

                //Check camera permission
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION);

                } else {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(this,
                                    "com.lamphongstore.lamphong.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                        }
                    }

                }
                break;

            case R.id.action_library:

                //Check external storage permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_STORAGE_PERMISSION);

                } else {
                    Intent intentLibrary = new Intent(Intent.ACTION_GET_CONTENT);
                    intentLibrary.addCategory(Intent.CATEGORY_OPENABLE);
                    intentLibrary.setType("image/*");
                    intentLibrary.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(intentLibrary, "Select Picture"),
                            REQUEST_LIBRARY);
                    return true;
                }
                break;
            case R.id.action_cancel:
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {

                galleryAddPic();
                int maxWidth = inputAvatar.getWidth();
                int maxHeight = inputAvatar.getHeight();
                instanceBitmap = ImgUtils.compressImageFromPath(mCurrentPhotoPath, maxWidth, maxHeight);
                inputAvatar.setImageBitmap(instanceBitmap);
            } else if (requestCode == REQUEST_LIBRARY) {

                if (data != null) {
                    Uri uri = data.getData();
                    String filePath = null;
                    try {
                        filePath = ImgUtils.getFilePath(this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    int maxHeight = inputAvatar.getHeight();
                    int maxWidth = inputAvatar.getWidth();
                    instanceBitmap = ImgUtils.compressImageFromPath(filePath, maxWidth, maxHeight);
                    inputAvatar.setImageBitmap(instanceBitmap);
                }

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.lamphongstore.lamphong.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                    }
                }

            } else {
                Toast.makeText(getContext(), "Please allow Camera permission to use this feature",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentLibrary = new Intent(Intent.ACTION_GET_CONTENT);
                intentLibrary.addCategory(Intent.CATEGORY_OPENABLE);
                intentLibrary.setType("image/*");
                intentLibrary.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intentLibrary, "Select Picture"),
                        REQUEST_LIBRARY);
            }
        }

    }

    private Context getContext() {
        return this;
    }

    public CircleImageView getInputAvatar() {
        return inputAvatar;
    }

    public Bitmap getInstanceBitmap() {
        return instanceBitmap;
    }

    public void clearInstaceBitmap() {
        this.instanceBitmap = null;
    }

    public String getBirthDateFormatted() {
        return birthDateFormatted;
    }

    public void showDatePickerDialog() {

        if (datePicker == null) {
            datePicker = new DatePickerFragment();
        } else {
            datePicker.setSelectedDate(mYear, mMonth, mDay);
        }
        datePicker.show(getFragmentManager(), "datePicker");

    }

    public UserDataStore getUserDataStore() {
        return mUserDataStore;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mYear = year;
        mMonth = month;
        mDay = dayOfMonth;
        month++;
        if (month < 10) {
            birthDateFormatted = year + "-0" + month + "-" + dayOfMonth;
            inputBirthday.setText(dayOfMonth + " - 0" + month + " - " + year);
        } else {
            birthDateFormatted = year + "-" + month + "-" + dayOfMonth;
            inputBirthday.setText(dayOfMonth + " - " + month + " - " + year);
        }
        requestFocus(inputAddress);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (instanceBitmap != null) {
            outState.putParcelable("instance", instanceBitmap);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Bitmap bitmap = savedInstanceState.getParcelable("instance");
        if (bitmap != null) {
            instanceBitmap = bitmap;
            inputAvatar.setImageBitmap(bitmap);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (needsDrawer) {
            super.onBackPressed();
        }
        setResult(RESULT_CANCELED);
        finish();
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
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        setResult(RESULT_OK);
        finish();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onError(LPError error) {
        showProgress(false);
        handleError(error, getContext());
    }

    @Override
    public void onClick(View v) {
        if (needsDrawer)
            super.onClick(v);
    }

}
