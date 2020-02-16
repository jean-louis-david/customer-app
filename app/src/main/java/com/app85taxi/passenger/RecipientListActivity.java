package com.app85taxi.passenger;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.adapter.files.RecipientListAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.app85taxi.passenger.R;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.MTextView;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RecipientListActivity extends AppCompatActivity {

    public static ArrayList<HashMap<String, String>> mainRecipientListData;
    public RecyclerView.Adapter adapter;
    public ArrayList<String> data = new ArrayList<>();
    public RecipientListAdapter recipientListAdapter;
    public ArrayList<HashMap<String, String>> recipientListData = new ArrayList<>();
    MTextView titleTxt;
    ImageView backImgView;
    ListView recipientListView;
    GeneralFunctions generalFunc;
    MTextView plusSignTxt;
    String vPackageTypeName = "", iPackageTypeId = "";
    int oldSize = 0;
    private boolean recipientExist;
    private LinearLayout SearchArea;
    private MaterialEditText searchEditBox;
    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // ignore
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // ignore
        }

        @Override
        public void afterTextChanged(Editable s) {
            Utils.printLog("Api", "*** Search value changed: " + s.toString() + "mainRecipientListData" + mainRecipientListData.size() + "oldSize" + oldSize);
            if (recipientListAdapter != null) {
                Utils.hideKeyboard(getActContext());
                recipientListAdapter.filter(s.toString());
                Utils.printLog("Api", "***** filter Size" + recipientListAdapter.filter(s.toString()));
            }

            if (!Utils.checkText(searchEditBox) && mainRecipientListData.size() == oldSize && recipientListData.size() == 0) {
                setOldList();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_list);

        generalFunc = new GeneralFunctions(getActContext());
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);

        recipientListView = (ListView) findViewById(R.id.recipientListView);
        plusSignTxt = (MTextView) findViewById(R.id.plusSignTxt);
        SearchArea = (LinearLayout) findViewById(R.id.SearchArea);
        plusSignTxt.setId(Utils.generateViewId());
        plusSignTxt.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());


        setLabels();

        recipientListAdapter = new RecipientListAdapter(getActContext(), recipientListData);
        recipientListView.setAdapter(recipientListAdapter);

        getRecipientList();

        recipientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (getCallingActivity() != null) {
                    Bundle bn = new Bundle();
                    bn.putString("recipientId", recipientListData.get(position).get("iRecipientId"));
                    bn.putString("recipientName", recipientListData.get(position).get("vName"));
                    bn.putString("recipientPhone", recipientListData.get(position).get("vPhoneCode") + "" + recipientListData.get(position).get("vPhone"));
                    bn.putString("recipientAddress", recipientListData.get(position).get("tAddress"));
                    bn.putString("recipientvLatitude", recipientListData.get(position).get("vLatitude"));
                    bn.putString("recipientvLongitude", recipientListData.get(position).get("vLongitude"));
                    bn.putString("recipientAddress", recipientListData.get(position).get("tAddress"));
                    vPackageTypeName = getIntent().hasExtra("vPackageTypeName") ? getIntent().getStringExtra("vPackageTypeName") : "";
                    iPackageTypeId = getIntent().hasExtra("iPackageTypeId") ? getIntent().getStringExtra("iPackageTypeId") : "";
                    bn.putString("oldRecipientId", getIntent().hasExtra("oldRecipientId") ? getIntent().getStringExtra("oldRecipientId") : "");
                    bn.putString("vPackageTypeName", vPackageTypeName);
                    bn.putString("iPackageTypeId", iPackageTypeId);
                    new StartActProcess(getActContext()).setOkResult(bn);
                    finish();
                }

            }
        });
    }

    public void getRecipientList() {
        recipientListData.clear();
        recipientListAdapter.notifyDataSetChanged();

        if (recipientListData != null && recipientListData.size() > 0) {
            Utils.printLog("recipientListData", "" + recipientListData.size());
            recipientListData.clear();
            recipientListAdapter.notifyDataSetChanged();
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getrecipients");
        parameters.put("iUserId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(),parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Recipient", "::" + responseString);

                if (responseString != null && !responseString.equals("")) {

//                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    JSONArray recipientsData = generalFunc.getJsonArray(CommonUtilities.message_str, responseString);
                    if (recipientsData != null) {

                        for (int i = 0; i < recipientsData.length(); i++) {
                            JSONObject recipientDetail = generalFunc.getJsonObject(recipientsData, i);

                            HashMap<String, String> map = new HashMap<String, String>();
                            Utils.printLog("recipientsData", "" + recipientDetail.toString());

                            map.put("iRecipientId", generalFunc.getJsonValue("iRecipientId", recipientDetail.toString()));
                            map.put("vName", generalFunc.getJsonValue("vName", recipientDetail.toString()));
                            map.put("tAddress", generalFunc.getJsonValue("tAddress", recipientDetail.toString()));
                            map.put("vPhone", generalFunc.getJsonValue("vPhone", recipientDetail.toString()));
                            map.put("vPhoneCode", generalFunc.getJsonValue("vPhoneCode", recipientDetail.toString()));
                            map.put("vEmail", generalFunc.getJsonValue("vEmail", recipientDetail.toString()));
                            map.put("vLatitude", generalFunc.getJsonValue("vLatitude", recipientDetail.toString()));
                            map.put("vLongitude", generalFunc.getJsonValue("vLongitude", recipientDetail.toString()));
                            map.put("LBL_DELETE_RECIPIENT_ALERT_TXT", generalFunc.retrieveLangLBl("", "LBL_DELETE_RECIPIENT_ALERT_TXT"));
                            map.put("LBL_BTN_YES_TXT", generalFunc.retrieveLangLBl("", "LBL_BTN_YES_TXT"));
                            map.put("LBL_BTN_NO_TXT", generalFunc.retrieveLangLBl("", "LBL_BTN_NO_TXT"));
                            map.put("LBL_PHONE", generalFunc.retrieveLangLBl("", "LBL_PHONE"));
                            map.put("LBL_EMAIL_LBL_TXT", generalFunc.retrieveLangLBl("", "LBL_EMAIL_LBL_TXT"));
                            Utils.printLog("vName", map.toString());
                            recipientListData.add(map);
                        }
                        mainRecipientListData = new ArrayList<HashMap<String, String>>(recipientListData);
                        setRecipientList();

                    } else {
//                        generalFunc.showGeneralMessage("", "LBL_ERROR_TXT");
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        setRecipientList();

    }

    private void setRecipientList() {
        oldSize = recipientListData.size();
        Utils.printLog("Api", "*** Recipient value changed: " + recipientListData.size() + "oldSize" + oldSize);

        if (recipientListData.size() > 0) {

            SearchArea.setVisibility(View.VISIBLE);

        } else {
            SearchArea.setVisibility(View.GONE);
        }

        recipientListAdapter.notifyDataSetChanged();


    }

    private void setOldList() {
        recipientListData.clear();
        recipientListData.addAll(mainRecipientListData);
        recipientListAdapter.notifyDataSetChanged();
    }


    private void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RECIPIENT_LIST_TXT"));
        searchEditBox = (MaterialEditText) findViewById(R.id.searchTxt);
        searchEditBox.setHideUnderline(true);
        searchEditBox.getLabelFocusAnimator().removeListener(null);
        searchEditBox.addTextChangedListener(searchTextWatcher);
        searchEditBox.setHint(generalFunc.retrieveLangLBl("", "LBL_COMPANY_TRIP_Search"));
    }

    public Context getActContext() {
        return RecipientListActivity.this;
    }

    public void callToRecipient(String vPhone) {
        try {


            String phnNo = vPhone.contains("+") ? vPhone : "+" + vPhone;
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + vPhone));
            startActivity(callIntent);

        } catch (Exception e) {
            // TODO: handle exception
        }


    }

    public void deleteRecipientFromLocal(String iRecipientId) {
        if (!generalFunc.retrieveValue("DeliverList").isEmpty()) {
            String data = generalFunc.retrieveValue("DeliverList");
            JSONArray deliveriesArr = generalFunc.getJsonArray("deliveries", data);
            JSONArray jaStore = new JSONArray();
            JSONObject deliveriesObj = new JSONObject();
            recipientExist = false;
            if (deliveriesArr != null) {
                for (int j = 0; j < deliveriesArr.length(); j++) {
                    JSONObject ja = generalFunc.getJsonObject(deliveriesArr, j);
                    JSONObject deliveryDetailsParametersStore = new JSONObject();
                    Utils.printLog("Api", "iRecipientId" + iRecipientId + "iRecipientId of list " + generalFunc.getJsonValue("iRecipientId", ja.toString()));
                    if (generalFunc.getJsonValue("iRecipientId", ja.toString()).equalsIgnoreCase(iRecipientId)) {
                        recipientExist = true;
                    } else {
                        try {
                            deliveryDetailsParametersStore.put("iRecipientId", "" + generalFunc.getJsonValue("iRecipientId", ja.toString()));
                            deliveryDetailsParametersStore.put("RecipientName", generalFunc.getJsonValue("RecipientName", ja.toString()));
                            deliveryDetailsParametersStore.put("RecipientAdd", generalFunc.getJsonValue("RecipientAdd", ja.toString()));
                            deliveryDetailsParametersStore.put("RecipientEmailAdd", generalFunc.getJsonValue("RecipientEmailAdd", ja.toString()));
                            deliveryDetailsParametersStore.put("RecipientPhnNo", generalFunc.getJsonValue("RecipientPhnNo", ja.toString()));
                            deliveryDetailsParametersStore.put("RecipientPhnCode", generalFunc.getJsonValue("RecipientPhnCode", ja.toString()));
                            deliveryDetailsParametersStore.put("RecipientAdditionalNote", generalFunc.getJsonValue("RecipientAdditionalNote", ja.toString()));
                            deliveryDetailsParametersStore.put("RecipientShipmentDetail", generalFunc.getJsonValue("RecipientShipmentDetail", ja.toString()));
                            deliveryDetailsParametersStore.put("RecipientWeight", generalFunc.getJsonValue("RecipientWeight", ja.toString()));
                            deliveryDetailsParametersStore.put("RecipientQuntity", generalFunc.getJsonValue("RecipientQuntity", ja.toString()));
                            deliveryDetailsParametersStore.put("vLattitude", generalFunc.getJsonValue("vLattitude", ja.toString()));
                            deliveryDetailsParametersStore.put("vLongitude", generalFunc.getJsonValue("vLongitude", ja.toString()));
                            deliveryDetailsParametersStore.put("vPackageTypeName", generalFunc.getJsonValue("vPackageTypeName", ja.toString()));
                            deliveryDetailsParametersStore.put("iPackageTypeId", generalFunc.getJsonValue("iPackageTypeId", ja.toString()));
                            jaStore.put(deliveryDetailsParametersStore);

                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }
                if (recipientExist == true) {
                    generalFunc.removeValue("DeliverList");
                    try {
                        generalFunc.storedata("DeliverList", deliveriesObj.put("deliveries", jaStore).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
        deleteRecipient(iRecipientId);
    }

    public void deleteRecipient(String iRecipientId) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "deleterecipient");
        parameters.put("iRecipientId", iRecipientId);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(),parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Recipient", "::" + responseString);
                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(CommonUtilities.action_str, responseString);

                    if (isDataAvail == true) {
                        getRecipientList();
                    } else {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(CommonUtilities.message_str, responseString)));
                    }

                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.ADD_RECIPIENT_REQ_CODE && resultCode == RESULT_OK) {
            getRecipientList();
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == plusSignTxt.getId()) {
                new StartActProcess(getActContext()).startActForResult(AddRecipientActivity.class, Utils.ADD_RECIPIENT_REQ_CODE);
//                new StartActProcess(getActContext()).startAct(AddRecipientActivity.class);
            } else if (i == R.id.backImgView) {
                RecipientListActivity.super.onBackPressed();
            }
        }
    }
}
