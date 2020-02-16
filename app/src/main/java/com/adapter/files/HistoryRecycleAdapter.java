package com.adapter.files;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.VolleyLibFiles.AppController;
import com.android.volley.toolbox.ImageLoader;
import com.general.files.GeneralFunctions;
import com.app85taxi.passenger.R;
import com.utils.Utils;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 09-07-2016.
 */
public class HistoryRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ArrayList<HashMap<String, String>> list;
    Context mContext;
    public GeneralFunctions generalFunc;

    private OnItemClickListener mItemClickListener;

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    boolean isFooterEnabled = false;
    View footerView;

    FooterViewHolder footerHolder;

    public HistoryRecycleAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc, boolean isFooterEnabled) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.isFooterEnabled = isFooterEnabled;
    }

    public interface OnItemClickListener {
        void onItemClickList(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list, parent, false);
            this.footerView = v;
            return new FooterViewHolder(v);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_design, parent, false);
            return new ViewHolder(view);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof ViewHolder) {
            final HashMap<String, String> item = list.get(position);
            final ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.historyNoHTxt.setText(item.get("LBL_BOOKING_NO") + "#");

            viewHolder.historyNoVTxt.setText(generalFunc.convertNumberWithRTL(item.get("vRideNo")));
            viewHolder.dateTxt.setText(generalFunc.getDateFormatedType(item.get("tTripRequestDateOrig"), Utils.OriginalDateFormate, Utils.dateFormateInList));
            viewHolder.statusHTxt.setText(item.get("LBL_Status") + ":");
            Utils.printLog("tSaddress", item.get("tSaddress"));
            viewHolder.sourceAddressTxt.setText(item.get("tSaddress"));
            viewHolder.sourceAddressHTxt.setText(item.get("LBL_PICK_UP_LOCATION"));
            viewHolder.destAddressHTxt.setText(item.get("LBL_DEST_LOCATION"));


            if (item.get("eType").equalsIgnoreCase(Utils.CabGeneralType_Deliver) || item.get("eType").equalsIgnoreCase(Utils.CabGeneralType_Ride) || item.get("eType").equalsIgnoreCase("UberX")) {
//                cell.rideTypeLbl.text = cell.rideDateLbl.text
//                cell.rideDateLbl.isHidden = true
                viewHolder.dateTxt.setVisibility(View.INVISIBLE);
                viewHolder.etypeTxt.setText(generalFunc.getDateFormatedType(item.get("tTripRequestDateOrig"), Utils.OriginalDateFormate, Utils.dateFormateInList));
            } else {
                viewHolder.etypeTxt.setText(item.get("eType"));
                viewHolder.dateTxt.setVisibility(View.VISIBLE);

            }
            //  if (!item.get("tDaddress").equals("") || item.get("DESTINATION").equals("No")) {
            if (item.get("tDaddress").equals("")) {
                viewHolder.destAddressTxt.setVisibility(View.GONE);
                viewHolder.destAddressHTxt.setVisibility(View.GONE);
                viewHolder.imagedest.setVisibility(View.GONE);
            } else {
                viewHolder.destAddressTxt.setVisibility(View.VISIBLE);
                viewHolder.destAddressHTxt.setVisibility(View.VISIBLE);
                viewHolder.imagedest.setVisibility(View.VISIBLE);
                viewHolder.destAddressTxt.setText(item.get("tDaddress"));
            }
            viewHolder.statusVTxt.setText(item.get("iActive"));

            viewHolder.contentArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClickList(view, position);
                    }
                }
            });


        } else {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            this.footerHolder = footerHolder;
        }


    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        public MTextView historyNoHTxt;
        public MTextView historyNoVTxt;
        public MTextView dateTxt;
        public MTextView sourceAddressTxt;
        public MTextView destAddressTxt;
        public MTextView sourceAddressHTxt;
        public MTextView destAddressHTxt;
        public MTextView statusHTxt;
        public MTextView statusVTxt;
        public LinearLayout contentArea;
        public MTextView etypeTxt;
        public ImageView imagedest;


        public ViewHolder(View view) {
            super(view);

            historyNoHTxt = (MTextView) view.findViewById(R.id.historyNoHTxt);
            historyNoVTxt = (MTextView) view.findViewById(R.id.historyNoVTxt);
            dateTxt = (MTextView) view.findViewById(R.id.dateTxt);
            sourceAddressTxt = (MTextView) view.findViewById(R.id.sourceAddressTxt);
            destAddressTxt = (MTextView) view.findViewById(R.id.destAddressTxt);
            sourceAddressHTxt = (MTextView) view.findViewById(R.id.sourceAddressHTxt);
            destAddressHTxt = (MTextView) view.findViewById(R.id.destAddressHTxt);
            imagedest = (ImageView) view.findViewById(R.id.imagedest);
            statusHTxt = (MTextView) view.findViewById(R.id.statusHTxt);
            statusVTxt = (MTextView) view.findViewById(R.id.statusVTxt);
            contentArea = (LinearLayout) view.findViewById(R.id.contentArea);
            etypeTxt = (MTextView) view.findViewById(R.id.etypeTxt);


        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout progressArea;

        public FooterViewHolder(View itemView) {
            super(itemView);

            progressArea = (LinearLayout) itemView;

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position) && isFooterEnabled == true) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        return position == list.size();
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (isFooterEnabled == true) {
            return list.size() + 1;
        } else {
            return list.size();
        }

    }

    public void addFooterView() {
//        Utils.printLog("Footer", "added");
        this.isFooterEnabled = true;
        notifyDataSetChanged();
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.VISIBLE);
    }

    public void removeFooterView() {
//        Utils.printLog("Footer", "removed");
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.GONE);
//        footerHolder.progressArea.setPadding(0, -1 * footerView.getHeight(), 0, 0);
    }
}
