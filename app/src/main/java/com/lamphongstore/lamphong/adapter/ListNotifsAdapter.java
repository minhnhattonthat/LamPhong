package com.lamphongstore.lamphong.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.data.CallBackDelNoti;
import com.lamphongstore.lamphong.data.ListNotifDataStore;
import com.lamphongstore.lamphong.model.LPError;
import com.lamphongstore.lamphong.model.NotificationItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

/**
 * Created by HungNguyen on 4/3/17.
 */

public class ListNotifsAdapter extends RecyclerView.Adapter<ListNotifsAdapter.ListNotifsHolder> {

    private ArrayList<NotificationItem> mArrayList;
    private Context mContext;
    private Dialog confirmDialog;
    private Button dialogConfirmButton;

    public ListNotifsAdapter(ArrayList<NotificationItem> arrayList, Context mContext) {
        this.mArrayList = arrayList;
        this.mContext = mContext;

        confirmDialog = new Dialog(mContext);
        confirmDialog.setContentView(R.layout.dialog_confirm_layout);
        TextView titleDialog = (TextView) confirmDialog.findViewById(R.id.title_dialog_confirm);
        TextView contentDialog = (TextView) confirmDialog.findViewById(R.id.content_dialog_confirm);
        dialogConfirmButton = (Button) confirmDialog.findViewById(R.id.confirm_button_dialog);
        Button dialogCancelButton = (Button) confirmDialog.findViewById(R.id.cancel_button_dialog);

        titleDialog.setText("Xác Nhận!!!");
        contentDialog.setText("Bạn thật sự muốn xoá");

        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });
    }

    @Override
    public ListNotifsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View listNotifsView = inflater.inflate(R.layout.item_notification, parent, false);

        return new ListNotifsHolder(listNotifsView);
    }

    @Override
    public void onBindViewHolder(ListNotifsHolder holder, int position) {

        final NotificationItem notificationItem = mArrayList.get(position);
        Spanned desFormatHtml = Html.fromHtml(notificationItem.getPayloadContent());
        holder.getNotifContent().setText(desFormatHtml);

        try {
            holder.getNotifDate().setText(getTextFromTime(notificationItem.getCreated_at()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return mArrayList.get(position).getId();
    }

    class ListNotifsHolder extends RecyclerView.ViewHolder {

        private TextView notifContent;
        private TextView notifDate;
        private ImageView notifCloseButton;

        private TextView getNotifContent() {
            return notifContent;
        }

        private TextView getNotifDate() {
            return notifDate;
        }

        private ListNotifsHolder(View itemView) {
            super(itemView);
            notifContent = (TextView) itemView.findViewById(R.id.notification_des);
            notifDate = (TextView) itemView.findViewById(R.id.notification_date);
            notifCloseButton = (ImageView) itemView.findViewById(R.id.notification_close);

            notifCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogConfirmButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ListNotifDataStore.getInstance().deleteNotiFromList(getAdapterPosition(), new CallBackDelNoti() {
                                @Override
                                public void onSuccess() {
                                    Log.e("TestDelNoti", "Success");

                                    notifyDataSetChanged();
                                    confirmDialog.dismiss();
                                }

                                @Override
                                public void onError(LPError error) {
                                    Log.e("TestDelNoti", error.getMsg());
                                    Log.e("TestDelNoti", error.getDetail());
                                    Log.e("TestDelNoti", getItemId() + "");
                                    confirmDialog.dismiss();
                                }
                            });
                        }
                    });
                    confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    confirmDialog.show();
                }
            });

        }

    }

    private String getTextFromTime(String initDate) throws ParseException {
        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
        Date date = formatInput.parse(initDate);
        long timeSince = currentTimeMillis() / 1000 - date.getTime() / 1000;

        if (timeSince < 60) {
            return "Gần đây";
        } else if (timeSince < 3600) {
            long displayTimeSince = timeSince / 60;
            return "Cách đây " + displayTimeSince + " phút";
        } else if (timeSince < 86400) {
            long displayTimeSince = timeSince / 3600;
            return "Cách đây " + displayTimeSince + " giờ";
        } else {
            long displayTimeSince = timeSince / 86400;
            return "Cách đây " + displayTimeSince + " ngày";
        }
    }

}
