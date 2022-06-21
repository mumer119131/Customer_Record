package com.example.customerrecord;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SearchFragment extends Fragment {
    private TextView tvSearchUsername, tvSearchPhone, tvSearchUserID;
    private Button btnSearch;
    private EditText edtSearchPhone;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private ImageView searchCustomerImage;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private long ONE_MEGABYTE;
    private LinearLayout linearLayoutDetails;
    private Boolean isCustomerFound;

    public SearchFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        // Inflate the layout for this fragment
        isCustomerFound = false ;
        ONE_MEGABYTE = 1024 * 1024 * 5;
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("Customers");
        tvSearchPhone = root.findViewById(R.id.tvSearchPhone);
        linearLayoutDetails = root.findViewById(R.id.linearLayoutDetails);
        tvSearchUsername = root.findViewById(R.id.tvSearchName);
        tvSearchUserID = root.findViewById(R.id.tvSerachUserID);
        btnSearch = root.findViewById(R.id.btnSearch);
        edtSearchPhone = root.findViewById(R.id.edtSearchPhone);
        searchCustomerImage = root.findViewById(R.id.imgCustomer);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchCustomer();

            }
        });



        return root;
    }

    private void searchCustomer() {
        linearLayoutDetails.setVisibility(View.GONE);
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        hideKeyboard(getActivity());
        isCustomerFound = false;
        String strPhoneToSearch = edtSearchPhone.getText().toString();
        if (TextUtils.isEmpty(strPhoneToSearch)){
            edtSearchPhone.setError("Enter phone number");
            edtSearchPhone.requestFocus();
            return;
        }

        databaseReference.child("Customers").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            int currentCustomer = 0;
            @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    currentCustomer += 1;
                    String phone = dataSnap.child("customerPhone").getValue(String.class);
                    assert phone != null;
                    if (phone.matches(strPhoneToSearch)) {
                        isCustomerFound = true;
                        StorageReference ref = storageReference.child(mAuth.getCurrentUser().getUid()).child(strPhoneToSearch);
                        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                searchCustomerImage.setImageBitmap(bmp);
                                tvSearchUsername.setText(dataSnap.child("customerName").getValue(String.class));
                                tvSearchPhone.setText(dataSnap.child("customerPhone").getValue(String.class));
                                tvSearchUserID.setText(dataSnap.child("userID").getValue(String.class));
                                linearLayoutDetails.setVisibility(View.VISIBLE);

                                progressDialog.dismiss();
                            }
                        });
                    }else if(currentCustomer == dataSnapshot.getChildrenCount() && !isCustomerFound){
                        Toast.makeText(getActivity(), "Phone doesn't Exist!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                         break;
                    }

                }


                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Internet Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}