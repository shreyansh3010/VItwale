package com.vitwale.vitwale.Home;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vitwale.vitwale.Organisation.Blog;
import com.vitwale.vitwale.Organisation.OrganisationFragment;
import com.vitwale.vitwale.R;
import com.vitwale.vitwale.SignUpSignIn.SignUpSignIn;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    View view;
    private SliderLayout mDemoSlider;

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private ProgressDialog mprogress;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_home, container, false);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabaseUser.keepSynced(true);
        mDatabase.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent lintent = new Intent(getActivity(), SignUpSignIn.class);
                    startActivity(lintent);
                    lintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(lintent);

                }
            }
        };

        mBlogList = (RecyclerView) view.findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(getActivity()));


        mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);
        HashMap<String,String> url_maps = new HashMap<String, String>();

        url_maps.put("1. Eride", "http://vitwale.com/images/carousel/eride111.jpg");
        url_maps.put("2. Riviera", "http://vitwale.com/images/carousel/carousel_2.jpg");
        url_maps.put("3. The Electronics Club", "http://vitwale.com/images/carousel/TEC_Banner1.png");
        url_maps.put("4. Entrepreneurship Cell", "http://vitwale.com/images/carousel/ECELL_Recruitment_CR.jpg");


       /* HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Hannibal",R.drawable.a);
        file_maps.put("Big Bang Theory",R.drawable.a);
        file_maps.put("House of Cards",R.drawable.a);
        file_maps.put("Game of Thrones", R.drawable.a);
*/
        for(String name : url_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Stack);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(3000);
        mDemoSlider.addOnPageChangeListener(this);
        /*ListView l = (ListView)findViewById(R.id.transformers);
        l.setAdapter(new TransformerAdapter(this));
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDemoSlider.setPresetTransformer(((TextView) view).getText().toString());
                Toast.makeText(MainActivity.this, ((TextView) view).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });*/


        checkUserExist();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        //mprogress.setMessage("Please wait...");
        //mprogress.show();
        FirebaseRecyclerAdapter<Blog, OrganisationFragment.BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, OrganisationFragment.BlogViewHolder>(

                Blog.class,
                R.layout.blog_row,
                OrganisationFragment.BlogViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(OrganisationFragment.BlogViewHolder viewHolder, Blog model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getActivity(),model.getImage());
                //mprogress.dismiss();
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);

    }


    private void checkUserExist() {

        if(mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)) {

                        Intent sintent = new Intent(getActivity(), SignUpSignIn.class);
                        sintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(sintent);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    /*public static class BlogViewHolder extends RecyclerView.ViewHolder {



        View mView;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){

            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setDesc(String desc){
            TextView post_desc = (TextView) mView.findViewById(R.id.post_text);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String image){
            final ProgressBar mprogressBar;
            mprogressBar = (ProgressBar) mView.findViewById(R.id.progressBar5);
            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image, new Callback() {
                @Override
                public void onSuccess() {
                    mprogressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {

                }
            });
        }
    }*/

    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getActivity(),slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }*/

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_custom_indicator:
                mDemoSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
                break;
            case R.id.action_custom_child_animation:
                mDemoSlider.setCustomAnimation(new ChildAnimationExample());
                break;
            case R.id.action_restore_default:
                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                break;
            case R.id.action_github:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/daimajia/AndroidImageSlider"));
                startActivity(browserIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}




