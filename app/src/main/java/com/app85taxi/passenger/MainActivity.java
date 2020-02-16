package com.app85taxi.passenger;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.datepicker.files.SlideDateTimeListener;
import com.datepicker.files.SlideDateTimePicker;
import com.dialogs.RequestNearestCab;
import com.fragments.CabSelectionFragment;
import com.fragments.DriverAssignedHeaderFragment;
import com.fragments.DriverDetailFragment;
import com.fragments.MainHeaderFragment;
import com.fragments.RequestPickUpFragment;
import com.general.files.AddDrawer;
import com.general.files.ConfigPubNub;
import com.general.files.CreateAnimation;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GcmBroadCastReceiver;
import com.general.files.GeneralFunctions;
import com.general.files.GetAddressFromLocation;
import com.general.files.GetLocationUpdates;
import com.general.files.HashMapComparator;
import com.general.files.LoadAvailableCab;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.general.files.UpdateFrequentTask;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.anim.loader.AVLoadingIndicatorView;
import com.view.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.utils.Utils.CabGeneralType_Deliver;
import static com.utils.Utils.generateNotification;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GetLocationUpdates.LocationUpdates {

    public GeneralFunctions generalFunc;
    public String userProfileJson = "";
    public String currentGeoCodeObject = "";
    public SlidingUpPanelLayout sliding_layout;
    public ImageView userLocBtnImgView;
    public Location userLocation;
    public ArrayList<HashMap<String, String>> currentLoadedDriverList;
    public ImageView emeTapImgView;
    //    public String app_type = "";
    public ConfigPubNub configPubNub;
    public AddDrawer addDrawer;
    MTextView titleTxt;
    SupportMapFragment map;
    GetLocationUpdates getLastLocation;
    GoogleMap gMap;
    boolean isFirstLocation = true;

    RelativeLayout dragView;
    RelativeLayout mainArea;
    View otherArea;
    FrameLayout mainContent;
    RelativeLayout uberXDriverListArea;
    MainHeaderFragment mainHeaderFrag;
    public CabSelectionFragment cabSelectionFrag;
    RequestPickUpFragment reqPickUpFrag;
    DriverAssignedHeaderFragment driverAssignedHeaderFrag;
    DriverDetailFragment driverDetailFrag;
    ArrayList<HashMap<String, String>> cabTypeList;
    public LoadAvailableCab loadAvailCabs;
    public Location pickUpLocation;
    public String selectedCabTypeId = "";
    public boolean isDestinationAdded = false;
    public String destLocLatitude = "";
    public String destLocLongitude = "";
    public String destAddress = "";
    boolean isCashSelected = true;
    RequestNearestCab requestNearestCab;
    ArrayList<HashMap<String, String>> uberXDriverList = new ArrayList<>();
    GcmBroadCastReceiver gcmMessageBroadCastReceiver;
    boolean isDriverAssigned = false;
    HashMap<String, String> driverAssignedData;
    String assignedDriverId = "";
    String assignedTripId = "";
    String DRIVER_REQUEST_METHOD = "All";
    GenerateAlertBox noCabAvailAlertBox;
    MTextView uberXNoDriverTxt;
    SelectableRoundedImageView driverImgView;
    UpdateFrequentTask allCabRequestTask;
    SendNotificationsToDriverByDist sendNotificationToDriverByDist;
    boolean isDestinationMode = false;
    String selectedDateTime = "";
    String selectedDateTimeZone = "";
    String cabRquestType = ""; // Later OR Now
    public String pickUpLocationAddress = "";
    String destLocationAddress = "";
    View rideArea;
    View deliverArea;
    android.support.v7.app.AlertDialog pickUpTypeAlertBox = null;
    Intent deliveryData;
    String eTripType = "";
    public String app_type = "Ride";
    android.support.v7.app.AlertDialog alertDialog_surgeConfirm;
    String required_str = "";

    RecyclerView uberXOnlineDriversRecyclerView;
    LinearLayout driver_detail_bottomView;
    String markerId = "";
    boolean isMarkerClickable = true;
    String currentUberXChoiceType = Utils.Cab_UberX_Type_List;
    String vUberXCategoryName = "";
    Handler ufxFreqTask = null;
    private String SelectedDriverId = "";
    String tripId = "";
    android.support.v7.app.AlertDialog onGoingTripAlertBox = null;
    public boolean isBackVisible = false;

    boolean isOkPressed = false;
    public DrawerLayout mDrawerLayout;
    private String tripStatus;
    private String currentTripId;

    public AVLoadingIndicatorView loaderView;
    public ImageView pinImgView;

    GetAddressFromLocation getAddressFromLocation;

    public ArrayList<String> cabTypesArrList = new ArrayList<>();
    public boolean iswallet = false;
    public boolean isUserLocbtnclik = false;
    public String tempPickupGeoCode = "";
    public String tempDestGeoCode = "";

    String RideDeliveryType = "";
    SelectableRoundedImageView deliverImgView, deliverImgViewsel, rideImgView, rideImgViewsel, otherImageView, otherImageViewsel;
    public boolean isUfx = false;

    public String uberXAddress = "";
    public double uberXlat = 0.0;
    public double uberXlong = 0.0;

    boolean istollenable = false;
    double tollamount = 0.0;
    String tollcurrancy = "";

    boolean isrideschedule = false;
    boolean isreqnow = false;

    ImageView prefBtnImageView;
    android.support.v7.app.AlertDialog pref_dialog;
    android.support.v7.app.AlertDialog tolltax_dialog;

    public boolean ishandicap = false;
    public boolean isfemale = false;
    boolean isTollCostdilaogshow = false;
    public LinearLayout noloactionview;

    MTextView noLocTitleTxt, noLocMsgTxt, settingTxt, pickupredirectTxt;

    public ImageView nolocmenuImgView;
    public ImageView nolocbackImgView;

    public boolean isFrompickupaddress = false;
    boolean istollIgnore = false;

    ProgressBar progressBar;

    boolean isgpsview = false;

    private FirebaseAuth auth;

    boolean isnotification = false;
    public String timeval = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        generalFunc = new GeneralFunctions(getActContext());
        cabSelectionFrag = null;

        //   userProfileJson = getIntent().getStringExtra("USER_PROFILE_JSON");
        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);

        if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equals(Utils.CabGeneralTypeRide_Delivery_UberX)) {
            RideDeliveryType = Utils.CabGeneralType_Ride;
        }

        isUfx = getIntent().getBooleanExtra("isufx", false);
        isnotification = getIntent().getBooleanExtra("isnotification", false);

        app_type = generalFunc.getJsonValue("APP_TYPE", userProfileJson);
        if (getIntent().hasExtra("tripId")) {
            tripId = getIntent().getStringExtra("tripId");
        }
        String TripDetails = generalFunc.getJsonValue("TripDetails", userProfileJson);

        if (TripDetails != null && !TripDetails.equals("")) {
            tripId = generalFunc.getJsonValue("iTripId", TripDetails);
        }

        prefBtnImageView = (ImageView) findViewById(R.id.prefBtnImageView);
        if (generalFunc.retrieveValue(CommonUtilities.FEMALE_RIDE_REQ_ENABLE).equalsIgnoreCase("No") &&
                generalFunc.retrieveValue(CommonUtilities.HANDICAP_ACCESSIBILITY_OPTION).equalsIgnoreCase("No")) {
            prefBtnImageView.setVisibility(View.GONE);
        } else if (generalFunc.retrieveValue(CommonUtilities.HANDICAP_ACCESSIBILITY_OPTION).equalsIgnoreCase("No") &&
                !generalFunc.retrieveValue(CommonUtilities.FEMALE_RIDE_REQ_ENABLE).equalsIgnoreCase("Yes")
                || (generalFunc.retrieveValue(CommonUtilities.FEMALE_RIDE_REQ_ENABLE).equalsIgnoreCase("Yes") &&
                generalFunc.getJsonValue("eGender", userProfileJson).equals("Male")
                && !generalFunc.retrieveValue(CommonUtilities.HANDICAP_ACCESSIBILITY_OPTION).equalsIgnoreCase("Yes"))) {

            prefBtnImageView.setVisibility(View.GONE);
        }

        addDrawer = new AddDrawer(getActContext(), userProfileJson);

        if (app_type.equalsIgnoreCase("UberX")) {
            addDrawer.configDrawer(true);
            selectedCabTypeId = getIntent().getStringExtra("SelectedVehicleTypeId");
            vUberXCategoryName = getIntent().getStringExtra("vCategoryName");
        } else {
            addDrawer.configDrawer(false);
        }

        if (app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
            if (isUfx) {
                selectedCabTypeId = getIntent().getStringExtra("SelectedVehicleTypeId");
                vUberXCategoryName = getIntent().getStringExtra("vCategoryName");
            }
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        uberXNoDriverTxt = (MTextView) findViewById(R.id.uberXNoDriverTxt);
        deliverImgView = (SelectableRoundedImageView) findViewById(R.id.deliverImgView);
        deliverImgViewsel = (SelectableRoundedImageView) findViewById(R.id.deliverImgViewsel);
        rideImgView = (SelectableRoundedImageView) findViewById(R.id.rideImgView);
        rideImgViewsel = (SelectableRoundedImageView) findViewById(R.id.rideImgViewsel);
        otherImageView = (SelectableRoundedImageView) findViewById(R.id.otherImageView);
        otherImageViewsel = (SelectableRoundedImageView) findViewById(R.id.otherImageViewsel);

        noloactionview = (LinearLayout) findViewById(R.id.noloactionview);
        noLocTitleTxt = (MTextView) findViewById(R.id.noLocTitleTxt);
        noLocMsgTxt = (MTextView) findViewById(R.id.noLocMsgTxt);
        settingTxt = (MTextView) findViewById(R.id.settingTxt);
        pickupredirectTxt = (MTextView) findViewById(R.id.pickupredirectTxt);
        settingTxt.setOnClickListener(new setOnClickList());
        pickupredirectTxt.setOnClickListener(new setOnClickList());
        nolocmenuImgView = (ImageView) findViewById(R.id.nolocmenuImgView);
        nolocbackImgView = (ImageView) findViewById(R.id.nolocbackImgView);
        nolocmenuImgView.setOnClickListener(new setOnClickList());
        nolocbackImgView.setOnClickListener(new setOnClickList());

        progressBar = (ProgressBar) findViewById(R.id.mProgressBar);


        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 35), 2,
                getActContext().getResources().getColor(R.color.white), deliverImgViewsel);

        deliverImgViewsel.setColorFilter(getActContext().getResources().getColor(R.color.black));

        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 30), 2,
                getActContext().getResources().getColor(R.color.white), deliverImgView);

        deliverImgView.setColorFilter(getActContext().getResources().getColor(R.color.black));

        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 35), 2,
                getActContext().getResources().getColor(R.color.white), rideImgViewsel);

        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 30), 2,
                getActContext().getResources().getColor(R.color.white), rideImgView);

        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 35), 2,
                getActContext().getResources().getColor(R.color.white), otherImageViewsel);

        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 30), 2,
                getActContext().getResources().getColor(R.color.white), otherImageView);

        loaderView = (AVLoadingIndicatorView) findViewById(R.id.loaderView);
        uberXOnlineDriversRecyclerView = (RecyclerView) findViewById(R.id.uberXOnlineDriversRecyclerView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        userLocBtnImgView = (ImageView) findViewById(R.id.userLocBtnImgView);
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapV2);
        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        dragView = (RelativeLayout) findViewById(R.id.dragView);
        mainArea = (RelativeLayout) findViewById(R.id.mainArea);
        otherArea = findViewById(R.id.otherArea);
        mainContent = (FrameLayout) findViewById(R.id.mainContent);
        driver_detail_bottomView = (LinearLayout) findViewById(R.id.driver_detail_bottomView);
        pinImgView = (ImageView) findViewById(R.id.pinImgView);

        uberXDriverListArea = (RelativeLayout) findViewById(R.id.uberXDriverListArea);
        emeTapImgView = (ImageView) findViewById(R.id.emeTapImgView);
        rideArea = findViewById(R.id.rideArea);
        deliverArea = findViewById(R.id.deliverArea);

        prefBtnImageView.setOnClickListener(new setOnClickList());
        gcmMessageBroadCastReceiver = new GcmBroadCastReceiver((MainActivity) getActContext());

        if (isPubNubEnabled()) {
            configPubNub = new ConfigPubNub(getActContext());
        }

        map.getMapAsync(MainActivity.this);

        setGeneralData();
        setLabels();

        if (generalFunc.isRTLmode()) {
            ((ImageView) findViewById(R.id.deliverImg)).setRotation(-180);
            ((ImageView) findViewById(R.id.rideImg)).setRotation(-180);
            ((ImageView) findViewById(R.id.rideImg)).setScaleY(-1);
            ((ImageView) findViewById(R.id.deliverImg)).setScaleY(-1);
        }


        new CreateAnimation(dragView, getActContext(), R.anim.design_bottom_sheet_slide_in, 100, true).startAnimation();

        userLocBtnImgView.setOnClickListener(new setOnClickList());
        emeTapImgView.setOnClickListener(new setOnClickList());
        rideArea.setOnClickListener(new setOnClickList());
        deliverArea.setOnClickListener(new setOnClickList());
        otherArea.setOnClickListener(new setOnClickList());

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            String restratValue_str = savedInstanceState.getString("RESTART_STATE");
            Utils.printLog("restratValue_str", restratValue_str);

            if (restratValue_str != null && !restratValue_str.equals("") && restratValue_str.trim().equals("true")) {
                releaseScheduleNotificationTask();
                generalFunc.restartApp();
            }
        }

        cabRquestType = "";

    }

    public void showprogress() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getActContext().getResources().getColor(R.color.appThemeColor_1), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public void hideprogress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }


    public void showLoader() {
        loaderView.setVisibility(View.VISIBLE);
    }

    public void hideLoader() {
        loaderView.setVisibility(View.GONE);

    }

    public void addcabselectionfragment() {
        setRiderDefaultView();
    }

    public void setSelectedDriverId(String driver_id) {
        SelectedDriverId = driver_id;
    }

    public void setLabels() {
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        ((MTextView) findViewById(R.id.rideTxt)).setText(generalFunc.retrieveLangLBl("Ride", "LBL_RIDE"));
        ((MTextView) findViewById(R.id.deliverTxt)).setText(generalFunc.retrieveLangLBl("Deliver", "LBL_DELIVER"));
        ((MTextView) findViewById(R.id.otherTxt)).setText(generalFunc.retrieveLangLBl("Other", "LBL_OTHER"));

        noLocTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_LOCATION_SERVICES_TURNED_OFF"));
        noLocMsgTxt.setText(generalFunc.retrieveLangLBl("", "LBL_LOCATION_SERVICES_TURNED_OFF_DETAILS"));
        settingTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TURN_ON_LOC_SERVICE"));
        pickupredirectTxt.setText(generalFunc.retrieveLangLBl("Enter pickup address", "LBL_ENTER_PICKUP_TXT"));
    }

    public boolean isPubNubEnabled() {
        String ENABLE_PUBNUB = generalFunc.getJsonValue("ENABLE_PUBNUB", userProfileJson);
        return ENABLE_PUBNUB.equalsIgnoreCase("Yes");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("RESTART_STATE", "true");
        super.onSaveInstanceState(outState);
    }

    public void setGeneralData() {
        generalFunc.storedata(CommonUtilities.MOBILE_VERIFICATION_ENABLE_KEY, generalFunc.getJsonValue("MOBILE_VERIFICATION_ENABLE", userProfileJson));
        String DRIVER_REQUEST_METHOD = generalFunc.getJsonValue("DRIVER_REQUEST_METHOD", userProfileJson);

        this.DRIVER_REQUEST_METHOD = DRIVER_REQUEST_METHOD.equals("") ? "All" : DRIVER_REQUEST_METHOD;
        Utils.printLog("DRIVER_REQUEST_METHOD", "DRIVER_REQUEST_METHOD::" + DRIVER_REQUEST_METHOD);

        // Set menu wallet n invite friend according status
        generalFunc.storedata(CommonUtilities.REFERRAL_SCHEME_ENABLE, generalFunc.getJsonValue("REFERRAL_SCHEME_ENABLE", userProfileJson));
        generalFunc.storedata(CommonUtilities.WALLET_ENABLE, generalFunc.getJsonValue("WALLET_ENABLE", userProfileJson));
    }

    public MainHeaderFragment getMainHeaderFrag() {
        return mainHeaderFrag;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        (findViewById(R.id.LoadingMapProgressBar)).setVisibility(View.GONE);

        if (googleMap == null) {
            return;
        }

        this.gMap = googleMap;

        //  getAddressFromLocation = new GetAddressFromLocation(getActContext(), generalFunc);

        if (generalFunc.checkLocationPermission(true) == true) {
            getMap().setMyLocationEnabled(true);
//            getMap().setPadding(0, Utils.dipToPixels(getActContext(), 60), 0, 0);
            getMap().getUiSettings().setTiltGesturesEnabled(false);
            getMap().getUiSettings().setCompassEnabled(false);
            getMap().getUiSettings().setMyLocationButtonEnabled(false);
            getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.hideInfoWindow();
                    return true;
                }
            });
            getMap().setOnMapClickListener(this);


        }

        String vTripStatus = generalFunc.getJsonValue("vTripStatus", userProfileJson);
        if (vTripStatus != null && (vTripStatus.equals("Active") || vTripStatus.equals("On Going Trip"))) {
            getMap().setMyLocationEnabled(false);

        }


//        if (mainHeaderFrag != null) {
//            mainHeaderFrag.setGoogleMapInstance(this.gMap);
//        }


        getLastLocation = new GetLocationUpdates(getActContext(), 8);
        getLastLocation.setLocationUpdatesListener(this);
