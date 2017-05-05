package com.vitwale.vitwale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.vitwale.vitwale.AboutUs.AboutUs;
import com.vitwale.vitwale.ChatBox.ChatActivity;
import com.vitwale.vitwale.ClubsGeneral.ClubsGeneral;
import com.vitwale.vitwale.ContactUs.ContactUs;
import com.vitwale.vitwale.Home.HomeFragment;
import com.vitwale.vitwale.Organisation.OrganisationFragment;
import com.vitwale.vitwale.Recruitments.Recruitments;
import com.vitwale.vitwale.SignUpSignIn.SignUpSignIn;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog mprogress;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUser;
    private TextView mprofileName, mProfileReg;
    private ImageView mProfileImage;
    private ImageView mSettings;
    private FirebaseUser user;
    private String name;
    private String uid;
    private FirebaseDatabase mDatabase;
    private Toolbar toolbar = null;
    private static final String REG_TOKEN = "REG_TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        HomeFragment fragment = new HomeFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Home");


        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUser.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent Loginintent = new Intent(MainActivity.this, SignUpSignIn.class);
                    Loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(Loginintent);
                }
                else {
                    final String uid = mAuth.getCurrentUser().getUid();
                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child(uid).child("name").getValue(String.class);
                            String img = dataSnapshot.child(uid).child("image").getValue(String.class);
                            String Reg = dataSnapshot.child(uid).child("Reg").getValue(String.class);
                            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                            View headerView = navigationView.getHeaderView(0);
                            mprofileName = (TextView) headerView.findViewById(R.id.profileName);
                            mprofileName.setText(name);
                            mProfileReg = (TextView) headerView.findViewById(R.id.profileReg);
                            mProfileReg.setText(Reg);
                            mProfileImage = (ImageView) headerView.findViewById(R.id.user_Img);
                            Picasso.with(getApplication()).load(img).into(mProfileImage);
                            mSettings = (ImageButton) headerView.findViewById(R.id.settingBtn);
                            mSettings.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(MainActivity.this, Profile.class);
                                    startActivity(i);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        checkUserExist();

    }


    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            HomeFragment fragment = new HomeFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Home");
        } else if (id == R.id.nav_organizations) {
            OrganisationFragment fragment = new OrganisationFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Organisations");

        } else if (id == R.id.nav_events) {
            Intent intent=new Intent(MainActivity.this, ClubsGeneral.class);
            startActivity(intent);

        } else if (id == R.id.nav_recruitments) {
            Recruitments fragment = new Recruitments();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Recruitments");



        } else if (id == R.id.nav_about_us) {
            AboutUs fragment = new AboutUs();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("About Us");

        } else if (id == R.id.nav_contact_us) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            getIntent().setData(Uri.parse(("mailto: ")));
            String[] to = {"vitwale@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL, to);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Hi! This is sent from my app.");
            intent.putExtra(Intent.EXTRA_TEXT, "Hey Whats Up, How you doing? This is my first email message");
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Send Email"));

        } else if (id == R.id.nav_chat_box) {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
            //getSupportActionBar().setTitle("Chat Box");

        } else if (id == R.id.nav_logout) {
            logout();                             // for trail purpose
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void logout() {
        mAuth.signOut();
    }
    private void checkUserExist() {

        if(mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)) {

                        Intent setupintent = new Intent(MainActivity.this, SignUpSignIn.class);
                        setupintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupintent);

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
