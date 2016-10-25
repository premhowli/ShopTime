package com.howlzzz.shoptime.ui.activeListDetails;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.howlzzz.shoptime.R;
import com.howlzzz.shoptime.model.ShopTime;
import com.howlzzz.shoptime.utils.Constants;

import java.util.HashMap;


public class EditListItemNameDialogFragment extends EditListDialogFragment {
    String mItemName, mItemId;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static EditListItemNameDialogFragment newInstance(ShopTime shoppingList,String itemName, String itemId,String listId) {
        EditListItemNameDialogFragment editListItemNameDialogFragment = new EditListItemNameDialogFragment();

        Bundle bundle = EditListDialogFragment.newInstanceHelper(shoppingList, R.layout.dialog_edit_item, listId);
        bundle.putString(Constants.KEY_LIST_ITEM_NAME, itemName);
        bundle.putString(Constants.KEY_LIST_ITEM_ID, itemId);
        editListItemNameDialogFragment.setArguments(bundle);

        return editListItemNameDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemName = getArguments().getString(Constants.KEY_LIST_ITEM_NAME);
        mItemId = getArguments().getString(Constants.KEY_LIST_ITEM_ID);
    }


    @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /** {@link EditListDialogFragment#createDialogHelper(int)} is a
         * superclass method that creates the dialog
         */
        Dialog dialog = super.createDialogHelper(R.string.positive_button_edit_item);
        super.helpSetDefaultValueEditText(mItemName);
        return dialog;
    }

    /**
     * Change selected list item name to the editText input if it is not empty
     */
    protected void doListEdit() {
        String nameInput = mEditTextForList.getText().toString();

                /**
                 +         * Set input text to be the current list item name if it is not empty and is not the
                 +         * previous name.
                 +         */
                if (!nameInput.equals("") && !nameInput.equals(mItemName)) {
                    Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL);

                 /* Make a map for the item you are editing the name of */
                    HashMap<String, Object> updatedItemToAddMap = new HashMap<String, Object>();
                    /* Add the new name to the update map*/
                    updatedItemToAddMap.put("/" + Constants.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS + "/"
                                    + mListId + "/" + mItemId + "/" + Constants.FIREBASE_PROPERTY_ITEM_NAME,
                            nameInput);


                    updatedItemToAddMap.put("/" + Constants.FIREBASE_LOCATION_ACTIVE_LISTS + "/"
                                    + mListId + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
                    /* Make the timestamp for last changed */
                    /*HashMap<String, Object> changedTimestampMap = new HashMap<>();
                    changedTimestampMap.put("created", ServerValue.TIMESTAMP);*/

                    /*updatedItemToAddMap.put("/" + Constants.FIREBASE_LOCATION_ACTIVE_LISTS +
                            "/" + mListId, changedTimestampMap);
                    *//* Add the updated timestamp *//*
                    updatedItemToAddMap.put("/" + Constants.FIREBASE_LOCATION_ACTIVE_LISTS +
                            "/" + mListId, changedTimestampMap);*/

                    /* Do the update */
                    firebaseRef.updateChildren(updatedItemToAddMap);

                }
    }
}
