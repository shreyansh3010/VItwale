package com.vitwale.vitwale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Profile extends AppCompatActivity {

    private ImageView mSetupImage;
    private Button mSubmitBtn;
    private DatabaseReference mDatabaseUser;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;
    private ProgressDialog mProgress;
    private EditText mprofileName, mProfileReg, mProfileEmail;
    private ImageView mProfileImage;
    private static final int GALLERY_REQUEST =1;
    private Uri mImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUser.keepSynced(true);
        mStorageImage = FirebaseStorage.getInstance().getReference().child("Profile_images");

        mProgress = new ProgressDialog(this);
        mProgress.setCanceledOnTouchOutside(false);

        mSubmitBtn = (Button) findViewById(R.id.UpdateBtn);

        mSetupImage = (ImageView) findViewById(R.id.user_image);

        mAuth = FirebaseAuth.getInstance();
        final String uid = mAuth.getCurrentUser().getUid();
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child(uid).child("name").getValue(String.class);
                String img = dataSnapshot.child(uid).child("image").getValue(String.class);
                String Reg = dataSnapshot.child(uid).child("Reg").getValue(String.class);
                String email = dataSnapshot.child(uid).child("email").getValue(String.class);
                mprofileName = (EditText) findViewById(R.id.user_name);
                mprofileName.setText(name);
                mProfileReg = (EditText) findViewById(R.id.user_reg);
                mProfileReg.setText(Reg);
                mProfileEmail = (EditText) findViewById(R.id.user_email);
                mProfileEmail.setText(email);
                mProfileImage = (ImageView) findViewById(R.id.user_image);
                Picasso.with(getApplication()).load(img).into(mProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mSetupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSetupAccount();
            }
        });
    }
    private void startSetupAccount() {
        final String name = mprofileName.getText().toString().trim();
        final String Reg = mProfileReg.getText().toString().trim();
        final String email = mProfileEmail.getText().toString().trim();
        final String user_id = mAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(Reg) && !TextUtils.isEmpty(email)){

            mDatabaseUser.child(user_id).child("name").setValue(name);
            mDatabaseUser.child(user_id).child("Reg").setValue(Reg);
            mDatabaseUser.child(user_id).child("email").setValue(email);

            if(mImageUri !=null){
                mProgress.setMessage("Updating...");
                mProgress.show();

                StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());

                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                        mDatabaseUser.child(user_id).child("image").setValue(downloadUri);
                        mProgress.dismiss();

                    }
                });

            }
            Toast.makeText(Profile.this,"Profile updated",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                mSetupImage.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
