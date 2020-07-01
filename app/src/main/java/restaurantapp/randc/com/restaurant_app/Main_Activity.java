package restaurantapp.randc.com.restaurant_app;
//This is a comment
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.av.smoothviewpager.Smoolider.SmoothViewpager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wang.avi.AVLoadingIndicatorView;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.av.smoothviewpager.utils.Smoolider_Utils.autoplay_viewpager;
import static com.av.smoothviewpager.utils.Smoolider_Utils.stop_autoplay_ViewPager;

public class Main_Activity extends AppCompatActivity {


    private SlidingRootNav slidingRootNav;

    private static final int POS_DASHBOARD = 0;
    private static final int POS_NOTIFICATION= 1;
    private static final int POS_PLUS = 2;
    private static final int POS_PROFILE = 3;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private ImageButton menuButton;

    private LinearLayout searchBarLayout;
    private Button settingsButton;

    private  RecyclerView filterView;

    private Button logoutButton;

    private ArrayList<filterItem> filterItemList;

    private RecyclerView.LayoutManager RecyclerViewLayoutManager;

    private LinearLayoutManager HorizontalLayout;

    private RecyclerView searchRecycler;
    private ArrayList<searchItem> searchList;
    private searchAdapter searchAdapter;
    private LinearLayoutManager verticalLayout;

    private  RecyclerView mainRecycler;
    private MainAdapter mainAdapter;
    private ArrayList<MainItem> mainItems;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;

    private NestedScrollView nestedScrollView;

    private boolean atBottom;

    private AVLoadingIndicatorView mainRecyclerLoader;

    private ArrayList<String> orderIds;
    private FirebaseFirestore db ;
    private double currectLat;
    private double currentLon;
    private String tempName;
    private String tempType;
    private double tempLat;
    private double tempLon;
    private String tempAddress;
    private boolean tempFruit;
    private boolean tempVeg;
    private String tempTotalWeight;
    private boolean tempMeat;
    private CustomSmoothViewPager ongoingViewPager;
    private String tempUrl;
    private boolean tempGrain;
    private TextView nodonations;
    private boolean tempDairy;
    private   DatabaseReference rootRef;
    private String dis;
    private ArrayList<OngoingItems> ongoingItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        currectLat=0.0;
        currentLon=0.0;
        searchBarLayout = findViewById(R.id.searchBarLayout);
        nestedScrollView = findViewById(R.id.mainNestedScrollView);
        db = FirebaseFirestore.getInstance();
        filterView = findViewById(R.id.filterView);
        menuButton = findViewById(R.id.menuButton);
        mainItems = new ArrayList<>();
        mainRecycler = findViewById(R.id.mainRecycler);
        nodonations = findViewById(R.id.no_donations);

        nodonations.setVisibility(View.GONE);
        mainRecyclerLoader = findViewById(R.id.mainRecycler_loader);
        mainRecyclerLoader.setVisibility(View.GONE);
        ongoingViewPager = findViewById(R.id.ongoingSmoolider);

        ongoingItems = new ArrayList<>();
        ongoingItems.add(new OngoingItems("Childrens NGO", null, 10.5f, true, false,true,false,true));
        ongoingItems.add(new OngoingItems("Pizza hut", null, 10.5f, true, true,true,false,true));
        ongoingItems.add(new OngoingItems("Pizza hut", null, 10.5f, true, false,true,false,true));
        ongoingViewPager.setPadding(175, 0, 175, 0);

       ongoingViewPager.setAdapter(new OngoingAdapter(ongoingItems, Main_Activity.this));
      //  autoplay_viewpager(ongoingViewPager,ongoingItems.size()+1);
      //  searchRecycler = findViewById(R.id.searchRecycler);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getDisplayName().equals("Restaurant")) {
            searchList = new ArrayList<>();
            rootRef = FirebaseDatabase.getInstance().getReference();
            getOrderIDS();
            Log.d("TAG", "run: listIntitialPos: " + TOTAL_PAGES);
            getDeviceLocation();
            searchList.add(new searchItem("Bangalore", "Restaurant", "Pizza Hut", R.drawable.restaurant2));
            searchList.add(new searchItem("Mumbai", "Restaurant", "Dominos", R.drawable.restaurant3));
            searchList.add(new searchItem("Bangalore", "NGO", "Ngo 1", R.drawable.ngo1));
            searchList.add(new searchItem("Bangalore", "NGO", "Ngo 2", R.drawable.ngo2));

            searchAdapter = new searchAdapter(searchList, Main_Activity.this);

            verticalLayout = new LinearLayoutManager(
                    Main_Activity.this,
                    LinearLayoutManager.VERTICAL,
                    false);

            // searchRecycler.setLayoutManager(verticalLayout);
            // searchRecycler.setAdapter(searchAdapter);


            mainRecycler = findViewById(R.id.mainRecycler);