//        if (loadAvailCabs == null && userLocation != null) {
//            initializeLoadCab();
//        }

//        if (driverAssignedHeaderFrag != null) {
//            driverAssignedHeaderFrag.setGoogleMap(getMap());
//        }


    }

    public void checkDrawerState() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START) == true) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }


    @Override
    public void onMapClick(LatLng latLng) {

        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public GoogleMap getMap() {
        return this.gMap;
    }

    public void setShadow() {
        if (cabSelectionFrag != null) {
            cabSelectionFrag.setShadow();
        }
    }

    public void setUserLocImgBtnMargin(int margin) {
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) (userLocBtnImgView).getLayoutParams();
//        params.bottomMargin = Utils.dipToPixels(getActContext(), margin);
//
//        userLocBtnImgView.setLayoutParams(params);
    }

    public void initializeLoadCab() {
        if (isDriverAssigned == true) {
            return;
        }
        loadAvailCabs = new LoadAvailableCab(getActContext(), generalFunc, selectedCabTypeId, userLocation,
                getMap(), userProfileJson);


        loadAvailCabs.pickUpAddress = pickUpLocationAddress;
        loadAvailCabs.currentGeoCodeResult = currentGeoCodeObject;
        loadAvailCabs.changeCabs();
    }

    public void updateCabs() {
        if (loadAvailCabs != null) {
            loadAvailCabs.setPickUpLocation(pickUpLocation);
            loadAvailCabs.changeCabs();
        }
    }

//    public void initializeViews() {
//        String vTripStatus = generalFunc.getJsonValue("vTripStatus", userProfileJson);
//
//        if (vTripStatus != null && (vTripStatus.equals("Active") || vTripStatus.equals("On Going Trip"))) {
//
//
//
//            //Assign driver
//            isDriverAssigned = true;
//            addDrawer.setIsDriverAssigned(isDriverAssigned);
//            if (driverAssignedHeaderFrag != null) {
//                driverAssignedHeaderFrag = null;
//            }
//            configureAssignedDriver(true);
//
//            configureDeliveryView(true);
//
//        } else {
//            //Set default view
//            if (mainHeaderFrag == null) {
//                mainHeaderFrag = new MainHeaderFragment();
//            }
//
//            if (cabSelectionFrag == null && !app_type.equalsIgnoreCase("UberX")) {
//                cabSelectionFrag = new CabSelectionFragment();
//            }
//
//            setCurrentType();
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.headerContainer, mainHeaderFrag).commit();
//            if (!app_type.equalsIgnoreCase("UberX")) {
//
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.dragView, cabSelectionFrag).commit();
//
//            } else {
//                (findViewById(R.id.dragView)).setVisibility(View.GONE);
//                setUserLocImgBtnMargin(5);
//                setPanelHeight(0);
//            }
//
//            configureDeliveryView(false);
//        }
//        Utils.runGC();
//    }

    public void showMessageWithAction(View view, String message, final Bundle bn) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_INDEFINITE).setAction(generalFunc.retrieveLangLBl("", "LBL_BTN_VERIFY_TXT"), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new StartActProcess(getActContext()).startActForResult(com.app85taxi.passenger.VerifyInfoActivity.class, bn, Utils.VERIFY_INFO_REQ_CODE);

                    }
                });
        snackbar.setActionTextColor(getActContext().getResources().getColor(R.color.appThemeColor_1));
        snackbar.setDuration(10000);
        snackbar.show();
    }

    public void initializeViews() {
        if (!generalFunc.getJsonValue("eEmailVerified", userProfileJson).equalsIgnoreCase("YES") ||
                !generalFunc.getJsonValue("ePhoneVerified", userProfileJson).equalsIgnoreCase("YES")) {

            Bundle bn = new Bundle();
            if (!generalFunc.getJsonValue("eEmailVerified", userProfileJson).equalsIgnoreCase("YES") &&
                    !generalFunc.getJsonValue("ePhoneVerified", userProfileJson).equalsIgnoreCase("YES")) {
                bn.putString("msg", "DO_EMAIL_PHONE_VERIFY");
            } else if (!generalFunc.getJsonValue("eEmailVerified", userProfileJson).equalsIgnoreCase("YES")) {
                bn.putString("msg", "DO_EMAIL_VERIFY");
            } else if (!generalFunc.getJsonValue("ePhoneVerified", userProfileJson).equalsIgnoreCase("YES")) {
                bn.putString("msg", "DO_PHONE_VERIFY");
            }

            bn.putString("UserProfileJson", userProfileJson);
            showMessageWithAction(mainArea, generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_ALERT_RIDER_TXT"), bn);

        }
        String vTripStatus = generalFunc.getJsonValue("vTripStatus", userProfileJson);

        if (vTripStatus != null && (vTripStatus.equals("Active") || vTripStatus.equals("On Going Trip"))) {

            String tripDetailJson = generalFunc.getJsonValue("TripDetails", userProfileJson);
            eTripType = generalFunc.getJsonValue("eType", tripDetailJson);
            Utils.printLog("eTripType>>", eTripType);
            Utils.printLog("Api", "vTripStatus" + vTripStatus + "eTripType" + eTripType + "tripId" + tripId);

            if (eTripType.equals("Deliver")) {
                eTripType = CabGeneralType_Deliver;

            }


            Utils.printLog("APP_TYPE", generalFunc.getJsonValue("APP_TYPE", userProfileJson));
            if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equals(Utils.CabGeneralTypeRide_Delivery_UberX)) {


                if (eTripType.equals(Utils.CabGeneralType_Ride) || eTripType.equals("Deliver") || eTripType.equals(CabGeneralType_Deliver)) {

//
                    if (!TextUtils.isEmpty(tripId)) {
                        //Assign driver

                        Utils.printLog("driverAssignedHeaderFragTripid", "null");
                        isDriverAssigned = true;
                        if (driverAssignedHeaderFrag != null) {
                            driverAssignedHeaderFrag = null;
                        }
                        configureAssignedDriver(true);

                        configureDeliveryView(true);
                    } else {
                        //setRiderDefaultView();
                        setMainHeaderView();
                        Utils.printLog("driverAssignedHeaderFragTripid", "notnull");
                    }
                } else {
                    setMainHeaderView();
                }

            } else {
                Utils.printLog("driverAssignedHeaderFrag", "null");
                isDriverAssigned = true;

                if (!TextUtils.isEmpty(tripId)) {
                    addDrawer.setIsDriverAssigned(true);
                    if (driverAssignedHeaderFrag != null) {
                        driverAssignedHeaderFrag = null;
                    }
                    configureAssignedDriver(true);

                    configureDeliveryView(true);
                } else {
                    setMainHeaderView();
                }

            }

        } else {
            // setRiderDefaultView();

            Utils.printLog("initializeViewsElse", "");

            setMainHeaderView();

        }

        Utils.runGC();
    }


    private void setMainHeaderView() {

        if (mainHeaderFrag == null) {

            mainHeaderFrag = new MainHeaderFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isUfx", isUfx);
            mainHeaderFrag.setArguments(bundle);
            mainHeaderFrag.setGoogleMapInstance(getMap());
        }

        super.onPostResume();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.headerContainer, mainHeaderFrag).commit();

        configureDeliveryView(false);

    }


    private void setRiderDefaultView() {
        //Set default view


        if (cabSelectionFrag == null) {
            Utils.printLog("triplist array::", cabTypesArrList.size() + "");
            Bundle bundle = new Bundle();
            bundle.putString("RideDeliveryType", RideDeliveryType);
            cabSelectionFrag = new CabSelectionFragment();
            cabSelectionFrag.setArguments(bundle);
            pinImgView.setVisibility(View.VISIBLE);


            //  new CreateAnimation(userLocBtnImgView, getActContext(), R.anim.design_bottom_sheet_slide_in, 240, true).startAnimation();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (userLocBtnImgView).getLayoutParams();
            params.bottomMargin = Utils.dipToPixels(getActContext(), 240);
//        }

        }

        if (driverAssignedHeaderFrag != null) {
            pinImgView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (userLocBtnImgView).getLayoutParams();
            params.bottomMargin = Utils.dipToPixels(getActContext(), 100);

        }

        setCurrentType();

        super.onPostResume();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.dragView, cabSelectionFrag).commit();


        configureDeliveryView(false);
    }

    private void setCurrentType() {

        if (cabSelectionFrag == null) {
            return;
        }

        cabSelectionFrag.currentCabGeneralType = Utils.CabGeneralType_Ride;

    }

    public void configureDeliveryView(boolean isHidden) {
        if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
            if (!isUfx) {
                (findViewById(R.id.deliveryArea)).setVisibility(View.VISIBLE);
                otherArea.setVisibility(View.VISIBLE);
                setUserLocImgBtnMargin(190);
            } else {
                (findViewById(R.id.deliveryArea)).setVisibility(View.GONE);
                setUserLocImgBtnMargin(105);
            }
        } else if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("Ride-Delivery") && isHidden == false) {
            (findViewById(R.id.deliveryArea)).setVisibility(View.VISIBLE);
            setUserLocImgBtnMargin(190);
        } else {
            (findViewById(R.id.deliveryArea)).setVisibility(View.GONE);
            setUserLocImgBtnMargin(105);
        }

    }

    private void showOngoingTripViewDialoge() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.confirmation_dialog_design_layout, null);
        builder.setView(dialogView);

        MTextView myOngoingTripBtn = (MTextView) dialogView.findViewById(R.id.myOngoingTripBtn);
        MTextView driverAcceptedRequest = (MTextView) dialogView.findViewById(R.id.driverAcceptedRequest);
        MTextView cancelBtn = (MTextView) dialogView.findViewById(R.id.cancelBtn);

        cancelBtn.setText(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));
        myOngoingTripBtn.setText(generalFunc.retrieveLangLBl("My Ongoing Trips", "LBL_MY_ONGOING_TRIPS_HEADER_TXT"));
        driverAcceptedRequest.setText(generalFunc.retrieveLangLBl("Driver has accepted your request and arriving at your location.You can check the status by tapping below button.", "LBL_ONGOING_TRIP_TXT"));

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeOnGoingTripAlertBox();
            }
        });


        new CreateRoundedView(getActContext().getResources().getColor(R.color.appThemeColor_2), Utils.dipToPixels(getActContext(), 8), Utils.dipToPixels(getActContext(), 1),
                getActContext().getResources().getColor(R.color.appThemeColor_bg_parent_1), myOngoingTripBtn);
        new CreateRoundedView(getActContext().getResources().getColor(R.color.appThemeColor_2), Utils.dipToPixels(getActContext(), 8), Utils.dipToPixels(getActContext(), 1),
                getActContext().getResources().getColor(R.color.appThemeColor_bg_parent_1), cancelBtn);


        myOngoingTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onGoingTripAlertBox != null) {
                    onGoingTripAlertBox.cancel();
                }
                isUfx = true;
                Bundle bn = new Bundle();
                bn.putString("UserProfileJson", userProfileJson);


            }
        });

        onGoingTripAlertBox = builder.create();
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(onGoingTripAlertBox);
        }
        onGoingTripAlertBox.show();

    }

    public void closeOnGoingTripAlertBox() {
        if (onGoingTripAlertBox != null) {
            onGoingTripAlertBox.cancel();
            if (app_type.equals(Utils.CabGeneralTypeRide_Delivery_UberX)) {
                if (!isUfx) {
                    Bundle bn = new Bundle();
                    bn.putString("uberXAddress", uberXAddress);

                    finishAffinity();
                } else {
                    Bundle bn = new Bundle();
                    bn.putString("USER_PROFILE_JSON", userProfileJson);
                    new StartActProcess(getActContext()).startActWithData(MainActivity.class, bn);
                    finishAffinity();
                }
            } else {
                Bundle bn = new Bundle();
                bn.putString("USER_PROFILE_JSON", userProfileJson);

                finishAffinity();

            }
        }

    }

    public void configDestinationMode(boolean isDestinationMode) {
        this.isDestinationMode = isDestinationMode;
        try {

            if (isDestinationMode == false) {
                setETA("\n" + "--");
                // pinImgView.setImageResource(R.drawable.pin_source_select);
                animateToLocation(getPickUpLocation().getLatitude(), getPickUpLocation().getLongitude());
                if (cabSelectionFrag != null) {
                    cabSelectionFrag.ride_now_btn.setEnabled(false);
                    cabSelectionFrag.ride_now_btn.setTextColor(Color.parseColor("#BABABA"));
                }
            } else {
                pinImgView.setImageResource(R.drawable.pin_dest_select);
                if (cabSelectionFrag != null) {
                    if (loadAvailCabs != null) {

                        if (loadAvailCabs.isAvailableCab) {
//                            cabSelectionFrag.ride_now_btn.setEnabled(true);
//                            cabSelectionFrag.ride_now_btn.setTextColor(getResources().getColor(R.color.btn_text_color_type2));
                        }
                    }
                }

                if (timeval.equalsIgnoreCase("\n" + "--")) {
                    cabSelectionFrag.ride_now_btn.setEnabled(false);
                    cabSelectionFrag.ride_now_btn.setTextColor(Color.parseColor("#BABABA"));
                } else {
                    cabSelectionFrag.ride_now_btn.setEnabled(true);
                    cabSelectionFrag.ride_now_btn.setTextColor(getResources().getColor(R.color.btn_text_color_type2));
                }
                pinImgView.setImageResource(R.drawable.pin_dest_select);
                if (isDestinationAdded == true && !getDestLocLatitude().trim().equals("") && !getDestLocLongitude().trim().equals("")) {
                    animateToLocation(generalFunc.parseDoubleValue(0.0, getDestLocLatitude()), generalFunc.parseDoubleValue(0.0, getDestLocLongitude()));
                }
            }

            if (mainHeaderFrag != null) {
                mainHeaderFrag.configDestinationMode(isDestinationMode);
            }
        } catch (Exception e) {

        }
    }

    public void animateToLocation(double latitude, double longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitude, longitude))
                    .zoom(gMap.getCameraPosition().zoom).build();
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void animateToLocation(double latitude, double longitude, float zoom) {
        if (latitude != 0.0 && longitude != 0.0) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitude, longitude)).zoom(zoom).build();
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void configureAssignedDriver(boolean isAppRestarted) {
        isDriverAssigned = true;
        addDrawer.setIsDriverAssigned(isDriverAssigned);
        driverDetailFrag = new DriverDetailFragment();
        driverAssignedHeaderFrag = new DriverAssignedHeaderFragment();
        Bundle bn = new Bundle();
        bn.putString("isAppRestarted", "" + isAppRestarted);

        driverAssignedData = new HashMap<>();
        releaseScheduleNotificationTask();
        if (isAppRestarted == true) {

            String tripDetailJson = generalFunc.getJsonValue("TripDetails", userProfileJson);
            String driverDetailJson = generalFunc.getJsonValue("DriverDetails", userProfileJson);
            String driverCarDetailJson = generalFunc.getJsonValue("DriverCarDetails", userProfileJson);

            String vTripPaymentMode = generalFunc.getJsonValue("vTripPaymentMode", tripDetailJson);
            String tEndLat = generalFunc.getJsonValue("tEndLat", tripDetailJson);
            String tEndLong = generalFunc.getJsonValue("tEndLong", tripDetailJson);
            String tDaddress = generalFunc.getJsonValue("tDaddress", tripDetailJson);

            if (vTripPaymentMode.equals("Cash")) {
                isCashSelected = true;
            } else {
                isCashSelected = false;
            }

            assignedDriverId = generalFunc.getJsonValue("iDriverId", tripDetailJson);
            assignedTripId = generalFunc.getJsonValue("iTripId", tripDetailJson);
            eTripType = generalFunc.getJsonValue("eType", tripDetailJson);

            if (!tEndLat.equals("0.0") && !tEndLong.equals("0.0")
                    && !tDaddress.equals("Not Set") && !tEndLat.equals("") && !tEndLong.equals("")
                    && !tDaddress.equals("")) {
                isDestinationAdded = true;
                destAddress = tDaddress;
                destLocLatitude = tEndLat;
                destLocLongitude = tEndLong;
            }

            driverAssignedData.put("PickUpLatitude", generalFunc.getJsonValue("tStartLat", tripDetailJson));
            driverAssignedData.put("PickUpLongitude", generalFunc.getJsonValue("tStartLong", tripDetailJson));
            driverAssignedData.put("vDeliveryConfirmCode", generalFunc.getJsonValue("vDeliveryConfirmCode", tripDetailJson));
            driverAssignedData.put("PickUpAddress", generalFunc.getJsonValue("tSaddress", tripDetailJson));
            driverAssignedData.put("vVehicleType", generalFunc.getJsonValue("vVehicleType", tripDetailJson));
            driverAssignedData.put("eType", generalFunc.getJsonValue("eType", tripDetailJson));
            driverAssignedData.put("DriverTripStatus", generalFunc.getJsonValue("vTripStatus", driverDetailJson));
            driverAssignedData.put("DriverPhone", generalFunc.getJsonValue("vPhone", driverDetailJson));
            driverAssignedData.put("DriverRating", generalFunc.getJsonValue("vAvgRating", driverDetailJson));
            driverAssignedData.put("DriverAppVersion", generalFunc.getJsonValue("iAppVersion", driverDetailJson));
            driverAssignedData.put("DriverLatitude", generalFunc.getJsonValue("vLatitude", driverDetailJson));
            driverAssignedData.put("DriverLongitude", generalFunc.getJsonValue("vLongitude", driverDetailJson));
            driverAssignedData.put("DriverImage", generalFunc.getJsonValue("vImage", driverDetailJson));
            driverAssignedData.put("DriverName", generalFunc.getJsonValue("vName", driverDetailJson));
            driverAssignedData.put("DriverCarPlateNum", generalFunc.getJsonValue("vLicencePlate", driverCarDetailJson));
            driverAssignedData.put("DriverCarName", generalFunc.getJsonValue("make_title", driverCarDetailJson));
            driverAssignedData.put("DriverCarModelName", generalFunc.getJsonValue("model_title", driverCarDetailJson));
            driverAssignedData.put("DriverCarColour", generalFunc.getJsonValue("vColour", driverCarDetailJson));
            driverAssignedData.put("vCode", generalFunc.getJsonValue("vCode", driverDetailJson));

        } else {

            if (currentLoadedDriverList == null) {
                generalFunc.restartApp();
                return;
            }

            boolean isDriverIdMatch = false;
            for (int i = 0; i < currentLoadedDriverList.size(); i++) {
                HashMap<String, String> driverDataMap = currentLoadedDriverList.get(i);
                String iDriverId = driverDataMap.get("driver_id");

                if (iDriverId.equals(assignedDriverId)) {
                    isDriverIdMatch = true;

                    driverAssignedData.put("PickUpLatitude", "" + getPickUpLocation().getLatitude());
                    driverAssignedData.put("PickUpLongitude", "" + getPickUpLocation().getLongitude());

                    if (mainHeaderFrag != null) {
                        driverAssignedData.put("PickUpAddress", mainHeaderFrag.getPickUpAddress());
                    } else {
                        driverAssignedData.put("PickUpAddress", pickUpLocationAddress);
                    }

                    driverAssignedData.put("vVehicleType", generalFunc.getSelectedCarTypeData(selectedCabTypeId, "VehicleTypes", "vVehicleType", userProfileJson));
                    driverAssignedData.put("vDeliveryConfirmCode", "");

                    driverAssignedData.put("DriverTripStatus", "");
                    driverAssignedData.put("DriverPhone", driverDataMap.get("vPhone_driver"));
                    driverAssignedData.put("DriverRating", driverDataMap.get("average_rating"));
                    driverAssignedData.put("DriverAppVersion", driverDataMap.get("iAppVersion"));
                    driverAssignedData.put("DriverLatitude", driverDataMap.get("Latitude"));
                    driverAssignedData.put("DriverLongitude", driverDataMap.get("Longitude"));
                    driverAssignedData.put("DriverImage", driverDataMap.get("driver_img"));
                    driverAssignedData.put("DriverName", driverDataMap.get("Name"));
                    driverAssignedData.put("DriverCarPlateNum", driverDataMap.get("vLicencePlate"));
                    driverAssignedData.put("DriverCarName", driverDataMap.get("make_title"));
                    driverAssignedData.put("DriverCarModelName", driverDataMap.get("model_title"));
                    driverAssignedData.put("DriverCarColour", driverDataMap.get("vColour"));
                    driverAssignedData.put("eType", getCurrentCabGeneralType());

                    break;
                }
            }

            if (isDriverIdMatch == false) {
                generalFunc.restartApp();
                return;
            }
        }

        driverAssignedData.put("iDriverId", assignedDriverId);
        driverAssignedData.put("iTripId", assignedTripId);
        driverAssignedData.put("PassengerName", generalFunc.getJsonValue("vName", userProfileJson));
        driverAssignedData.put("PassengerImageName", generalFunc.getJsonValue("vImgName", userProfileJson));

        bn.putSerializable("TripData", driverAssignedData);
        driverAssignedHeaderFrag.setArguments(bn);
//        driverAssignedHeaderFrag.setGoogleMap(getMap());
        if (!TextUtils.isEmpty(tripId)) {
            driverAssignedHeaderFrag.isBackVisible = true;
        }
        driverDetailFrag.setArguments(bn);
        driverDetailFrag.setArguments(bn);

        Location pickUpLoc = new Location("");
        pickUpLoc.setLatitude(generalFunc.parseDoubleValue(0.0, driverAssignedData.get("PickUpLatitude")));
        pickUpLoc.setLongitude(generalFunc.parseDoubleValue(0.0, driverAssignedData.get("PickUpLongitude")));
        this.pickUpLocation = pickUpLoc;

        if (mainHeaderFrag != null) {
            mainHeaderFrag.releaseResources();
            mainHeaderFrag = null;
        }

        if (cabSelectionFrag != null) {
            cabSelectionFrag.releaseResources();
            cabSelectionFrag = null;
        }

        Utils.runGC();
        Bundle extras = getIntent().getExtras();

        if (isnotification) {
            chatMsg();

        }
        setPanelHeight(175);

        try {
            super.onPostResume();
        } catch (Exception e) {

        }

        if (driverDetailFrag != null) {
            deliverArea.setVisibility(View.GONE);
            otherArea.setEnabled(false);
            deliverArea.setEnabled(false);
            rideArea.setEnabled(false);
        }

        if (!isFinishing()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.headerContainer, driverAssignedHeaderFrag).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.dragView, driverDetailFrag).commit();
            setUserLocImgBtnMargin(105);

            new CreateAnimation(userLocBtnImgView, getActContext(), R.anim.design_bottom_sheet_slide_in, 100, true).startAnimation();

            if (generalFunc.retrieveValue("OPEN_CHAT").equals("Yes")) {
                generalFunc.storedata("OPEN_CHAT", "No");
                Bundle bnChat = new Bundle();

                bnChat.putString("iFromMemberId", driverAssignedData.get("iDriverId"));
                bnChat.putString("FromMemberImageName", driverAssignedData.get("DriverImage"));
                bnChat.putString("iTripId", driverAssignedData.get("iTripId"));
                bnChat.putString("FromMemberName", driverAssignedData.get("DriverName"));

                new StartActProcess(getActContext()).startActWithData(com.app85taxi.passenger.ChatActivity.class, bnChat);
            }

        } else {
            generalFunc.restartApp();
        }
    }

    @Override
    public void onLocationUpdate(Location location) {
        this.userLocation = location;

        CameraPosition cameraPosition = cameraForUserPosition();

        if (isFirstLocation == true) {

            Utils.printLog("SearchLatitude", "::" + getIntent().getStringExtra("latitude"));
            Utils.printLog("SearchLongitude", "::" + getIntent().getStringExtra("longitude"));

            initializeViews();

            if (cameraPosition != null) {
                Utils.printLog("in camera position", "::");
                getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            isFirstLocation = false;
        }
    }

    public void setETA(String time) {

        timeval = time;
        Utils.printLog("ETA_TIME", time);
        if (cabSelectionFrag != null) {

            if (mainHeaderFrag.area_source.getVisibility() == View.VISIBLE) {
                Bitmap marker_time_ic = generalFunc.writeTextOnDrawable(getActContext(), R.drawable.driver_time_marker_new, time, false);

                pinImgView.setImageBitmap(marker_time_ic);
                pinImgView.setPadding(0, 0, 0, 80);
            }
        }
    }

    public CameraPosition cameraForUserPosition() {

        double currentZoomLevel = getMap() == null ? Utils.defaultZomLevel : getMap().getCameraPosition().zoom;
        String TripDetails = generalFunc.getJsonValue("TripDetails", userProfileJson);

        String vTripStatus = generalFunc.getJsonValue("vTripStatus", userProfileJson);
        if (generalFunc.isLocationEnabled()) {
            double startLat = generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("tStartLat", TripDetails));
            double startLong = generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("tStartLong", TripDetails));

            if (vTripStatus != null && startLat != 0.0 && startLong != 0.0 && ((vTripStatus.equals("Active") || vTripStatus.equals("On Going Trip") || !vTripStatus.equals("NONE")))) {

                Location tempickuploc = new Location("temppickkup");

                tempickuploc.setLatitude(startLat);
                tempickuploc.setLongitude(startLong);

                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(tempickuploc.getLatitude(), tempickuploc.getLongitude()))
                        .zoom((float) currentZoomLevel).build();

                return cameraPosition;
            }
        } else {
            return null;
        }

        if (Utils.defaultZomLevel > currentZoomLevel) {
            currentZoomLevel = Utils.defaultZomLevel;
        }
        if (userLocation != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude()))
                    .zoom((float) currentZoomLevel).build();

            return cameraPosition;
        } else {
            return null;
        }
    }

    public void redirectToMapOrList(String choiceType, boolean autoLoad) {

        if (autoLoad == true && currentUberXChoiceType.equalsIgnoreCase(Utils.Cab_UberX_Type_Map)) {
            Utils.printLog("currentUberXChoiceType", "" + choiceType);
            return;
        }

        Utils.printLog("nn currentUberXChoiceType", "" + choiceType);
        this.currentUberXChoiceType = choiceType;

        mainHeaderFrag.listTxt.setBackgroundColor(choiceType.equalsIgnoreCase(Utils.Cab_UberX_Type_List) ?
                Color.parseColor("#FFFFFF") : getResources().getColor(R.color.appThemeColor_1));
        mainHeaderFrag.mapTxt.setBackgroundColor(choiceType.equalsIgnoreCase(Utils.Cab_UberX_Type_List) ?
                getResources().getColor(R.color.appThemeColor_1) : Color.parseColor("#FFFFFF"));

        mainHeaderFrag.mapTxt.setTextColor(choiceType.equalsIgnoreCase(Utils.Cab_UberX_Type_List) ?
                Color.parseColor("#FFFFFF") : Color.parseColor("#1C1C1C"));
        mainHeaderFrag.listTxt.setTextColor(choiceType.equalsIgnoreCase(Utils.Cab_UberX_Type_List) ?
                Color.parseColor("#1C1C1C") : Color.parseColor("#FFFFFF"));
        if (driver_detail_bottomView != null || driver_detail_bottomView.getVisibility() == View.VISIBLE) {
//                CLOSE BOTTOM_VIEW
//            closeBottomView();
            driver_detail_bottomView.setVisibility(View.GONE);
        }
//        Utils.printLog("currentLoadedDriverList", "" + currentLoadedDriverList.size());
        if (choiceType.equalsIgnoreCase(Utils.Cab_UberX_Type_List)) {
            Utils.printLog("currentUberXChoiceType", "" + currentUberXChoiceType);

//            mainContent.setVisibility(View.GONE);

//            closeDriverListAreaLoader(uberXDriverList);

            configDriverListForUfx();

        } else {
            if (currentLoadedDriverList != null) {
                Utils.printLog("currentLoadedDriverList", "null" + currentLoadedDriverList.toString());
            }
            (findViewById(R.id.driverListAreaLoader)).setVisibility(View.GONE);
            mainContent.setVisibility(View.VISIBLE);
            uberXDriverListArea.setVisibility(View.GONE);
        }
    }


    public void configDriverListForUfx() {

        if (ufxFreqTask != null) {
//            ufxFreqTask.stopRepeatingTask();
            return;
        }

        (findViewById(R.id.driverListAreaLoader)).setVisibility(View.VISIBLE);
        (findViewById(R.id.searchingDriverTxt)).setVisibility(View.VISIBLE);
        ((MTextView) findViewById(R.id.searchingDriverTxt)).setText(generalFunc.retrieveLangLBl("Searching Provider", "LBL_SEARCH_PROVIDER_WAIT_TXT"));
        uberXNoDriverTxt.setVisibility(View.GONE);

//        final UpdateFrequentTask freqTask = new UpdateFrequentTask(1500);
//        this.ufxFreqTask = freqTask;


        Handler handler = new Handler();
        this.ufxFreqTask = handler;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (currentLoadedDriverList != null) {
                    uberXDriverList.addAll(currentLoadedDriverList);
                }


                if (uberXDriverList.size() > 0) {
                    uberXNoDriverTxt.setVisibility(View.GONE);
                } else {
                    uberXNoDriverTxt.setVisibility(View.VISIBLE);
                }

                (findViewById(R.id.driverListAreaLoader)).setVisibility(View.GONE);
                (findViewById(R.id.searchingDriverTxt)).setVisibility(View.GONE);


                ufxFreqTask = null;

            }
        }, 3000);

