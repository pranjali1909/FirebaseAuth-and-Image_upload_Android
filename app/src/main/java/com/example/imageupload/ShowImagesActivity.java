package com.example.imageupload;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.imageupload.MyAdapter;
import com.example.imageupload.Upload;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class ShowImagesActivity extends AppCompatActivity {

    private Context mContext;
    private FirebaseFirestore fstore;
    private RecyclerView recyclerView;
    private ArrayList<Upload> datalist;
    private MyAdapter fadaptor;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_images);

        fstore = FirebaseFirestore.getInstance();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        progressDialog = new ProgressDialog(this);

        datalist = new ArrayList<>();
        fadaptor = new MyAdapter(getApplicationContext(), datalist);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);


        fetchdata();



    }

    private void fetchdata() {

        fstore.collection("uploads").orderBy("pTime").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot d:list)
                        {
                            Upload obj = d.toObject(Upload.class);
                            datalist.add(obj);
                        }
                        recyclerView.setAdapter(fadaptor);
                        fadaptor.notifyDataSetChanged();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ShowImagesActivity.this,"Error: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


    }




}
