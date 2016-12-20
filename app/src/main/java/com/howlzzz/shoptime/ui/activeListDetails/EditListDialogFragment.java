package com.howlzzz.shoptime.ui.activeListDetails;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.howlzzz.shoptime.R;
import com.howlzzz.shoptime.model.ShopTime;
import com.howlzzz.shoptime.utils.Constants;


public abstract class EditListDialogFragment extends DialogFragment {
    EditText mEditTextForList;
    int mResource;
    String mListId, mOwner, mEncodedEmail,mDisplayName;


    /**
     * Helper method that creates a basic bundle of all of the information needed to change
     * values in a shopping list.
     *
     * @param shoppingList
     * @param resource
     * @return
     */
    protected static Bundle newInstanceHelper(ShopTime shoppingList, int resource,String listId,String encodedEmail,String displayName) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_LIST_ID,listId);
        bundle.putInt(Constants.KEY_LAYOUT_RESOURCE, resource);
        bundle.putString(Constants.KEY_LIST_OWNER, shoppingList.getOwner());
        bundle.putString(Constants.KEY_ENCODED_EMAIL, encodedEmail);
        bundle.putString(Constants.KEY_DISPLAY_NAME,displayName);
        return bundle;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListId=getArguments().getString(Constants.KEY_LIST_ID);
        mResource = getArguments().getInt(Constants.KEY_LAYOUT_RESOURCE);
        mOwner = getArguments().getString(Constants.KEY_LIST_OWNER);
        mEncodedEmail = getArguments().getString(Constants.KEY_ENCODED_EMAIL);
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

    protected Dialog createDialogHelper(int stringResourceForPositiveButton) {
        /* Use the Builder class for convenient dialog construction */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        /* Get the layout inflater */
        LayoutInflater inflater = getActivity().getLayoutInflater();
        /* Inflate the layout, set root ViewGroup to null*/
        View rootView = inflater.inflate(mResource, null);
        mEditTextForList = (EditText) rootView.findViewById(R.id.edit_text_list_dialog);

        /**
         * Call doListEdit() when user taps "Done" keyboard action
         */
        mEditTextForList.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    doListEdit();

                    /**
                     * Close the dialog fragment when done
                     */
                    EditListDialogFragment.this.getDialog().cancel();
                }
                return true;
            }
        });
        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout */
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(stringResourceForPositiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        doListEdit();

                        /**
                         * Close the dialog fragment
                         */
                        EditListDialogFragment.this.getDialog().cancel();
                    }
                })
                .setNegativeButton(R.string.negative_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        /**
                         * Close the dialog fragment
                         */
                        EditListDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    /**
     * Set the EditText text to be the inputted text
     * and put the pointer at the end of the input
     *
     * @param defaultText
     */
    protected void helpSetDefaultValueEditText(String defaultText) {
        mEditTextForList.setText(defaultText);
        mEditTextForList.setSelection(defaultText.length());
    }

    public abstract Dialog onCreateDialog(Bundle savedInstanceState);

    /**
     * Method to be overriden with whatever edit is supposed to happen to the list
     */
    protected abstract void doListEdit();




}
