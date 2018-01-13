package com.lamphongstore.lamphong.material;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.lamphongstore.lamphong.R;

import java.util.List;

/**
 * Created by bipug on 4/2/17.
 */

public class ShareHelper {
    Context context;
    String subject;
    String body;
    ShareDialog shareDialog;

    public ShareHelper(Context context, String subject, String body, ShareDialog shareDialog) {
        this.context = context;
        this.subject = subject;
        this.body = body;
        this.shareDialog = shareDialog;
    }

    public void share() {
        Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        List activities = context.getPackageManager().queryIntentActivities(sendIntent, 0);
        GridView gridView = new GridView(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog_card = builder.create();
        //---- Set alert dialog card
        dialog_card.getWindow().setGravity(Gravity.BOTTOM);
        dialog_card.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog_card.setTitle("Chia sẻ mã giới thiệu của bạn ..");
        dialog_card.getWindow().getAttributes().windowAnimations = R.anim.slide_up;

        //-----Create ShareIntentListAdapter
        final ShareIntentListAdapter adapter = new ShareIntentListAdapter((Activity) context, R.layout.share_message_list_item, activities.toArray());
        //----set GridView
        //calculate suitable column for every single device screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        float scalefactor = context.getResources().getDisplayMetrics().density * 100;
        int columns = (int) ((float) screenWidth / scalefactor) / 2;
        if (columns == 0 || columns == 1) columns = 2;
        //------------Set gridview
        gridView.setNumColumns(columns);
        gridView.setAdapter(adapter);
        gridView.setHorizontalSpacing(10);
        gridView.setVerticalSpacing(10);
        Animation animation_slide_up = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        GridLayoutAnimationController controller = new GridLayoutAnimationController(animation_slide_up, .2f, .2f);
        gridView.setLayoutAnimation(controller);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ResolveInfo info = (ResolveInfo) adapter.getItem(i);
                //Check if user choose facebook app
                if (info.activityInfo.packageName.contains("com.facebook.katana")) {
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent content = new ShareLinkContent.Builder()
                                .setContentDescription(body)
                                .setContentTitle(subject)
                                .setContentUrl(Uri.parse("http://lamphongstore.com"))
                                .build();
                        shareDialog.show(content);
                    }

                } else {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_TEXT, body);
                    ((Activity) context).startActivity(intent);
                }
            }
        });

        dialog_card.setView(gridView);
        dialog_card.show();
    }

}
