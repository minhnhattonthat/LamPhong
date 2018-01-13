package com.lamphongstore.lamphong.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;

import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.activities.abstracted.AbstractBaseActivity;
import com.lamphongstore.lamphong.data.UserManager;

import static com.lamphongstore.lamphong.ApiLamPhong.RULE_MEMBER_INFORMATION;
import static com.lamphongstore.lamphong.ApiLamPhong.SALES_INFORMATION;
import static com.lamphongstore.lamphong.ApiLamPhong.SHARE_CODE_INFORMATION;


public class WebViewActivity extends AbstractBaseActivity {
    private final String TAG = "WebViewActivity";
    String url = "";
    WebView mWebView;
    ProgressBar loading;
    Handler myHandler;
    Button shareButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareButton = (Button) findViewById(R.id.button_share_code);
        if (getIntent() != null && getIntent().getExtras() != null) {
            String extra = getIntent().getStringExtra("REDIRECT");
            if (extra.equalsIgnoreCase("RULES")) {
                url = RULE_MEMBER_INFORMATION;
                shareButton.setVisibility(View.GONE);
            } else if (extra.equalsIgnoreCase("SALES")) {
                url = SALES_INFORMATION;
                shareButton.setVisibility(View.GONE);
            } else {
                url = SHARE_CODE_INFORMATION;
                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareUserCodeToAnotherApp();
                    }
                });
            }
        }

        //---
        populateUserInformation();
        initViews();
        //-----
        myHandler = new Handler();
        showProgress(true);
        //---
        if (!isNetworkAvailable()) {
            showWarningDialog("Không có mạng", "Không tìm thấy kết nối mạng!");
            showProgress(false);
            mWebView.setVisibility(View.GONE);
        } else {
            //-------------
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.setWebViewClient(new WebViewClient() {

                boolean timeout = true;


                @SuppressWarnings("deprecation")
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    // Handle the error
                    showErrorDialog(String.valueOf(errorCode), description);
                    showProgress(false);
                    view.loadUrl("about:blank");
                }


                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
                    super.onReceivedError(view, request, error);

                }


                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    mWebView.setVisibility(View.GONE);
                    showProgress(true);
                    Runnable run = new Runnable() {
                        @Override
                        public void run() {
                            if (timeout) {
                                showWarningDialog("Kết nối thất bại !", "Kết nối lâu hơn bình thường");
                                mWebView.setVisibility(View.GONE);
                                showProgress(false);
                                mWebView.stopLoading();
                            }
                        }
                    };
                    Handler myHandler = new Handler(Looper.myLooper());
                    myHandler.postDelayed(run, 5000);
                }


                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    timeout = false;
                    showProgress(false);
                    mWebView.loadUrl("javascript:(function() { " +
                            "document.querySelector('header').innerHTML = ''; " +
                            "document.querySelector('footer').innerHTML = ''; " +
                            "document.querySelector('#column-right').style.display = 'none'; " +
                            "})()");
                    mWebView.loadUrl("javascript:(function() { $(document).ready(function() { $('.breadcrumb').css('display','none');  }); })()");
                    mWebView.loadUrl("javascript:(function() { $(document).ready(function() { $('#uhchatmobile').css('display','none');  }); })()");
                    mWebView.loadUrl("javascript:(function() { $(document).ready(function() { $('#uhchat').css('display','none');  }); })()");
                    mWebView.loadUrl("javascript:(function() { $(document).ready(function() { $('.journal-blog-feed').css('display','none');  }); })()");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.post(new Runnable() {
                                @Override
                                public void run() {
                                    showButton();
                                    mWebView.setVisibility(View.VISIBLE);
                                    showProgress(false);

                                }
                            });
                        }

                    }, 150);


                }


                @TargetApi(Build.VERSION_CODES.N)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    final Uri uri = request.getUrl();
                    return handleUri(uri);
                }

                private boolean handleUri(final Uri uri) {
                    Log.i(TAG, "Uri =" + uri);

                    final String host = uri.getHost();
                    // Based on some condition you need to determine if you are going to load the url
                    // in your web view itself or in a browser.
                    // You can use `host` or `scheme` or any part of the `uri` to decide.
                    if (host.contains("lamphong")) {
                        // Returning false means that you are going to load this url in the webView itself
                        mWebView.setVisibility(View.GONE);
                        showProgress(true);
                        return false;
                    } else {
                        // Returning true means that you need to handle what to do with the url
                        // e.g. open web page in a Browser
                        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        return true;
                    }
                }
            });
            mWebView.loadUrl(url);

        }
    }

    private void showButton() {
        if (url.contains("share")) {
            shareButton.setVisibility(View.VISIBLE);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void populateUserInformation() {

        if (UserManager.getInstance().getAvatar() != null) {
            drawerUserAvatar.setImageBitmap(UserManager.getInstance().getAvatar());
        }
        drawerFullName.setText(UserManager.getInstance().getUser().getFullname());
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void initViewsDrawerLayout() {
        super.initViewsDrawerLayout();
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                drawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(Gravity.START);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (position) {
                            case 0:
                                finish();
                                break;
                            case 1:
                                gotoAccountManager();
                                finish();
                                break;
                            case 2:
                                webViewRedirectToShare();
                                break;
                            case 3:
                                gotoLamPhongStore();
                                finish();
                                break;
                            case 4:
                                webViewRedirectToPoint();
                                break;
                            case 5:
                                webViewRedirectToPromotion();
                                break;
                            case 6:
                                logOutDialog.show();
                                break;
                        }
                    }
                }, DELAY_TIME);

            }
        });


    }

    private void webViewRedirectToShare() {
        if (url.contains("share")) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            gotoShareCode();
            finish();
        }
    }

    public void webViewRedirectToPoint() {
        if (url.contains("rules")) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            gotoPoint();
            finish();
        }
    }

    public void webViewRedirectToPromotion() {
        if (url.contains("su-kien-khuyen-mai")) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            gotoPromotion();
            finish();
        }
    }

    @Override
    protected void initViews() {

        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        loading = (ProgressBar) findViewById(R.id.web_loading);
        initViewsDrawerLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TO_CHANGE_PROFILE && resultCode == RESULT_OK) {
            drawerFullName.setText(UserManager.getInstance().getUser().getFullname());
            if (!UserManager.getInstance().getUser().getImage().getImgUrl().isEmpty())
                setImageFromUrl(UserManager.getInstance().getUser().getImage().getImgUrl());
            MainActivity.isCurrentlyLogIn = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}