package com.adapter.files;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.general.files.GeneralFunctions;

import com.app85taxi.passenger.R;
import com.app85taxi.passenger.RecipientListActivity;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipientListAdapter extends BaseAdapter {

    Context mContext;
    List<HashMap<String, String>> dataList = new ArrayList<>();
    private static List<HashMap<String, String>> dataListMain = new ArrayList<>();
    GeneralFunctions generalFunc;
    RecipientListActivity recipientListActivity;

    public RecipientListAdapter(Context mContext, List<HashMap<String, String>> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
        this.dataListMain = dataList;
        generalFunc = new GeneralFunctions(mContext);

        recipientListActivity = (RecipientListActivity) mContext;

    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    // Do Search...
    public int filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        ArrayList<HashMap<String, String>> tempList = new ArrayList<HashMap<String, String>>(dataListMain);
        // Clear the filter list

        // If there is no search value, then add all original list items to filter list
        if (TextUtils.isEmpty(text)) {
            dataList.clear();
            dataList.addAll(dataListMain);
        } else {
            dataList.clear();

            // Iterate in the original List and add it to filter list...
            for (HashMap<String, String> item : tempList) {

                if (item.get("vName").toLowerCase().contains(text.toLowerCase()) ||
                        item.get("vPhone").toLowerCase().contains(text.toLowerCase()) || item.get("vPhoneCode").toLowerCase().contains(text.toLowerCase())) {
                    // Adding Matched items
                    dataList.add(item);
                }
            }
        }

        Utils.printLog("Api", "Filtered list" + dataList.toString());
        // Set on UI Thread
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Notify the List that the DataSet has changed...
                notifyDataSetChanged();
            }
        });

        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.recipient_list_item, null);
        }
        final HashMap<String, String> map = dataList.get(position);

        final MTextView nameTxt = (MTextView) convertView.findViewById(R.id.name);
        final MTextView addrTxt = (MTextView) convertView.findViewById(R.id.addr);
        final MTextView phoneTxt = (MTextView) convertView.findViewById(R.id.phone);
        final MTextView emailTxt = (MTextView) convertView.findViewById(R.id.email);
        final ImageView deleteRecipientItemImgView = (ImageView) convertView.findViewById(R.id.deleteRecipientImgView);
        final LinearLayout phoneArea = (LinearLayout) convertView.findViewById(R.id.phoneArea);

        deleteRecipientItemImgView.setColorFilter(mContext.getResources().getColor(R.color.appThemeColor_2));
        nameTxt.setText(map.get("vName"));
        addrTxt.setText(map.get("tAddress"));
        phoneTxt.setText(map.get("LBL_PHONE") + ":  " + map.get("vPhone"));
        emailTxt.setText(map.get("LBL_EMAIL_LBL_TXT") + ":  " + map.get("vEmail"));

        phoneArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipientListActivity.callToRecipient(map.get("vPhone"));
            }
        });
        deleteRecipientItemImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GenerateAlertBox generateAlertBox = new GenerateAlertBox(mContext);
                generateAlertBox.setContentMessage("", map.get("LBL_DELETE_RECIPIENT_ALERT_TXT"));

                generateAlertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                    @Override
                    public void handleBtnClick(int btn_id) {
                        if (btn_id == 1) {
                            recipientListActivity.deleteRecipientFromLocal(map.get("iRecipientId"));
                        }
                    }
                });
                generateAlertBox.setPositiveBtn(map.get("LBL_BTN_YES_TXT"));
                generateAlertBox.setNegativeBtn(map.get("LBL_BTN_NO_TXT"));
                generateAlertBox.showAlertBox();
            }
        });
        return convertView;
    }
}
