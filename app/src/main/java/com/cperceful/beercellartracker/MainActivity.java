package com.cperceful.beercellartracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private EditText editTextBeerSearch;
    private Button searchButton;
    private TextView breweryText;
    private TextView beerNameText;
    private TextView abvText;
    private TextView beerStyleText;
    private ImageView beerIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        editTextBeerSearch = findViewById(R.id.editText_beerSearch);
        searchButton = findViewById(R.id.button_search);
        breweryText = findViewById(R.id.textView_brewery);
        beerNameText = findViewById(R.id.textView_beerName);
        abvText = findViewById(R.id.textView_beerABV);
        beerStyleText = findViewById(R.id.textView_beerStyle);
        beerIcon = findViewById(R.id.imageView_beerIcon);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = editTextBeerSearch.getText().toString();
                getBeer(search);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Constants.DEBUG_TAG, "Getting username");
        getUsername();
        beerIcon.setVisibility(View.INVISIBLE);
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


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getBeer(String search){
        RequestParams params = new RequestParams();
        params.put("key", BuildConfig.API_KEY);
        params.put("q", search);
        makeRequest(params);
    }

    private void makeRequest(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Constants.API_URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                BeerDataModel model = BeerDataModel.fromJson(response);

                if (model == null){
                    Toast.makeText(MainActivity.this, "Search returned no results", Toast.LENGTH_SHORT).show();
                } else {
                    updateUI(model);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){
                Log.d(Constants.DEBUG_TAG, "Request failed: " + e.toString());
            }
        });
    }

    private void updateUI(BeerDataModel model){
        breweryText.setText(model.getBrewery());
        beerNameText.setText(model.getName());
        beerStyleText.setText(model.getStyle());
        abvText.setText(model.getAbv() + "%");

    }
}

