package com.lamphongstore.lamphong.activities;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.activities.abstracted.AbstractBaseActivity;
import com.lamphongstore.lamphong.adapter.AdsPagerAdapter;
import com.lamphongstore.lamphong.adapter.ListNotifsAdapter;
import com.lamphongstore.lamphong.data.AdsDataStore;
import com.lamphongstore.lamphong.data.ListNotifDataStore;
import com.lamphongstore.lamphong.data.UserDataStore;
import com.lamphongstore.lamphong.data.UserManager;
import com.lamphongstore.lamphong.material.WrapContentViewPager;
import com.lamphongstore.lamphong.model.AdsItem;
import com.lamphongstore.lamphong.model.LPError;
import com.lamphongstore.lamphong.model.NotificationItem;
import com.lamphongstore.lamphong.model.PointInfo;
import com.lamphongstore.lamphong.model.PromotionItem;
import com.lamphongstore.lamphong.model.User;
import com.liulishuo.magicprogresswidget.MagicProgressCircle;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.lamphongstore.lamphong.utils.LPFirebaseMessagingService.UPDATE_POINT;

public class MainActivity extends AbstractBaseActivity {

    private static final int TO_CHANGE_PROFILE = 10;
    private static final int TO_LOG_IN = 11;
    private static int POINT_START;

    public static boolean isCurrentlyLogIn = false;

