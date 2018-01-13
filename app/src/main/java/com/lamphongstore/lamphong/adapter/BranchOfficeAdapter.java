package com.lamphongstore.lamphong.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.model.ImageResource;
import com.lamphongstore.lamphong.model.Store;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bipug on 3/16/17.
 */

public class BranchOfficeAdapter extends RecyclerView.Adapter<BranchOfficeAdapter.ViewHolder>  {
    ArrayList<Store> dataStores;
    Context context;


    public BranchOfficeAdapter(ArrayList<Store> dataStores, Context context) {
        this.dataStores = dataStores;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.branch_office_item_row, parent, false);
        final TextView address = (TextView) itemView.findViewById(R.id.txtAddressBranchOffice);
        final TextView hotline = (TextView) itemView.findViewById(R.id.txtHotlineBranchOffice);
        hotline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Gọi điện")
                        .setMessage("Bạn có muốn gọi tới chi nhánh " + address.getText().toString() + " ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                onCall(hotline.getText().toString().trim());
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_action_phone)
                        .show();

            }
        });

        return new ViewHolder(itemView);
    }

    public void onCall(String phoneNumber) {
        Log.e("Adapter", phoneNumber);
        phoneNumber = phoneNumber.replaceFirst("[0]", "+84");
        phoneNumber = phoneNumber.replaceAll("Hotline: ", "");
        Log.e("Adapter phone number", phoneNumber);
        Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
        callIntent.setData(Uri.parse("tel:" + phoneNumber));    //this is the phone number calling
        //check permission
        //If the device is running Android 6.0 (API level 23) and the app's targetSdkVersion is 23 or higher,
        //the system asks the user to grant approval.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request permission from user if the app hasn't got the required permission
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                    10);
            return;
        } else {
            //have got permission
            try {
                context.startActivity(callIntent);  //call activity and make phone call
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(context, "Không tìm thấy hoạt động g", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txtPlace.setText(dataStores.get(position).getCity());
        holder.txtHotline.setText("Hotline: " + dataStores.get(position).getPhone());
        holder.txtAddress.setText(dataStores.get(position).getAddress());
        ImageResource image = dataStores.get(position).getImage();

        //--- List images from server
        final List<ImageResource> imageBrowserList = dataStores.get(position).getImages();


        AndroidNetworking.get(image.getImgUrl()).setTag("imageRequestTag")
                .setPriority(Priority.MEDIUM)
                .setBitmapMaxHeight(300)
                .setBitmapMaxWidth(300)
                .setBitmapConfig(Bitmap.Config.ARGB_8888)
                .build()
                .getAsBitmap(new BitmapRequestListener() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Drawable d = new BitmapDrawable(context.getResources(), response);
                        holder.placeImg.setBackground(d);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error: ", anError.getMessage());
                    }
                });
        if (imageBrowserList != null){
            holder.placeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ImageViewer.Builder(context,imageBrowserList)
                            .setFormatter(new ImageViewer.Formatter<ImageResource>() {
                                @Override
                                public String format(ImageResource imgResource) {
                                    return imgResource.getImgUrl();
                                }
                            })
                            .setStartPosition(0).show();
                }
            });
        }
        holder.txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmm = Uri.parse("geo:"+ dataStores.get(position).getLatitude() +", " +dataStores.get(position).getLongtitude() + "?q="+ dataStores.get(position).getLatitude() + ", "+ dataStores.get(position).getLongtitude()+ "("+ dataStores.get(position).getName() +")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmm);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataStores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPlace;
        TextView txtAddress;
        TextView txtHotline;
        ImageView placeImg;

        public ViewHolder(View itemView) {
            super(itemView);
            txtPlace = (TextView) itemView.findViewById(R.id.txtPlaceBranchOffice);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddressBranchOffice);
            txtHotline = (TextView) itemView.findViewById(R.id.txtHotlineBranchOffice);
            placeImg = (ImageView) itemView.findViewById(R.id.imageBranchOffice);
            Typeface sfNS = Typeface.createFromAsset(context.getAssets(), "fonts/SF-UI-Text-Regular.otf");
            Typeface sfNSbold = Typeface.createFromAsset(context.getAssets(), "fonts/SF-UI-Text-Bold.otf");
            Typeface sfNSMedium = Typeface.createFromAsset(context.getAssets(), "fonts/SF-UI-Text-Medium.otf");
            txtAddress.setTypeface(sfNS);
            txtHotline.setTypeface(sfNSMedium);
            txtPlace.setTypeface(sfNSMedium);

        }
    }
}
