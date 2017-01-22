package com.howlzzz.shoptime.ui.activeLists;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.howlzzz.shoptime.R;
import com.howlzzz.shoptime.model.ShopTime;
import com.howlzzz.shoptime.ui.activeListDetails.ActiveListDetailsActivity;
import com.howlzzz.shoptime.utils.Constants;
import com.howlzzz.shoptime.utils.Utils;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass that shows a list of all shopping lists a user can see.
 * Use the {@link ShoppingListsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListsFragment extends Fragment {
    private ListView mListView;
    String mEncodedEmail;
    /*private TextView mTextViewListName;
    private TextView mTextViewOwner;
    private TextView mTextViewTime;

    private TextView mTextViewEditTime;*/
    private ActiveListAdapter mActiveListAdapter;
    private String mEmail;

    public ShoppingListsFragment() {
        /* Required empty public constructor */
    }

    /**
     * Create fragment and pass bundle with data as it's arguments
     * Right now there are not arguments...but eventually there will be.
     */
    public static ShoppingListsFragment newInstance(String encodedEmail) {
        ShoppingListsFragment fragment = new ShoppingListsFragment();

        Bundle args = new Bundle();
        args.putString(Constants.KEY_ENCODED_EMAIL,encodedEmail);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEmail = getArguments().getString(Constants.KEY_ENCODED_EMAIL);
        mEncodedEmail=Utils.encodeEmail(mEmail);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * Initalize UI elements
         */
        View rootView = inflater.inflate(R.layout.fragment_shopping_lists, container, false);
        initializeScreen(rootView);

        //String ref=new Firebase(Constants.FIREBASE_URL).child(Constants.FIREBASE_LOCATION_ACTIVE_LIST).getKey();
        //Firebase refListName = new Firebase(Constants.FIREBASE_URL).child(Constants.FIREBASE_LOCATION_ACTIVE_LIST);

        /*Firebase activeListsRef = new Firebase(Constants.FIREBASE_URL_ACTIVE_LISTS);

        *//*Create the adapter, giving it the activity, model class, layout for each row in
        the list and finally, a reference to the Firebase location with the list data*//*
        mActiveListAdapter = new ActiveListAdapter(getActivity(), ShopTime.class, R.layout.single_active_list, activeListsRef);
        mListView.setAdapter(mActiveListAdapter);*/








        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopTime selectedList = mActiveListAdapter.getItem(position);
                               if (selectedList != null) {
                                    Intent intent = new Intent(getActivity(), ActiveListDetailsActivity.class);
                                   /* Get the list ID using the adapter's get ref method to get the Firebase
        * ref and then grab the key.
                      */
                                        String listId = mActiveListAdapter.getRef(position).getKey();
                                        intent.putExtra(Constants.KEY_LIST_ID, listId);
                                   intent.putExtra(Constants.KEY_ENCODED_EMAIL,mEncodedEmail);
                                        /* Starts an active showing the details for the selected list */
                                                startActivity(intent);
                                    }
            }
        });

        return rootView;
    }


    @Override
        public void onResume() {
                super.onResume();
                final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String sortOrder = sharedPref.getString(Constants.KEY_PREF_SORT_ORDER_LISTS, Constants.ORDER_BY_KEY);

            // TODO We need to recreate the adapter when the user comes back from the SettingsActivity.
            // Because of this, adapter creation should be moved out of onCreateView.
            // When creating the adapter, you should grab the key/value pair that was set in SettingsActivity
            // and modify the Firebase query that is passed to the adapter accordingly.
                Query orderedActiveUserListsRef;
        Firebase activeListsRef = new Firebase(Constants.FIREBASE_URL_USER_LISTS).child(mEncodedEmail);
                /**
         +         * Sort active lists by "date created"
         +         * if it's been selected in the SettingsActivity
         +         */
                if (sortOrder.equals(Constants.ORDER_BY_KEY)) {
                    orderedActiveUserListsRef = activeListsRef.orderByKey();
                } else {

                    /**
                     +             * Sort active by lists by name or datelastChanged. Otherwise
                     +             * depending on what's been selected in SettingsActivity
                    +             */

                    orderedActiveUserListsRef = activeListsRef.orderByChild(sortOrder);
                }

                /**
                 +         * Create the adapter with selected sort order
                 +         */
                mActiveListAdapter = new ActiveListAdapter(getActivity(), ShopTime.class,
                        R.layout.single_active_list, orderedActiveUserListsRef);

                /**
                 +         * Set the adapter to the mListView
                 +         */
                mListView.setAdapter(mActiveListAdapter);
            }



    @Override
    public void onPause() {
        super.onPause();
        mActiveListAdapter.cleanup();
    }


    /**
     * Link list view from XML
     */
    private void initializeScreen(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_view_active_lists);
    }




    /**
     * ---------------------POPULATING THE LIST WITH DATAS-------------------
     * ---------------------THE ABOUVE METHODE IS TAKING CARE OF THAT NOW------------------
     */
    /*private void initializeScreen(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_view_active_lists);
        mTextViewListName = (TextView) rootView.findViewById(R.id.text_view_list_name);
        mTextViewOwner =(TextView) rootView.findViewById(R.id.text_view_created_by_user);
        mTextViewTime=(TextView) rootView.findViewById(R.id.text_view_edit_time);
        mTextViewEditTime = (TextView) rootView.findViewById(R.id.text_view_edit_time);

    }*/
}