//        freqTask.setTaskRunListener(new UpdateFrequentTask.OnTaskRunCalled() {
//            @Override
//            public void onTaskRun() {
//                freqTask.stopRepeatingTask();
//
//                Utils.printLog("LoadingTask","3");
//
//                if (uberXOnlineDriverListAdapter == null) {
//                    uberXOnlineDriverListAdapter = new UberXOnlineDriverListAdapter(getActContext(), uberXDriverList, generalFunc, 0.0, 0.0);
//                    uberXOnlineDriversRecyclerView.setAdapter(uberXOnlineDriverListAdapter);
//                    uberXOnlineDriversRecyclerView.setLayoutManager(new LinearLayoutManager(getActContext()));
//
//                    uberXOnlineDriverListAdapter.setOnItemClickListener(new UberXOnlineDriverListAdapter.OnItemClickListener() {
//                        @Override
//                        public void onItemClickList(View v, int position) {
//
//                            if (currentLoadedDriverList.size() > 0) {
//                                SelectedDriverId = currentLoadedDriverList.get(position).get("driver_id");
//                                loadAvailCabs.getMarkerDetails(SelectedDriverId);
//                                Utils.printLog("SelectedDriverId", "" + SelectedDriverId);
//                            }
//                        }
//                    });
//                }
//
//                if (uberXDriverList.size() > 0) {
//                    uberXNoDriverTxt.setVisibility(View.GONE);
//                } else {
//                    uberXNoDriverTxt.setVisibility(View.VISIBLE);
//                }
//
////                (findViewById(R.id.driverListAreaLoader)).setVisibility(View.GONE);
////                (findViewById(R.id.searchingDriverTxt)).setVisibility(View.GONE);
//
//                uberXOnlineDriverListAdapter.notifyDataSetChanged();
//
//            }
//        });
//        freqTask.startRepeatingTask();

    }

    private void showNoDriver() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                uberXNoDriverTxt.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }

    public void closeDriverListAreaLoader(final ArrayList uberXDriverList) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((ProgressBar) findViewById(R.id.driverListAreaLoader)).setVisibility(View.GONE);
                ((MTextView) findViewById(R.id.searchingDriverTxt)).setVisibility(View.GONE);
                if (uberXDriverList.size() < 0 || uberXDriverList.size() == 0) {
                    showNoDriver();
                } else {
                    uberXNoDriverTxt.setVisibility(View.GONE);
                }
            }
        }, 2000);
    }

    public CameraPosition cameraForPickUpPosition() {

        if (getPickUpLocation() == null) {
            return null;
        }
        double currentZoomLevel = getMap().getCameraPosition().zoom;

        if (Utils.defaultZomLevel > currentZoomLevel) {
            currentZoomLevel = Utils.defaultZomLevel;
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(getPickUpLocation().getLatitude(), getPickUpLocation().getLongitude()))
                .zoom((float) currentZoomLevel).build();

        return cameraPosition;
    }

    public void OpenCardPaymentAct(boolean fromcabselection) {
        iswallet = true;
        Bundle bn = new Bundle();
        bn.putString("UserProfileJson", userProfileJson);
        bn.putBoolean("fromcabselection", fromcabselection);
        new StartActProcess(getActContext()).startActForResult(com.app85taxi.passenger.CardPaymentActivity.class, bn, Utils.CARD_PAYMENT_REQ_CODE);
    }

    public boolean isPickUpLocationCorrect() {
        String pickUpLocAdd = mainHeaderFrag != null ? (mainHeaderFrag.getPickUpAddress().equals(
                generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT")) ? "" : mainHeaderFrag.getPickUpAddress()) : "";

        if (!pickUpLocAdd.equals("")) {
            return true;
        }
        return false;
    }

    public void continuePickUpProcess() {
        String pickUpLocAdd = mainHeaderFrag != null ? (mainHeaderFrag.getPickUpAddress().equals(
                generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT")) ? "" : mainHeaderFrag.getPickUpAddress()) : "";

        Utils.printLog("pickUpLocAdd", "" + pickUpLocAdd);
        if (!pickUpLocAdd.equals("")) {
//            if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("Ride-Delivery") ||
//                    generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX")) {

            if (isUfx) {
                choosePickUpOption();
            } else if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX")) {
                Utils.printLog("choosePickUpOption", "choosePickUpOption");
                choosePickUpOption();
            } else {
                setCabReqType(Utils.CabReqType_Now);
                checkSurgePrice("");
            }
        }
    }

    public void visibleCabselectionView() {
        (findViewById(R.id.dragView)).setVisibility(View.VISIBLE);
    }

    public String getCurrentCabGeneralType() {

        if (app_type.equalsIgnoreCase("Ride-Delivery")) {
            if (!RideDeliveryType.equals("")) {

                if (RideDeliveryType.equals(CabGeneralType_Deliver)) {
                    return "Deliver";
                }

            } else {

                return Utils.CabGeneralType_Ride;
            }
        }

        if (cabSelectionFrag != null) {
            return cabSelectionFrag.getCurrentCabGeneralType();
        } else if (!eTripType.trim().equals("")) {
            return eTripType;
        }


        return app_type;
    }

    public void chooseDateTime() {

        if (isPickUpLocationCorrect() == false) {
            return;
        }

        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(new SlideDateTimeListener() {
                    @Override
                    public void onDateTimeSet(Date date) {


                        selectedDateTime = Utils.convertDateToFormat("yyyy-MM-dd HH:mm:ss", date);
                        selectedDateTimeZone = Calendar.getInstance().getTimeZone().getID();

                        if (Utils.isValidTimeSelect(date, TimeUnit.HOURS.toMillis(1)) == false) {

                            generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("Invalid pickup time", "LBL_INVALID_PICKUP_TIME"),
                                    generalFunc.retrieveLangLBl("Please make sure that pickup time is after atleast an hour from now.", "LBL_INVALID_PICKUP_NOTE_MSG"));
                            return;
                        }

                        if (Utils.isValidTimeSelectForLater(date, TimeUnit.DAYS.toMillis(30)) == false) {

                            generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("Invalid pickup time", "LBL_INVALID_PICKUP_TIME"),
                                    generalFunc.retrieveLangLBl("Please make sure that pickup time is after atleast an 1 month from now.", "LBL_INVALID_PICKUP_NOTE_MONTH_MSG"));
                            return;
                        }


                        setCabReqType(Utils.CabReqType_Later);

                        String selectedTime = Utils.convertDateToFormat("yyyy-MM-dd HH:mm:ss", date);
                        Utils.printLog("selectedTime", "::" + selectedTime);
                        checkSurgePrice(selectedTime);
                    }
                })
                .setInitialDate(new Date())
                .setMinDate(Calendar.getInstance().getTime())
                        //.setMaxDate(maxDate)
                .setIs24HourTime(true)
                        //.setTheme(SlideDateTimePicker.HOLO_DARK)
                .setIndicatorColor(getResources().getColor(R.color.appThemeColor_2))
                .build()
                .show();
    }

    public void setCabTypeList(ArrayList<HashMap<String, String>> cabTypeList) {
        this.cabTypeList = cabTypeList;
    }

    public void changeCabType(String selectedCabTypeId) {
        this.selectedCabTypeId = selectedCabTypeId;

        if (loadAvailCabs != null) {
            loadAvailCabs.setCabTypeId(this.selectedCabTypeId);
        }
        updateCabs();
    }

    public String getSelectedCabTypeId() {

        return this.selectedCabTypeId;

    }

    public void choosePickUpOption() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.design_pick_up_type_dialog, null);
        builder.setView(dialogView);

        LinearLayout pickUpLaterArea = (LinearLayout) dialogView.findViewById(R.id.pickUpLaterArea);
        LinearLayout pickUpNowArea = (LinearLayout) dialogView.findViewById(R.id.pickUpNowArea);
        ImageView pickUpLaterImgView = (ImageView) dialogView.findViewById(R.id.pickUpLaterImgView);
        ImageView pickUpNowImgView = (ImageView) dialogView.findViewById(R.id.pickUpNowImgView);
        MButton btn_type1 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type1)).getChildView();

        btn_type1.setText(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));

        ((MTextView) dialogView.findViewById(R.id.pickUpTypeHTxt)).setText(generalFunc.retrieveLangLBl("Select your PickUp type",
                "LBL_SELECT_PICKUP_TYPE_HEADER"));
        if (isUfx) {
            ((MTextView) dialogView.findViewById(R.id.rideNowTxt)).setText(generalFunc.retrieveLangLBl("Request Now", "LBL_REQUEST_NOW"));
            ((MTextView) dialogView.findViewById(R.id.rideLaterTxt)).setText(generalFunc.retrieveLangLBl("Request Later", "LBL_REQUEST_LATER"));
        } else if (getCurrentCabGeneralType().equals("UberX")) {

            ((MTextView) dialogView.findViewById(R.id.rideNowTxt)).setText(generalFunc.retrieveLangLBl("Request Now", "LBL_REQUEST_NOW"));
            ((MTextView) dialogView.findViewById(R.id.rideLaterTxt)).setText(generalFunc.retrieveLangLBl("Request Later", "LBL_REQUEST_LATER"));
        } else if (!getCurrentCabGeneralType().equals(CabGeneralType_Deliver)) {

            ((MTextView) dialogView.findViewById(R.id.rideNowTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_NOW"));
            ((MTextView) dialogView.findViewById(R.id.rideLaterTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_LATER"));
        }


        pickUpLaterImgView.setColorFilter(Color.parseColor("#FFFFFF"));
        pickUpNowImgView.setColorFilter(Color.parseColor("#FFFFFF"));

        pickUpLaterArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePickUpTypeAlertBox();
                chooseDateTime();
            }
        });


        btn_type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePickUpTypeAlertBox();
            }
        });

        pickUpNowArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCabReqType(Utils.CabReqType_Now);
                closePickUpTypeAlertBox();
                checkSurgePrice("");
            }
        });

        new CreateRoundedView(android.R.color.transparent, Utils.dipToPixels(getActContext(), 40),
                Utils.dipToPixels(getActContext(), 1), Color.parseColor("#FFFFFF"), (dialogView.findViewById(R.id.pickUpLaterImgView)), true);

        new CreateRoundedView(android.R.color.transparent, Utils.dipToPixels(getActContext(), 40),
                Utils.dipToPixels(getActContext(), 1), Color.parseColor("#FFFFFF"), (dialogView.findViewById(R.id.pickUpNowImgView)), true);


        pickUpTypeAlertBox = builder.create();
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(pickUpTypeAlertBox);
        }
        pickUpTypeAlertBox.show();

    }

    public void closePickUpTypeAlertBox() {
        if (pickUpTypeAlertBox != null) {
            pickUpTypeAlertBox.cancel();
        }
    }

    public void checkSurgePrice(final String selectedTime) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "checkSurgePrice");
        parameters.put("SelectedCarTypeID", "" + getSelectedCabTypeId());
        if (!selectedTime.trim().equals("")) {
            parameters.put("SelectedTime", selectedTime);
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {
                Utils.printLog("responseString", "::" + responseString);
                if (responseString != null && !responseString.equals("")) {

                    generalFunc.sendHeartBeat();

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {
                        if (!selectedTime.trim().equals("")) {
                            //  parameters.put("SelectedTime", selectedTime);
                            setRideSchedule();
                        } else {
                            requestPickUp();
                        }

                        // setRideSchedule();


                    } else {
                        openSurgeConfirmDialog(responseString, selectedTime);
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void openSurgeConfirmDialog(String responseString, final String selectedTime) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.surge_confirm_design, null);
        builder.setView(dialogView);

        ((MTextView) dialogView.findViewById(R.id.headerMsgTxt)).setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
        ((MTextView) dialogView.findViewById(R.id.surgePriceTxt)).setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("SurgePrice", responseString)));
        ((MTextView) dialogView.findViewById(R.id.payableTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_PAYABLE_AMOUNT"));
        ((MTextView) dialogView.findViewById(R.id.tryLaterTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_TRY_LATER"));

        MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ACCEPT_SURGE"));
        btn_type2.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog_surgeConfirm.dismiss();

                if (!selectedTime.trim().equals("")) {
                    //  parameters.put("SelectedTime", selectedTime);
                    setRideSchedule();
                } else {
                    requestPickUp();
                }
                // setRideSchedule();
            }
        });
        (dialogView.findViewById(R.id.tryLaterTxt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog_surgeConfirm.dismiss();
                closeRequestDialog(false);
            }
        });


        alertDialog_surgeConfirm = builder.create();
        alertDialog_surgeConfirm.setCancelable(false);
        alertDialog_surgeConfirm.setCanceledOnTouchOutside(false);
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(alertDialog_surgeConfirm);
        }

        alertDialog_surgeConfirm.show();
    }

    public void pickUpLocClicked() {

        if (reqPickUpFrag != null) {
            reqPickUpFrag = null;
            Utils.runGC();
        }
//        if (pickUpLocSelectedFrag != null) {
//            pickUpLocSelectedFrag = null;
//            Utils.runGC();
//        }

        Utils.runGC();
//        pickUpLocSelectedFrag = new PickUpLocSelectedFragment();
//
//        pickUpLocSelectedFrag.setPickUpLocSelectedFrag(pickUpLocSelectedFrag);
//        pickUpLocSelectedFrag.setGoogleMap(getMap());
//        pickUpLocSelectedFrag.setPickUpAddress(mainHeaderFrag.getPickUpAddress());


        //  reqPickUpFrag = new RequestPickUpFragment();

//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.headerContainer, pickUpLocSelectedFrag).commit();

//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.dragView, reqPickUpFrag).commit();
//
//        new CreateAnimation(userLocBtnImgView, getActContext(), R.anim.design_bottom_sheet_slide_in, 600, true).startAnimation();
//        new CreateAnimation(dragView, getActContext(), R.anim.design_bottom_sheet_slide_in, 600, true).startAnimation();
//        isMarkerClickable = false;


        configureDeliveryView(true);
    }

    public void setDefaultView() {

        try {
            super.onPostResume();
        } catch (Exception e) {

        }


        cabRquestType = "";

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.headerContainer, mainHeaderFrag).commit();


        if (!app_type.equalsIgnoreCase("UberX")) {
            Utils.printLog("reqPickUpFrag", "cabselection");
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.dragView, cabSelectionFrag).commit();


        } else if (app_type.equalsIgnoreCase("UberX")) {


            if (reqPickUpFrag != null) {
                Utils.printLog("reqPickUpFrag", "reqPickUpFragremove");
                getSupportFragmentManager().beginTransaction().remove(reqPickUpFrag).commit();
            }

            Utils.printLog("DragView", "Yes");
            (findViewById(R.id.dragView)).setVisibility(View.GONE);
            setUserLocImgBtnMargin(5);
        }


