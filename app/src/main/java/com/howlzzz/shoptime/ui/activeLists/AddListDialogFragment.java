package com.howlzzz.shoptime.ui.activeLists;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.howlzzz.shoptime.R;
import com.howlzzz.shoptime.model.ShopTime;
import com.howlzzz.shoptime.utils.Constants;
import com.howlzzz.shoptime.utils.Utils;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Adds a new shopping list
 */
public class AddListDialogFragment extends DialogFragment {
    EditText mEditTextListName;
    String mEncodedEmail,mDisplayName;
    private String mEmail;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddListDialogFragment newInstance(String encodedEmail,String displayName) {
        AddListDialogFragment addListDialogFragment = new AddListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_ENCODED_EMAIL, encodedEmail);
        bundle.putString(Constants.KEY_DISPLAY_NAME,displayName);
        addListDialogFragment.setArguments(bundle);
        return addListDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEmail = getArguments().getString(Constants.KEY_ENCODED_EMAIL);
        mEncodedEmail=Utils.encodeEmail(mEmail);
        Log.e("addlist",mEncodedEmail+"   "+mEmail);
        mDisplayName=getArguments().getString(Constants.KEY_DISPLAY_NAME);
    }

    /**
     * Open the keyboard automatically when the dialog fragment is opened
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_list, null);
        mEditTextListName = (EditText) rootView.findViewById(R.id.edit_text_list_name);


        /**
         * Call addShoppingList() when user taps "Done" keyboard action
         */
        mEditTextListName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addShoppingList();
                }
                return true;
            }
        });

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addShoppingList();
                    }
                });

        return builder.create();
    }

    /**
     * Add new active list
     */
    public void addShoppingList() {

        /*Firebase ref=new Firebase(Constants.FIREBASE_URL);
        String userEnteredName=mEditTextListName.getText().toString();
        ShopTime shoppingList=new ShopTime(userEnteredName,"Anonymous");
        ref.child("ActiveList").setValue(shoppingList);*/
        //ref.child("ListName").setValue(mEditTextListName.getText().toString());


        String userEnteredName = mEditTextListName.getText().toString();
        //String owner = "Anonymous Owner";

        /**
         * If EditText input is not empty
         */
        if (!userEnteredName.equals("")) {

            /**
             * Create Firebase references
             */
            //Firebase listsRef = new Firebase(Constants.FIREBASE_URL_ACTIVE_LISTS);


            /* Save listsRef.push() to maintain same random Id */
            Firebase userListsRef = new Firebase(Constants.FIREBASE_URL_USER_LISTS).child(mEncodedEmail);
                        final Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL);

                        Firebase newListRef = userListsRef.push();

            final String listId = newListRef.getKey();


            HashMap<String, Object> updateShoppingListData = new HashMap<>();

            /**
             * Set raw version of date to the ServerValue.TIMESTAMP value and save into
             * timestampCreatedMap
             */
            /*HashMap<String, Object> timestampCreated = new HashMap<>();
            timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);*/

            /* Build the shopping list */
            ShopTime newShoppingList = new ShopTime(userEnteredName, mEncodedEmail,mDisplayName);
            Log.e("AddListDialogeFragment",mEncodedEmail);

            /* Add the shopping list */
            HashMap<String, Object> shoppingListMap = (HashMap<String, Object>)
                                new ObjectMapper().convertValue(newShoppingList, Map.class);

            Utils.updateMapForAllWithValue(null, listId, mEncodedEmail,
                                updateShoppingListData, "", shoppingListMap);

                        firebaseRef.updateChildren(updateShoppingListData);


            /* Close the dialog fragment */
            AddListDialogFragment.this.getDialog().cancel();
        }
    }

}

