package de.goblin.chelltalkapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    public void onClick(View view) {
        Animation button_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.ani_cardview_clicked);
        button_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
              // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
              // Animation ended, execute the if statement here
              if (view.getId() == R.id.cardView_dashboard) {
                  switchScreens("Dashboard");
              }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
              // Animation repeated
            }
        });
        view.startAnimation(button_animation);
    }

    public void switchScreens(String screen) {
        switch (screen) {
            case "Dashboard":
                Intent intent = new Intent(HomeActivity.this, DashboardActivity.class);
                startActivity(intent);
        }
    }
}