//        configPickUpDrag(true, true, true);
        configDestinationMode(false);
        userLocBtnImgView.performClick();
        //pickUpLocSelectedFrag = null;
        //  mainHeaderFrag=null;
        reqPickUpFrag = null;
        Utils.runGC();

//        if (cabSelectionFrag != null) {
//            setPanelHeight(cabSelectionFrag.currentPanelDefaultStateHeight);
//        } else {
//            setPanelHeight(app_type.equalsIgnoreCase(Utils.CabGeneralType_UberX) ? 0 : 100);
//        }

        if (!app_type.equalsIgnoreCase("UberX")) {

            configureDeliveryView(false);
        }

        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

//        if (cabSelectionFrag != null) {
//            cabSelectionFrag.configRideLaterBtnArea(false);
//            new CreateAnimation(userLocBtnImgView, getActContext(), R.anim.design_bottom_sheet_slide_in, 600, true).startAnimation();
//        }
        new CreateAnimation(dragView, getActContext(), R.anim.design_bottom_sheet_slide_in, 600, true).startAnimation();


        if (loadAvailCabs != null) {
            loadAvailCabs.setTaskKilledValue(false);
            loadAvailCabs.onResumeCalled();
        }


    }

    public void setPanelHeight(int value) {
        sliding_layout.setPanelHeight(Utils.dipToPixels(getActContext(), value));
    }

    public void onPickUpLocChanged(Location pickUpLocation) {
        this.pickUpLocation = pickUpLocation;

        updateCabs();
    }

    public Location getPickUpLocation() {
        return this.pickUpLocation;
    }

    public String getPickUpLocationAddress() {
        return this.pickUpLocationAddress;
    }


    public void notifyCarSearching() {
        setETA("\n" + "--");

        if (cabSelectionFrag != null) {

            if (!isDestinationMode) {

                cabSelectionFrag.ride_now_btn.setEnabled(false);
                cabSelectionFrag.ride_now_btn.setTextColor(Color.parseColor("#BABABA"));
            }

        }
    }

    public void notifyNoCabs() {

        setETA("\n" + "--");
        setCurrentLoadedDriverList(new ArrayList<HashMap<String, String>>());

        if (noCabAvailAlertBox == null && generalFunc.getJsonValue("SITE_TYPE", userProfileJson).equalsIgnoreCase("Demo") && !app_type.equalsIgnoreCase("UberX")) {
            String prefix_msg = getCurrentCabGeneralType().equals(CabGeneralType_Deliver) ? "LBL_NO_DELIVERY_NOTE_1_TXT" : "LBL_NO_CARS_NOTE_1_TXT";
            String suffix_msg = getCurrentCabGeneralType().equals(CabGeneralType_Deliver) ? "LBL_NO_DELIVERY_NOTE_2_TXT" : "LBL_NO_CARS_NOTE_2_TXT";

            //if (cabSelectionFrag != null) {
//                buildNoCabMessage(generalFunc.retrieveLangLBl("", prefix_msg) + " " +
//                                generalFunc.getSelectedCarTypeData(getSelectedCabTypeId(), "VehicleTypes", "vVehicleType", userProfileJson)
//                                + ". " + generalFunc.retrieveLangLBl("", suffix_msg),
//                        generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
            // }

        }

        if (cabSelectionFrag != null) {
            cabSelectionFrag.ride_now_btn.setEnabled(false);
            cabSelectionFrag.ride_now_btn.setTextColor(Color.parseColor("#BABABA"));
        }
    }

    public void buildNoCabMessage(String message, String positiveBtn) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(true);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();
            }
        });
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(positiveBtn);
        generateAlert.showAlertBox();

        this.noCabAvailAlertBox = generateAlert;
    }

    public void notifyCabsAvailable() {
        if (cabSelectionFrag != null && loadAvailCabs.listOfDrivers.size() > 0) {
            if (cabSelectionFrag.isroutefound) {
                if (loadAvailCabs != null) {

                    if (loadAvailCabs.isAvailableCab) {
                        if (!timeval.equalsIgnoreCase("\n" + "--")) {
                            cabSelectionFrag.ride_now_btn.setEnabled(true);
                            cabSelectionFrag.ride_now_btn.setTextColor(getResources().getColor(R.color.btn_text_color_type2));
                        }
                    }
                }
            }
        }
    }

    public void onMapCameraChanged() {
        if (cabSelectionFrag != null) {
            if (mainHeaderFrag != null) {
                notifyCarSearching();
                cabSelectionFrag.img_ridelater.setEnabled(false);

                if (isDestinationMode == true) {
                    mainHeaderFrag.setDestinationAddress(generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT"));

                } else {
                    mainHeaderFrag.setPickUpAddress(generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT"));

                }

            }
            cabSelectionFrag.findRoute();
        }
    }

    public void onAddressFound(String address) {
        if (cabSelectionFrag != null) {
            notifyCabsAvailable();
            cabSelectionFrag.img_ridelater.setEnabled(true);
            if (mainHeaderFrag != null) {

                if (isDestinationMode == true) {

                    mainHeaderFrag.setDestinationAddress(address);
                } else {
                    mainHeaderFrag.setPickUpAddress(address);
                }
            }
        } else {
            if (isUserLocbtnclik) {
                isUserLocbtnclik = false;
                mainHeaderFrag.setPickUpAddress(address);

            }
        }


    }

    public void setDestinationPoint(String destLocLatitude, String destLocLongitude, String destAddress, boolean isDestinationAdded) {
        this.isDestinationAdded = isDestinationAdded;
        this.destLocLatitude = destLocLatitude;
        this.destLocLongitude = destLocLongitude;
        this.destAddress = destAddress;
    }

    public boolean getDestinationStatus() {
        return this.isDestinationAdded;
    }

    public String getDestLocLatitude() {
        return this.destLocLatitude;
    }

    public String getDestLocLongitude() {
        return this.destLocLongitude;
    }

    public String getDestAddress() {
        return this.destAddress;
    }

    public void setCashSelection(boolean isCashSelected) {
        this.isCashSelected = isCashSelected;
    }

    public String getCabReqType() {
        return this.cabRquestType;
    }

    public void setCabReqType(String cabRquestType) {
        this.cabRquestType = cabRquestType;
    }

    public Bundle getFareEstimateBundle() {
        Bundle bn = new Bundle();
        bn.putString("PickUpLatitude", "" + getPickUpLocation().getLatitude());
        bn.putString("PickUpLongitude", "" + getPickUpLocation().getLongitude());
        bn.putString("isDestinationAdded", "" + getDestinationStatus());
        bn.putString("DestLocLatitude", "" + getDestLocLatitude());
        bn.putString("DestLocLongitude", "" + getDestLocLongitude());
        bn.putString("DestLocAddress", "" + getDestAddress());
        bn.putString("SelectedCarId", "" + getSelectedCabTypeId());
        bn.putString("UserProfileJson", "" + userProfileJson);

        return bn;
    }

    public void setRideSchedule() {
        isrideschedule = true;

        if (getDestinationStatus() == false && generalFunc.retrieveValue(CommonUtilities.APP_DESTINATION_MODE).equalsIgnoreCase(CommonUtilities.STRICT_DESTINATION)) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ADD_DEST_MSG_BOOK_RIDE"));
        } else {

            getTollcostValue("", "");
        }
    }

    public void setDeliverySchedule() {

        if (getDestinationStatus() == false) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please add your destination location " +
                    "to deliver your package.", "LBL_ADD_DEST_MSG_DELIVER_ITEM"));
        } else {

            Bundle bn = new Bundle();
            bn.putString("UserProfileJson", userProfileJson);
            bn.putString("isDeliverNow", "" + getCabReqType().equals(Utils.CabReqType_Now));


        }
    }

    public void bookRide() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "ScheduleARide");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("pickUpLocAdd", mainHeaderFrag != null ? mainHeaderFrag.getPickUpAddress() : "");
        parameters.put("pickUpLatitude", "" + getPickUpLocation().getLatitude());
        parameters.put("pickUpLongitude", "" + getPickUpLocation().getLongitude());
        parameters.put("destLocAdd", getDestAddress());
        parameters.put("destLatitude", getDestLocLatitude());
        parameters.put("destLongitude", getDestLocLongitude());
        parameters.put("scheduleDate", selectedDateTime);
        parameters.put("iVehicleTypeId", getSelectedCabTypeId());
        parameters.put("TimeZone", selectedDateTimeZone);
        parameters.put("CashPayment", "" + isCashSelected);
        parameters.put("PickUpAddGeoCodeResult", tempPickupGeoCode);
        parameters.put("DestAddGeoCodeResult", tempDestGeoCode);

        String handicapval = "";
        String femaleval = "";
        if (ishandicap) {
            handicapval = "Yes";

        } else {
            handicapval = "No";
        }
        if (isfemale) {
            femaleval = "Yes";

        } else {
            femaleval = "No";
        }
