package com.howlzzz.shoptime.ui.activeListDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;
import com.howlzzz.shoptime.R;
import com.howlzzz.shoptime.model.ShopTime;
import com.howlzzz.shoptime.model.ShoppingListItem;

/**
 * Created by PHowli on 10/24/2016.
 */
public class ActiveListItemAdapter extends FirebaseListAdapter<ShoppingListItem> {

    public ActiveListItemAdapter(Activity activity, Class<ShoppingListItem> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, ShoppingListItem shoppingListItem, int i) {

        ImageButton buttonRemoveItem = (ImageButton) view.findViewById(R.id.button_remove_item);
               TextView textViewMealItemName = (TextView) view.findViewById(R.id.text_view_active_list_item_name);

                textViewMealItemName.setText(shoppingListItem.getItemName());

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
                                                   removeItem();
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

    private void removeItem() {

        }
}
