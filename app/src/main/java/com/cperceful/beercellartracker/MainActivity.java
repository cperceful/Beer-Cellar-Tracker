package com.cperceful.beercellartracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private TextView usernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameText = findViewById(R.id.textView_username);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Constants.DEBUG_TAG, "Getting username");
        getUsername();
    }

    private void getUsername(){
        //TODO: add listener to database reference. Not standalone variable. Idiot.
        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(Constants.DEBUG_TAG, "Pop pop! It's starting");
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                Log.d(Constants.DEBUG_TAG, "getCurrentUser() called");
                if (firebaseUser == null) {
                    Log.d(Constants.DEBUG_TAG, "Firebase user object returned null");
                } else {
                    String userId = firebaseUser.getUid();
                    Log.d(Constants.DEBUG_TAG, "Got userId " + userId);

                    User user = dataSnapshot.child("users").child(userId).getValue(User.class);
                    Log.d(Constants.DEBUG_TAG, "Found user, " + user);

                    String username = user.getUsername();
                    Log.d(Constants.DEBUG_TAG, "Got username " + username);

                    usernameText.setText(username);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
