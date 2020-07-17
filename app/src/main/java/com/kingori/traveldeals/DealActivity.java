package com.kingori.traveldeals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;  // This gets a reference to the location in the database where you will read/write data
    private static final int PICTURE_RESULT = 42;
    EditText txtTitle;
    EditText txtDescription;
    EditText txtPrice;
    ImageView imageView;
    TravelDeal mDeal;
    TextView txtDescription2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        // This is the entry point for accessing a Firebase Realtime Database
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtDescription2 = findViewById(R.id.txtDescription2);
        txtPrice = findViewById(R.id.txtPrice);
        imageView = findViewById(R.id.image);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null) {
            deal = new TravelDeal();
        }
        this.mDeal = deal;
        txtTitle.setText(deal.getTitle());

        txtPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl());

        Button btnImage = findViewById(R.id.btnImage);
        if (FirebaseUtil.isAdmin) {
            txtDescription2.setVisibility(View.INVISIBLE);
            txtDescription.setVisibility(View.VISIBLE);
            txtDescription.setText(deal.getDescription());

            btnImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  // This type of intent allows a user to select a particular kind of data and return it
                    intent.setType("image/jpeg");  // This sets the type of data to jpeg images
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);  // This specifies that we should receive only data that is on the device
                    startActivityForResult(Intent.createChooser(intent, "Insert picture"), PICTURE_RESULT);
                }
            });
        }
        else {
            txtDescription.setVisibility(View.INVISIBLE);
            txtDescription2.setVisibility(View.VISIBLE);
            txtDescription2.setText(deal.getDescription());
            btnImage.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
        }
        else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_SHORT).show();
                clean();
                backToList();
                return true;
            case R.id.delete_menu:
                if (mDeal != null) {
                    deleteDeal();
                }
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            StorageReference ref = FirebaseUtil.mStorageReference.child(imageUri.getLastPathSegment());
            UploadTask uploadTask = ref.putFile(imageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            String pictureName = taskSnapshot.getStorage().getPath();
                            mDeal.setImageUrl(imageUrl);
                            mDeal.setImageName(pictureName);
                            showImage(imageUrl);
                            Log.d("Image url is ", imageUrl);
                            Log.d("Image name is ", pictureName);
                        }
                    });
                }
            });
        }
    }

    private void saveDeal() {
        mDeal.setTitle(txtTitle.getText().toString());
        mDeal.setDescription(txtDescription.getText().toString());
        mDeal.setPrice(txtPrice.getText().toString());
        if (mDeal.getId() == null) {
            mDatabaseReference.push().setValue(mDeal);
        }
        else {
            mDatabaseReference.child(mDeal.getId()).setValue(mDeal);
        }
    }

    private void deleteDeal() {
        if (mDeal.getId() == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show();
        }
        else {
            mDatabaseReference.child(mDeal.getId()).removeValue();
            Toast.makeText(this, "Deal Deleted", Toast.LENGTH_SHORT).show();
        }

        if (mDeal.getImageName() != null && !mDeal.getImageName().isEmpty()) {
            StorageReference pictureRef = FirebaseUtil.mStorage.getReference().child(mDeal.getImageName());
            pictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete image ", "Image successfully deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete image unsuccess", e.getMessage());
                }
            });
        }
    }

    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void clean() {
        txtTitle.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtTitle.requestFocus();
    }

    private void enableEditTexts(boolean isEnabled) {
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, (width*2/3))
                    .centerCrop()
                    .into(imageView);
        }
    }

}