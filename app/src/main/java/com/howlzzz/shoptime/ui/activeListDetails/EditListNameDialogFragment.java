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
import com.howlzzz.shoptime.utils.Utils;

import java.util.HashMap;


public class EditListNameDialogFragment extends EditListDialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOG_TAG = ActiveListDetailsActivity.class.getSimpleName();
    //private static final String LOG_TAG = ActiveListDetailsActivity.class.getSimpleName();
    String mListName;
    /**
     * Public static constructor that creates fragment and passes a bundle with data into it when adapter is created
     */
    public static EditListNameDialogFragment newInstance(ShopTime shoppingList,String listId,String encodedEmail,String displayName) {
        EditListNameDialogFragment editListNameDialogFragment = new EditListNameDialogFragment();
        Bundle bundle = EditListDialogFragment.newInstanceHelper(shoppingList, R.layout.dialog_edit_list,listId,encodedEmail,displayName);
        bundle.putString(Constants.KEY_LIST_NAME, shoppingList.getListName());
        editListNameDialogFragment.setArguments(bundle);
        return editListNameDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListName = getArguments().getString(Constants.KEY_LIST_NAME);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /** {@link EditListDialogFragment#createDialogHelper(int)} is a
         * superclass method that creates the dialog
         **/
        Dialog dialog = super.createDialogHelper(R.string.positive_button_edit_item);

        return dialog;
    }

    /**
     * Changes the list name in all copies of the current list
     */
    protected void doListEdit() {

        final String inputListName = mEditTextForList.getText().toString();

        /**
         * Set input text to be the current list name if it is not empty
         */
        if (!inputListName.equals("") && mListName != null &&
                                mListId != null && !inputListName.equals(mListName)) {

            Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL);

                /**
                 * If editText input is not equal to the previous name
                 */



                    //Firebase shoppingListRef = new Firebase(Constants.FIREBASE_URL_ACTIVE_LISTS).child(mListId);

            HashMap<String, Object> updatedListData = new HashMap<String, Object>();

                    /* Make a Hashmap for the specific properties you are changing */
                    HashMap<String, Object> updatedProperties = new HashMap<String, Object>();
                    updatedProperties.put(Constants.FIREBASE_PROPERTY_LIST_NAME, inputListName);

            /* Add the value to update at the specified property for all lists */
                        Utils.updateMapForAllWithValue(mListId, mEncodedEmail, updatedListData,
                                Constants.FIREBASE_PROPERTY_LIST_NAME, inputListName);

                    /* Add the timestamp for last changed to the updatedProperties Hashmap */
                    //HashMap<String, Object> changedTimestampMap = new HashMap<>();
                    //changedTimestampMap.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    /* Add the updated timestamp */
            firebaseRef.updateChildren(updatedListData);
                }
            }
        }



