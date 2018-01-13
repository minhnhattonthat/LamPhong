package com.lamphongstore.lamphong.activities.abstracted;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.activities.BranchOfficeActivity;
import com.lamphongstore.lamphong.activities.LoginActivity;
import com.lamphongstore.lamphong.activities.MainActivity;
import com.lamphongstore.lamphong.activities.ProfileActivity;
import com.lamphongstore.lamphong.activities.WebViewActivity;
import com.lamphongstore.lamphong.adapter.ListMenuAdapter;
import com.lamphongstore.lamphong.data.UserDataStore;
import com.lamphongstore.lamphong.data.UserManager;
import com.lamphongstore.lamphong.material.CustomSweetAlertDialog;
import com.lamphongstore.lamphong.material.ShareHelper;
import com.lamphongstore.lamphong.material.SideMenu;
import com.lamphongstore.lamphong.model.LPError;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_TASK_ON_HOME;


/**
 * Created by Norvia on 29/03/2017.
 */

public abstract class AbstractBaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected static final int TO_CHANGE_PROFILE = 10;
    protected static final long DELAY_TIME = 150;
    private static SharedPreferences sharedPreferences;
    // Declaring the Toolbar Object
    public Toolbar toolbar;
    //font
    public Typeface mainFont;
    protected TextView toolbarTitle;

    protected TextView drawerFullName;
    protected CircleImageView drawerUserAvatar;
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle drawerToggle;
    protected ListView drawerList;
    protected NavigationView navigationView;

    protected Dialog logOutDialog;
    protected Button confirmLogOutDialog;

    //-------- Sweet Dialog
    protected CustomSweetAlertDialog alertWarningDialog;
    protected CustomSweetAlertDialog alertErrorDialog;
    protected CustomSweetAlertDialog alertSuccessDialog;

    protected Context context;

    //Log out
    protected UserDataStore userDataStore = new UserDataStore();
    protected boolean needsDrawer = true;
    AlertDialog progressDialog;
    //facebook
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        if (needsDrawer) {
            setContentViewWithDrawer(getLayoutResourceId());
        } else {
            setContentView(getLayoutResourceId());
        }

        context = this;

        //for caching texts
        sharedPreferences = getSharedPreferences("user_state", Context.MODE_PRIVATE);

        //loading progressDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_progress_bar);
        builder.setCancelable(false);
        progressDialog = builder.create();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());

        mainFont = Typeface.createFromAsset(getAssets(), "fonts/SF-UI-Text-Regular.otf");

        initViews();
        initLogoutDialog();

        //Facebook -----------------------------
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
    }

    protected void setContentViewWithDrawer(int layoutResID) {
        setContentView(R.layout.activity_base_drawer_layout);

        FrameLayout activityContainer = (FrameLayout) findViewById(R.id.content_layout);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        toolbar = (Toolbar) findViewById(R.id.base_toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.drawer_side_menu);
        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.root_drawer_layout);
        View header = navigationView.getHeaderView(0);
        drawerFullName = (TextView) header.findViewById(R.id.header_name);
        drawerUserAvatar = (CircleImageView) header.findViewById(R.id.header_avatar);

        this.setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                @Override
                public void onDrawerClosed(final View drawerView) {
                    super.onDrawerClosed(drawerView);
                    supportInvalidateOptionsMenu();
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    supportInvalidateOptionsMenu();
                }
            };
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(drawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawerToggle.setDrawerIndicatorEnabled(false);
            drawerToggle.setHomeAsUpIndicator(R.drawable.ic_icon_hamburger);
        } else {
            drawerToggle.setDrawerIndicatorEnabled(true);
        }
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static void handleError(LPError error, Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            CustomSweetAlertDialog dialogCustom = new CustomSweetAlertDialog(context, CustomSweetAlertDialog.ERROR_TYPE);
            dialogCustom.setTitleText("Không thành công!");
            dialogCustom.setContentText(error.getShow());
            dialogCustom.show();
        } else {
            Toast.makeText(context, error.getShow(), Toast.LENGTH_SHORT).show();
        }

    }

    protected void showWarningDialog(String titleWarning, String contentWarning) {
        if (Build.VERSION.SDK_INT >= 21) {
            alertWarningDialog = new CustomSweetAlertDialog(context, CustomSweetAlertDialog.WARNING_TYPE);
            alertWarningDialog.setTitleText(titleWarning);
            alertWarningDialog.setContentText(contentWarning);
            alertWarningDialog.setConfirmClickListener(new CustomSweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(CustomSweetAlertDialog sweetAlertDialog) {
                    alertWarningDialog.dismiss();
                }
            });
            alertWarningDialog.show();
        } else {
            Toast.makeText(context, contentWarning, Toast.LENGTH_SHORT).show();
        }
    }

    protected void showErrorDialog(String titleError, String contentError) {
        if (Build.VERSION.SDK_INT >= 21) {
            alertErrorDialog = new CustomSweetAlertDialog(context, CustomSweetAlertDialog.ERROR_TYPE);
            alertErrorDialog.setTitleText(titleError);
            alertErrorDialog.setContentText(contentError);
            alertErrorDialog.show();
        } else {
            Toast.makeText(context, titleError, Toast.LENGTH_SHORT).show();
        }

    }

    protected void showSuccessDialog(String titleSuccess, String contentSuccess) {
        if (Build.VERSION.SDK_INT >= 21) {
            alertSuccessDialog = new CustomSweetAlertDialog(context, CustomSweetAlertDialog.SUCCESS_TYPE);
            alertSuccessDialog.setTitleText(titleSuccess);
            alertSuccessDialog.setContentText(contentSuccess);
            alertSuccessDialog.setConfirmClickListener(new CustomSweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(CustomSweetAlertDialog sweetAlertDialog) {
                    alertSuccessDialog.dismiss();
                }
            });
            alertSuccessDialog.show();
        } else {
            Toast.makeText(context, titleSuccess, Toast.LENGTH_SHORT).show();
        }

    }

    protected void setToolbarTitle(String title) {
        toolbarTitle.setText(title);
    }

    protected void gotoLoginActivity() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void shareUserCodeToAnotherApp() {
        String shareBody = "Mã giới thiệu của tôi là " + UserManager.getInstance().getUser().getUser_code();
        ShareHelper helper = new ShareHelper(this, "Chia sẻ mã giới thiệu", shareBody, shareDialog);
        helper.share();
    }

    protected void gotoShareCode() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.setFlags(FLAG_ACTIVITY_TASK_ON_HOME | FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("REDIRECT", "SHARE");
        startActivity(intent);
    }

    protected void gotoPoint() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.setFlags(FLAG_ACTIVITY_TASK_ON_HOME | FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("REDIRECT", "RULES");
        startActivity(intent);
    }

    protected void gotoPromotion() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.setFlags(FLAG_ACTIVITY_TASK_ON_HOME | FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("REDIRECT", "SALES");
        startActivity(intent);
    }

    protected void gotoAccountManager() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivityForResult(intent, TO_CHANGE_PROFILE);
    }

    protected void gotoLamPhongStore() {
        Intent intent = new Intent(this, BranchOfficeActivity.class);
        intent.setFlags(FLAG_ACTIVITY_TASK_ON_HOME | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected void initViewsDrawerLayout() {
        //setup views
        drawerList = (ListView) findViewById(R.id.drawer_menu);
        View header = navigationView.getHeaderView(0);
        drawerFullName = (TextView) header.findViewById(R.id.header_name);
        drawerUserAvatar = (CircleImageView) header.findViewById(R.id.header_avatar);
        //event click
        drawerUserAvatar.setOnClickListener(this);
        //set fonts
        drawerFullName.setTypeface(mainFont);

        ListMenuAdapter adapter = new ListMenuAdapter(this, SideMenu.SIDE_MENU_ITEM_LIST);
        drawerList.setAdapter(adapter);

    }

    protected void setImageFromUrl(String image_url) {
        AndroidNetworking.get(image_url)
                .setTag("image_request")
                .setPriority(Priority.HIGH)
                .setBitmapMaxHeight(400)
                .setBitmapMaxWidth(400)
                .setBitmapConfig(Bitmap.Config.ARGB_8888)
                .build()
                .getAsBitmap(new BitmapRequestListener() {
                    @Override
                    public void onResponse(Bitmap response) {
                        drawerUserAvatar.setImageBitmap(response);
                        UserManager.getInstance().setAvatar(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context, "Error: " + anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.e("HOME", "ABC");
                drawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (needsDrawer)
            drawerToggle.syncState();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (needsDrawer)
            drawerToggle.onConfigurationChanged(newConfig);
    }

    protected abstract int getLayoutResourceId();

    protected abstract void initViews();

    private void initLogoutDialog() {

        logOutDialog = new Dialog(this);
        logOutDialog.setContentView(R.layout.dialog_confirm_layout);
        TextView titleDialog = (TextView) logOutDialog.findViewById(R.id.title_dialog_confirm);
        TextView contentDialog = (TextView) logOutDialog.findViewById(R.id.content_dialog_confirm);
        confirmLogOutDialog = (Button) logOutDialog.findViewById(R.id.confirm_button_dialog);
        Button cancelDialog = (Button) logOutDialog.findViewById(R.id.cancel_button_dialog);
        if (logOutDialog.getWindow() != null)
            logOutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        titleDialog.setText(R.string.dialog_logout_title);
        contentDialog.setText(R.string.dialog_logout_content);
        confirmLogOutDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                logOutDialog.dismiss();
                //call directly from userManager
                userDataStore.deleteDeviceToken(new UserDataStore.OnResponseListener() {
                    @Override
                    public void onContactServerSuccess() {
                        showProgress(false);
                        UserManager.getInstance().clear();
                        MainActivity.isCurrentlyLogIn = false;
                        drawerUserAvatar.setImageResource(R.drawable.ic_default_avatar);
                        gotoLoginActivity();
                        Toast.makeText(context, "Log out success", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(LPError error) {
                        showProgress(false);
                        logOutDialog.dismiss();
                        UserManager.getInstance().clear();
                        MainActivity.isCurrentlyLogIn = false;
                        drawerUserAvatar.setImageResource(R.drawable.ic_default_avatar);
                        gotoLoginActivity();
                        Toast.makeText(context, "Log out success", Toast.LENGTH_SHORT).show();

                    }

                });
            }
        });

        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutDialog.dismiss();
            }
        });
    }

    protected void showProgress(final boolean show) {
        if (show && progressDialog.getWindow() != null) {
            progressDialog.show();
            progressDialog.getWindow().setLayout(350, 350);
        } else {
            progressDialog.cancel();
        }
    }

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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.header_avatar) {
            if (this instanceof MainActivity) {
                gotoAccountManager();

            } else if (this instanceof ProfileActivity) {
                drawerLayout.closeDrawer(GravityCompat.START);

            } else {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

}
