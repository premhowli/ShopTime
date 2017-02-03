package com.howlzzz.shoptime.ui.activeListDetails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.howlzzz.shoptime.R;
import com.howlzzz.shoptime.model.ShopTime;
import com.howlzzz.shoptime.model.ShoppingListItem;
import com.howlzzz.shoptime.model.User;
import com.howlzzz.shoptime.ui.BaseActivity;
import com.howlzzz.shoptime.ui.sharing.ShareListActivity;
import com.howlzzz.shoptime.utils.Constants;
import com.howlzzz.shoptime.utils.Utils;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the details screen for the selected shopping list
 */
public class ActiveListDetailsActivity extends BaseActivity {
    private static final String LOG_TAG = ActiveListDetailsActivity.class.getSimpleName();
    private ListView mListView;
    private String mListId,mEmail,mEmailEncoded;
    private boolean mCurrentUserIsOwner = false;
    private Firebase mActiveListRef,mCurrentUserRef, mCurrentListRef,mFirebaseRef;
    private ActiveListItemAdapter mActiveListItemAdapter;
    private ShopTime mShoppingList;
    private ValueEventListener mActiveListRefListener, mCurrentUserRefListener, mCurrentListRefListener;
    private Button mButtonShopping;
    private User mCurrentUser;
    private TextView mTextViewPeopleShopping;
        /* Stores whether the current user is shopping */
    private boolean mShopping = false;
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
                mEmail=intent.getStringExtra(Constants.KEY_ENCODED_EMAIL);
        mEmailEncoded=Utils.encodeEmail(mEmail);
                if (mListId == null) {
                    /* No point in continuing without a valid ID. */
                                finish();
                    return;
                }

        //mActiveListRef = new Firebase(Constants.FIREBASE_URL_ACTIVE_LISTS).child(mListId);
        mFirebaseRef=new Firebase(Constants.FIREBASE_URL);
        mCurrentListRef=new Firebase(Constants.FIREBASE_URL_USER_LISTS).child(mEmailEncoded).child(mListId);
        mCurrentUserRef = new Firebase(Constants.FIREBASE_URL_USERS).child(Utils.encodeEmail(mEmailEncoded));
        Firebase listItemsRef = new Firebase(Constants.FIREBASE_URL_SHOPPING_LIST_ITEMS).child(mListId);
        /**
         * Link layout elements from XML and setup the toolbar
         */

        initializeScreen();
        /* Calling invalidateOptionsMenu causes onCreateOptionsMenu to be called */