            verticalLayout = new LinearLayoutManager(
                    Main_Activity.this,
                    LinearLayoutManager.VERTICAL,
                    false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            mainRecyclerLoader.setVisibility(View.VISIBLE);
            mainRecyclerLoader.show();


            nestedScrollView.getViewTreeObserver()
                    .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            if (nestedScrollView.getChildAt(0).getBottom()
                                    <= (nestedScrollView.getHeight() + nestedScrollView.getScrollY() + 500)) {
                                //scroll view is at bottom
                                if (!atBottom) {
                                    atBottom = true;
                                    Log.d("tag", "onScrollChanged: reached bottom");
                                    if (orderIds.size() > 10) {
                                        mainRecyclerLoader.setVisibility(View.VISIBLE);
                                        mainRecyclerLoader.show();
                                        loadNextPage();
                                        Log.d("tag", "onScrollChanged:loaded items");
                                    }


                                }

                            } else {
                                if (atBottom) {
                                    atBottom = false;
                                    Log.d("tag", "onScrollChanged:not at bottom");
                                }
                                //scroll view is not at bottom
                            }
                        }
                    });


            RecyclerViewLayoutManager
                    = new LinearLayoutManager(
                    getApplicationContext());

            filterItemList = new ArrayList<filterItem>();

            filterItemList.add(new filterItem("Nearby", R.drawable.icons8_nearby, false));
            filterItemList.add(new filterItem("Orders", R.drawable.icons8_mostorders, false));
            filterItemList.add(new filterItem("Followers", R.drawable.icons8_person, false));
            filterItemList.add(new filterItem("Likes", R.drawable.icons8_likes2, false));
            filterItemList.add(new filterItem("Verified", R.drawable.icons8_verified_account, false));


            filterAdapter1 filterAdapter1 = new filterAdapter1(filterItemList);

            HorizontalLayout
                    = new LinearLayoutManager(
                    Main_Activity.this,
                    LinearLayoutManager.HORIZONTAL,
                    false);


            filterView.setLayoutManager(HorizontalLayout);

            // Set adapter on recycler view
            filterView.setAdapter(filterAdapter1);
        }

        slidingRootNav = new SlidingRootNavBuilder(this)

                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();



        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter2 = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_NOTIFICATION),
                createItemFor(POS_PLUS),
                createItemFor(POS_PROFILE)));
        adapter2.setListener(new DrawerAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                Main_Activity.this.onItemSelected(position);
            }
        });

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter2);

        adapter2.setSelected(POS_DASHBOARD);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.openMenu();
            }
        });

        logoutButton = findViewById(R.id.logoutButton);
        settingsButton = findViewById(R.id.settingsButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });





    }

    @Override
    public void onBackPressed() {
        // Disabling back button for current activity


    }

    public void onItemSelected(int position) {



        slidingRootNav.closeMenu();

        switch (position)
        {
            case 0:
            {
                slidingRootNav.closeMenu();
                break;
            }
            case 1:
            {

            }
            case 2:
            {
                Intent intent = new Intent(Main_Activity.this, addClass.class);
                startActivity(intent);
                break;
            }


            case 3:
            {
                Intent intent = new Intent(Main_Activity.this, profileClass.class);
                intent.putExtra("from","main");
                startActivity(intent);
                break;
            }





        }

    }


    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withTextTint(color(R.color.greenText))
                .withSelectedIconTint(color(R.color.white))
                .withSelectedTextTint(color(R.color.white));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    private void logout()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main_Activity.this);
        builder.setCancelable(true);
        builder.setTitle("Log Out");
        builder.setMessage("Are sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Main_Activity.this, loginpage.class);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void loadFirstPage() {
        Log.d("TAG", "loadFirstPage: " + orderIds.size());

        int max =10;
        if (orderIds.size()<10)
        {
            max = orderIds.size();
        }
        db = FirebaseFirestore.getInstance();

        retriever(0,max, false, 0);

    }

    public void retriever(int i, int max, boolean check, int intitialPos) {
        if (i < max) {
            String id = orderIds.get(i).trim();
            String userId = id.substring(0, id.indexOf("-")).trim();

            tempDairy = tempFruit = tempGrain = tempMeat = tempVeg = tempVeg = false;
            tempName = tempUrl = tempType = "";




            db.collection(Constants.rest_fire).document(
                        userId).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    tempName = (String) documentSnapshot.get(Constants.username);
                                    tempAddress = (String) documentSnapshot.getString("Address");
                                    tempUrl = (String) documentSnapshot.get(Constants.url_user);
                                    Log.d("TAG", "URL:" + tempUrl);
                                    tempType = (String) documentSnapshot.get(Constants.type_user);
                                    dis = "-";
                                    try {
                                        GeoPoint geoPoint = documentSnapshot.getGeoPoint("Location");
                                        tempLat = geoPoint.getLatitude();
                                        tempLon = geoPoint.getLongitude();


                                        if(currectLat!=0&&currentLon!=0) {
                                            float[] results = new float[1];
                                            Location.distanceBetween(tempLat, tempLon,
                                                    currectLat, currentLon, results);
                                            dis = Math. round(results[0] / 100) / 10.0+"KM";
                                        }
                                    }catch (Exception e) { }




                                    rootRef.child(Constants.orderName_fire).child(id).child(Constants.foodName_fire).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.hasChild(Constants.dairyName_fire)) {
                                                tempDairy = true;
                                            }
                                            if (snapshot.hasChild(Constants.fruitName_fire)) {
                                                tempFruit = true;
                                            }
                                            if (snapshot.hasChild(Constants.vegName_fire)) {
                                                tempVeg = true;
                                            }
                                            if (snapshot.hasChild(Constants.meatName_fire)) {
                                                tempMeat = true;
                                            }
                                            if (snapshot.hasChild(Constants.grainsName_fire)) {
                                                tempGrain = true;
                                            }

                                            rootRef.child(Constants.orderName_fire).child(id).child("Info").child("Total Weight").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    tempTotalWeight =snapshot.getValue().toString();

                                                    Log.d("CHECK", "TEMP WEIGHT:"+tempTotalWeight);
                                                    mainItems.add(new MainItem("Bangalore, Karnataka", tempType, dis,  tempTotalWeight, tempName, tempFruit, tempVeg, tempMeat, tempDairy, false, tempGrain, tempUrl, userId, id, tempAddress));
                                                    retriever(i+1,max, check, intitialPos);
                                                    }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.d("TAG", "onFailure: " + error.toString());
                                                }
                                                    });




                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.d("TAG", "onFailure: " + error.toString());
                                        }
                                    });

                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG", "onFailure: " + e.toString());
                            }
                        });


            }

        else {
            if (!check) {

                mainAdapter = new MainAdapter(Main_Activity.this, mainItems);
                mainRecycler.setLayoutManager(verticalLayout);
                mainRecycler.setAdapter(mainAdapter);
                mainRecycler.setItemAnimator(new DefaultItemAnimator());
                mainRecyclerLoader.setVisibility(View.GONE);
                mainRecyclerLoader.hide();
            }

            else {
                mainAdapter.notifyItemRangeChanged(intitialPos, mainItems.size() - 1);
                mainRecyclerLoader.setVisibility(View.GONE);
                mainRecyclerLoader.hide();
            }
        }
        }


    private void loadNextPage() {


        if (mainItems != null && mainAdapter != null &&mainItems.size()<orderIds.size()) {
            Log.d("TAG", "loadNextPage: " + currentPage);
            //List<MainItem> newItems = new ArrayList<>();


                    int intitialPos = mainItems.size() - 1;
                     int listIntitialPos = currentPage * 10;
                     int listFinalPos = ((currentPage+1)*10-1);


                    if (currentPage==(TOTAL_PAGES-1)) {

                        listFinalPos = orderIds.size()-1;

                    }
                    Log.d("TAG", "run: listFinalPos: "+listFinalPos);
                    currentPage+=1;


                    isLoading = false;

                    retriever(intitialPos, listFinalPos, true, intitialPos);





                    if (currentPage != TOTAL_PAGES) {
                        //add animation
                    } else
                        isLastPage = true;



        }
        else
        {
            mainRecyclerLoader.hide();
            mainRecyclerLoader.setVisibility(View.GONE);

        }
    }

    private void getOrderIDS()
    {
        orderIds = new ArrayList<>();

        db.collection(Constants.orderName_fire).document(Constants.order_list_fire)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists())
                {
                    orderIds = (ArrayList) documentSnapshot.get(Constants.order_list_field);
                    if (orderIds!=null && orderIds.size()>0) {
                        TOTAL_PAGES = (int) Math.ceil(orderIds.size() / (10.0f));
                        loadFirstPage();
                        nodonations.setVisibility(View.GONE);
                    }
                    else {
                        mainRecyclerLoader.hide();
                        mainRecyclerLoader.setVisibility(View.GONE);
                        nodonations.setVisibility(View.VISIBLE);
                    }


                }

            }
        });






    }
    private void getDeviceLocation(){
        Log.d("TAG", "getDeviceLocation: getting the devices current location");

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{


            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Log.d("TAG", "onComplete: found location");
                        Location currentLocation = (Location) task.getResult();

                        if(currentLocation!=null) {
                           currectLat = currentLocation.getLatitude();
                           currentLon = currentLocation.getLongitude();
                        }

                    }else{
                        Log.d("TAG", "onComplete: current location is null");
                        Toast.makeText(Main_Activity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch (SecurityException e){
            Log.e("TAG", "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

}