    //DataAds
    AdsDataStore adsDataStore = new AdsDataStore();
    //DataNoti (noti + promotion)
    ListNotifDataStore listNotifDataStore = new ListNotifDataStore();
    //Check network
    ConnectivityManager connectivityManager;
    private MagicProgressCircle pointProgressCircle;
    private TextView currentPointText;
    private TextView requiredPointText;
    private WrapContentViewPager viewPager;
    private AdsPagerAdapter pageAdapter;
    private RelativeLayout promotionLayout;
    private TextView promotionTitle;
    private TextView promotionDescription;
    private TextView promotionDate;
    private TextView mFullName;
    private TextView memberType;
    private CircleImageView mainUserAvatar;
    private ListNotifsAdapter listNotifsAdapter;
    private SwipeRefreshLayout refreshLayout;
    private int currentPosition;
    private ArrayList<AdsItem> fakeList;
    private boolean firstScroll = true;
    private User currentUser;
    //pages control
    private int dotsCount;
    private LinearLayout linearLayout;
    private int currentPoint;
    private int nextLevelPoint;
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Objects.equals(intent.getAction(), UPDATE_POINT)) {
                updatePointRelatedViews();

            }
            ListNotifDataStore.getInstance().setListNotifListener(new ListNotifDataStore.ListNotifListener() {
                @Override
                public void onSuccess() {
                    populateNotifs();
                    notifyApp();
                }

                @Override
                public void onError() {

                }
            });
            ListNotifDataStore.getInstance().getListNotifs();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            UserManager.getInstance().loadUserFromLocal();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        registerReceiver(myReceiver, new IntentFilter(UPDATE_POINT));

        //check network
        connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                        isCurrentlyLogIn = false;
                        mainUserAvatar.setImageResource(R.drawable.ic_default_avatar);
                        drawerUserAvatar.setImageResource(R.drawable.ic_default_avatar);
                        gotoLoginActivity();
                        Toast.makeText(context, "Log out success", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(LPError error) {
                        showProgress(false);
                        logOutDialog.dismiss();
                        UserManager.getInstance().clear();
                        isCurrentlyLogIn = false;
                        mainUserAvatar.setImageResource(R.drawable.ic_default_avatar);
                        drawerUserAvatar.setImageResource(R.drawable.ic_default_avatar);
                        gotoLoginActivity();
                        Toast.makeText(context, "Log out success", Toast.LENGTH_SHORT).show();

                    }

                });
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!UserManager.getInstance().hasCurrentUser()) {
            gotoLoginActivity();
        } else if (!isCurrentlyLogIn) {
            isCurrentlyLogIn = true;

            // Display user cached from login
            populateUserInfo();
            loadData();
        }

    }

    @Override
    protected void initViews() {
        initViewsDrawerLayout();
        initUserViews();
        initAdsViewPager();
        initPromotionPanel();
        initViewsListNotifs();
    }

    @Override
    protected void initViewsDrawerLayout() {
        super.initViewsDrawerLayout();
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                drawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(Gravity.START);

                if (position == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }
                    }, DELAY_TIME);

                } else {
                    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                    if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch (position) {
                                    case 1:
                                        gotoAccountManager();
                                        break;
                                    case 2:
                                        gotoShareCode();
                                        break;
                                    case 3:
                                        gotoLamPhongStore();
                                        break;
                                    case 4:
                                        gotoPoint();
                                        break;
                                    case 5:
                                        gotoPromotion();
                                        break;
                                    case 6:
                                        logOutDialog.show();
                                        break;
                                }
                            }
                        }, DELAY_TIME);
                    } else {
                        showWarningDialog("Cảnh báo!!!", "Bạn đang không có kết nối internet");
                    }
                }
            }

        });
    }

    private void initUserViews() {
        //setup views
        currentPointText = (TextView) findViewById(R.id.current_point);
        requiredPointText = (TextView) findViewById(R.id.required_point_text);
        pointProgressCircle = (MagicProgressCircle) findViewById(R.id.progress_point);
        mFullName = (TextView) findViewById(R.id.main_name);
        mainUserAvatar = (CircleImageView) findViewById(R.id.main_avatar);
        memberType = (TextView) findViewById(R.id.main_rank);
        TextView textPoint = (TextView) findViewById(R.id.text_point);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        FloatingActionButton mButtonQrCode = (FloatingActionButton) findViewById(R.id.float_action_button);

        //setup fonts
        textPoint.setTypeface(mainFont);
        currentPointText.setTypeface(mainFont);
        requiredPointText.setTypeface(mainFont);
        mFullName.setTypeface(mainFont);
        memberType.setTypeface(mainFont);

        pointProgressCircle.setPercent(0);

        //event click
        mButtonQrCode.setOnClickListener(this);
        pointProgressCircle.setOnClickListener(this);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userDataStore.getUserFromAPI();
                loadData();
            }
        });

        refreshLayout.setColorSchemeColors(Color.parseColor("#DA3741"));

        //listener for getting user data from network
        userDataStore.setOnResponseListener(new UserDataStore.OnResponseListener() {
            @Override
            public void onContactServerSuccess() {

                populateUserInfo();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(LPError error) {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void initPromotionPanel() {
        //setup views
        promotionLayout = (RelativeLayout) findViewById(R.id.promotion);
        ImageView closePromotion = (ImageView) findViewById(R.id.promotion_close);
        promotionTitle = (TextView) findViewById(R.id.promotion_title);
        promotionDescription = (TextView) findViewById(R.id.promotion_des);
        promotionDate = (TextView) findViewById(R.id.promotion_date);

        //event click
        closePromotion.setOnClickListener(this);
    }

    private void initViewsListNotifs() {
        //setup Views
        RecyclerView listNotifsView = (RecyclerView) findViewById(R.id.list_notification);
        LinearLayoutManager listNotifsLayoutManager = new LinearLayoutManager(this);
        listNotifsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //setAdapter
        listNotifsAdapter = new ListNotifsAdapter(UserManager.getInstance().getNotificationsList(), this);
        listNotifsAdapter.setHasStableIds(true);
        listNotifsView.setLayoutManager(listNotifsLayoutManager);
        listNotifsView.setAdapter(listNotifsAdapter);

        listNotifDataStore.setListNotifListener(new ListNotifDataStore.ListNotifListener() {
            @Override
            public void onSuccess() {
                populateNotifs();
                populatePromotion();
            }

            @Override
            public void onError() {
                UserManager.getInstance().loadNotificationsFromLocal();
                UserManager.getInstance().loadPromotionFromLocal();
                populateNotifs();
                populatePromotion();

            }
        });

    }

    private void initAdsViewPager() {
        //setup views
        linearLayout = (LinearLayout) findViewById(R.id.page_count);
        viewPager = (WrapContentViewPager) findViewById(R.id.page_promotion);

        //setAdapter
        fakeList = new ArrayList<>();
        pageAdapter = new AdsPagerAdapter(fakeList, this);
        viewPager.setAdapter(pageAdapter);

        //autoScroll
        setAutoScrollPageViewer();

        //add page control
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (firstScroll) {
                    viewPager.setCurrentItem(1);
                    firstScroll = false;
                }
            }

            @Override
            public void onPageSelected(int position) {

                currentPosition = position;
                int pageCount = dotsCount + 2;
                if (position == 0) {
                    viewPager.setCurrentItem(pageCount - 2, false);
                } else if (position == pageCount - 1) {
                    viewPager.setCurrentItem(1, false);
                }
                if (position == 0) {
                    drawPageSelectionIndicators(pageCount - 3);
                } else if (position == pageCount - 1) {
                    drawPageSelectionIndicators(0);
                } else {
                    drawPageSelectionIndicators(position - 1);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        //listener for getting ads data from network
        adsDataStore.setAdsResultListener(new AdsDataStore.AdsResultListener() {
            @Override
            public void onSuccess() {
                populateAds();
            }

            @Override
            public void onError() {
                UserManager.getInstance().loadAdsListFromLocal();
                populateAds();
            }
        });
    }

    private void updatePointRelatedViews() {
        final PointInfo pointInfo = UserManager.getInstance().getUser().getPointInfo();
        final int newCurrentPoint = pointInfo.getPoint();
        final int newNextLevelPoint = pointInfo.getNextLevel();
        float newPercent = (float) newCurrentPoint / newNextLevelPoint;
        final int POINT_UP = newCurrentPoint - currentPoint;
        POINT_START = currentPoint;

        if (newNextLevelPoint != nextLevelPoint) {

            pointProgressCircle.setSmoothPercent(1, 1000);

            new CountDownTimer(1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    POINT_START = POINT_START + POINT_UP / 10;
                    currentPointText.setText(String.valueOf(POINT_START));

                }

                @Override
                public void onFinish() {
                    currentPointText.setText(String.valueOf(nextLevelPoint));
                    memberType.setText(pointInfo.getMemberType());
                    nextLevelPoint = newNextLevelPoint;
                    Toast.makeText(MainActivity.this, "Lên cmn level", Toast.LENGTH_SHORT).show();
                    POINT_START = 0;
                }
            }.start();

        }

        pointProgressCircle.setSmoothPercent(newPercent, 1000);

        new CountDownTimer(1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                POINT_START = POINT_START + POINT_UP / 10;
                currentPointText.setText(String.valueOf(POINT_START));

            }

            @Override
            public void onFinish() {
                currentPoint = newCurrentPoint;
                currentPointText.setText(String.valueOf(currentPoint));
                POINT_START = 0;
            }
        }.start();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Load data functions
    private void loadData() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            listNotifDataStore.getListNotifs();
            adsDataStore.getListAds();

        } else {
            showWarningDialog("Cảnh báo!!!", "Bạn đang không có kết nối internet");

            //load from cache
            UserManager.getInstance().loadPromotionFromLocal();
            UserManager.getInstance().loadNotificationsFromLocal();
            UserManager.getInstance().loadAdsListFromLocal();

            populatePromotion();
            populateNotifs();
            populateAds();
            refreshLayout.setRefreshing(false);
        }

    }

    private void populateUserInfo() {
        currentUser = UserManager.getInstance().getUser();

        mFullName.setText(currentUser.getFullname());
        memberType.setText(currentUser.getMemberType());

        drawerFullName.setText(currentUser.getFullname());

        if (UserManager.getInstance().getAvatar() != null) {
            mainUserAvatar.setImageBitmap(UserManager.getInstance().getAvatar());
            drawerUserAvatar.setImageBitmap(UserManager.getInstance().getAvatar());
        } else if (!UserManager.getInstance().getUser().getImage().getImgUrl().isEmpty()) {
            setImageFromUrl(UserManager.getInstance().getUser().getImage().getImgUrl());
        }

        //Point related views
        PointInfo pointInfo = currentUser.getPointInfo();
        currentPoint = pointInfo.getPoint();
        String nextLevelType = pointInfo.getNextLevelType();
        String needPoint = String.valueOf(pointInfo.getNeedPoint());
        nextLevelPoint = pointInfo.getNextLevel();

        float pointPercent = (float) currentPoint / nextLevelPoint;
        pointProgressCircle.setPercent(0);
        pointProgressCircle.setSmoothPercent(pointPercent, 1000);

        //Make a countdown for current point
        POINT_START = 0;
        new CountDownTimer(1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                POINT_START = POINT_START + currentPoint / 10;
                currentPointText.setText(String.valueOf(POINT_START));
            }

            @Override
            public void onFinish() {
                currentPointText.setText(String.valueOf(currentPoint));
            }
        }.start();

        Spanned desFormatHtml;
        if (currentPoint >= 2000) {
            desFormatHtml = Html.fromHtml("Bạn đã đạt thành viên"
                    + "<br/>" + "<b><font color=\"#FF0000\">Platinum</font></b>");
        } else {
            desFormatHtml = Html.fromHtml("Bạn cần " + "<b>" + "<font color=\"#000000\">" +
                    needPoint + " điểm" + "</font>" + "</b>"
                    + "<br/>" + "nữa để đạt"
                    + "<br/>" + "thành viên " + nextLevelType);
        }
        requiredPointText.setText(desFormatHtml);

    }

    private void populatePromotion() {
        if (UserManager.getInstance().getPromotionItem() != null && !UserManager.getInstance().getPromotionItem().isRead()) {
            PromotionItem promotionItem = UserManager.getInstance().getPromotionItem();
            promotionTitle.setText(promotionItem.getTitle());
            Spanned desPromotionFormat = Html.fromHtml(promotionItem.getDescription());
            promotionDescription.setTypeface(mainFont);
            promotionDescription.setText(desPromotionFormat);
            try {
                promotionDate.setText(promotionItem.getDate_to());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            promotionLayout.setVisibility(View.VISIBLE);
        }
    }

    private void populateNotifs() {

        listNotifsAdapter.notifyDataSetChanged();
    }

    private void populateAds() {
        if (UserManager.getInstance().getAdsList().size() != 0) {
            ArrayList<AdsItem> adsList = UserManager.getInstance().getAdsList();
            dotsCount = adsList.size();

            fakeList.add(0, adsList.get(dotsCount - 1));

            for (int j = 0; j < dotsCount; j++) {
                fakeList.add(j + 1, adsList.get(j));
            }
            fakeList.add(dotsCount + 1, adsList.get(0));

            pageAdapter.notifyDataSetChanged();
        } else {
            viewPager.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }
    }

    @Override
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
                        mainUserAvatar.setImageBitmap(response);
                        drawerUserAvatar.setImageBitmap(response);
                        UserManager.getInstance().setAvatar(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(MainActivity.this, "Error: " + anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    protected void gotoQrCode() {
        if (currentUser.getRole().equals("user")) {
            Intent intent = new Intent(MainActivity.this, GenerateQRActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (currentUser.getRole().equals("admin")) {
            Intent intent = new Intent(MainActivity.this, ScanQRActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    //setup page control
    private void drawPageSelectionIndicators(int mPosition) {
        if (linearLayout != null) {
            linearLayout.removeAllViews();
        }
        ImageView[] dots = new ImageView[dotsCount];
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            if (i == mPosition) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.page_selected));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.page_unselected));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(7, 0, 7, 0);
            linearLayout.addView(dots[i], params);
        }
    }

    //auto scroll after 3s
    private void setAutoScrollPageViewer() {
        final android.os.Handler handler = new android.os.Handler();
        final Runnable update = new Runnable() {
            public void run() {
                if (currentPosition == dotsCount + 1) {
                    currentPosition = 1;
                }
                viewPager.setCurrentItem(currentPosition++, true);
            }
        };

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 5000, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //clear receiver
        unregisterReceiver(myReceiver);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            isCurrentlyLogIn = false;
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.float_action_button:
                gotoQrCode();
                break;
            case R.id.promotion_close:
                initConfirmDialog();
                break;
            case R.id.header_avatar:
                drawerLayout.closeDrawer(Gravity.START);
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gotoAccountManager();
                        }
                    }, 120);
                } else {
                    showWarningDialog("Cảnh báo!!!", "Bạn đang không có kết nối internet");
                }
                break;
            case R.id.progress_point:
                gotoPoint();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TO_CHANGE_PROFILE && resultCode == RESULT_OK) {
            currentUser = UserManager.getInstance().getUser();

            mFullName.setText(currentUser.getFullname());
            drawerFullName.setText(UserManager.getInstance().getUser().getFullname());
            if (!currentUser.getImage().getImgUrl().isEmpty())
                setImageFromUrl(currentUser.getImage().getImgUrl());
            isCurrentlyLogIn = true;
        } else if (requestCode == TO_LOG_IN) {
            isCurrentlyLogIn = false;
            if (resultCode == RESULT_CANCELED)
                finish();

        }
    }

    @Override
    protected void gotoLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent,TO_LOG_IN);
    }

    private void notifyApp() {

        ArrayList<NotificationItem> arrayListNotifs = UserManager.getInstance().getNotificationsList();

        String content = arrayListNotifs.get(0).getContent();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icon_gift)
                        .setContentText(content)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, mBuilder.build());
    }

    private void initConfirmDialog() {
        final Dialog confirmDialogLayout = new Dialog(this);
        confirmDialogLayout.setContentView(R.layout.dialog_confirm_layout);
        TextView titleDialog = (TextView) confirmDialogLayout.findViewById(R.id.title_dialog_confirm);
        TextView contentDialog = (TextView) confirmDialogLayout.findViewById(R.id.content_dialog_confirm);
        Button confirmDialog = (Button) confirmDialogLayout.findViewById(R.id.confirm_button_dialog);
        Button cancelDialog = (Button) confirmDialogLayout.findViewById(R.id.cancel_button_dialog);
        if (confirmDialogLayout.getWindow() != null)
            confirmDialogLayout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        titleDialog.setText(R.string.dialog_delete_title);
        contentDialog.setText(R.string.dialog_delete_content);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManager.getInstance().getPromotionItem().setRead(true);
                UserManager.getInstance().cachePromotion();
                promotionLayout.setVisibility(View.GONE);
                confirmDialogLayout.dismiss();
            }
        });

        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialogLayout.dismiss();
            }
        });
        confirmDialogLayout.show();
    }

}
