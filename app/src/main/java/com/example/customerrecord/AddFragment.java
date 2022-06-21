package com.example.customerrecord;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;


public class AddFragment extends Fragment {
    private EditText edtName, edtPhone;
    private Button btnAddCustomer, btnCustomerImageSelect;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private Boolean isCustomerAlreadyExist;
    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add, container, false);
        edtName = root.findViewById(R.id.edtCustomerName);
        edtPhone = root.findViewById(R.id.edtCustomerPhone);
        btnAddCustomer = root.findViewById(R.id.btnAddCustomer);
        btnCustomerImageSelect = root.findViewById(R.id.btnCustomerImageSelect);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("Customers");
        mAuth = FirebaseAuth.getInstance();


        btnCustomerImageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btnAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        return root;


    }

    private void uploadImage() {

        String strName = edtName.getText().toString();
        String strPhone = edtPhone.getText().toString();


        if (TextUtils.isEmpty(strName)){
            edtName.setError("Enter the Name");
            edtName.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(strPhone)){
            edtPhone.setError("Enter the Phone Number");
            edtPhone.requestFocus();
            return;
        }

        if(filePath != null){
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading....");
            progressDialog.show();

            StorageReference ref = storageReference.child(mAuth.getCurrentUser().getUid()).child(strPhone);

            //Check if a customer number already exist
            isCustomerAlreadyExist = false;

            databaseReference.child("Customers").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                int currentCustomer = 0;
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                        currentCustomer += 1;
                        String phone = dataSnap.child("customerPhone").getValue(String.class);
                        assert phone != null;
                        if (phone.matches(strPhone)){
                            Toast.makeText(getActivity(), "Customer with phone already exist", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            isCustomerAlreadyExist = true;
                            break;


                        }else if(currentCustomer == dataSnapshot.getChildrenCount() && !isCustomerAlreadyExist){
                            // Adding Data to Storage and Realtime Database

                            Customer customer = new Customer(strName, strPhone, filePath.toString(), databaseReference.push().getKey());
                            databaseReference.child("Customers").child(mAuth.getCurrentUser().getUid()).child(strPhone).setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        // Add Image into database
                                        ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getActivity(), "Customer Data Uploaded", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    Toast.makeText(getActivity(), "Unable to upload image to database", Toast.LENGTH_SHORT).show();
                                                }
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getActivity(), "Unable to Upload Data", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });


                        }

                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void checkIfNumberAlreadyExist(String strPhone) {
        isCustomerAlreadyExist = false;

        databaseReference.child("Customers").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            int currentCustomer = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    currentCustomer += 1;
                    String phone = dataSnap.child("customerPhone").getValue(String.class);
                    assert phone != null;
                    if (phone.matches(strPhone)){
                        Toast.makeText(getActivity(), "passed", Toast.LENGTH_SHORT).show();
                        isCustomerAlreadyExist = true;

                    }else if(currentCustomer == dataSnapshot.getChildrenCount() && !isCustomerAlreadyExist){
                        Toast.makeText(getActivity(), "SECOND PASS", Toast.LENGTH_SHORT).show();
                        isCustomerAlreadyExist = false;
                        break;
                    }

                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image from here ..."), PICK_IMAGE_REQUEST);



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == Activity.RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();


        }
    }



}