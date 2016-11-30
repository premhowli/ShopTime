package com.howlzzz.shoptime.ui.activeListDetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.howlzzz.shoptime.R;
import com.howlzzz.shoptime.model.ShopTime;
import com.howlzzz.shoptime.model.ShoppingListItem;
import com.howlzzz.shoptime.ui.BaseActivity;
import com.howlzzz.shoptime.utils.Constants;

/**
 * Represents the details screen for the selected shopping list
 */
public class ActiveListDetailsActivity extends BaseActivity {
    private static final String LOG_TAG = ActiveListDetailsActivity.class.getSimpleName();
    private ListView mListView;
    private String mListId;
    private Firebase mActiveListRef;
    private ActiveListItemAdapter mActiveListItemAdapter;
    private ShopTime mShoppingList;
    private ValueEventListener mActiveListRefListener;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_list_details);

        // TODO Here is where you should set everything up for the adapter, much like you
        // did with the ShoppingListsFragment class and the ActiveListAdapter.
        // I've created the list item layout "single_active_list_item.xml" for you to use.

        /* Get the push ID from the extra passed by ShoppingListFragment */
               Intent intent = this.getIntent();
                mListId = intent.getStringExtra(Constants.KEY_LIST_ID);
                if (mListId == null) {
                    /* No point in continuing without a valid ID. */
                                finish();
                    return;
                }

        mActiveListRef = new Firebase(Constants.FIREBASE_URL_ACTIVE_LISTS).child(mListId);
        Firebase listItemsRef = new Firebase(Constants.FIREBASE_URL_SHOPPING_LIST_ITEMS).child(mListId);
        /**
         * Link layout elements from XML and setup the toolbar
         */

        initializeScreen();
        /* Calling invalidateOptionsMenu causes onCreateOptionsMenu to be called */
        invalidateOptionsMenu();

        mActiveListItemAdapter = new ActiveListItemAdapter(this, ShoppingListItem.class,R.layout.single_active_list_item, listItemsRef, mListId);
         /* Create ActiveListItemAdapter and set to listView */
                mListView.setAdapter(mActiveListItemAdapter);



        //getting the proper name and set as title
        mActiveListRefListener = mActiveListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ShopTime shoppingList = dataSnapshot.getValue(ShopTime.class);

                if (shoppingList == null) {
                    finish();
                    /**
                     * Make sure to call return, otherwise the rest of the method will execute,
                     * even after calling finish.
                     */
                    return;
                }
                mShoppingList = shoppingList;

                //mActiveListItemAdapter.setShoppingList(mShoppingList);
                mActiveListItemAdapter.setShoppingList(mShoppingList);
                /* Calling invalidateOptionsMenu causes onCreateOptionsMenu to be called */
                invalidateOptionsMenu();

                /* Set title appropriately. */
                setTitle(shoppingList.getListName());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG,
                        getString(R.string.log_error_the_read_failed) +
                                firebaseError.getMessage());
            }
        });

        /**
         * Set up click listeners for interaction.
         */

        /* Show edit list item name dialog on listView item long click event */
        /*mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                *//* Check that the view is not the empty footer item *//*
                if (view.getId() != R.id.list_view_footer_empty) {
                    showEditListItemNameDialog();
                }
                return true;
            }
        });*/
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();



    mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                /* Check that the view is not the empty footer item */
            if(view.getId() != R.id.list_view_footer_empty) {
                //showEditListItemNameDialog();

                ShoppingListItem shoppingListItem = mActiveListItemAdapter.getItem(position);

                                    if (shoppingListItem != null) {
                                       String itemName = shoppingListItem.getItemName();
                                        String itemId = mActiveListItemAdapter.getRef(position).getKey();

                                        showEditListItemNameDialog(itemName, itemId);
                                        return true;
                                    }
            }
            return false;
        }
    });
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_list_details, menu);

        /**
         * Get menu items
         */
        MenuItem remove = menu.findItem(R.id.action_remove_list);
        MenuItem edit = menu.findItem(R.id.action_edit_list_name);
        MenuItem share = menu.findItem(R.id.action_share_list);
        MenuItem archive = menu.findItem(R.id.action_archive);

        /* Only the edit and remove options are implemented */
        remove.setVisible(true);
        edit.setVisible(true);
        share.setVisible(false);
        archive.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /**
         * Show edit list dialog when the edit action is selected
         */
        if (id == R.id.action_edit_list_name) {
            showEditListNameDialog();
            return true;
        }

        /**
         * removeList() when the remove action is selected
         */
        if (id == R.id.action_remove_list) {
            removeList();
            return true;
        }

        /**
         * Eventually we'll add this
         */
        if (id == R.id.action_share_list) {
            return true;
        }

        /**
         * archiveList() when the archive action is selected
         */
        if (id == R.id.action_archive) {
            archiveList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Cleanup when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mActiveListItemAdapter.cleanup();
        mActiveListRef.removeEventListener(mActiveListRefListener);
    }

    /**
     * Link layout elements from XML and setup the toolbar
     */
    private void initializeScreen() {
        mListView = (ListView) findViewById(R.id.list_view_shopping_list_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        /* Common toolbar setup */
        setSupportActionBar(toolbar);
        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        /* Inflate the footer, set root layout to null*/
        View footer = getLayoutInflater().inflate(R.layout.footer_empty, null);
        mListView.addFooterView(footer);
    }


    /**
     * Archive current list when user selects "Archive" menu item
     */
    public void archiveList() {
    }


    /**
     * Start AddItemsFromMealActivity to add meal ingredients into the shopping list
     * when the user taps on "add meal" fab
     */
    public void addMeal(View view) {
    }

    /**
     * Remove current shopping list and its items from all nodes
     */
    public void removeList() {


        FragmentManager fm=getSupportFragmentManager();
        RemoveListDialogFragment dia=RemoveListDialogFragment.newInstance(mShoppingList,mListId);
        dia.show(fm,"removelistShow");
        /* Create an instance of the dialog fragment and show it */
        /*RemoveListDialogFragment dialog = RemoveListDialogFragment.newInstance(mShoppingList);
        dialog.show(getFragmentManager(), "RemoveListDialogFragment");*/
    }

    /**
     * Show the add list item dialog when user taps "Add list item" fab
     */
    public void showAddListItemDialog(View view) {
        /* Create an instance of the dialog fragment and show it */
        FragmentManager fm=getSupportFragmentManager();
        AddListItemDialogFragment dia=AddListItemDialogFragment.newInstance(mShoppingList,mListId, mEncodedEmail);
        dia.show(fm,"AddListItemDialogShow");

        /*android.support.v4.app.DialogFragment dialog = AddListItemDialogFragment.newInstance(mShoppingList);
        dialog.show(getFragmentManager(),"");*/
    }

    /**
     * Show edit list name dialog when user selects "Edit list name" menu item
     */
    public void showEditListNameDialog() {
        /* Create an instance of the dialog fragment and show it */

        FragmentManager fm=getSupportFragmentManager();
        EditListNameDialogFragment dia=EditListNameDialogFragment.newInstance(mShoppingList,mListId, mEncodedEmail);
        dia.show(fm,"EditListNameDialogShow");




       /* EditListNameDialogFragment dialog = EditListNameDialogFragment.newInstance(mShoppingList);
        EditListDialogFragment temp=dialog;
        android.support.v4.app.DialogFragment s=new android.support.v4.app.DialogFragment();
        s=dialog;
        s.show(this.getFragmentManager(),"");*/
    }

    /**
     * Show the edit list item name dialog after longClick on the particular item
     * @param itemName
     * @param itemId
     */
    public void showEditListItemNameDialog(String itemName, String itemId) {


        FragmentManager fm=getSupportFragmentManager();
        EditListDialogFragment dia=EditListItemNameDialogFragment.newInstance(mShoppingList, itemName, itemId, mListId, mEncodedEmail);
        dia.show(fm,"EditListItemNameShow");

        /* Create an instance of the dialog fragment and show it */
        /*EditListItemNameDialogFragment dialog = EditListItemNameDialogFragment.newInstance(mShoppingList);
        dialog.show(this.getFragmentManager(), "EditListItemNameDialogFragment");*/
    }

    /**
     * This method is called when user taps "Start/Stop shopping" button
     */
    public void toggleShopping(View view) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        /*Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ActiveListDetails Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse(""),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("")
        );*/
        //AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ActiveListDetails Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.howlzzz.shoptime.ui.activeListDetails/http/host/path")
        );*/
        //AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
