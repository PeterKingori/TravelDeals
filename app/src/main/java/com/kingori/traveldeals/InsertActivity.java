package com.kingori.traveldeals;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;  // This is the entry point for accessing a Firebase Realtime Database
    private DatabaseReference mDatabaseReference;  // This gets a reference to the location in the database where you will read/write data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("traveldeals");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

}

// First step
// Get a debug signing certificate. This is required in order to implement Google
// sign-in for authentication in our app. This will be done later but it's better to set up the
// application right from the start. Firebase will require a hash representation for the debug
// key store, and you need to use the keytool command line tool in order to get it. The debug keystore
// is a group of hashed characters that you can get through the "keytool" command. They identify
// your computer so they should be kept secret. They're also called SHA1 characters.
// You can also get the SHA1 of your signing certificate using the Gradle signingReport command:
//    ./gradlew signingReport