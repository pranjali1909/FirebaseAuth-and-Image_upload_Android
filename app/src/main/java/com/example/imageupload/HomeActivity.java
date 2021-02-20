package com.example.imageupload;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    private ImageView myimage;
    private TextView Email;
    private Button Upload,Retreive;
    private FirebaseAuth f;
    private FirebaseFirestore fstore;
    private Uri filepath;
    private String downloadurl;
    private TextView Logout;
    FirebaseStorage storage;
    StorageReference storageReference;
    private static final int Gallery_pick = 1000;
    private static final int Permission_code = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_upload);
        myimage=findViewById(R.id.imageView);
        Retreive=findViewById(R.id.btnRetrieve);
        Upload=findViewById(R.id.buttonUpload);
        storage=FirebaseStorage.getInstance();
        storageReference= storage.getReference();
        f=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        Logout=findViewById(R.id.logout);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setMessage("Logout?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(HomeActivity.this, "You have logged out!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HomeActivity.this, MainActivity.class));
                        finish();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();

            }
        });
        myimage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions,Permission_code);
                    }else{
                        pickImageFromGallery();
                    }

                }else{
                    pickImageFromGallery();
                }
            }
        });
        Retreive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog
                        = new ProgressDialog(HomeActivity.this);
                progressDialog.setTitle("Retrieving...");
                progressDialog.show();
                String id=f.getCurrentUser().getUid();
                fstore.collection("images").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String uri=documentSnapshot.getString("image");
                        if(uri.equals("noImage")){
                            myimage.setImageResource(R.drawable.ic_launcher_background);
                            progressDialog.dismiss();
                        }
                        else{
                            Picasso.get().load(uri).into(myimage);
                        }

                    }
                });
            }
        });
        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }
    private void pickImageFromGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery,Gallery_pick);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Permission_code:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    pickImageFromGallery();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Permission denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_pick && resultCode==RESULT_OK && data!=null){
            filepath=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                myimage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage()
    {
        if (filepath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(HomeActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());
            // adding listeners on upload
            // or failure of image
            ref.putFile(filepath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while(!uriTask.isSuccessful());

                                    downloadurl = uriTask.getResult().toString();
                                    if(uriTask.isSuccessful())
                                    {

                                        Savingimage(downloadurl);
                                    }
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(HomeActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(HomeActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }
    private void Savingimage(String url) {
        String id = f.getCurrentUser().getUid();

        fstore.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,String> userMap = new HashMap<>();

                        userMap.put("image",downloadurl.toString());
                        fstore.collection("images").document(id).set(userMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(HomeActivity.this,"Uploaded",Toast.LENGTH_LONG).show();
                                    }
                                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeActivity.this,"Error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }
}