package com.creative.hanjung.app.driver;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.creative.hanjung.app.R;
import com.creative.hanjung.app.util.UtilClass;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarResultsDialogActivity extends Activity {
    private static final String TAG = "CarResultsDialogActivity";

    @Bind(R.id.textView1) TextView tv_data1;
    @Bind(R.id.textView2) TextView tv_data2;
    @Bind(R.id.textView3) TextView tv_data3;
    @Bind(R.id.textView4) TextView tv_data4;
    @Bind(R.id.textView5) TextView tv_data5;
    @Bind(R.id.textView6) TextView tv_data6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.car_results_popup);
        ButterKnife.bind(this);

        int position= getIntent().getIntExtra("position", 0);
        try {
            ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String,String>>) getIntent().getSerializableExtra("list");

            tv_data1.setText(list.get(position).get("data1"));
            tv_data2.setText(list.get(position).get("data2"));
            tv_data3.setText(list.get(position).get("data3"));
            tv_data4.setText(list.get(position).get("data4"));
            tv_data5.setText(list.get(position).get("data5"));
            tv_data6.setText(list.get(position).get("data6"));

        }catch (Exception e){
            e.printStackTrace();
        }

    }//onCreate

    @OnClick(R.id.textButton1)
    public void doClose() {
        finish();
    }


}
