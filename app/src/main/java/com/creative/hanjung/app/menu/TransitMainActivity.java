package com.creative.hanjung.app.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.creative.hanjung.app.R;
import com.creative.hanjung.app.fragment.FragDriverMenuActivity;
import com.creative.hanjung.app.util.BackPressCloseSystem;
import com.creative.hanjung.app.util.SettingPreference;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransitMainActivity extends AppCompatActivity {

    private static final String TAG = "TransitMainActivity";

    private BackPressCloseSystem backPressCloseSystem;
    private SettingPreference pref = new SettingPreference("loginData",this);

    public static String userNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_car);
        ButterKnife.bind(this);
        backPressCloseSystem = new BackPressCloseSystem(this);

        userNo = pref.getValue("sabun_no","").trim();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        backPressCloseSystem.onBackPressed();
    }

    @OnClick(R.id.imageView1)
    public void getMenu1() {
        Intent intent = new Intent(getBaseContext(),FragDriverMenuActivity.class);
        intent.putExtra("title", "배차조회");
        startActivity(intent);
    }

    @OnClick(R.id.imageView2)
    public void getMenu2() {
        Intent intent = new Intent(getBaseContext(),FragDriverMenuActivity.class);
        intent.putExtra("title", "실적조회");
        startActivity(intent);
    }

}