//        parameters.put("eType", "Ride");

        parameters.put("HandicapPrefEnabled", handicapval);
        parameters.put("PreferFemaleDriverEnable", femaleval);

        parameters.put("vTollPriceCurrencyCode", tollcurrancy);
        String tollskiptxt = "";

        if (istollIgnore) {
            tollamount = 0;
            tollskiptxt = "Yes";

        } else {
            tollskiptxt = "No";
        }
        parameters.put("fTollPrice", tollamount + "");
        parameters.put("eTollSkipped", tollskiptxt);


        parameters.put("eType", getCurrentCabGeneralType());
        if (reqPickUpFrag != null) {
            parameters.put("PromoCode", reqPickUpFrag.getAppliedPromoCode());
        }
        if (app_type.equalsIgnoreCase("UberX")) {
            parameters.put("Quantity", getIntent().getStringExtra("Quantity"));
        }
        if (app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
            if (isUfx) {
                parameters.put("Quantity", getIntent().getStringExtra("Quantity"));
            }

        }
//        for (Map.Entry entry : parameters.entrySet()) {
//            Utils.printLog("Printing", "Yes");
//            Utils.printLog("" + entry.getKey(), ":::" + entry.getValue());
//        }
//        Utils.printLog("pickUpLocAdd", "::" + parameters.get("pickUpLocAdd"));
//        Utils.printLog("scheduleDate", "::" + parameters.get("scheduleDate"));
//        Utils.printLog("TimeZone", "::" + parameters.get("TimeZone"));
//        Utils.printLog("iVehicleTypeId", "::" + parameters.get("iVehicleTypeId"));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {


                    if (generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equals("DO_EMAIL_PHONE_VERIFY") ||
                            generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equals("DO_PHONE_VERIFY") ||
                            generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equals("DO_EMAIL_VERIFY")) {
                        Bundle bn = new Bundle();
                        bn.putString("msg", "" + generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
                        bn.putString("UserProfileJson", userProfileJson);
                        accountVerificationAlert(generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_ALERT_RIDER_TXT"), bn);

                        return;
                    }

                    String action = generalFunc.getJsonValue(CommonUtilities.action_str, responseString);

                    if (action.equals("1")) {
                        setDestinationPoint("", "", "", false);
                        setDefaultView();
                        showBookingAlert(generalFunc.retrieveLangLBl("",
                                generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("",
                                generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    }

                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void chatMsg() {


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


//            FirebaseUser user = auth.getCurrentUser();


     /*  if (user == null) {
            signupForChate(driverDetailFrag.getTripData());
        } else {
            doLoginForChat(driverDetailFrag.getTripData());
        }*/
        //new OpenChatDetailDialog(MainActivity.this, driverDetailFrag.getTripData(), generalFunc, "");


        Bundle bn = new Bundle();

        bn.putString("iFromMemberId", driverDetailFrag.getTripData().get("iDriverId"));
        bn.putString("FromMemberImageName", driverDetailFrag.getTripData().get("DriverImage"));
        bn.putString("iTripId", driverDetailFrag.getTripData().get("iTripId"));
        bn.putString("FromMemberName", driverDetailFrag.getTripData().get("DriverName"));


        new StartActProcess(getActContext()).startActWithData(com.app85taxi.passenger.ChatActivity.class, bn);



       /* (findViewById(R.id.LoadingMapProgressBar)).setVisibility(View.VISIBLE);
        auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (user!=null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(generalFunc.getJsonValue("vName", userProfileJson)+" "+generalFunc.getJsonValue("vLastName", userProfileJson))
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Api", "User profile updated.");
                                    }
                                }
                            });
                }
                (findViewById(R.id.LoadingMapProgressBar)).setVisibility(View.GONE);
                if (!task.isSuccessful()) {

//                            Toast.makeText(MainActivity.this,"Authentication failed.", Toast.LENGTH_LONG).show();
                    generalFunc.showError();
                } else {
                    new OpenChatDetailDialog(MainActivity.this, driverDetailFrag.getTripData(), generalFunc, "");
                }

            }
        });*/


    }


    private void doLoginForChat(final HashMap<String, String> tripDataMap) {

          /*  try to login */
        (findViewById(R.id.LoadingMapProgressBar)).setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(tripDataMap.get("iTripId") + "Passenger" + generalFunc.getMemberId() + "@gmail.com", "Passenger_" + tripDataMap.get("iTripId") + generalFunc.getMemberId())
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
//                                    progressBar.setVisibility(View.GONE);
                        (findViewById(R.id.LoadingMapProgressBar)).setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
//                                        if (password.length() < 6) {
//                                            inputPassword.setError(getString(R.string.minimum_password));
//                                        } else {
                            //  Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
//                                        }

                            signupForChate(tripDataMap);
                        } else {
                            //  new OpenChatDetailDialog(MainActivity.this, tripDataMap, generalFunc, "");
                            Bundle bn = new Bundle();

                            bn.putString("iFromMemberId", tripDataMap.get("iDriverId"));
                            bn.putString("FromMemberImageName", tripDataMap.get("DriverImage"));
                            bn.putString("iTripId", tripDataMap.get("iTripId"));
                            bn.putString("FromMemberName", tripDataMap.get("DriverName"));


                            new StartActProcess(getActContext()).startActWithData(com.app85taxi.passenger.ChatActivity.class, bn);
                        }
                    }
                });
    }

    private void signupForChate(final HashMap<String, String> tripDataMap) {
        (findViewById(R.id.LoadingMapProgressBar)).setVisibility(View.VISIBLE);

        try {


            auth.createUserWithEmailAndPassword(tripDataMap.get("iTripId") + "Passenger" + generalFunc.getMemberId() + "@gmail.com", "Passenger_" + tripDataMap.get("iTripId") + generalFunc.getMemberId())
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            (findViewById(R.id.LoadingMapProgressBar)).setVisibility(View.GONE);
//                        Toast.makeText(MainActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
//                        progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                doLoginForChat(driverDetailFrag.getTripData());
                            } else {
                                // new OpenChatDetailDialog(MainActivity.this, tripDataMap, generalFunc, "");
                                Bundle bn = new Bundle();

                                bn.putString("iFromMemberId", tripDataMap.get("iDriverId"));
                                bn.putString("FromMemberImageName", tripDataMap.get("DriverImage"));
                                bn.putString("iTripId", tripDataMap.get("iTripId"));
                                bn.putString("FromMemberName", tripDataMap.get("DriverName"));


                                new StartActProcess(getActContext()).startActWithData(com.app85taxi.passenger.ChatActivity.class, bn);

                            }
                        }
                    });
        } catch (Exception e) {
            Utils.printLog("chat exception", e.toString());
        }

    }

    public void showBookingAlert() {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());


        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();

                if (btn_id == 0) {
                    Bundle bn = new Bundle();
                    bn.putString("UserProfileJson", userProfileJson);
                    bn.putBoolean("isrestart", true);
                    new StartActProcess(getActContext()).startActWithData(com.app85taxi.passenger.HistoryActivity.class, bn);


                } else {
                    Bundle bn = new Bundle();
                    bn.putString("USER_PROFILE_JSON", userProfileJson);

                    new StartActProcess(getActContext()).startActWithData(MainActivity.class, bn);

                    finishAffinity();

                }
            }
        });
        // generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_VIEW_BOOKINGS"));

        generateAlert.showAlertBox();
    }

    public void showBookingAlert(String message) {
        android.support.v7.app.AlertDialog alertDialog;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_booking_view, null);
        builder.setView(dialogView);

        final MTextView titleTxt = (MTextView) dialogView.findViewById(R.id.titleTxt);
        final MTextView mesasgeTxt = (MTextView) dialogView.findViewById(R.id.mesasgeTxt);


        titleTxt.setText(generalFunc.retrieveLangLBl("Booking Successful", "LBL_BOOKING_SUCESS"));
        mesasgeTxt.setText(message);


        builder.setNegativeButton(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Bundle bn = new Bundle();
                bn.putString("USER_PROFILE_JSON", userProfileJson);


                new StartActProcess(getActContext()).startActWithData(MainActivity.class, bn);

                finishAffinity();
            }
        });

        builder.setPositiveButton(generalFunc.retrieveLangLBl("Done", "LBL_VIEW_BOOKINGS"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Bundle bn = new Bundle();
                bn.putString("UserProfileJson", userProfileJson);
                bn.putBoolean("isrestart", true);
                new StartActProcess(getActContext()).startActWithData(com.app85taxi.passenger.HistoryActivity.class, bn);
            }
        });


        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