        mCurrentUserRefListener = mCurrentUserRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User currentUser = dataSnapshot.getValue(User.class);
                            if (currentUser != null) mCurrentUser = currentUser;
                            else finish();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Log.e(LOG_TAG,
                                    getString(R.string.log_error_the_read_failed)+
                                            firebaseError.getMessage());
                        }
                    });

                final Activity thisActivity = this;



        invalidateOptionsMenu();
        Log.e("Activelistdetailsactivity",mEmail);
        mActiveListItemAdapter = new ActiveListItemAdapter(this, ShoppingListItem.class,R.layout.single_active_list_item, listItemsRef.orderByChild(Constants.FIREBASE_PROPERTY_BOUGHT_BY), mListId,mEmail);
         /* Create ActiveListItemAdapter and set to listView */
                mListView.setAdapter(mActiveListItemAdapter);



        //getting the proper name and set as title
        mCurrentListRefListener = mCurrentListRef.addValueEventListener(new ValueEventListener() {
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

                Log.e(LOG_TAG," mEncodeEmail "+" "+mEmail);
                /* Check if the current user is owner */
                mCurrentUserIsOwner = Utils.checkIfOwner(mShoppingList, mEmail);
                if(mCurrentUserIsOwner){
                    Log.e(LOG_TAG,
                            "OWner found");
                }
                else{
                    Log.e(LOG_TAG,
                            "OWner not found"+"  "+mShoppingList.getEmail()+ "  "+mEmail);
                }
                /* Calling invalidateOptionsMenu causes onCreateOptionsMenu to be called */
                invalidateOptionsMenu();

                /* Set title appropriately. */
                setTitle(shoppingList.getListName());


                HashMap<String, User> usersShopping = mShoppingList.getUsersShopping();
                               if (usersShopping != null && usersShopping.size() != 0 &&
                                        usersShopping.containsKey(mEmailEncoded)) {
                                    mShopping = true;
                                    mButtonShopping.setText(getString(R.string.button_stop_shopping));
                                    mButtonShopping.setBackgroundColor(ContextCompat.getColor(ActiveListDetailsActivity.this, R.color.dark_grey));
                                } else {
                                    mButtonShopping.setText(getString(R.string.button_start_shopping));
                                    mButtonShopping.setBackgroundColor(ContextCompat.getColor(ActiveListDetailsActivity.this, R.color.primary_dark));
                                    mShopping = false;
                                }

                setWhosShoppingText(mShoppingList.getUsersShopping());


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
        ////debug start
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /* Check that the view is not the empty footer item */
                        if (view.getId() != R.id.list_view_footer_empty) {
                            final ShoppingListItem selectedListItem = mActiveListItemAdapter.getItem(position);
                            String itemId = mActiveListItemAdapter.getRef(position).getKey();

                            if (selectedListItem != null) {

                                /* If current user is shopping */
                                if (mShopping) {

                                    /* Create map and fill it in with deep path multi write operations list */
                                    HashMap<String, Object> updatedItemBoughtData = new HashMap<String, Object>();
                                           /* Buy selected item if it is NOT already bought */
                                    if (!selectedListItem.isBought()) {
                                        updatedItemBoughtData.put(Constants.FIREBASE_PROPERTY_BOUGHT, true);
                                        updatedItemBoughtData.put(Constants.FIREBASE_PROPERTY_BOUGHT_BY, mEmail);
                                    } else {
                                        updatedItemBoughtData.put(Constants.FIREBASE_PROPERTY_BOUGHT, false);
                                        updatedItemBoughtData.put(Constants.FIREBASE_PROPERTY_BOUGHT_BY, null);
                                    }

                                    /* Do update */
                                    Firebase firebaseItemLocation = new Firebase(Constants.FIREBASE_URL_SHOPPING_LIST_ITEMS)
                                            .child(mListId).child(itemId);
                                    firebaseItemLocation.updateChildren(updatedItemBoughtData, new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            if (firebaseError != null) {
                                                Log.d(LOG_TAG, getString(R.string.log_error_updating_data) +
                                                        firebaseError.getMessage());
                                            }

                                        }
                                    });
                                }
                                else{

                                    /*Toast.makeText(getApplicationContext(),"Better first go to shopping mode",Toast.LENGTH_LONG);
                                    Log.e(LOG_TAG,"shopping mode set first");*/
                                }

                            }
                        }
                    }
                });

                ////debug end

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
        remove.setVisible(mCurrentUserIsOwner);
        edit.setVisible(mCurrentUserIsOwner);
        share.setVisible(mCurrentUserIsOwner);
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
            Intent intent = new Intent(ActiveListDetailsActivity.this, ShareListActivity.class);
            intent.putExtra(Constants.KEY_LIST_ID, mListId);
            startActivity(intent);
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
        //mActiveListRef.removeEventListener(mActiveListRefListener);
        mCurrentListRef.removeEventListener(mCurrentListRefListener);
        mCurrentUserRef.removeEventListener(mCurrentUserRefListener);
    }

    /**
     * Link layout elements from XML and setup the toolbar
     */
    private void initializeScreen() {
        mListView = (ListView) findViewById(R.id.list_view_shopping_list_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        mButtonShopping = (Button) findViewById(R.id.button_shopping);
        mTextViewPeopleShopping = (TextView) findViewById(R.id.text_view_people_shopping);
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


    private void setWhosShoppingText(HashMap<String, User> usersShopping) {

                if (usersShopping != null) {
                    ArrayList<String> usersWhoAreNotYou = new ArrayList<>();
                    /**
             +             * If at least one user is shopping
             +             * Add userName to the list of users shopping if this user is not current user
          +             */
                    for (User user : usersShopping.values()) {
                        if (user != null && !(user.getEmail().equals(mEncodedEmail))) {
                            usersWhoAreNotYou.add(user.getName());
                        }
                    }

                    int numberOfUsersShopping = usersShopping.size();
                    String usersShoppingText;

                    /**
                     +             * If current user is shopping...
                     +             * If current user is the only person shopping, set text to "You are shopping"
                     +             * If current user and one user are shopping, set text "You and userName are shopping"
                     +             * Else set text "You and N others shopping"
                     +             */
                    if (mShopping) {
                        switch (numberOfUsersShopping) {
                            case 1:
                                usersShoppingText = getString(R.string.text_you_are_shopping);
                                break;
                            case 2:
                                usersShoppingText = String.format(
                                        getString(R.string.text_you_and_other_are_shopping),
                                        usersWhoAreNotYou.get(0));
                                break;
                            default:
                                usersShoppingText = String.format(
                                        getString(R.string.text_you_and_number_are_shopping),
                                        usersWhoAreNotYou.size());
                        }
                        /**
                 +                 * If current user is not shopping..
                 +                 * If there is only one person shopping, set text to "userName is shopping"
                 +                 * If there are two users shopping, set text "userName1 and userName2 are shopping"
                 +                 * Else set text "userName and N others shopping"
         +                 */
                    } else {
                        switch (numberOfUsersShopping) {
                            case 1:
                                usersShoppingText = String.format(
                                        getString(R.string.text_other_is_shopping),
                                        usersWhoAreNotYou.get(0));
                                break;
                            case 2:
                                usersShoppingText = String.format(
                                        getString(R.string.text_other_and_other_are_shopping),
                                        usersWhoAreNotYou.get(0),
                                        usersWhoAreNotYou.get(1));
                                break;
                            default:
                                usersShoppingText = String.format(
                                        getString(R.string.text_other_and_number_are_shopping),
                                        usersWhoAreNotYou.get(0),
                                        usersWhoAreNotYou.size() - 1);
                        }
                    }
                    mTextViewPeopleShopping.setText(usersShoppingText);
                } else {
                    mTextViewPeopleShopping.setText("");
                }
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
        AddListItemDialogFragment dia=AddListItemDialogFragment.newInstance(mShoppingList,mListId, mEmailEncoded,mDisplayName);
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
        EditListNameDialogFragment dia=EditListNameDialogFragment.newInstance(mShoppingList,mListId, mEmail,mDisplayName);
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
        EditListDialogFragment dia=EditListItemNameDialogFragment.newInstance(mShoppingList, itemName, itemId, mListId, mEmail,mDisplayName);
        dia.show(fm,"EditListItemNameShow");

        /* Create an instance of the dialog fragment and show it */
        /*EditListItemNameDialogFragment dialog = EditListItemNameDialogFragment.newInstance(mShoppingList);
        dialog.show(this.getFragmentManager(), "EditListItemNameDialogFragment");*/
    }

    /**
     * This method is called when user taps "Start/Stop shopping" button
     */
    public void toggleShopping(View view) {
        /**
         +         * If current user is already shopping, remove current user from usersShopping map
         +         */
                /*Firebase usersShoppingRef = new Firebase(Constants.FIREBASE_URL_ACTIVE_LISTS)
                        .child(mListId).child(Constants.FIREBASE_PROPERTY_USERS_SHOPPING)
                        .child(mEmailEncoded);*/
        Log.e(LOG_TAG, "shooping toggle   " + mEmailEncoded + "   ");

        HashMap<String, Object> updatedUserData = new HashMap<String, Object>();
        String s = Utils.encodeEmail(mEncodedEmail);
        String propertyToUpdate = Constants.FIREBASE_PROPERTY_USERS_SHOPPING + "/" + s;

        Log.e(LOG_TAG, propertyToUpdate + mShopping + mShoppingList.getEmail());

                /* Either add or remove the current user from the usersShopping map */
                if (mShopping) {

                               /* Add the value to update at the specified property for all lists */
                               Utils.updateMapForAllWithValue(mListId, mShoppingList.getOwner(), updatedUserData,
                                       propertyToUpdate, null);
                               /* Appends the timestamp changes for all lists */
                    //Utils.updateMapWithTimestampLastChanged(mListId, mShoppingList.getOwner(), updatedUserData);


                               /* Do a deep-path update */
                               mFirebaseRef.updateChildren(updatedUserData);
                } else {
                    /**
                     +             * If current user is not shopping, create map to represent User model add to usersShopping map
                     +             */
                                HashMap<String, Object> currentUser = (HashMap<String, Object>)
                                        new ObjectMapper().convertValue(mCurrentUser, Map.class);

                                /* Add the value to update at the specified property for all lists */
                    Utils.updateMapForAllWithValue(mListId, mShoppingList.getEmail(), updatedUserData, propertyToUpdate, currentUser);
                                /* Appends the timestamp changes for all lists */
                    //Utils.updateMapWithTimestampLastChanged(mListId, mShoppingList.getEmail(), updatedUserData);

                                /* Do a deep-path update */
                                mFirebaseRef.updateChildren(updatedUserData);
                }
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
