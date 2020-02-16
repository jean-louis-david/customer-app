package com.app85taxi.passenger;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.adapter.files.ViewPagerAdapter;
import com.fragments.BookingFragment;
import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.MTextView;
import com.view.MaterialTabs;

import java.util.ArrayList;

public class MyBookingsActivity extends AppCompatActivity {

    MTextView titleTxt;
    ImageView backImgView;

    public GeneralFunctions generalFunc;

    String userProfileJson;
    CharSequence[] titles;
    String app_type = "Ride";
    boolean isrestart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        generalFunc = new GeneralFunctions(getActContext());

        userProfileJson = getIntent().getStringExtra("UserProfileJson");
        isrestart = getIntent().getBooleanExtra("isrestart", false);

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);

        backImgView.setOnClickListener(new setOnClickList());
        setLabels();

        ViewPager appLogin_view_pager = (ViewPager) findViewById(R.id.appLogin_view_pager);
        MaterialTabs material_tabs = (MaterialTabs) findViewById(R.id.material_tabs);

        app_type = generalFunc.getJsonValue("APP_TYPE", userProfileJson);
        ArrayList<Fragment> fragmentList = new ArrayList<>();

        titles = new CharSequence[]{generalFunc.retrieveLangLBl("", "LBL_RIDE")};
        fragmentList.add(generateBookingFrag(Utils.CabGeneralType_Ride));
        material_tabs.setVisibility(View.GONE);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, fragmentList);
        appLogin_view_pager.setAdapter(adapter);
        material_tabs.setViewPager(appLogin_view_pager);
    }

    public BookingFragment generateBookingFrag(String bookingType) {
        BookingFragment frag = new BookingFragment();
        Bundle bn = new Bundle();
        bn.putString("BOOKING_TYPE", bookingType);
        frag.setArguments(bn);
        return frag;
    }

    public void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MY_BOOKINGS"));
    }

    @Override
    protected void onResume() {

        app_type = generalFunc.getJsonValue("APP_TYPE", userProfileJson);
        super.onResume();
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    if (isrestart) {

                    } else {
                        MyBookingsActivity.super.onBackPressed();
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isrestart) {

        } else {
            super.onBackPressed();
        }
    }

    public Context getActContext() {
        return MyBookingsActivity.this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
