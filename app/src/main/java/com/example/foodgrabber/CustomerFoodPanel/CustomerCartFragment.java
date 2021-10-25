package com.example.foodgrabber.CustomerFoodPanel;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodgrabber.Customer;
import com.example.foodgrabber.R;
import com.example.foodgrabber.ReusableCode.ReusableCodeForAll;
import com.example.foodgrabber.SendNotification.APIService;
import com.example.foodgrabber.SendNotification.Client;
import com.example.foodgrabber.SendNotification.Data;
import com.example.foodgrabber.SendNotification.MyResponse;
import com.example.foodgrabber.SendNotification.NotificationSender;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class CustomerCartFragment extends Fragment {

    RecyclerView recyclecart;
    private List<Cart> cartModelList;
    private CustomerCartAdapter adapter;
    private LinearLayout TotalBtns;
    DatabaseReference databaseReference, data, reference, ref, getRef, dataa;
    public static TextView grandt;
    Button remove, placeorder;
    String address, Addnote;
    String Latitude_Lagitude;
    String DishId, RandomUId, ChefId;
    Button btn_PickLocation;
    TextView tv_MyLocation;
    WifiManager wifiManager;
    EditText localaddress;
    View view;
    private final static int PLACE_PICKER_REQUEST = 999;
    private ProgressDialog progressDialog;
    private APIService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Cart");
        View v = inflater.inflate(R.layout.fragment_customercart, null);
        recyclecart = v.findViewById(R.id.recyclecart);
        recyclecart.setHasFixedSize(true);
        recyclecart.setLayoutManager(new LinearLayoutManager(getContext()));
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        cartModelList = new ArrayList<>();
        grandt = v.findViewById(R.id.GT);
        remove = v.findViewById(R.id.RM);
        placeorder = v.findViewById(R.id.PO);
        TotalBtns = v.findViewById(R.id.TotalBtns);
        //wifiManager= (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        customercart();
        return v;
    }
    private void customercart() {

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child("CartItems").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                cartModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cart cart = snapshot.getValue(Cart.class);

                    cartModelList.add(cart);
                }
                if (cartModelList.size() == 0) {
                    TotalBtns.setVisibility(View.INVISIBLE);
                } else {
                    TotalBtns.setVisibility(View.VISIBLE);
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Are you sure you want to remove Dish");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    FirebaseDatabase.getInstance().getReference("Cart").child("CartItems").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                    FirebaseDatabase.getInstance().getReference("Cart").child("GrandTotal").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

                                }
                            });
                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();

                        }
                    });


                    String UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    data = FirebaseDatabase.getInstance().getReference("Customer").child(UserID);
                    data.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final Customer customer = dataSnapshot.getValue(Customer.class);
                            placeorder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    FirebaseDatabase.getInstance().getReference("AlreadyOrdered").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("isOrdered").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            String ss = "";
                                            if (dataSnapshot.exists()) {
                                                ss = dataSnapshot.getValue(String.class);
                                            }

                                            if (ss.trim().equalsIgnoreCase("false") || ss.trim().equalsIgnoreCase("")) {

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                builder.setTitle("Pick Address");
                                                LayoutInflater inflater = getActivity().getLayoutInflater();
                                                view = inflater.inflate(R.layout.enter_address, null);
                                                btn_PickLocation = view.findViewById(R.id.btnPickLocation);
                                                //tv_MyLocation= view.findViewById(R.id.LA);
                                                builder.setView(view);


                                                final EditText addnote = (EditText) view.findViewById(R.id.addnote);

                                                btn_PickLocation.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        //Disable Wifi
                                                        //wifiManager.setWifiEnabled(false);
                                                        openPlacePicker();
                                                    }

                                                });


                                                //localaddress.setText(address+"2");

                                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {


                                                        progressDialog.setMessage("Please wait...");
                                                        progressDialog.show();

                                                        reference = FirebaseDatabase.getInstance().getReference("Cart").child("CartItems").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                RandomUId = UUID.randomUUID().toString();
                                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                                    final Cart cart1 = dataSnapshot1.getValue(Cart.class);
                                                                    DishId = cart1.getDishID();
                                                                    //address = ;     //Edit here
                                                                    Addnote = addnote.getText().toString().trim();
                                                                    final HashMap<String, String> hashMap = new HashMap<>();
                                                                    hashMap.put("ChefId", cart1.getChefId());
                                                                    hashMap.put("DishID", cart1.getDishID());
                                                                    hashMap.put("DishName", cart1.getDishName());
                                                                    hashMap.put("DishQuantity", cart1.getDishQuantity());
                                                                    hashMap.put("Price", cart1.getPrice());
                                                                    hashMap.put("TotalPrice", cart1.getTotalprice());
                                                                    FirebaseDatabase.getInstance().getReference("CustomerPendingOrders").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(RandomUId).child("Dishes").child(DishId).setValue(hashMap);

                                                                }
                                                                ref = FirebaseDatabase.getInstance().getReference("Cart").child("GrandTotal").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("GrandTotal");
                                                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        String grandtotal = dataSnapshot.getValue(String.class);
                                                                        localaddress = (EditText) view.findViewById(R.id.LA);
                                                                        address = localaddress.getText().toString().trim();

                                                                        HashMap<String, String> hashMap1 = new HashMap<>();
                                                                        hashMap1.put("Address", address);
                                                                        hashMap1.put("Latitude_Lagitude", Latitude_Lagitude);
                                                                        hashMap1.put("GrandTotalPrice", String.valueOf(grandtotal));
                                                                        hashMap1.put("MobileNumber", customer.getMobileno());
                                                                        hashMap1.put("Name", customer.getFirstName() + " " + customer.getLastName());
                                                                        hashMap1.put("Note", Addnote);
                                                                        FirebaseDatabase.getInstance().getReference("CustomerPendingOrders").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(RandomUId).child("OtherInformation").setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                FirebaseDatabase.getInstance().getReference("Cart").child("CartItems").child(FirebaseAuth.getInstance().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        FirebaseDatabase.getInstance().getReference("Cart").child("GrandTotal").child(FirebaseAuth.getInstance().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                getRef = FirebaseDatabase.getInstance().getReference("CustomerPendingOrders").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(RandomUId).child("Dishes");
                                                                                                getRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                                                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                                                                                            final CustomerPendingOrders customerPendingOrders = dataSnapshot2.getValue(CustomerPendingOrders.class);
                                                                                                            String d = customerPendingOrders.getDishID();
                                                                                                            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                                                                            ChefId = customerPendingOrders.getChefId();
                                                                                                            final HashMap<String, String> hashMap2 = new HashMap<>();
                                                                                                            hashMap2.put("ChefId", ChefId);
                                                                                                            hashMap2.put("DishId", customerPendingOrders.getDishID());
                                                                                                            hashMap2.put("DishName", customerPendingOrders.getDishName());
                                                                                                            hashMap2.put("DishQuantity", customerPendingOrders.getDishQuantity());
                                                                                                            hashMap2.put("Price", customerPendingOrders.getPrice());
                                                                                                            hashMap2.put("RandomUID", RandomUId);
                                                                                                            hashMap2.put("TotalPrice", customerPendingOrders.getTotalPrice());
                                                                                                            hashMap2.put("UserId", userid);

                                                                                                            FirebaseDatabase.getInstance().getReference("ChefPendingOrders").child(ChefId).child(RandomUId).child("Dishes").child(d).setValue(hashMap2);
                                                                                                        }
                                                                                                        dataa = FirebaseDatabase.getInstance().getReference("CustomerPendingOrders").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(RandomUId).child("OtherInformation");
                                                                                                        dataa.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                            @Override
                                                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                CustomerPendingOrders1 customerPendingOrders1 = dataSnapshot.getValue(CustomerPendingOrders1.class);
                                                                                                                HashMap<String, String> hashMap3 = new HashMap<>();
                                                                                                                hashMap3.put("Address", customerPendingOrders1.getAddress());
                                                                                                                hashMap3.put("Latitude_Lagitude", customerPendingOrders1.getLatitude_Lagitude());
                                                                                                                hashMap3.put("GrandTotalPrice", customerPendingOrders1.getGrandTotalPrice());
                                                                                                                hashMap3.put("MobileNumber", customerPendingOrders1.getMobileNumber());
                                                                                                                hashMap3.put("Name", customerPendingOrders1.getName());
                                                                                                                hashMap3.put("Note", customerPendingOrders1.getNote());
                                                                                                                hashMap3.put("RandomUID", RandomUId);

                                                                                                                FirebaseDatabase.getInstance().getReference("ChefPendingOrders").child(ChefId).child(RandomUId).child("OtherInformation").setValue(hashMap3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(Void aVoid) {

                                                                                                                        FirebaseDatabase.getInstance().getReference("AlreadyOrdered").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("isOrdered").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(Void aVoid) {

                                                                                                                                FirebaseDatabase.getInstance().getReference().child("Tokens").child(ChefId).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                                    @Override
                                                                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                                        String usertoken = dataSnapshot.getValue(String.class);
                                                                                                                                        sendNotifications(usertoken, "New Order", "You have a new Order", "Order");
                                                                                                                                        progressDialog.dismiss();
                                                                                                                                        ReusableCodeForAll.ShowAlert(getContext(), "", "Your Order has been shifted to Pending state, please wait until the Chef accept your order.");
                                                                                                                                    }

                                                                                                                                    @Override
                                                                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                                                    }
                                                                                                                                });

                                                                                                                            }
                                                                                                                        });


                                                                                                                    }


                                                                                                                });
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                            }
                                                                                                        });
//                                                                                                            }
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });
//                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                                        dialog.dismiss();
                                                    }
                                                });
                                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                AlertDialog aler = builder.create();
                                                aler.show();

                                            } else {
                                                ReusableCodeForAll.ShowAlert(getContext(), "Error", "It seems you have already placed the order, So you cannot place another order until the delivery of first order");
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                adapter = new CustomerCartAdapter(getContext(), cartModelList);
                recyclecart.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void openPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            //startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            //Enable Wifi
            //wifiManager.setWifiEnabled(true);

        } catch (GooglePlayServicesRepairableException e) {
            Log.d("Exception",e.getMessage());

            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d("Exception",e.getMessage());

            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(getActivity(), data);

                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;
                    Latitude_Lagitude = String.valueOf(latitude)+","+String.valueOf(longitude);

                    Geocoder geocoder;
                    List<Address> addresses = null;
                    geocoder = new Geocoder(getContext(), Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                   // String country = addresses.get(0).getCountryName();
                   // String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                   address =knownName+", " +city + ","+state;

                   localaddress = (EditText) view.findViewById(R.id.LA);
                   localaddress.setText(address);
            }
        }
    }


    private void sendNotifications(String usertoken, String title, String message, String order) {

        Data data = new Data(title, message, order);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}
