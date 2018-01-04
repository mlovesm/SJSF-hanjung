package com.creative.hanjung.app.driver;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.creative.hanjung.app.R;
import com.creative.hanjung.app.adaptor.DriverAdapter;
import com.creative.hanjung.app.menu.TransitMainActivity;
import com.creative.hanjung.app.retrofit.Datas;
import com.creative.hanjung.app.retrofit.RetrofitService;
import com.creative.hanjung.app.util.UtilClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarResultFragment extends Fragment {
    private static final String TAG = "CarResultFragment";
    private ProgressDialog pDlalog = null;
    private RetrofitService service;
    private String title;
    private String userNo;

    private ArrayList<HashMap<String,String>> arrayList;
    private DriverAdapter mAdapter;

    private Animation slideUp;
    private Animation slideDown;
    private boolean isDown = true;

    @Bind(R.id.linear1) LinearLayout layout;
    @Bind(R.id.listView1) ListView listView;
    @Bind(R.id.top_title) TextView textTitle;
    @Bind(R.id.imageView1) ImageView exButton;
    @Bind(R.id.date_button) TextView tv_date;
    @Bind(R.id.textView1) TextView tv_data1;
    @Bind(R.id.textView2) TextView tv_data2;
    @Bind(R.id.textView3) TextView tv_data3;
    @Bind(R.id.textView4) TextView tv_data4;
    @Bind(R.id.textView5) TextView tv_data5;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.car_result, container, false);
        ButterKnife.bind(this, view);
        service= RetrofitService.rest_api.create(RetrofitService.class);

        userNo= TransitMainActivity.userNo;
        title= getArguments().getString("title");
        textTitle.setText(title);
        tv_date.setText(UtilClass.getCurrentDate(3, "."));

        getTransportInfo();
        getTransportResultsInfo();

        slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slideUp.setAnimationListener(animationListener);
        slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slideDown.setAnimationListener(animationListener);

        listView.setOnItemClickListener(new ListViewItemClickListener());

        return view;
    }//onCreateView

    public void startAnimation() {
        isDown = !isDown;

        if (isDown) {
            layout.startAnimation(slideDown);
            exButton.setImageResource(R.drawable.circle_minus);
        } else {
            layout.startAnimation(slideUp);
            exButton.setImageResource(R.drawable.circle_plus);
        }
    }

    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            layout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (!isDown) {
                layout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    public void getTransportInfo(){
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        UtilClass.showProcessingDialog(dialog);

        Call<Datas> call = service.listData("Common","transportInfo", userNo);
        call.enqueue(new Callback<Datas>() {
            @Override
            public void onResponse(Call<Datas> call, Response<Datas> response) {
                UtilClass.logD(TAG, "response="+response);
                if (response.isSuccessful()) {
                    UtilClass.logD(TAG, "isSuccessful="+response.body().toString());
                    String status= response.body().getStatus();
                    try {
                        if(status.equals("success")){
                            tv_data1.setText(response.body().getList().get(0).get("상호"));
                            tv_data2.setText(response.body().getList().get(0).get("등록번호"));
                            tv_data3.setText(userNo);
                            tv_data4.setText(response.body().getList().get(0).get("성명"));
                            tv_data5.setText(response.body().getList().get(0).get("주소"));
                        }

                    } catch ( Exception e ) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "에러코드 CarResult 1", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "response isFailed", Toast.LENGTH_SHORT).show();
                }
                if(dialog!=null) dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Datas> call, Throwable t) {
                if(dialog!=null) dialog.dismiss();
                UtilClass.logD(TAG, "onFailure="+call.toString()+", "+t);
                Toast.makeText(getActivity(), "onFailure CarResult 1",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getTransportResultsInfo(){
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        UtilClass.showProcessingDialog(dialog);

        Call<Datas> call = service.listData("Common","transportResults", userNo, tv_date.getText().toString());
        call.enqueue(new Callback<Datas>() {
            @Override
            public void onResponse(Call<Datas> call, Response<Datas> response) {
                UtilClass.logD(TAG, "response="+response);
                if (response.isSuccessful()) {
                    UtilClass.logD(TAG, "isSuccessful="+response.body().toString());
                    String status= response.body().getStatus();
                    try {
                        if(response.body().getCount()==0){
                            Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        arrayList = new ArrayList<>();
                        arrayList.clear();
                        for(int i=0; i<response.body().getList().size();i++){
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("key", String.valueOf(i+1));
                            hashMap.put("ord",response.body().getList().get(i).get("ORD"));
                            hashMap.put("data1",response.body().getList().get(i).get("일자"));
                            hashMap.put("data2",response.body().getList().get(i).get("품목"));
                            hashMap.put("data3",response.body().getList().get(i).get("운송구간"));
                            hashMap.put("data4",response.body().getList().get(i).get("중량"));
//                            if(response.body().getList().get(i).get("중량") !=null){
//                                float num= Float.parseFloat(response.body().getList().get(i).get("중량"));
//                                hashMap.put("data4", UtilClass.numericZeroCheck(String.valueOf(Math.round(num * 100) / 100.0)));
//                            }else{
//                                hashMap.put("data4", "");
//                            }
                            if(response.body().getList().get(i).get("단가") !=null){
                                hashMap.put("data5",UtilClass.commaToNum(UtilClass.numericZeroCheck(response.body().getList().get(i).get("단가"))));
                            }else{
                                hashMap.put("data5", "");
                            }
                            if(response.body().getList().get(i).get("금액") !=null){
                                hashMap.put("data6",UtilClass.commaToNum(UtilClass.numericZeroCheck(response.body().getList().get(i).get("금액"))));
                            }else{
                                hashMap.put("data6", "");
                            }
                            arrayList.add(hashMap);
                        }
                        mAdapter = new DriverAdapter(getActivity(), arrayList, "CarResults");
                        listView.setAdapter(mAdapter);
//                        UtilClass.setListViewHeightBasedOnChildren(listView);

                    } catch ( Exception e ) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "에러코드 CarResult 2", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "response isFailed", Toast.LENGTH_SHORT).show();
                }
                if(dialog!=null) dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Datas> call, Throwable t) {
                if(dialog!=null) dialog.dismiss();
                UtilClass.logD(TAG, "onFailure="+call.toString()+", "+t);
                Toast.makeText(getActivity(), "onFailure CarResult 2",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.imageView1)
    public void expandableView() {
        startAnimation();
    }

    //날짜 필드
    private static final String DATE_PICKER = "mDatePicker";
    private static final String DAY_FIELD = "day";
    private static final String ID = "id";
    private static final String ANDROID = "android";
    private Context mContext;
    private DatePickerDialog mDatePickerDialog;

    //날짜설정
    @OnClick(R.id.date_button)
    public void getDateDialog() {
        mContext = getActivity();
        int year = 0, month = 0;
        if(tv_date.length()>0){
            String date= tv_date.getText().toString();
            int point= date.indexOf(".");
            year= Integer.parseInt(date.substring(0,point));
            month= Integer.parseInt(date.substring(point+1));

        }

        if (Build.VERSION.SDK_INT == 24) {    // Android 7.0 Nougat, API 24
            final Context contextThemeWrapper = new ContextThemeWrapper(mContext, android.R.style.Theme_Holo_Light_Dialog);
            try {
                mDatePickerDialog = new FixedHoloDatePickerDialog(contextThemeWrapper, date_listener, year, month, -1);

            } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException |
                    InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            mDatePickerDialog= dialogDatePicker(year, month);
        }
        mDatePickerDialog.show();

    }


    //24 제외 버전
    public DatePickerDialog dialogDatePicker(int year, int month) {
        mDatePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, date_listener, year, month, -1);
        try {
            Field[] datePickerDialogFields = mDatePickerDialog.getClass().getDeclaredFields();
            for (Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals(DATE_PICKER)) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker =
                            (DatePicker) datePickerDialogField.get(mDatePickerDialog);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        customDatePicker(datePicker);
                    } else {
                        if ("mDaySpinner".equals(datePickerDialogField.getName())
                                || "mDaySpinner".equals(datePickerDialogField
                                .getName())
//                                    || "mMonthPicker".equals(datePickerField.getName())
//                                    || "mMonthSpinner".equals(datePickerField.getName())
                                ) {
                            datePickerDialogField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerDialogField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException: ", e);
        }

        return mDatePickerDialog;
    }

    //24버전
    DatePicker mDatePicker;
    private final class FixedHoloDatePickerDialog extends DatePickerDialog {
        private FixedHoloDatePickerDialog(Context context, OnDateSetListener callBack, int year,
                                          int monthOfYear, int dayOfMonth)
                throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException,
                InvocationTargetException, InstantiationException, java.lang.InstantiationException {
            super(context, callBack, year, monthOfYear, dayOfMonth);

            final Field field =
                    this.findField(DatePickerDialog.class, DatePicker.class, DATE_PICKER);
            assert field != null;
            try {
                mDatePicker = (DatePicker) field.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            final Class<?> delegateClass =
                    Class.forName("android.widget.DatePicker$DatePickerDelegate");
            final Field delegateField =
                    this.findField(DatePicker.class, delegateClass, "mDelegate");
            assert delegateField != null;
            final Object delegate = delegateField.get(mDatePicker);
            final Class<?> spinnerDelegateClass =
                    Class.forName("android.widget.DatePickerSpinnerDelegate");
            if (delegate.getClass() != spinnerDelegateClass) {
                delegateField.set(mDatePicker, null);
                mDatePicker.removeAllViews();
                final Constructor spinnerDelegateConstructor =
                        spinnerDelegateClass.getDeclaredConstructor(DatePicker.class, Context.class,
                                AttributeSet.class, int.class, int.class);
                spinnerDelegateConstructor.setAccessible(true);
                final Object spinnerDelegate =
                        spinnerDelegateConstructor.newInstance(mDatePicker, context, null,
                                android.R.attr.datePickerStyle, 0);
                delegateField.set(mDatePicker, spinnerDelegate);
                mDatePicker.init(year, monthOfYear, dayOfMonth, this);
                customDatePicker(mDatePicker);
                mDatePicker.setCalendarViewShown(false);
                mDatePicker.setSpinnersShown(true);
            }
        }

        private Field findField(Class objectClass, Class fieldClass, String expectedName) {
            try {
                final Field field = objectClass.getDeclaredField(expectedName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
            }
            for (final Field field : objectClass.getDeclaredFields()) {
                if (field.getType() == fieldClass) {
                    field.setAccessible(true);
                    return field;
                }
            }
            return null;
        }
    }

    private void customDatePicker(DatePicker datePicker) {
        int daySpinnerId = Resources.getSystem().getIdentifier(DAY_FIELD, ID, ANDROID);
        if (daySpinnerId != 0) {
            View daySpinner = datePicker.findViewById(daySpinnerId);
            if (daySpinner != null) {
                daySpinner.setVisibility(View.GONE);
            }
        }
    }

    private DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Toast.makeText(getActivity(), year + "년" + (monthOfYear+1) + "월", Toast.LENGTH_SHORT).show();
            String month= UtilClass.addZero(monthOfYear+1);

            tv_date.setText(year+"."+month);
            getTransportResultsInfo();
        }
    };

    @OnClick(R.id.top_home)
    public void goDriverHome() {
        UtilClass.goDriverHome(getActivity());
    }


    //ListView의 item (상세)
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), CarResultsDialogActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("list", arrayList);
            startActivity(intent);
        }
    }

}