//        final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        positiveButton.setTextColor(getResources().getColor(R.color.gray));
//        final Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//        negativeButton.setTextColor(getResources().getColor(R.color.black));

    }


    public void deliverNow(Intent data) {

        this.deliveryData = data;

        requestPickUp();
    }

    public void requestPickUp() {


        setLoadAvailCabTaskValue(true);
        requestNearestCab = new RequestNearestCab(getActContext(), generalFunc);
        requestNearestCab.run();

        String driverIds = getAvailableDriverIds();
        Utils.printLog("driverIdsList", "::" + driverIds);

        JSONObject cabRequestedJson = new JSONObject();
        try {
            cabRequestedJson.put("Message", "CabRequested");
            cabRequestedJson.put("sourceLatitude", "" + getPickUpLocation().getLatitude());
            cabRequestedJson.put("sourceLongitude", "" + getPickUpLocation().getLongitude());
            cabRequestedJson.put("PassengerId", generalFunc.getMemberId());
            cabRequestedJson.put("PName", generalFunc.getJsonValue("vName", userProfileJson) + " "
                    + generalFunc.getJsonValue("vLastName", userProfileJson));
            cabRequestedJson.put("PPicName", generalFunc.getJsonValue("vImgName", userProfileJson));
            cabRequestedJson.put("PFId", generalFunc.getJsonValue("vFbId", userProfileJson));
            cabRequestedJson.put("PRating", generalFunc.getJsonValue("vAvgRating", userProfileJson));
            cabRequestedJson.put("PPhone", generalFunc.getJsonValue("vPhone", userProfileJson));
            cabRequestedJson.put("PPhoneC", generalFunc.getJsonValue("vPhoneCode", userProfileJson));
            cabRequestedJson.put("REQUEST_TYPE", getCurrentCabGeneralType());

            cabRequestedJson.put("selectedCatType", vUberXCategoryName);
            if (getDestinationStatus() == true) {
                cabRequestedJson.put("destLatitude", "" + getDestLocLatitude());
                cabRequestedJson.put("destLongitude", "" + getDestLocLongitude());
            } else {
                cabRequestedJson.put("destLatitude", "");
                cabRequestedJson.put("destLongitude", "");
            }

            if (deliveryData != null) {

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Utils.printLog("Data:Drivers:", "::" + cabRequestedJson.toString());

        if (!generalFunc.getJsonValue("Message", cabRequestedJson.toString()).equals("")) {
            requestNearestCab.setRequestData(driverIds, cabRequestedJson.toString());

            if (DRIVER_REQUEST_METHOD.equals("All")) {
                sendReqToAll(driverIds, cabRequestedJson.toString());
            } else if (DRIVER_REQUEST_METHOD.equals("Distance") || DRIVER_REQUEST_METHOD.equals("Time")) {
                sendReqByDist(driverIds, cabRequestedJson.toString());
            } else {
                sendReqToAll(driverIds, cabRequestedJson.toString());
            }
        }


    }

    public void sendReqToAll(String driverIds, String cabRequestedJson) {
        isreqnow = true;
        //sendRequestToDrivers(driverIds, cabRequestedJson);
        getTollcostValue(driverIds, cabRequestedJson);

        if (allCabRequestTask != null) {
            allCabRequestTask.stopRepeatingTask();
            allCabRequestTask = null;
        }
        allCabRequestTask = new UpdateFrequentTask(35 * 1000);
        allCabRequestTask.startRepeatingTask();
        allCabRequestTask.setTaskRunListener(new UpdateFrequentTask.OnTaskRunCalled() {
            @Override
            public void onTaskRun() {
                setRetryReqBtn(true);
                allCabRequestTask.stopRepeatingTask();
            }
        });

    }

    public void sendReqByDist(String driverIds, String cabRequestedJson) {
        if (sendNotificationToDriverByDist == null) {
            sendNotificationToDriverByDist = new SendNotificationsToDriverByDist(driverIds, cabRequestedJson);
        } else {
            sendNotificationToDriverByDist.startRepeatingTask();
        }
    }

    public void setRetryReqBtn(boolean isVisible) {
        if (isVisible == true) {
            if (requestNearestCab != null) {
                requestNearestCab.setVisibilityOfRetryArea(View.VISIBLE);
            }
        } else {
            if (requestNearestCab != null) {
                requestNearestCab.setVisibilityOfRetryArea(View.GONE);
            }
        }
    }

    public void retryReqBtnPressed(String driverIds, String cabRequestedJson) {

        if (DRIVER_REQUEST_METHOD.equals("All")) {
            sendReqToAll(driverIds, cabRequestedJson.toString());
        } else if (DRIVER_REQUEST_METHOD.equals("Distance") || DRIVER_REQUEST_METHOD.equals("Time")) {
            sendReqByDist(driverIds, cabRequestedJson.toString());
        } else {
            sendReqToAll(driverIds, cabRequestedJson.toString());
        }

        setRetryReqBtn(false);
    }

    public void setLoadAvailCabTaskValue(boolean value) {
        if (loadAvailCabs != null) {
            loadAvailCabs.setTaskKilledValue(value);
        }
    }

    public void setCurrentLoadedDriverList(ArrayList<HashMap<String, String>> currentLoadedDriverList) {
        this.currentLoadedDriverList = currentLoadedDriverList;

        if (app_type.equalsIgnoreCase("UberX")) {
            // load list here but wait for 5 seconds
            Utils.printLog("currentLoadedDriverList", "" + currentLoadedDriverList);
            redirectToMapOrList(Utils.Cab_UberX_Type_List, true);
//            if (currentLoadedDriverList.size() > 0) {
//                pickUpLocLabelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SET_PICK_UP_LOCATION_TXT"));
//            } else {
//                pickUpLocLabelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SEARCH_CAR_WAIT_TXT"));
//            }
        }
    }

    public ArrayList<String> getDriverLocationChannelList() {

        ArrayList<String> channels_update_loc = new ArrayList<>();

        if (currentLoadedDriverList != null) {

            for (int i = 0; i < currentLoadedDriverList.size(); i++) {
                channels_update_loc.add(Utils.pubNub_Update_Loc_Channel_Prefix + "" + (currentLoadedDriverList.get(i).get("driver_id")));

                Utils.printLog("Channels:", "::i:" + i + "::" + channels_update_loc.get(i));
            }

        }
        return channels_update_loc;
    }

    public ArrayList<String> getDriverLocationChannelList(ArrayList<HashMap<String, String>> listData) {

        ArrayList<String> channels_update_loc = new ArrayList<>();

        if (listData != null) {

            for (int i = 0; i < listData.size(); i++) {
                channels_update_loc.add(Utils.pubNub_Update_Loc_Channel_Prefix + "" + (listData.get(i).get("driver_id")));

                Utils.printLog("Channels:", "::i:" + i + "::" + channels_update_loc.get(i));
            }

        }
        return channels_update_loc;
    }

    public String getAvailableDriverIds() {
        String driverIds = "";

        ArrayList<HashMap<String, String>> finalLoadedDriverList = new ArrayList<HashMap<String, String>>();
        finalLoadedDriverList.addAll(currentLoadedDriverList);

        if (DRIVER_REQUEST_METHOD.equals("Distance")) {
            Collections.sort(finalLoadedDriverList, new HashMapComparator("DIST_TO_PICKUP"));
        }

        for (int i = 0; i < finalLoadedDriverList.size(); i++) {
            String iDriverId = finalLoadedDriverList.get(i).get("driver_id");

            driverIds = driverIds.equals("") ? iDriverId : (driverIds + "," + iDriverId);
        }

        return driverIds;
    }


    public void sendRequestToDrivers(String driverIds, String cabRequestedJson) {


        HashMap<String, String> requestCabData = new HashMap<String, String>();
        requestCabData.put("type", "sendRequestToDrivers");

        requestCabData.put("message", cabRequestedJson);
        requestCabData.put("userId", generalFunc.getMemberId());
        requestCabData.put("CashPayment", "" + isCashSelected);

        requestCabData.put("PickUpAddress", getPickUpLocationAddress());


        requestCabData.put("vTollPriceCurrencyCode", tollcurrancy);
        String tollskiptxt = "";

        if (istollIgnore) {
            tollamount = 0;
            tollskiptxt = "Yes";

        } else {
            tollskiptxt = "No";
        }
        requestCabData.put("fTollPrice", tollamount + "");
        requestCabData.put("eTollSkipped", tollskiptxt);

        //    requestCabData.put("SelectedCarTypeID", "" + getSelectedCabTypeId());

        if ((app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX))) {
            if (isUfx) {
                requestCabData.put("driverIds", generalFunc.retrieveValue(CommonUtilities.SELECTEDRIVERID));

            } else {
                requestCabData.put("driverIds", driverIds);
            }

        }
        if ((app_type.equalsIgnoreCase("UberX"))) {

            requestCabData.put("driverIds", generalFunc.retrieveValue(CommonUtilities.SELECTEDRIVERID));
        } else {

            requestCabData.put("driverIds", driverIds);

        }
        requestCabData.put("SelectedCarTypeID", "" + selectedCabTypeId);

        requestCabData.put("DestLatitude", getDestLocLatitude());
        requestCabData.put("DestLongitude", getDestLocLongitude());
        requestCabData.put("DestAddress", getDestAddress());

        requestCabData.put("PickUpLatitude", "" + getPickUpLocation().getLatitude());
        requestCabData.put("PickUpLongitude", "" + getPickUpLocation().getLongitude());

        Utils.printLog("getCurrentCabGeneralType()", getCurrentCabGeneralType());
        requestCabData.put("eType", getCurrentCabGeneralType());


        Utils.printLog("Api", "eType at sendRequestToDrivers:::" + this.getCurrentCabGeneralType());


        if ((app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX))) {
            if (isUfx) {
                requestCabData.put("Quantity", getIntent().getStringExtra("Quantity"));

            }


        }
        if (app_type.equalsIgnoreCase("UberX")) {
            requestCabData.put("Quantity", getIntent().getStringExtra("Quantity"));
        }

        if (cabSelectionFrag != null) {
            requestCabData.put("PromoCode", cabSelectionFrag.getAppliedPromoCode());
        }

//        requestCabData.put("PickUpAddGeoCodeResult", tempPickupGeoCode);
//        requestCabData.put("DestAddGeoCodeResult", tempDestGeoCode);

        Utils.printLog("requestCabData", "::" + requestCabData.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), requestCabData);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (cabSelectionFrag != null) {
                    cabSelectionFrag.isclickableridebtn = false;
                }
                Utils.printLog("responseString", "::" + responseString);
                if (responseString != null && !responseString.equals("")) {

                    generalFunc.sendHeartBeat();

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == false) {
                        Bundle bn = new Bundle();
                        bn.putString("msg", "" + generalFunc.getJsonValue(CommonUtilities.message_str, responseString));
                        bn.putString("UserProfileJson", userProfileJson);

                        String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);

                        if (message.equals("SESSION_OUT")) {
                            closeRequestDialog(false);
                            generalFunc.notifySessionTimeOut();
                            Utils.runGC();
                            return;
                        }
                        if (message.equals("NO_CARS") || message.equals("LBL_PICK_DROP_LOCATION_NOT_ALLOW")
                                || message.equals("LBL_DROP_LOCATION_NOT_ALLOW") || message.equals("LBL_PICKUP_LOCATION_NOT_ALLOW")) {
                            closeRequestDialog(false);
                            buildMessage(generalFunc.retrieveLangLBl("", message.equals("NO_CARS") ? "LBL_NO_CAR_AVAIL_TXT" : message), generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), false);
                        } else if (message.equals(CommonUtilities.GCM_FAILED_KEY) || message.equals(CommonUtilities.APNS_FAILED_KEY)) {
                            releaseScheduleNotificationTask();
                            generalFunc.restartApp();
                        } else if (generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equals("DO_EMAIL_PHONE_VERIFY") ||
                                generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equals("DO_PHONE_VERIFY") ||
                                generalFunc.getJsonValue(CommonUtilities.message_str, responseString).equals("DO_EMAIL_VERIFY")) {
                            closeRequestDialog(true);
                            accountVerificationAlert(generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_ALERT_RIDER_TXT"), bn);

                        } else {
                            closeRequestDialog(false);
                            buildMessage(generalFunc.retrieveLangLBl("", "LBL_REQUEST_FAILED_PROCESS"), generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), true);
                        }

                    }
                } else {
                    closeRequestDialog(true);
                    buildMessage(generalFunc.retrieveLangLBl("", "LBL_REQUEST_FAILED_PROCESS"), generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), false);
                }
            }
        });
        exeWebServer.execute();

        generalFunc.sendHeartBeat();
    }

    public void accountVerificationAlert(String message, final Bundle bn) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                if (btn_id == 1) {
                    generateAlert.closeAlertBox();
                    (new StartActProcess(getActContext())).startActForResult(com.app85taxi.passenger.VerifyInfoActivity.class, bn, Utils.VERIFY_INFO_REQ_CODE);
                } else if (btn_id == 0) {
                    generateAlert.closeAlertBox();
                }
            }
        });
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_CANCEL_TRIP_TXT"));
        generateAlert.showAlertBox();

    }

    public void closeRequestDialog(boolean isSetDefault) {
        if (requestNearestCab != null) {
            requestNearestCab.dismissDialog();
        }

        releaseScheduleNotificationTask();

        if (isSetDefault == true) {
            setDefaultView();
        }

    }

    public void releaseScheduleNotificationTask() {
        if (allCabRequestTask != null) {
            allCabRequestTask.stopRepeatingTask();
            allCabRequestTask = null;
        }

        if (sendNotificationToDriverByDist != null) {
            sendNotificationToDriverByDist.stopRepeatingTask();
            sendNotificationToDriverByDist = null;
        }
    }

    public DriverDetailFragment getDriverDetailFragment() {
        return driverDetailFrag;
    }

    public void buildMessage(String message, String positiveBtn, final boolean isRestart) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();
                if (isRestart == true) {
                    generalFunc.restartApp();
                } else if (!TextUtils.isEmpty(tripId) && eTripType.equals(CabGeneralType_Deliver)) {

                    generalFunc.autoLogin(MainActivity.this, tripId);
                }
            }
        });
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(positiveBtn);
        generateAlert.showAlertBox();
    }

    public void buildTripEndMessage(String message, String positiveBtn) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();

                String tripDetailJson = generalFunc.getJsonValue("TripDetails", userProfileJson);
                eTripType = generalFunc.getJsonValue("eType", tripDetailJson);

                if (!eTripType.equals(CabGeneralType_Deliver)) {
                    Bundle bn = new Bundle();
                    bn.putString("tripId", generalFunc.getJsonValue("iTripId", tripDetailJson));
                    new StartActProcess(getActContext()).startActWithData(com.app85taxi.passenger.RatingActivity.class, bn);
                    stopReceivingPrivateMsg();
                    ActivityCompat.finishAffinity(MainActivity.this);
                } else if (!TextUtils.isEmpty(tripId) && eTripType.equals(CabGeneralType_Deliver) && isOkPressed != true) {
                    isOkPressed = true;
                    generalFunc.autoLogin(MainActivity.this, tripId);

                } else if (TextUtils.isEmpty(tripId)) {
                    isOkPressed = false;
                    //generalFunc.restartApp();
                    generalFunc.restartwithGetDataApp();
                } else if (!TextUtils.isEmpty(tripId) && eTripType.equals(CabGeneralType_Deliver) && tripStatus.equalsIgnoreCase("TripCanelled")) {
                    generalFunc.autoLogin(MainActivity.this, tripId);
                }
            }
        });
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(positiveBtn);
        generateAlert.showAlertBox();
    }

    public void onGcmMessageArrived(String message) {

        Utils.printLog("GCM", "::" + message);
        String driverMsg = generalFunc.getJsonValue("Message", message);
        currentTripId = generalFunc.getJsonValue("iTripId", message);

        if (driverMsg.equals("CabRequestAccepted")) {
//            closeRequestDialog(false);
//            configureDeliveryView(true);
//
            isDriverAssigned = true;
            addDrawer.setIsDriverAssigned(isDriverAssigned);
            userLocBtnImgView.setVisibility(View.GONE);
//
//            assignedDriverId = generalFunc.getJsonValue("iDriverId", message);
//            assignedTripId = generalFunc.getJsonValue("iTripId", message);
//            if (generalFunc.isJSONkeyAvail("iCabBookingId", message) == true && !generalFunc.getJsonValue("iCabBookingId", message).trim().equals("")) {
//                generalFunc.restartApp();
//            } else {
//                configureAssignedDriver(false);
//            }

            if (generalFunc.checkLocationPermission(true) == true) {
                getMap().setMyLocationEnabled(false);
            }

            isDriverAssigned = true;

            assignedDriverId = generalFunc.getJsonValue("iDriverId", message);
            assignedTripId = generalFunc.getJsonValue("iTripId", message);


            if (generalFunc.isJSONkeyAvail("iCabBookingId", message) == true && !generalFunc.getJsonValue("iCabBookingId", message).trim().equals("")) {
                // generalFunc.restartApp();
                generalFunc.restartwithGetDataApp();
            } else {

                Utils.printLog("Api", "getCurrentCabGeneralType" + getCurrentCabGeneralType());

                if ((!TextUtils.isEmpty(tripId) && (getCurrentCabGeneralType().equalsIgnoreCase(CabGeneralType_Deliver)) ||
                        getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_Ride)
                        || getCurrentCabGeneralType().equalsIgnoreCase("Deliver"))) {
                    configureAssignedDriver(false);
                    pinImgView.setVisibility(View.GONE);
                    closeRequestDialog(false);
                    configureDeliveryView(true);
                } else {
                    pinImgView.setVisibility(View.GONE);
                    setDestinationPoint("", "", "", false);
                    closeRequestDialog(true);
                    showOngoingTripViewDialoge();
                }
            }
            tripStatus = "Assigned";

        } else if (driverMsg.equals("TripEnd")) {
            if (isDriverAssigned == false) {
                return;
            }
//            tripStatus = "TripEnd";
//            if (getCurrentCabGeneralType().equals(Utils.CabGeneralType_Deliver)) {
//                buildTripEndMessage(generalFunc.retrieveLangLBl("Your package has been successfully delivered.", "LBL_DELIVERY_END_MSG_TXT"),
//                        generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
//            } else {
//                buildTripEndMessage(generalFunc.retrieveLangLBl("", "LBL_END_TRIP_DIALOG_TXT"),
//                        generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
//            }
//
//            if (driverAssignedHeaderFrag != null) {
//                driverAssignedHeaderFrag.setTaskKilledValue(true);
//            }
            tripStatus = "TripEnd";
            if (driverAssignedHeaderFrag != null) {


                if ((!TextUtils.isEmpty(tripId) && (getCurrentCabGeneralType().equalsIgnoreCase(CabGeneralType_Deliver)))) {
                    if (isUfx) {
                        if (tripId.equalsIgnoreCase(currentTripId)) {

                            buildTripEndMessage(generalFunc.retrieveLangLBl("Your package has been successfully delivered.", "LBL_DELIVERY_END_MSG_TXT"),
                                    generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                        }

                    } else {
                        buildTripEndMessage(generalFunc.retrieveLangLBl("Your package has been successfully delivered.", "LBL_DELIVERY_END_MSG_TXT"),
                                generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    }
                }

               /* if (getCurrentCabGeneralType().equals(Utils.CabGeneralType_Deliver) && driverAssignedHeaderFrag!=null) {
                    buildTripEndMessage(generalFunc.retrieveLangLBl("Your package has been successfully delivered.", "LBL_DELIVERY_END_MSG_TXT"),
                            generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                }*/
                else {
                    buildTripEndMessage(generalFunc.retrieveLangLBl("", "LBL_END_TRIP_DIALOG_TXT"),
                            generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                }

                if (driverAssignedHeaderFrag != null) {
                    driverAssignedHeaderFrag.setTaskKilledValue(true);
                }


            }

        } else if (driverMsg.equals("TripStarted")) {
            if (isDriverAssigned == false) {
                return;
            }
//            tripStatus = "TripStarted";
//            buildMessage(generalFunc.retrieveLangLBl("", "LBL_START_TRIP_DIALOG_TXT"),
//                    generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), false);
//
//            if (driverAssignedHeaderFrag != null) {
//                driverAssignedHeaderFrag.setTripStartValue(true);
//                driverAssignedHeaderFrag.removeSourceTimeMarker();
//            }
//
//            if (driverDetailFrag != null) {
//                driverDetailFrag.configTripStartView(generalFunc.getJsonValue("VerificationCode", message));
//            }
            tripStatus = "TripStarted";
//            if ((!TextUtils.isEmpty(tripId) && (getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_Deliver)) ||
//                    getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_Ride))) {
//                if (tripId.equalsIgnoreCase(currentTripId)) {
//                    buildMessage(generalFunc.retrieveLangLBl("", "LBL_START_TRIP_DIALOG_TXT"),
//                            generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), false);
//                }
//            } else {

            if (driverAssignedHeaderFrag != null) {

                buildMessage(generalFunc.retrieveLangLBl("", "LBL_START_TRIP_DIALOG_TXT"),
                        generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), false);

                if (driverAssignedHeaderFrag != null) {
                    driverAssignedHeaderFrag.setTripStartValue(true);
                    driverAssignedHeaderFrag.removeSourceTimeMarker();
                }

                if (driverDetailFrag != null) {
                    driverDetailFrag.configTripStartView(generalFunc.getJsonValue("VerificationCode", message));
                }
            }
            //  }

        } else if (driverMsg.equals("DestinationAdded")) {
            if (isDriverAssigned == false) {
                return;
            }

            generateNotification(getActContext(), generalFunc.retrieveLangLBl("Destination is added by driver.", "LBL_DEST_ADD_BY_DRIVER"));
            buildMessage(generalFunc.retrieveLangLBl("Destination is added by driver.", "LBL_DEST_ADD_BY_DRIVER"), generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), false);

            String destLatitude = generalFunc.getJsonValue("DLatitude", message);
            String destLongitude = generalFunc.getJsonValue("DLongitude", message);
            String destAddress = generalFunc.getJsonValue("DAddress", message);

            setDestinationPoint(destLatitude, destLongitude, destAddress, true);
            if (driverAssignedHeaderFrag != null) {
                driverAssignedHeaderFrag.setDestinationAddress();
                driverAssignedHeaderFrag.configDestinationView();
            }
        } else if (driverMsg.equals("TripCancelledByDriver")) {


            if (MyApp.getCurrentAct() instanceof com.app85taxi.passenger.ChatActivity) {
                com.app85taxi.passenger.ChatActivity chatActobj = (com.app85taxi.passenger.ChatActivity) MyApp.getCurrentAct();
                chatActobj.onGcmMessageArrived(generalFunc.getJsonValue("Reason", message));
            }

            if (isDriverAssigned == false) {
                return;
            }
//            tripStatus = "TripCanelled";
//            String reason = generalFunc.getJsonValue("Reason", message);
//            String isTripStarted = generalFunc.getJsonValue("isTripStarted", message);
//
//            if (isTripStarted.equals("true")) {
//                Utils.generateNotification(getActContext(), generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + reason + "\n" + generalFunc.retrieveLangLBl("", "LBL_CANCEL_TRIP_BY_DRIVER_MSG_SUFFIX"));
//
//                buildTripEndMessage(generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + reason + "\n" + generalFunc.retrieveLangLBl("", "LBL_CANCEL_TRIP_BY_DRIVER_MSG_SUFFIX"),
//                        generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
//            } else {
//                Utils.generateNotification(getActContext(), generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + reason + "\n" + generalFunc.retrieveLangLBl("", "LBL_CANCEL_TRIP_BY_DRIVER_MSG_SUFFIX"));
//
//                buildMessage(generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + reason + "\n" + generalFunc.retrieveLangLBl("", "LBL_CANCEL_TRIP_BY_DRIVER_MSG_SUFFIX"),
//                        generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), true);
//            }
//            if (driverAssignedHeaderFrag != null) {
//                driverAssignedHeaderFrag.setTaskKilledValue(true);
//            }
            tripStatus = "TripCanelled";
            if (driverAssignedHeaderFrag != null) {
                String reason = generalFunc.getJsonValue("Reason", message);
                String isTripStarted = generalFunc.getJsonValue("isTripStarted", message);

                if (isTripStarted.equals("true")) {
                    Utils.generateNotification(MainActivity.this, generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + reason);

                    if (tripId.equalsIgnoreCase(currentTripId) || (getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_Ride)) || (getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX))) {

                        buildTripEndMessage(generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + reason,
                                generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    }
                } else {
                    Utils.generateNotification(MainActivity.this, generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + reason);

                    buildMessage(generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + reason,
                            generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), true);
                }
                if (driverAssignedHeaderFrag != null) {
                    driverAssignedHeaderFrag.setTaskKilledValue(true);
                }
            }
        }
    }

    public DriverAssignedHeaderFragment getDriverAssignedHeaderFrag() {
        return driverAssignedHeaderFrag;
    }

    public void unSubscribeCurrentDriverChannels() {
        if (configPubNub != null && currentLoadedDriverList != null) {
            configPubNub.unSubscribeToChannels(getDriverLocationChannelList());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (loadAvailCabs != null) {
            loadAvailCabs.onPauseCalled();
        }

        if (driverAssignedHeaderFrag != null) {
            driverAssignedHeaderFrag.onPauseCalled();
        }

        unSubscribeCurrentDriverChannels();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (generalFunc.isLocationEnabled()) {
            isFrompickupaddress = true;
            //   NoLocationView();
        }

        //String TripDetails = generalFunc.getJsonValue("TripDetails", userProfileJson);

        String vTripStatus = generalFunc.getJsonValue("vTripStatus", userProfileJson);

        if (vTripStatus != null && !(vTripStatus.contains("Not Active"))) {

            try {
                // userLocBtnImgView.setVisibility(View.GONE);

                if (!vTripStatus.contains("Not Requesting")) {
                    if (gMap != null) {
                        if (generalFunc.checkLocationPermission(true) == true) {

                            getMap().setMyLocationEnabled(false);
                        }
                    }
                } else {
                    if (!isgpsview) {
                        NoLocationView();
                    }
                }
            } catch (Exception e) {

            }


        } else {
            if (!isgpsview) {
                NoLocationView();
            }

        }

        userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        if (addDrawer != null) {
            addDrawer.userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
        }

        if (iswallet) {

            userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);

            if (addDrawer != null) {
                addDrawer.changeUserProfileJson(userProfileJson);
            }
            iswallet = false;
        }

        if (loadAvailCabs != null) {
            loadAvailCabs.onResumeCalled();
        }
        app_type = generalFunc.getJsonValue("APP_TYPE", userProfileJson);

        registerGcmMsgReceiver();


        if (driverAssignedHeaderFrag != null) {
            driverAssignedHeaderFrag.onResumeCalled();
            pinImgView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (userLocBtnImgView).getLayoutParams();
            params.bottomMargin = Utils.dipToPixels(getActContext(), 100);
        }

        if (configPubNub != null && currentLoadedDriverList != null) {
            configPubNub.subscribeToChannels(getDriverLocationChannelList());
        }

        if (configPubNub != null) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (configPubNub != null) {
                        configPubNub.subscribeToPrivateChannel();
                    }
                }
            }, 5000);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unRegisterGcmReceiver();
        stopReceivingPrivateMsg();

        releaseScheduleNotificationTask();
    }

    public void registerGcmMsgReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonUtilities.driver_message_arrived_intent_action);

        registerReceiver(gcmMessageBroadCastReceiver, filter);
    }

    public void stopReceivingPrivateMsg() {
        if (configPubNub != null) {
            configPubNub.unSubscribeToPrivateChannel();
            generalFunc.sendHeartBeat();
            configPubNub.releaseInstances();
            configPubNub = null;
            Utils.runGC();
        }
    }

    public void unRegisterGcmReceiver() {
        try {
            unregisterReceiver(gcmMessageBroadCastReceiver);
        } catch (Exception e) {

        }
    }

    public void setDriverImgView(SelectableRoundedImageView driverImgView) {
        this.driverImgView = driverImgView;
    }

    public Bitmap getDriverImg() {

        try {
            if (driverImgView != null) {
                driverImgView.buildDrawingCache();
                Bitmap driverBitmap = driverImgView.getDrawingCache();

                if (driverBitmap != null) {
                    return driverBitmap;
                } else {
                    return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_no_pic_user);
                }
            }

            return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_no_pic_user);
        } catch (Exception e) {
            return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_no_pic_user);
        }
    }

    public Bitmap getUserImg() {
        try {
            ((SelectableRoundedImageView) findViewById(R.id.userImgView)).buildDrawingCache();
            Bitmap userBitmap = ((SelectableRoundedImageView) findViewById(R.id.userImgView)).getDrawingCache();
//        Bitmap userBitmap = ((BitmapDrawable) ((SelectableRoundedImageView) findViewById(R.id.userImgView)).getDrawable()).getBitmap();

            if (userBitmap != null) {
                return userBitmap;
            } else {
                return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_no_pic_user);
            }
        } catch (Exception e) {
            return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_no_pic_user);
        }

    }

    public void pubNubStatus(String status) {

    }

    public void pubNubMsgArrived(final String message) {

        Utils.printLog("message::OUT UI thread", "YES::" + generalFunc.getJsonValue("MsgType", message));

        currentTripId = generalFunc.getJsonValue("iTripId", message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.printLog("message::IN UI thread", "YES");

                String msgType = generalFunc.getJsonValue("MsgType", message);

                Utils.printLog("msgType::IN UI thread", "msgType::" + msgType);
                if (msgType.equals("TripEnd")) {
                    Utils.printLog("tripend", "called");

                }
                if (msgType.equals("LocationUpdate")) {
                    if (loadAvailCabs == null) {
                        return;
                    }

                    String iDriverId = generalFunc.getJsonValue("iDriverId", message);
                    String vLatitude = generalFunc.getJsonValue("vLatitude", message);
                    String vLongitude = generalFunc.getJsonValue("vLongitude", message);

                    Marker driverMarker = getDriverMarkerOnPubNubMsg(iDriverId, false);


                    LatLng driverLocation_update = new LatLng(generalFunc.parseDoubleValue(0.0, vLatitude),
                            generalFunc.parseDoubleValue(0.0, vLongitude));

                    if (driverAssignedHeaderFrag != null) {
                        driverAssignedHeaderFrag.rotateMarkerBasedonDistance(driverLocation_update);
                    }
//                    Utils.animateMarker(driverMarker, driverLocation_update, false, gMap, generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX") ? false : true,false);

                    Utils.printLog("message::iDriverId", "MainAct:iDriverId" + iDriverId);


                } else if (msgType.equals("TripRequestCancel")) {
//                    tripStatus = "TripCanelled";
//                    if (DRIVER_REQUEST_METHOD.equals("Distance") || DRIVER_REQUEST_METHOD.equals("Time")) {
//                        if (sendNotificationToDriverByDist != null) {
//                            sendNotificationToDriverByDist.incTask();
//                        }
//                    }

                    tripStatus = "TripCanelled";
                    if (TextUtils.isEmpty(tripId) && eTripType.equals(CabGeneralType_Deliver) && getCurrentCabGeneralType().equals(CabGeneralType_Deliver)) {
                        if (tripId.equalsIgnoreCase(currentTripId)) {
                            if (DRIVER_REQUEST_METHOD.equals("Distance") || DRIVER_REQUEST_METHOD.equals("Time")) {
                                if (sendNotificationToDriverByDist != null) {
                                    sendNotificationToDriverByDist.incTask();
                                }
                            }
                        }
                    } else {
                        if (DRIVER_REQUEST_METHOD.equals("Distance") || DRIVER_REQUEST_METHOD.equals("Time")) {
                            if (sendNotificationToDriverByDist != null) {
                                sendNotificationToDriverByDist.incTask();
                            }
                        }
                    }
                } else if (msgType.equals("LocationUpdateOnTrip")) {
                    if (driverAssignedHeaderFrag != null) {
                        driverAssignedHeaderFrag.updateDriverLocation(message);
                    }
                } else if (msgType.equals("DriverArrived")) {
//                    tripStatus = "DriverArrived";
//                    if (driverAssignedHeaderFrag != null) {
//                        driverAssignedHeaderFrag.isDriverArrived = true;
//                        driverAssignedHeaderFrag.isDriverArrivedNotGenerated = true;
//                        addDrawer.configDrawer(false);
//                        Utils.generateNotification(getActContext(), generalFunc.retrieveLangLBl("", "LBL_DRIVER_ARRIVED_NOTIFICATION"));
//                        driverAssignedHeaderFrag.setDriverStatusTitle(generalFunc.retrieveLangLBl("", "LBL_DRIVER_ARRIVED_TXT"));
//                    }


                    tripStatus = "DriverArrived";
                    if (driverAssignedHeaderFrag != null) {
                        driverAssignedHeaderFrag.isDriverArrived = true;
                        driverAssignedHeaderFrag.setDriverStatusTitle(generalFunc.retrieveLangLBl("", "LBL_DRIVER_ARRIVED_TXT"));
                    }

                    Utils.printLog("eTripType", eTripType);
                    if (!isUfx) {
                        if (!eTripType.equals("UberX")) {
                            Utils.generateNotification(getActContext(), generalFunc.retrieveLangLBl("", "LBL_DRIVER_ARRIVED_TXT"));
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DRIVER_ARRIVED_NOTIFICATION"));
                        }
                    }

                    if (TextUtils.isEmpty(tripId) && eTripType.equals(CabGeneralType_Deliver) && getCurrentCabGeneralType().equals(CabGeneralType_Deliver)) {
                        if (tripId.equalsIgnoreCase(currentTripId)) {
                            if (!eTripType.equals("UberX")) {
                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DRIVER_ARRIVED_TXT"));
                            }

                        }
                    } else {
                        if (!isUfx) {
                            if (!eTripType.equals("UberX") && eTripType != null && !eTripType.equals("")) {
                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DRIVER_ARRIVED_TXT"));
                            }
                        }
                    }
                } else {

                    onGcmMessageArrived(message);

                }

            }
        });

        Utils.printLog("message::", "MainAct:" + message);
    }

    public Marker getDriverMarkerOnPubNubMsg(String iDriverId, boolean isRemoveFromList) {
        ArrayList<Marker> currentDriverMarkerList = loadAvailCabs.getDriverMarkerList();

        for (int i = 0; i < currentDriverMarkerList.size(); i++) {
            Marker marker = currentDriverMarkerList.get(i);

            String driver_id = marker.getTitle().replace("DriverId", "");

            if (driver_id.equals(iDriverId)) {

                if (isRemoveFromList) {
                    loadAvailCabs.getDriverMarkerList().remove(i);
                }

                return marker;
            }

        }

        return null;
    }

    public Integer getDriverMarkerPosition(String iDriverId) {
        ArrayList<Marker> currentDriverMarkerList = loadAvailCabs.getDriverMarkerList();

        for (int i = 0; i < currentDriverMarkerList.size(); i++) {
            Marker marker = currentDriverMarkerList.get(i);
            String driver_id = marker.getTitle().replace("DriverId----------DriverId----------", "");
            if (driver_id.equals(iDriverId)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBackPressed() {


        if (addDrawer.checkDrawerState(false)) {
            return;
        }

        if (cabSelectionFrag == null) {
            super.onBackPressed();


        } else {
            getSupportFragmentManager().beginTransaction().remove(cabSelectionFrag).commit();
            cabSelectionFrag = null;
            mainHeaderFrag.menuImgView.setVisibility(View.VISIBLE);
            mainHeaderFrag.backImgView.setVisibility(View.GONE);
            cabTypesArrList.clear();
            //  mainHeaderFrag.setDestinationAddress(generalFunc.retrieveLangLBl("", "LBL_ADD_DESTINATION_BTN_TXT"));
            mainHeaderFrag.setDefaultView();
            pinImgView.setVisibility(View.GONE);
            selectedCabTypeId = loadAvailCabs.getFirstCarTypeID();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (userLocBtnImgView).getLayoutParams();
            params.bottomMargin = Utils.dipToPixels(getActContext(), 10);


            return;
        }


        if ((!TextUtils.isEmpty(tripId) &&
                (getCurrentCabGeneralType().equalsIgnoreCase(CabGeneralType_Deliver) &&
                        eTripType.equalsIgnoreCase(CabGeneralType_Deliver)))) {
           /* tripId="";
            setRiderDefaultView();
            setDefaultView();
            // reset Map
            resetMap();*/
            generalFunc.autoLogin(MainActivity.this, "");
           /* new OpenMainProfile(getActContext(),
                    generalFunc.getJsonValue(CommonUtilities.message_str, userProfileJson), false, generalFunc,"").startProcess();
            */
            return;
        }

        super.onBackPressed();
    }

    public Context getActContext() {
        return MainActivity.this;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, 1, 0, "" + generalFunc.retrieveLangLBl("", "LBL_CALL_TXT"));
        menu.add(0, 2, 0, "" + generalFunc.retrieveLangLBl("", "LBL_MESSAGE_TXT"));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == 1) {

            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + driverDetailFrag.getDriverPhone()));
                startActivity(callIntent);
            } catch (Exception e) {
                // TODO: handle exception
            }

        } else if (item.getItemId() == 2) {

            try {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", "" + driverDetailFrag.getDriverPhone());
                startActivity(smsIntent);
            } catch (Exception e) {
                // TODO: handle exception
            }

        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.MY_PROFILE_REQ_CODE && resultCode == RESULT_OK && data != null) {
            String userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
            this.userProfileJson = userProfileJson;
            addDrawer.changeUserProfileJson(this.userProfileJson);
        } else if (requestCode == Utils.VERIFY_INFO_REQ_CODE && resultCode == RESULT_OK && data != null) {

            String msgType = data.getStringExtra("MSG_TYPE");

            if (msgType.equalsIgnoreCase("EDIT_PROFILE")) {
                addDrawer.openMenuProfile();
            }
            this.userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
            addDrawer.userProfileJson = this.userProfileJson;
            addDrawer.buildDrawer();
        } else if (requestCode == Utils.VERIFY_INFO_REQ_CODE) {

            this.userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
            addDrawer.userProfileJson = this.userProfileJson;
            addDrawer.buildDrawer();
        } else if (requestCode == Utils.CARD_PAYMENT_REQ_CODE && resultCode == RESULT_OK && data != null) {
            iswallet = true;
            String userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
            this.userProfileJson = userProfileJson;
            addDrawer.changeUserProfileJson(this.userProfileJson);
        } else if (requestCode == Utils.DELIVERY_DETAILS_REQ_CODE && resultCode == RESULT_OK && data != null) {
            if (!getCabReqType().equals(Utils.CabReqType_Later)) {
                deliverNow(data);
            } else {

            }
        } else if (requestCode == Utils.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                LatLng placeLocation = place.getLatLng();

                setDestinationPoint(placeLocation.latitude + "", placeLocation.longitude + "", place.getAddress().toString(), true);
                mainHeaderFrag.setDestinationAddress(place.getAddress().toString());

                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(placeLocation, 14.0f);

                if (gMap != null) {
                    gMap.clear();
                    gMap.moveCamera(cu);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);


                generalFunc.showMessage(generalFunc.getCurrentView(MainActivity.this),
                        status.getStatusMessage());
            } else if (requestCode == RESULT_CANCELED) {

            }


        } else if (requestCode == Utils.ASSIGN_DRIVER_CODE) {

            if (app_type.equals(Utils.CabGeneralTypeRide_Delivery_UberX)) {
                if (!isUfx) {
                    Bundle bn = new Bundle();
                    bn.putString("USER_PROFILE_JSON", userProfileJson);

                    finishAffinity();
                } else {
                    isUfx = false;
                    Bundle bn = new Bundle();
                    bn.putString("USER_PROFILE_JSON", userProfileJson);
                    new StartActProcess(getActContext()).startActWithData(MainActivity.class, bn);
                    finishAffinity();
                }
            } else {
                Bundle bn = new Bundle();
                bn.putString("USER_PROFILE_JSON", userProfileJson);


                finishAffinity();

            }
        } else if (requestCode == Utils.REQUEST_CODE_GPS_ON) {

            isgpsview = true;

            if (generalFunc.isLocationEnabled()) {
                showprogress();
                if (getLastLocation == null) {
                    getLastLocation = new GetLocationUpdates(getActContext(), 8);
                    getLastLocation.setLocationUpdatesListener(this);
                }
                if (getLastLocation != null) {


                    final Handler handler = new Handler();
                    int delay = 1000; //milliseconds

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            isgpsview = true;
                            //do something
                            if (getLastLocation.getLocation() != null) {
                                isgpsview = false;
                                hideprogress();

                                userLocation = getLastLocation.getLocation();
                                pickUpLocation = getLastLocation.getLocation();
                                NoLocationView();


                                if (mainHeaderFrag != null) {
                                    mainHeaderFrag.refreshFragment();

                                }
                            } else {
                                handler.postDelayed(this, 1000);
                            }


                        }
                    }, delay);
                }


            } else {
                isgpsview = false;
            }

        } else if (requestCode == Utils.SEARCH_PICKUP_LOC_REQ_CODE && resultCode == RESULT_OK && data != null && gMap != null) {

//            mainAct.configPickUpDrag(true, false, false);

            if (resultCode == RESULT_OK) {
                isFrompickupaddress = true;

                noloactionview.setVisibility(View.GONE);
                if (mainHeaderFrag != null) {
                    mainHeaderFrag.area_source.setVisibility(View.VISIBLE);

                }
                Place place = PlaceAutocomplete.getPlace(getActContext(), data);

                LatLng placeLocation = place.getLatLng();

                mainHeaderFrag.setPickUpAddress(place.getAddress().toString());

                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(placeLocation.latitude, placeLocation.longitude))
                        .zoom(gMap.getCameraPosition().zoom).build();
                gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                pickUpLocationAddress = place.getAddress().toString();

                pickUpLocation.setLatitude(placeLocation.latitude);
                pickUpLocation.setLongitude(placeLocation.longitude);
                if (userLocation == null) {
                    final Location location = new Location("user");
                    location.setLatitude(placeLocation.latitude);
                    location.setLongitude(placeLocation.longitude);
                    userLocation = location;
                    pickUpLocation = userLocation;
                }
                if (loadAvailCabs != null) {
                    loadAvailCabs.pickUpAddress = pickUpLocationAddress;
                    loadAvailCabs.changeCabs();

                }


                if (cabSelectionFrag != null) {
                    cabSelectionFrag.findRoute();
                }

                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(placeLocation, 14.0f);

                if (gMap != null) {
                    gMap.clear();
                    gMap.moveCamera(cu);
                }


            }

        }


    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == userLocBtnImgView.getId()) {
                isUserLocbtnclik = true;

                if (cabSelectionFrag == null) {
                    //mainHeaderFrag.setPickUpAddress();
                    CameraPosition cameraPosition = cameraForUserPosition();
                    if (cameraPosition != null)
                        getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                } else if (cabSelectionFrag != null) {
                    CameraPosition cameraPosition = cameraForUserPosition();
                    if (cameraPosition != null)
                        getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            } else if (i == emeTapImgView.getId()) {
                Bundle bn = new Bundle();
                bn.putString("UserProfileJson", userProfileJson);
                bn.putString("TripId", assignedTripId);
                new StartActProcess(getActContext()).startActWithData(com.app85taxi.passenger.ConfirmEmergencyTapActivity.class, bn);
            } else if (i == rideArea.getId()) {
                ((ImageView) findViewById(R.id.rideImg)).setImageResource(R.mipmap.ride_on);
                rideImgViewsel.setVisibility(View.VISIBLE);
                rideImgView.setVisibility(View.GONE);
                deliverImgView.setVisibility(View.VISIBLE);
                deliverImgViewsel.setVisibility(View.GONE);
                otherImageView.setVisibility(View.VISIBLE);
                otherImageViewsel.setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.deliverImg)).setImageResource(R.mipmap.delivery_off);

                ((MTextView) findViewById(R.id.rideTxt)).setTextColor(Color.parseColor("#000000"));
                //  ((MTextView) findViewById(R.id.rideTxt)).setBackground(getResources().getDrawable(R.mipmap.ride_on));
                ((MTextView) findViewById(R.id.deliverTxt)).setTextColor(Color.parseColor("#000000"));

                //  ((MTextView) findViewById(R.id.deliverTxt)).setBackground(getResources().getDrawable(R.mipmap.delivery_off));


                if (cabSelectionFrag != null) {
                    cabSelectionFrag.changeCabGeneralType(Utils.CabGeneralType_Ride);
                }
                RideDeliveryType = Utils.CabGeneralType_Ride;

            } else if (i == deliverArea.getId()) {

                rideImgViewsel.setVisibility(View.GONE);
                rideImgView.setVisibility(View.VISIBLE);
                deliverImgView.setVisibility(View.GONE);
                deliverImgViewsel.setVisibility(View.VISIBLE);
                otherImageView.setVisibility(View.VISIBLE);
                otherImageViewsel.setVisibility(View.GONE);

                ((ImageView) findViewById(R.id.rideImg)).setImageResource(R.mipmap.ride_off);
                ((ImageView) findViewById(R.id.deliverImg)).setImageResource(R.mipmap.delivery_on);

                ((MTextView) findViewById(R.id.rideTxt)).setTextColor(Color.parseColor("#000000"));
                // ((MTextView) findViewById(R.id.rideTxt)).setBackground(getResources().getDrawable(R.mipmap.ride_off));

                ((MTextView) findViewById(R.id.deliverTxt)).setTextColor(Color.parseColor("#000000"));
                //  ((MTextView) findViewById(R.id.deliverTxt)).setBackground(getResources().getDrawable(R.mipmap.delivery_on));

                if (cabSelectionFrag != null) {
                    cabSelectionFrag.changeCabGeneralType(CabGeneralType_Deliver);
                }
                RideDeliveryType = CabGeneralType_Deliver;
            } else if (i == otherArea.getId()) {
                rideImgViewsel.setVisibility(View.GONE);
                rideImgView.setVisibility(View.VISIBLE);
                deliverImgView.setVisibility(View.VISIBLE);
                deliverImgViewsel.setVisibility(View.GONE);
                otherImageView.setVisibility(View.GONE);
                otherImageViewsel.setVisibility(View.VISIBLE);


                RideDeliveryType = Utils.CabGeneralType_Ride;
                Bundle bn = new Bundle();
                bn.putBoolean("isback", true);
                bn.putString("USER_PROFILE_JSON", userProfileJson);

            } else if (i == prefBtnImageView.getId()) {

                userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);
                if (generalFunc.retrieveValue(CommonUtilities.FEMALE_RIDE_REQ_ENABLE).equalsIgnoreCase("Yes") && generalFunc.getJsonValue("eGender", userProfileJson).equals("")) {
                    genderDailog();

                } else {
                    openPrefrancedailog();
                }
            } else if (i == settingTxt.getId()) {
                isgpsview = true;
                new StartActProcess(getActContext()).
                        startActForResult(Settings.ACTION_LOCATION_SOURCE_SETTINGS, Utils.REQUEST_CODE_GPS_ON);

            } else if (i == pickupredirectTxt.getId()) {


                try {
                    LatLngBounds bounds = null;


                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setBoundsBias(bounds)
                            .build(MainActivity.this);
                    startActivityForResult(intent, Utils.SEARCH_PICKUP_LOC_REQ_CODE);
                } catch (Exception e) {

                }

            } else if (i == nolocbackImgView.getId()) {
                nolocmenuImgView.setVisibility(View.VISIBLE);
                nolocbackImgView.setVisibility(View.GONE);


            } else if (i == nolocmenuImgView.getId()) {
                addDrawer.checkDrawerState(true);
            }

        }
    }

    public void openPrefrancedailog() {


        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.activity_prefrance, null);

        final MTextView TitleTxt = (MTextView) dialogView.findViewById(R.id.TitleTxt);

        final CheckBox checkboxHandicap = (CheckBox) dialogView.findViewById(R.id.checkboxHandicap);
        final CheckBox checkboxFemale = (CheckBox) dialogView.findViewById(R.id.checkboxFemale);

        if (generalFunc.retrieveValue(CommonUtilities.HANDICAP_ACCESSIBILITY_OPTION).equalsIgnoreCase("yes")) {
            checkboxHandicap.setVisibility(View.VISIBLE);
        } else {
            checkboxHandicap.setVisibility(View.GONE);
        }

        if (generalFunc.retrieveValue(CommonUtilities.FEMALE_RIDE_REQ_ENABLE).equalsIgnoreCase("yes")) {
            if (!generalFunc.getJsonValue("eGender", userProfileJson).equalsIgnoreCase("Male")) {
                checkboxFemale.setVisibility(View.VISIBLE);
            } else {
                checkboxFemale.setVisibility(View.GONE);
            }
        } else {
            checkboxFemale.setVisibility(View.GONE);
        }
        if (isfemale) {
            checkboxFemale.setChecked(true);
        }

        if (ishandicap) {
            checkboxHandicap.setChecked(true);
        }
        MButton btn_type2 = btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
        int submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        btn_type2.setText(generalFunc.retrieveLangLBl("Update", "LBL_UPDATE"));
        btn_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref_dialog.dismiss();
                if (checkboxFemale.isChecked()) {
                    isfemale = true;

                } else {
                    isfemale = false;

                }
                if (checkboxHandicap.isChecked()) {
                    ishandicap = true;

                } else {
                    ishandicap = false;
                }

                if (loadAvailCabs != null) {
                    loadAvailCabs.changeCabs();
                }

            }
        });


        builder.setView(dialogView);
        TitleTxt.setText(generalFunc.retrieveLangLBl("Prefrance", "LBL_PREFRANCE_TXT"));
        checkboxHandicap.setText(generalFunc.retrieveLangLBl("Filter handicap accessibility drivers only", "LBL_MUST_HAVE_HANDICAP_ASS_CAR"));
        checkboxFemale.setText(generalFunc.retrieveLangLBl("Accept Female Only trip request", "LBL_ACCEPT_FEMALE_REQ_ONLY_PASSENGER"));


        pref_dialog = builder.create();
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(pref_dialog);
        }
        pref_dialog.show();

    }

    public class SendNotificationsToDriverByDist implements Runnable {

        String[] list_drivers_ids;
        String cabRequestedJson;
        int mInterval = 35 * 1000;
        int current_position_driver_id = 0;
        private Handler mHandler_sendNotification;

        public SendNotificationsToDriverByDist(String list_drivers_ids, String cabRequestedJson) {
            this.list_drivers_ids = list_drivers_ids.split(",");
            this.cabRequestedJson = cabRequestedJson;
            mHandler_sendNotification = new Handler();


            startRepeatingTask();
        }

        @Override
        public void run() {
            Utils.printLog("Notification task", "called run");

            setRetryReqBtn(false);

            if ((current_position_driver_id + 1) <= list_drivers_ids.length) {

                sendRequestToDrivers(list_drivers_ids[current_position_driver_id], cabRequestedJson);
                current_position_driver_id = current_position_driver_id + 1;

                mHandler_sendNotification.postDelayed(this, mInterval);
            } else {

                setRetryReqBtn(true);

                stopRepeatingTask();
            }

        }


        public void stopRepeatingTask() {
            mHandler_sendNotification.removeCallbacks(this);
            mHandler_sendNotification.removeCallbacksAndMessages(null);
            current_position_driver_id = 0;
        }

        public void incTask() {
            mHandler_sendNotification.removeCallbacks(this);
            mHandler_sendNotification.removeCallbacksAndMessages(null);
            this.run();
        }

        public void startRepeatingTask() {
            stopRepeatingTask();

            this.run();
        }

    }

    public void getTollcostValue(final String driverIds, final String cabRequestedJson) {
        if (generalFunc.retrieveValue(CommonUtilities.ENABLE_TOLL_COST).equalsIgnoreCase("yes")) {

            String url = CommonUtilities.TOLLURL + generalFunc.retrieveValue(CommonUtilities.TOLL_COST_APP_ID)
                    + "&app_code=" + generalFunc.retrieveValue(CommonUtilities.TOLL_COST_APP_CODE) + "&waypoint0=" + getPickUpLocation().getLatitude()
                    + "," + getPickUpLocation().getLongitude() + "&waypoint1=" + getDestLocLatitude() + "," + getDestLocLongitude() + "&mode=fastest;car";

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), url, true);
            exeWebServer.setLoaderConfig(getActContext(), false, generalFunc);
            exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
                @Override
                public void setResponse(String responseString) {


                    if (responseString != null && !responseString.equals("")) {
                        try {

                            String costs = generalFunc.getJsonValue("costs", responseString);

                            String currency = generalFunc.getJsonValue("currency", costs);
                            String details = generalFunc.getJsonValue("details", costs);
                            String tollCost = generalFunc.getJsonValue("tollCost", details);
                            if (!currency.equals("") && currency != null) {
                                tollcurrancy = currency;
                            }
                            if (!tollCost.equals("") && tollCost != null && !tollCost.equals("0.0")) {
                                tollamount = generalFunc.parseDoubleValue(0.0, tollCost);
                            }


                            TollTaxDialog(driverIds, cabRequestedJson);


                        } catch (Exception e) {

                        }


                    } else {
                        generalFunc.showError();
                    }

                }

            });
            exeWebServer.execute();


        } else {

            if (isrideschedule) {
                isrideschedule = false;
                bookRide();
            } else if (isreqnow) {
                isreqnow = false;
                sendRequestToDrivers(driverIds, cabRequestedJson);
            }


        }

    }

    public void TollTaxDialog(final String driverIds, final String cabRequestedJson) {

        if (!isTollCostdilaogshow) {
            if (tollamount != 0.0 && tollamount != 0 && tollamount != 0.00) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());

                LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.dialog_tolltax, null);

                final MTextView tolltaxTitle = (MTextView) dialogView.findViewById(R.id.tolltaxTitle);
                final MTextView tollTaxMsg = (MTextView) dialogView.findViewById(R.id.tollTaxMsg);
                final MTextView cancelTxt = (MTextView) dialogView.findViewById(R.id.cancelTxt);

                final CheckBox checkboxTolltax = (CheckBox) dialogView.findViewById(R.id.checkboxTolltax);

                checkboxTolltax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (checkboxTolltax.isChecked()) {
                            istollIgnore = true;
                        } else {
                            istollIgnore = false;
                        }

                    }
                });


                MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
                int submitBtnId = Utils.generateViewId();
                btn_type2.setId(submitBtnId);
                btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"));
                btn_type2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tolltax_dialog.dismiss();
                        isTollCostdilaogshow = true;

                        if (isrideschedule) {
                            isrideschedule = false;
                            bookRide();
                        } else if (isreqnow) {
                            isreqnow = false;
                            sendRequestToDrivers(driverIds, cabRequestedJson);
                        }

                    }
                });


                builder.setView(dialogView);
                tolltaxTitle.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_ROUTE"));
                tollTaxMsg.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_PRICE_DESC") + ": " + tollcurrancy + " " + tollamount);

                checkboxTolltax.setText(generalFunc.retrieveLangLBl("", "LBL_IGNORE_TOLL_ROUTE"));
                cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

                cancelTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tolltax_dialog.dismiss();
