package com.howlzzz.shoptime.ui.activeListDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;
import com.howlzzz.shoptime.R;
import com.howlzzz.shoptime.model.ShopTime;
import com.howlzzz.shoptime.model.ShoppingListItem;
import com.howlzzz.shoptime.model.User;
import com.howlzzz.shoptime.utils.Constants;
import com.howlzzz.shoptime.utils.Utils;

import java.util.HashMap;

/**
 * Created by PHowli on 10/24/2016.
 */
public class ActiveListItemAdapter extends FirebaseListAdapter<ShoppingListItem> {

    private ShopTime mShoppingList;
    private String mListId,mEncodedEmail;
    private HashMap<String, User> mSharedWithUsers;

    public ActiveListItemAdapter(Activity activity, Class<ShoppingListItem> modelClass, int modelLayout, Query ref, String listId,String encodedEmail) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
        this.mListId = listId;
        this.mEncodedEmail=encodedEmail;
    }


    public void setShoppingList(ShopTime shoppingList) {
               this.mShoppingList = shoppingList;
                this.notifyDataSetChanged();
            }


    public void setSharedWithUsers(HashMap<String, User> sharedWithUsers) {
        this.mSharedWithUsers = sharedWithUsers;
        this.notifyDataSetChanged();
    }
    @Override
    protected void populateView(View view, ShoppingListItem shoppingListItem, int position) {

        ImageButton buttonRemoveItem = (ImageButton) view.findViewById(R.id.button_remove_item);
               //TextView textViewMealItemName = (TextView) view.findViewById(R.id.text_view_active_list_item_name);
        //debug start
        TextView textViewItemName = (TextView) view.findViewById(R.id.text_view_active_list_item_name);
                final TextView textViewBoughtByUser = (TextView) view.findViewById(R.id.text_view_bought_by_user);
                TextView textViewBoughtBy = (TextView) view.findViewById(R.id.text_view_bought_by);

                String owner = shoppingListItem.getOwner();

                textViewItemName.setText(shoppingListItem.getItemName());


                setItemAppearanceBaseOnBoughtStatus(owner, textViewBoughtByUser, textViewBoughtBy, buttonRemoveItem,
                        textViewItemName, shoppingListItem);
        //debug end



                //textViewMealItemName.setText(shoppingListItem.getItemName());
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
        Utils.updateMapWithTimestampLastChanged(mSharedWithUsers, mListId, mShoppingList.getOwner(), updatedRemoveItemMap);
                firebaseRef.updateChildren(updatedRemoveItemMap);
        }
///debug start
private void setItemAppearanceBaseOnBoughtStatus(String owner, final TextView textViewBoughtByUser,
                                                 TextView textViewBoughtBy, ImageButton buttonRemoveItem,
                                                 TextView textViewItemName, ShoppingListItem item) {
            /**
     +         * If selected item is bought
     +         * Set "Bought by" text to "You" if current user is owner of the list
     +         * Set "Bought by" text to userName if current user is NOT owner of the list
     +         * Set the remove item button invisible if current user is NOT list or item owner
     +         */
            if (item.isBought() && item.getBoughtBy() != null) {

                textViewBoughtBy.setVisibility(View.VISIBLE);
                textViewBoughtByUser.setVisibility(View.VISIBLE);
                buttonRemoveItem.setVisibility(View.INVISIBLE);

                /* Add a strike-through */
                textViewItemName.setPaintFlags(textViewItemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                if (item.getBoughtBy().equals(mEncodedEmail)) {
                    textViewBoughtByUser.setText(mActivity.getString(R.string.text_you));
                } else {
                    String boughtBy= Utils.encodeEmail(item.getBoughtBy());
                    Firebase boughtByUserRef = new Firebase(Constants.FIREBASE_URL_USERS).child(boughtBy);
                    /* Get the item's owner's name; use a SingleValueEvent listener for memory efficiency */
                    boughtByUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                textViewBoughtByUser.setText(user.getName());
                            }
                        }
                      @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Log.e(mActivity.getClass().getSimpleName(),
                                    mActivity.getString(R.string.log_error_the_read_failed) +
                                            firebaseError.getMessage());
                        }
                    });
                }
            } else {
                /**
         +             * If selected item is NOT bought
         +             * Set "Bought by" text to be empty and invisible
         +             * Set the remove item button visible if current user is owner of the list or selected item
         +             */

                            /* Remove the strike-through */
                            textViewItemName.setPaintFlags(textViewItemName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                textViewBoughtBy.setVisibility(View.INVISIBLE);
                textViewBoughtByUser.setVisibility(View.INVISIBLE);
                textViewBoughtByUser.setText("");
                buttonRemoveItem.setVisibility(View.VISIBLE);
            }
        }

}
