package com.vitwale.vitwale.AboutUs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vitwale.vitwale.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUs extends Fragment {

    public AboutUs() {
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_about_us, container, false);
        return view;

    }
}