//                        isTollCostdilaogshow = true;
//                        tollamount = 0;
//                        if (isrideschedule) {
//                            isrideschedule = false;
//                            bookRide();
//                        } else if (isreqnow) {
//                            isreqnow = false;
//                            sendRequestToDrivers(driverIds, cabRequestedJson);
//                        }

                        closeRequestDialog(true);
                    }
                });


                tolltax_dialog = builder.create();
                if (generalFunc.isRTLmode() == true) {
                    generalFunc.forceRTLIfSupported(tolltax_dialog);
                }
                tolltax_dialog.show();
            } else {
                if (isrideschedule) {
                    isrideschedule = false;
                    bookRide();
                } else if (isreqnow) {
                    isreqnow = false;
                    sendRequestToDrivers(driverIds, cabRequestedJson);
                }
            }
        } else {
            if (isrideschedule) {
                isrideschedule = false;
                bookRide();
            } else if (isreqnow) {
                isreqnow = false;
                sendRequestToDrivers(driverIds, cabRequestedJson);
            }

//        if (!isTollCostdilaogshow) {
//
//            if (tollamount != 0.0 && tollamount != 0 && tollamount != 0.00) {
//                final GenerateAlertBox generateAlertBox = new GenerateAlertBox(getActContext());
//                generateAlertBox.setCancelable(false);
//                generateAlertBox.setContentMessage("TollTax Title", "TollTax Amount" + " : " + tollamount);
//                generateAlertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
//                    @Override
//                    public void handleBtnClick(int btn_id) {
//                        generateAlertBox.closeAlertBox();
//
//                        if (btn_id == 1) {
//                            isTollCostdilaogshow = true;
//
//                            if (isrideschedule) {
//                                isrideschedule = false;
//                                bookRide();
//                            } else if (isreqnow) {
//                                isreqnow = false;
//                                sendRequestToDrivers(driverIds, cabRequestedJson);
//                            }
//
//
//                        } else if (btn_id == 0) {
//                            isTollCostdilaogshow = true;
//                            tollamount = 0;
//                            if (isrideschedule) {
//                                isrideschedule = false;
//                                bookRide();
//                            } else if (isreqnow) {
//                                isreqnow = false;
//                                sendRequestToDrivers(driverIds, cabRequestedJson);
//                            }
//
//                        }
//                    }
//                });
//
//                generateAlertBox.setPositiveBtn(generalFunc.retrieveLangLBl("confirm", "LBL_CONFIRM_TXT"));
//                generateAlertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
//
//                generateAlertBox.showAlertBox();
//            } else {
//                if (isrideschedule) {
//                    isrideschedule = false;
//                    bookRide();
//                } else if (isreqnow) {
//                    isreqnow = false;
//                    sendRequestToDrivers(driverIds, cabRequestedJson);
//                }
//
//            }
//        } else {
//            if (isrideschedule) {
//                isrideschedule = false;
//                bookRide();
//            } else if (isreqnow) {
//                isreqnow = false;
//                sendRequestToDrivers(driverIds, cabRequestedJson);
//            }
//

        }
    }

    public void callgederApi(String egender)

    {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "updateUserGender");
        parameters.put("UserType", Utils.userType);
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("eGender", egender);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {
                Utils.printLog("Response", "::" + responseString);


                boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);


                String message = generalFunc.getJsonValue(CommonUtilities.message_str, responseString);
                if (isDataAvail) {
                    generalFunc.storedata(CommonUtilities.USER_PROFILE_JSON, message);
                    userProfileJson = generalFunc.retrieveValue(CommonUtilities.USER_PROFILE_JSON);

                    prefBtnImageView.performClick();
                }


            }
        });
        exeWebServer.execute();
    }

    public void genderDailog() {
        closeDrawer();
        final Dialog builder = new Dialog(getActContext(), R.style.Theme_Dialog);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(R.layout.gender_view);
        builder.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

//        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogView = inflater.inflate(R.layout.gender_view, null);

        final MTextView genderTitleTxt = (MTextView) builder.findViewById(R.id.genderTitleTxt);
        final MTextView maleTxt = (MTextView) builder.findViewById(R.id.maleTxt);
        final MTextView femaleTxt = (MTextView) builder.findViewById(R.id.femaleTxt);
        final ImageView gendercancel = (ImageView) builder.findViewById(R.id.gendercancel);
        final ImageView gendermale = (ImageView) builder.findViewById(R.id.gendermale);
        final ImageView genderfemale = (ImageView) builder.findViewById(R.id.genderfemale);
        final LinearLayout male_area = (LinearLayout) builder.findViewById(R.id.male_area);
        final LinearLayout female_area = (LinearLayout) builder.findViewById(R.id.female_area);

        genderTitleTxt.setText(generalFunc.retrieveLangLBl("Select your gender to continue", "LBL_SELECT_GENDER"));
        maleTxt.setText(generalFunc.retrieveLangLBl("Male", "LBL_MALE_TXT"));
        femaleTxt.setText(generalFunc.retrieveLangLBl("FeMale", "LBL_FEMALE_TXT"));

        gendercancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        male_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callgederApi("Male");
                builder.dismiss();

            }
        });
        female_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callgederApi("Female");
                builder.dismiss();

            }
        });


