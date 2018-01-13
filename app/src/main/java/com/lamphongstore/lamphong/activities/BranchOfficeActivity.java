package com.lamphongstore.lamphong.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.activities.abstracted.AbstractBaseActivity;
import com.lamphongstore.lamphong.adapter.BranchOfficeAdapter;
import com.lamphongstore.lamphong.data.UserManager;
import com.lamphongstore.lamphong.model.LPError;
import com.lamphongstore.lamphong.model.Store;
import com.lamphongstore.lamphong.data.StoreDataStore;


import java.util.ArrayList;

public class BranchOfficeActivity extends AbstractBaseActivity {
    RecyclerView recyclerView;
    ArrayList<Store> listStore;
    private StoreDataStore storeDataStore;
    private final String TAG = BranchOfficeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeDataStore = new StoreDataStore();
        listStore = new ArrayList<>();
        populateUserInformation();
        //Init menu

        initViews();

        Log.d(TAG, "GET Data Network");
        initialDataFromNetworking();


    }


    private void populateUserInformation() {

        if (UserManager.getInstance().getAvatar() != null) {
            drawerUserAvatar.setImageBitmap(UserManager.getInstance().getAvatar());
        }
        drawerFullName.setText(UserManager.getInstance().getUser().getFullname());
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
                                gotoShareCode();
                                break;
                            case 3:
                                drawerLayout.closeDrawer(GravityCompat.START);
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
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_branch_office;
    }

    @Override
    protected void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.branch_office_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
        marginLayoutParams.setMargins(0, 10, 0, 0);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutParams(marginLayoutParams);
        //------ Get JSON
        Fresco.initialize(this);
        initViewsDrawerLayout();


    }


    public void initialDataFromNetworking() {
        storeDataStore.getDataFromServer(new StoreDataStore.OnResponseListener() {
            @Override
            public void onSuccess(ArrayList<Store> storeList) {
                BranchOfficeAdapter adapterBranchOffice = new BranchOfficeAdapter(storeList, BranchOfficeActivity.this);
                recyclerView.setAdapter(adapterBranchOffice);
            }

            @Override
            public void onError(LPError lpError) {
                Toast.makeText(BranchOfficeActivity.this, lpError.getShow(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TO_CHANGE_PROFILE && resultCode == RESULT_OK){
            drawerFullName.setText(UserManager.getInstance().getUser().getFullname());
            if(!UserManager.getInstance().getUser().getImage().getImgUrl().isEmpty())
                setImageFromUrl(UserManager.getInstance().getUser().getImage().getImgUrl());
            MainActivity.isCurrentlyLogIn = false;
        }
    }

}