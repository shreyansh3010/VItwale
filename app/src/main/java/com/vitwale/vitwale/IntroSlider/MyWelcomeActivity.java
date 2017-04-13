package com.vitwale.vitwale.IntroSlider;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.stephentuso.welcome.BackgroundColor;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.FragmentWelcomePage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.stephentuso.welcome.WelcomeHelper;
import com.vitwale.vitwale.IntroSlider.DoneFragment;
import com.vitwale.vitwale.R;

/**
 * Created by Chirag Arora on 11-04-2017.
 */

public class MyWelcomeActivity extends WelcomeActivity {
    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)

                .defaultBackgroundColor(new BackgroundColor(Color.WHITE))
                .defaultTitleTypefacePath("Roboto-Bold.ttf")
                .defaultHeaderTypefacePath("Roboto-Bold.ttf")
                .page(new TitlePage(R.drawable.logo, "Light Theme")
                        .titleColorResource(this, R.color.colorAccent))
                .page(new BasicPage(R.drawable.logo, "Easy styling",
                        "All colors can be customized with styles")
                        .headerColorResource(this, R.color.colorAccent))
                .page(new BasicPage(R.drawable.logo, "Easy styling",
                        "All colors can be customized with styles")
                        .headerColorResource(this, R.color.colorAccent))
                .page(new FragmentWelcomePage() {
                    @Override
                    protected Fragment fragment() {
                        return new DoneFragment();

                    }

                })
                .useCustomDoneButton(true)
                .swipeToDismiss(false)
                .backButtonNavigatesPages(false)
                .build();
        }

    }
