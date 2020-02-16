package com.app85taxi.passenger;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapter.files.ChatMessage;
import com.adapter.files.ChatMessagesRecycleAdapter;
import com.firebase.ui.database.FirebaseListAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    Context mContext;
    GeneralFunctions generalFunc;
    String isFrom = "";
    private FirebaseListAdapter<ChatMessage> adapter;

    EditText input;

    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private ChatMessagesRecycleAdapter chatAdapter;
    private ArrayList<ChatMessage> chatList;
    private int count = 0;
    ProgressBar LoadingProgressBar;
    HashMap<String, String> data_trip_ada;

    // GcmBroadCastReceiver gcmMessageBroadCastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_trip_chat_detail_dialog);


        data_trip_ada = new HashMap<>();
        data_trip_ada.put("iFromMemberId", getIntent().getStringExtra("iFromMemberId"));
        data_trip_ada.put("FromMemberImageName", getIntent().getStringExtra("FromMemberImageName"));
        data_trip_ada.put("iTripId", getIntent().getStringExtra("iTripId"));
        data_trip_ada.put("FromMemberName", getIntent().getStringExtra("FromMemberName"));

        mContext = ChatActivity.this;

        generalFunc = new GeneralFunctions(ChatActivity.this);

        //gcmMessageBroadCastReceiver = new GcmBroadCastReceiver((ChatActivity) getActContext());
        registerGcmMsgReceiver();

        chatList = new ArrayList<>();
        count = 0;
        show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterGcmReceiver();
    }

    public void onGcmMessageArrived(String message) {

        Utils.printLog("GCM", "::" + message);
        String driverMsg = generalFunc.getJsonValue("Message", message);
        String currentTripId = generalFunc.getJsonValue("iTripId", message);

        Utils.generateNotification(ChatActivity.this, generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + message);

        buildMessage(generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + message,
                generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), true);


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
                }
            }
        });
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(positiveBtn);
        generateAlert.showAlertBox();
    }

    public void registerGcmMsgReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonUtilities.driver_message_arrived_intent_action);

        //registerReceiver(gcmMessageBroadCastReceiver, filter);
    }

    public void unRegisterGcmReceiver() {
        try {
            //   unregisterReceiver(gcmMessageBroadCastReceiver);
        } catch (Exception e) {

        }
    }


    public Context getActContext() {
        return ChatActivity.this;
    }

    public void show() {

        ImageView msgbtn = (ImageView) findViewById(R.id.msgbtn);
        input = (EditText) findViewById(R.id.input);

        input.setHint(generalFunc.retrieveLangLBl("Enter a message", "LBL_ENTER_MSG_TXT"));
        LoadingProgressBar = ((ProgressBar) findViewById(R.id.ProgressBar));


        (findViewById(R.id.backImgView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.hideKeyboard(ChatActivity.this);
                onBackPressed();

            }
        });

        msgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utils.checkText(input)) {
                    FirebaseDatabase.getInstance()
                            .getReference("cubetaxiplus-app")
                            .push()
                            .setValue(new ChatMessage(input.getText().toString().trim(),
                                    getIntent().getStringExtra("FromMemberName"), generalFunc.getMemberId() + "_" + getIntent().getStringExtra("iTripId") + "_" + CommonUtilities.app_type)
                            );

                    sendTripMessageNotification(input.getText().toString().trim());

                    // Clear the input
                    input.setText("");
                } else {

                }


            }
        });


//        setTitle(mRecipient);
        ((MTextView) findViewById(R.id.titleTxt)).setText(getIntent().getStringExtra("FromMemberName"));


        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("cubetaxiplus-app");

        final RecyclerView chatCategoryRecyclerView = (RecyclerView) findViewById(R.id.chatCategoryRecyclerView);


        chatAdapter = new ChatMessagesRecycleAdapter(mContext, chatList, generalFunc, data_trip_ada);
        chatCategoryRecyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();

//        ((MTextView) dialogMessages.findViewById(R.id.titleTxt)).setText(data_trip.get("PName"));

        DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference("cubetaxiplus-app");
        com.google.firebase.database.ChildEventListener childEventListener = new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d("Api", "onChildAdded:" + dataSnapshot.getKey());
                count++;
                // A new comment has been added, add it to the displayed list
                ChatMessage comment = dataSnapshot.getValue(ChatMessage.class);
                Log.d("Api", "onChildAdded:" + comment.getMessageId());
                try {
                    if (comment.getMessageId().equals(generalFunc.getMemberId() + "_" + data_trip_ada.get("iTripId") + "_" + CommonUtilities.app_type) || comment.getMessageId().equals(data_trip_ada.get("iFromMemberId") + "_" + data_trip_ada.get("iTripId") + "_Driver")) {
                        chatList.add(comment);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (count >= dataSnapshot.getChildrenCount()) {
                    //stop progress bar here
                    LoadingProgressBar.setVisibility(View.GONE);

                }
//                Utils.printLog("Api", "onChildAdded:" +chatList.size());
                chatAdapter.notifyDataSetChanged();
                ((ProgressBar) findViewById(R.id.ProgressBar)).setVisibility(View.GONE);
                chatCategoryRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d("Api", "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                ChatMessage newComment = dataSnapshot.getValue(ChatMessage.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d("Api", "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d("Api", "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                ChatMessage movedComment = dataSnapshot.getValue(ChatMessage.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Log.w("Api", "postComments:onCancelled", databaseError.toException());
//                Toast.makeText(mContext, "Failed to load comments.", Toast.LENGTH_SHORT).show();
            }
        };
        mainRef.addChildEventListener(childEventListener);


    }


    public void sendTripMessageNotification(String message) {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "SendTripMessageNotification");
        parameters.put("UserType", Utils.userType);
        parameters.put("iFromMemberId", generalFunc.getMemberId());
        parameters.put("iTripId", data_trip_ada.get("iTripId"));
        parameters.put("iToMemberId", data_trip_ada.get("iFromMemberId"));
        parameters.put("tMessage", message);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        exeWebServer.setLoaderConfig(mContext, false, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                Utils.printLog("Response", "::" + responseString);

            }
        });
        exeWebServer.execute();
    }

    public String lastChars(String a, String b) {
        String str1 = "";
        if (a.length() >= 1) {
            str1 = a.substring(b.length() - 1);
        }
        return str1;
    }
}
