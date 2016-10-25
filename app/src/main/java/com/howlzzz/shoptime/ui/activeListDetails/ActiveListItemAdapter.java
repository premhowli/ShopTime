package com.howlzzz.shoptime.ui.activeListDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;
import com.howlzzz.shoptime.R;
import com.howlzzz.shoptime.model.ShopTime;
import com.howlzzz.shoptime.model.ShoppingListItem;
import com.howlzzz.shoptime.utils.Constants;

import java.util.HashMap;

/**
 * Created by PHowli on 10/24/2016.
 */
public class ActiveListItemAdapter extends FirebaseListAdapter<ShoppingListItem> {

    private ShopTime mShoppingList;
    private String mListId;

    public ActiveListItemAdapter(Activity activity, Class<ShoppingListItem> modelClass, int modelLayout, Query ref, String listId) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
        this.mListId = listId;
    }


    public void setShoppingList(ShopTime shoppingList) {
               this.mShoppingList = shoppingList;
                this.notifyDataSetChanged();
            }

    @Override
    protected void populateView(View view, ShoppingListItem shoppingListItem, int position) {

        ImageButton buttonRemoveItem = (ImageButton) view.findViewById(R.id.button_remove_item);
               TextView textViewMealItemName = (TextView) view.findViewById(R.id.text_view_active_list_item_name);

                textViewMealItemName.setText(shoppingListItem.getItemName());
                final String itemToRemoveId = this.getRef(position).getKey();

                /**
                 +         * Set the on click listener for "Remove list item" button
                +         */
                buttonRemoveItem.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {

                                   AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity, R.style.CustomTheme_Dialog)
                                           .setTitle(mActivity.getString(R.string.remove_item_option))
                                           .setMessage(mActivity.getString(R.string.dialog_message_are_you_sure_remove_item))
                                          .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                removeItem(itemToRemoveId);
                                                dialog.dismiss();
                                              }
                                         })
                                           .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                 /* Dismiss the dialog */
                                                dialog.dismiss();
                                            }
                                       })
                                     .setIcon(android.R.drawable.ic_dialog_alert);
                                   AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                               }
                          });
    }

    private void removeItem(String itemId) {
        Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL);

                /* Make a map for the removal */
                HashMap<String, Object> updatedRemoveItemMap = new HashMap<String, Object>();
                /* Remove the item by passing null */
                updatedRemoveItemMap.put("/" + Constants.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS + "/"
                        + mListId + "/" + itemId, null);
                /* Make the timestamp for last changed */
                /*HashMap<String, Object> changedTimestampMap = new HashMap<>();
                changedTimestampMap.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);*/

                /* Add the updated timestamp */
                /*updatedRemoveItemMap.put("/" + Constants.FIREBASE_LOCATION_ACTIVE_LISTS +
                        "/" + mListId + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED, changedTimestampMap);*/

                /* Do the update */
                firebaseRef.updateChildren(updatedRemoveItemMap);
        }
}
