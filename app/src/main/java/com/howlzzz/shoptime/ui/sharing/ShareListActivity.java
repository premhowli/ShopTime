package com.howlzzz.shoptime.ui.sharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.howlzzz.shoptime.R;
import com.howlzzz.shoptime.model.ShopTime;
import com.howlzzz.shoptime.model.User;
import com.howlzzz.shoptime.ui.BaseActivity;
import com.howlzzz.shoptime.utils.Constants;
import com.howlzzz.shoptime.utils.Utils;

import java.util.HashMap;

/**
 * Allows for you to check and un-check friends that you share the current list with
 */
public class ShareListActivity extends BaseActivity {
    private static final String LOG_TAG = ShareListActivity.class.getSimpleName();
    private ListView mListView;
    private FriendAdapter mFriendAdapter;
    private String mListId;
    private Firebase mActiveListRef;
    private Firebase mSharedWithRef;
    private ValueEventListener mActiveListRefListener;
    private ShopTime mShoppingList;
    private ValueEventListener mSharedWithListener;
    private HashMap<String, User> mSharedWithUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);


        Intent intent = this.getIntent();
        mListId = intent.getStringExtra(Constants.KEY_LIST_ID);
        if (mListId == null) {
                    /* No point in continuing without a valid ID. */
            finish();
            return;
        }
        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen();


        /**
         +         * Create Firebase references
         +         */
        String encodedEmail = Utils.encodeEmail(mEncodedEmail);
        Firebase currentUserFriendsRef = new Firebase(Constants.FIREBASE_URL_USER_FRIENDS).child(encodedEmail);


        mActiveListRef = new Firebase(Constants.FIREBASE_URL_USER_LISTS).child(encodedEmail).child(mListId);
        mSharedWithRef = new Firebase(Constants.FIREBASE_URL_LISTS_SHARED_WITH).child(mListId);

        /**
         +         * Add ValueEventListeners to Firebase references
         +         * to control get data and control behavior and visibility of elements
         +         */

        mActiveListRefListener = mActiveListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ShopTime shoppingList = dataSnapshot.getValue(ShopTime.class);

                /**
                 +                 * Saving the most recent version of current shopping list into mShoppingList
                 +                 * and pass it to setShoppingList() if present
                 +                 * finish() the activity otherwise
                 +                 */
                if (shoppingList != null) {
                    mShoppingList = shoppingList;
                    mFriendAdapter.setShoppingList(mShoppingList);
                } else {
                    finish();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG,
                        getString(R.string.log_error_the_read_failed) +
                                firebaseError.getMessage());
            }
        });


        mSharedWithListener = mSharedWithRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSharedWithUsers = new HashMap<String, User>();
                for (DataSnapshot currentUser : dataSnapshot.getChildren()) {
                    mSharedWithUsers.put(currentUser.getKey(), currentUser.getValue(User.class));
                }
                mFriendAdapter.setSharedWithUsers(mSharedWithUsers);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG,
                        getString(R.string.log_error_the_read_failed) +
                                firebaseError.getMessage());
            }
        });

        /**
         +         * Set interactive bits, such as click events/adapters
         +         */
        mFriendAdapter = new FriendAdapter(ShareListActivity.this, User.class,
                R.layout.single_user_item, currentUserFriendsRef, mListId);
        
                /* Set adapter for the listView */
        mListView.setAdapter(mFriendAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFriendAdapter.cleanup();
        mActiveListRef.removeEventListener(mActiveListRefListener);
        mSharedWithRef.removeEventListener(mSharedWithListener);
    }

    /**
     * Link layout elements from XML and setup the toolbar
     */
    public void initializeScreen() {
        mListView = (ListView) findViewById(R.id.list_view_friends_share);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Launch AddFriendActivity to find and add user to current user's friends list
     * when the button AddFriend is pressed
     */
    public void onAddFriendPressed(View view) {
        Intent intent = new Intent(ShareListActivity.this, AddFriendActivity.class);
        startActivity(intent);
    }
}
