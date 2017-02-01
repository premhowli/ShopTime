package com.howlzzz.shoptime.ui.sharing;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;
import com.howlzzz.shoptime.R;
import com.howlzzz.shoptime.model.User;
import com.howlzzz.shoptime.utils.Constants;
import com.howlzzz.shoptime.utils.Utils;

/**
 * Populates the list_view_friends_autocomplete inside AddFriendActivity
 */
public class AutocompleteFriendAdapter extends FirebaseListAdapter<User> {

    private final String mEncodedEmail;

    /**
     * Public constructor that initializes private instance variables when adapter is created
     */
    public AutocompleteFriendAdapter(Activity activity, Class<User> modelClass, int modelLayout, Query ref, String encodedEmail) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
        this.mEncodedEmail = encodedEmail;
    }

    @Override
    protected void populateView(View view, final User user, int i) {


        TextView textViewFriendEmail = (TextView) view.findViewById(R.id.text_view_autocomplete_item);
        textViewFriendEmail.setText(Utils.decodeEmail(user.getEmail()));

        /**
         +         * Set the onClickListener to a single list item
         +         * If selected email is not friend already and if it is not the
         +         * current user's email, we add selected user to current user's friends
         +         */
        textViewFriendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 +                 * If selected user is not current user proceed
                 +                 */
                if (isNotCurrentUser(user)) {
                    String eEmail = Utils.encodeEmail(mEncodedEmail);
                    Firebase currentUserFriendsRef = new Firebase(Constants.FIREBASE_URL_USER_FRIENDS).child(eEmail);
                    String s = Utils.encodeEmail(user.getEmail());
                    final Firebase friendRef = currentUserFriendsRef.child(s);

                    /**
                     +                     * Add listener for single value event to perform a one time operation
                     +                     */
                    friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            /**
                             +                             * Add selected user to current user's friends if not in friends yet
                             +                             */
                            if (isNotAlreadyAdded(dataSnapshot, user)) {
                                friendRef.setValue(user);
                                mActivity.finish();
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
            }
        });
    }



    /**
     * Protected method that populates the view attached to the adapter (list_view_friends_autocomplete)
     * with items inflated from single_autocomplete_item.xml
     * populateView also handles data changes and updates the listView accordingly
     */


    /** Checks if the friend you try to add is the current user **/
    private boolean isNotCurrentUser(User user) {

        if (user.getEmail().equals(mEncodedEmail)) {
                        /* Toast appropriate error message if the user is trying to add themselves  */
            Toast.makeText(mActivity,
                    mActivity.getResources().getString(R.string.toast_you_cant_add_yourself),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /** Checks if the friend you try to add is already added, given a dataSnapshot of a user **/
    private boolean isNotAlreadyAdded(DataSnapshot dataSnapshot, User user) {

        if (dataSnapshot.getValue(User.class) != null) {
                    /* Toast appropriate error message if the user is already a friend of the user */
            String friendError = String.format(mActivity.getResources().
                            getString(R.string.toast_is_already_your_friend),
                    user.getName());

            Toast.makeText(mActivity,
                    friendError,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