//        gender = builder.create();
//        gender.setCancelable(false);
//        if (generalFunc.isRTLmode() == true) {
//            generalFunc.forceRTLIfSupported(gender);
//        }
//        if (!gender.isShowing()) {
//            gender.show();
//        }

        builder.show();

    }

    public void NoLocationView() {
        if (!isFrompickupaddress) {
            if (!generalFunc.isLocationEnabled()) {
                if (userLocation == null) {
                    noloactionview.setVisibility(View.VISIBLE);
                    if (mainHeaderFrag != null) {
                        mainHeaderFrag.area_source.setVisibility(View.GONE);
                        mainHeaderFrag.area2.setVisibility(View.GONE);

                    }
                } else {
                    noloactionview.setVisibility(View.VISIBLE);
                    if (mainHeaderFrag != null) {
                        mainHeaderFrag.area_source.setVisibility(View.GONE);
                        mainHeaderFrag.area2.setVisibility(View.GONE);

                    }

                }
            } else {
                isFrompickupaddress = true;
//                if (mainHeaderFrag != null) {
//                    mainHeaderFrag.area_source.setVisibility(View.VISIBLE);
//                }
                noloactionview.setVisibility(View.GONE);
            }
        } else {
//            if(mainHeaderFrag!=null) {
//                mainHeaderFrag.area_source.setVisibility(View.VISIBLE);
//            }
            noloactionview.setVisibility(View.GONE);
        }

    }
}
