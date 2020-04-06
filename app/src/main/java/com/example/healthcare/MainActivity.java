package com.example.healthcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.healthcare.Adapter.NewsFeedAdapter;
import com.example.healthcare.Dialog.NetworkBox;
import com.example.healthcare.Fragments.AskQuestionFragment;
import com.example.healthcare.Fragments.CategoryFragment;
import com.example.healthcare.Fragments.HomeFragment;
import com.example.healthcare.Fragments.ProfileFragment;
import com.example.healthcare.Fragments.UnAnswerdQuestionFragment;
import com.example.healthcare.Model.Address_components;
import com.example.healthcare.Model.DoctorInfo;
import com.example.healthcare.Model.NewsFeedModel;
import com.example.healthcare.Model.PatientInfo;
import com.example.healthcare.Model.Results;
import com.example.healthcare.Model.Root;
import com.example.healthcare.Model.TypeOfUser;
import com.example.healthcare.Model.UserConstantModel;
import com.example.healthcare.Model.UserModel;
import com.example.healthcare.Notification.Token;
import com.example.healthcare.Remote.Common;
import com.example.healthcare.Remote.IGoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.healthcare.BastardClass.firebaseUser;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    BottomNavigationView bottomNavigationView;

    DatabaseReference reference;
    public static String userType="";
    public static String  CURRENT_CITY ="";
    public static String CURRENT_LOCATION_ADDRESS;
    public String COUNTRY="country";
    public String POLITICAL ="political";
    public String LOCAL="locality";
    public String ADMINISTRATIVE_AREA_LEVEL_2="administrative_area_level_2";
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    private static final int MY_PERMISSION_CODE = 100;
    public GoogleApiClient googleApiClient;
    IGoogleApiClient iGoogleApiClient;
    Location mlocation;
    Root currentPlace;
    DialogFragment alertMessageConnection = new NetworkBox();

    public String CURRENT_USER;
    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    private static final String DEBUG_TAG = "NetworkStatusExample";
    boolean isWifiConn = false;
    boolean isMobileConn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationViewListener);


        iGoogleApiClient= Common.getGoogleAPIService();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkLocationPermission()){
                getPermission();
            }else {
                buildGoogleApiClient();
            }

        }

        updateToken(FirebaseInstanceId.getInstance().getToken());
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        if(connectivityManager != null) {
            connectivityManager.registerNetworkCallback(builder.build(),
                    new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(@NonNull Network network) {
                            super.onAvailable(network);
                        }

                        @Override
                        public void onLost(@NonNull Network network) {
                            super.onLost(network);
                        }
                    });
        }
    }


    private void updateToken(String token){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);
    }

    public void getUsertype(){
        final String uId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference=FirebaseDatabase.getInstance().getReference();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userType=dataSnapshot.child("userType").child(uId).child("type").getValue(String.class);
                GetCurrentUserInfo((userType.equals("patient"))?"user":"doctor");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"Network error, Please check your connection",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public  void GetCurrentUserInfo(final String currentUser){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(currentUser);
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    if (currentUser.equals("user")){
                        PatientInfo userModel=dataSnapshot.getValue(PatientInfo.class);
                        UserConstantModel.Name = userModel.getPatientName();
                        UserConstantModel.Email = userModel.getPatientEmail();
                        UserConstantModel.Uid =  userModel.getPatientUid();
                        UserConstantModel.UserType = userModel.getUserType();
                        UserConstantModel.ImageUri = userModel.getImageUrl();
                        Log.i("constants = ",UserConstantModel.Uid);

                    }else {
                        DoctorInfo userModel=dataSnapshot.getValue(DoctorInfo.class);
                        UserConstantModel.Name = userModel.getDoctorName();
                        UserConstantModel.Email = userModel.getDoctorEmail();
                        UserConstantModel.Uid =  userModel.getDoctorUid();
                        UserConstantModel.ImageUri = userModel.getImageUrl();
                        UserConstantModel.UserType = userModel.getUser_type();
                    }


                }else {
                    Toast.makeText(MainActivity.this, "your data did not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUrl() {

        StringBuilder stringBuilder=new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?");
        stringBuilder.append("latlng="+mlocation.getLatitude()+","+mlocation.getLongitude());
        stringBuilder.append("&key="+getResources().getString(R.string.browser_key));
        Log.d("geturl",stringBuilder.toString());
        return stringBuilder.toString();
    }


    private void nearByPlaces() {


        iGoogleApiClient.getDetailOfCurrentLocation(getUrl())
                .enqueue(new Callback<Root>() {
                    @Override
                    public void onResponse(Call<Root> call, Response<Root> response) {
                        currentPlace=response.body();
                        int count=0;
                        if (response.isSuccessful()){
                            for (int i=0;i<response.body().getResults().size();i++){
                                count++;
                                Results myplaces = response.body().getResults().get(i);
                                for (int j=0;j<myplaces.getAddress_components().size();j++){
                                    List<String> types_of_city=myplaces.getAddress_components().get(j).getTypes();
                                    for (int k=0;k<types_of_city.size();k++){
                                        if (ADMINISTRATIVE_AREA_LEVEL_2.equals(types_of_city.get(k)) && CURRENT_CITY.equals("")){
                                            CURRENT_CITY=myplaces.getAddress_components().get(j).getLong_name();
                                            CURRENT_LOCATION_ADDRESS=myplaces.getFormatted_address();
                                            bottomNavigationView.setSelectedItemId(R.id.home);
                                            i=response.body().getResults().size()+1;
                                            j=myplaces.getAddress_components().size()+1;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Root> call, Throwable t) {

                    }
                });

    }

    private void getPermission(){


        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_CODE);
        }else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_CODE);
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else
            return false;
    }



    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationViewListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.home:
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment, new HomeFragment()).commit();
                            getSupportActionBar().setTitle("NewsFeed");
                            return true;
                        case R.id.unanswerd:
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment, new UnAnswerdQuestionFragment()).commit();
                            getSupportActionBar().setTitle("Un Answered");
                            return true;
                        case R.id.askQuestion:
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment, new AskQuestionFragment()).commit();
                            getSupportActionBar().setTitle("Ask Question");
                            return true;
                        case R.id.categories:
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment, new CategoryFragment()).commit();
                            getSupportActionBar().setTitle("Categories");
                            return true;
                        case R.id.profile:
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment, new ProfileFragment()).commit();
                            getSupportActionBar().setTitle("Profile");
                            return true;

                    }


                    return false;
                }

            };

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "connect to API", Toast.LENGTH_SHORT).show();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mlocation=location;
                getUsertype();
                nearByPlaces();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MainActivity.this, "Disconnect", Toast.LENGTH_SHORT).show();
        googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "connection problem", Toast.LENGTH_SHORT).show();
    }


    private synchronized void buildGoogleApiClient() {
        googleApiClient=new GoogleApiClient.Builder(MainActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        googleApiClient.connect();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_CODE:
            {
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        buildGoogleApiClient();
                        Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }



}
