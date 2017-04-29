package com.vitwale.vitwale.Home;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
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
import com.firebase.client.Firebase;
import com.firebase.client.annotations.NotNull;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vitwale.vitwale.Organisation.Blog;
import com.vitwale.vitwale.Organisation.OrganisationFragment;
import com.vitwale.vitwale.R;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout mDemoSlider;
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private ProgressDialog mprogress;
    View view;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment_tab_club
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);

        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://vitwale-e0508.firebaseio.com");
        mDatabase.child("Blog").keepSynced(true);

        mBlogList = (RecyclerView) view.findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBlogList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        mDatabase.child("home_slider").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    String img1 = dataSnapshot.child("img1").getValue(String.class);
                    String img2 = dataSnapshot.child("img2").getValue(String.class);
                    String img3 = dataSnapshot.child("img3").getValue(String.class);
                    String img4 = dataSnapshot.child("img4").getValue(String.class);
                HashMap<String,String> url_maps = new HashMap<String, String>();

                url_maps.put("1. Eride", img1);
                url_maps.put("2. Riviera", img2);
                url_maps.put("3. The Electronics Club", img3);
                url_maps.put("4. Entrepreneurship Cell", img4);
                for(String name : url_maps.keySet()){
                    TextSliderView textSliderView = new TextSliderView(getActivity());
                    // initialize a SliderLayout
                    textSliderView
                            .description(name)
                            .image(url_maps.get(name))
                            .setScaleType(BaseSliderView.ScaleType.Fit);

                    //add your extra information
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra",name);

                    mDemoSlider.addSlider(textSliderView);

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Stack);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(3000);
        mDemoSlider.addOnPageChangeListener(this);
        mprogress = new ProgressDialog(getActivity());
        mprogress.setCanceledOnTouchOutside(false);

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        mprogress.setMessage("Please wait...");
        mprogress.show();
        FirebaseRecyclerAdapter<Blog, OrganisationFragment.BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, OrganisationFragment.BlogViewHolder>(

                Blog.class,
                R.layout.blog_row,
                OrganisationFragment.BlogViewHolder.class,
                mDatabase.child("Blog")
        ) {
            @Override
            protected void populateViewHolder(OrganisationFragment.BlogViewHolder viewHolder, Blog model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getActivity(),model.getImage());
                mprogress.dismiss();
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }



    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment_tab_club is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getActivity(),slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}




