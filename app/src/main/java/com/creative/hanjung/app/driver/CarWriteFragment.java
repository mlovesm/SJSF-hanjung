package com.creative.hanjung.app.driver;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.creative.hanjung.app.R;
import com.creative.hanjung.app.adaptor.DriverAdapter;
import com.creative.hanjung.app.menu.TransitMainActivity;
import com.creative.hanjung.app.retrofit.Datas;
import com.creative.hanjung.app.retrofit.RetrofitService;
import com.creative.hanjung.app.util.UtilClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarWriteFragment extends Fragment {
    private static final String TAG = "CarWriteFragment";
    private ProgressDialog pDlalog = null;
    private RetrofitService service;
    private String title;

    private String userNo;
    private String weight;
    private ArrayList<HashMap<String,String>> arrayList;
    private HashMap<String, String> carAllocationMap;
    private DriverAdapter mAdapter;

    @Bind(R.id.top_title) TextView textTitle;
    @Bind(R.id.listView1) ListView listView;
    @Bind(R.id.date_button) TextView tv_date;
    @Bind(R.id.textView1) TextView tv_data1;
    @Bind(R.id.textView2) TextView tv_data2;
    @Bind(R.id.textView3) TextView tv_data3;
    @Bind(R.id.textView4) TextView tv_data4;
    @Bind(R.id.textView5) TextView tv_data5;
    @Bind(R.id.textView6) TextView tv_data6;
    @Bind(R.id.textView7) TextView tv_data7;
    @Bind(R.id.editText1) EditText et_data1;
    @Bind(R.id.editText2) EditText et_memo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.car_order, container, false);
        ButterKnife.bind(this, view);
        service= RetrofitService.rest_api.create(RetrofitService.class);

        userNo= TransitMainActivity.userNo;

        title= getArguments().getString("title");
        textTitle.setText(title);
        tv_date.setText(UtilClass.getCurrentDate(1, "."));

        view.findViewById(R.id.top_save).setVisibility(View.VISIBLE);

        getDriverInfo();
        getDriverAllocationInfo();

        listView.setOnItemClickListener(new ListViewItemClickListener());


        return view;
    }//onCreateView

    public void getDriverInfo(){
        RetrofitService service = RetrofitService.rest_api.create(RetrofitService.class);

        Call<Datas> call = service.listData("Common","driverInfo", userNo);
        call.enqueue(new Callback<Datas>() {
            @Override
            public void onResponse(Call<Datas> call, Response<Datas> response) {
                UtilClass.logD(TAG, "response="+response);
                if (response.isSuccessful()) {
                    UtilClass.logD(TAG, "isSuccessful="+response.body().toString());
                    String status= response.body().getStatus();
                    try {
                        if(status.equals("success")){
                            tv_data1.setText(response.body().getList().get(0).get("기사명"));
                            tv_data2.setText(response.body().getList().get(0).get("차량번호"));
                        }

                    } catch ( Exception e ) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "에러코드 Driver 1", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "response isFailed", Toast.LENGTH_SHORT).show();
                }
                if(pDlalog!=null) pDlalog.dismiss();
            }

            @Override
            public void onFailure(Call<Datas> call, Throwable t) {
                if(pDlalog!=null) pDlalog.dismiss();
                UtilClass.logD(TAG, "onFailure="+call.toString()+", "+t);
                Toast.makeText(getActivity(), "onFailure Driver 1",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDriverAllocationInfo(){
        carAllocationMap = null;
        weight= "";
        RetrofitService service = RetrofitService.rest_api.create(RetrofitService.class);

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        UtilClass.showProcessingDialog(dialog);

        Call<Datas> call = service.listData("Common","driverAllocationInfo", userNo, tv_date.getText().toString());
        call.enqueue(new Callback<Datas>() {
            @Override
            public void onResponse(Call<Datas> call, Response<Datas> response) {
                UtilClass.logD(TAG, "response="+response);
                if (response.isSuccessful()) {
                    UtilClass.logD(TAG, "isSuccessful="+response.body().toString());
                    String status= response.body().getStatus();
                    try {
                        tv_data3.setText("");
                        tv_data4.setText("");
                        tv_data5.setText("");
                        tv_data6.setText("");
                        tv_data7.setText("");
                        if(response.body().getCount()==0){
                            Toast.makeText(getActivity(), "배차 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        arrayList = new ArrayList<>();
                        arrayList.clear();
                        for(int i=0; i<response.body().getList().size();i++){
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("data1",response.body().getList().get(i).get("장비코드"));
                            hashMap.put("data2",response.body().getList().get(i).get("구분"));
                            hashMap.put("data3",response.body().getList().get(i).get("순번"));
                            hashMap.put("data4",response.body().getList().get(i).get("배차계획순번"));
                            hashMap.put("data5",response.body().getList().get(i).get("운송량"));
                            arrayList.add(hashMap);
                        }
                        mAdapter = new DriverAdapter(getActivity(), arrayList, "CarWrite");
                        listView.setAdapter(mAdapter);
                        UtilClass.setListViewHeightBasedOnChildren(listView);

                    } catch ( Exception e ) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "에러코드 Driver 2", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "onFailure Driver 2",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String hang= arrayList.get(position).get("data2").toString();
            weight= arrayList.get(position).get("data5").toString();
            UtilClass.logD(TAG, weight);
            String car_idx= arrayList.get(position).get("data4").toString();
            getDriverAllocationDetailInfo(hang, car_idx);
        }
    }

    public void getDriverAllocationDetailInfo(String hang, String car_idx){
        Map<String, Object> map = new HashMap();
        map.put("gear_code", userNo);
        map.put("hang", hang);
        map.put("car_idx", car_idx);
        map.put("date", tv_date.getText());

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        UtilClass.showProcessingDialog(dialog);
        dialog.setCancelable(true);

        Call<Datas> call = service.listData("Common","driverAllocationDetailInfo", map);
        call.enqueue(new Callback<Datas>() {
            @Override
            public void onResponse(Call<Datas> call, Response<Datas> response) {
                UtilClass.logD(TAG, "response="+response);
                if (response.isSuccessful()) {
                    UtilClass.logD(TAG, "isSuccessful="+response.body().toString());
                    String status= response.body().getStatus();
                    try {
                        if(status.equals("success")){
                            if(response.body().getCount()!=0){
                                tv_data3.setText(response.body().getList().get(0).get("운송품목"));
                                tv_data4.setText(response.body().getList().get(0).get("출발지"));
                                tv_data5.setText(response.body().getList().get(0).get("도착지"));
                                tv_data6.setText(response.body().getList().get(0).get("작업내역"));
                                tv_data7.setText(response.body().getList().get(0).get("지시톤수")+ "  TON");

                                carAllocationMap= response.body().getList().get(0);
                            }else{
                                Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                            }

                        }

                    } catch ( Exception e ) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "에러코드 Driver 3", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "onFailure Driver 3",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.top_home)
    public void goDriverHome() {
        UtilClass.goDriverHome(getActivity());
    }

    @OnClick({R.id.textButton1, R.id.top_save})
    public void alertDialogSave(){
        alertDialog("S");
    }

    public void alertDialog(final String gubun){
        final AlertDialog.Builder alertDlg = new AlertDialog.Builder(getActivity());
        alertDlg.setTitle("알림");
        if(gubun.equals("S")){
            if (carAllocationMap==null) {
                Toast.makeText(getActivity(), "내역을 선택하세요.",Toast.LENGTH_SHORT).show();
                return;
            }else{
                if(weight.equals("0.00")){
                    alertDlg.setMessage("작성하시겠습니까?");
                }else{
                    Toast.makeText(getActivity(), "값이 있습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        // '예' 버튼이 클릭되면
        alertDlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(gubun.equals("S")){
                    postData();
                }
            }
        });
        // '아니오' 버튼이 클릭되면
        alertDlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();  // AlertDialog를 닫는다.
            }
        });
        alertDlg.show();
    }

    //작성,수정
    public void postData() {
        Map<String, Object> map = new HashMap();
        if (carAllocationMap==null) {
            Toast.makeText(getActivity(), "내역을 선택하세요.",Toast.LENGTH_SHORT).show();
            return;
        }else{
            map.put("gear_code", carAllocationMap.get("장비코드"));
            map.put("trans_code", carAllocationMap.get("운송코드"));
            map.put("work_data", carAllocationMap.get("작업내역"));
            map.put("hang", carAllocationMap.get("상행경유하행"));
            map.put("idx", carAllocationMap.get("배차계획순번"));
        }
        map.put("date", tv_date.getText());
        map.put("weight", et_data1.getText());
        map.put("etc", et_memo.getText());
        map.put("user_no", TransitMainActivity.userNo);

       pDlalog = new ProgressDialog(getActivity());
        UtilClass.showProcessingDialog(pDlalog);
        
        Call<Datas> call = service.insertData("Common","driverAllocationInsert", map);
        call.enqueue(new Callback<Datas>() {
            @Override
            public void onResponse(Call<Datas> call, Response<Datas> response) {
                UtilClass.logD(TAG, "response="+response);
                if (response.isSuccessful()) {
                    UtilClass.logD(TAG, "isSuccessful="+response.body().toString());
                    handleResponse(response);
                }else{
                    Toast.makeText(getActivity(), "작업에 실패하였습니다.",Toast.LENGTH_LONG).show();
                }
                if(pDlalog!=null) pDlalog.dismiss();
            }

            @Override
            public void onFailure(Call<Datas> call, Throwable t) {
                if(pDlalog!=null) pDlalog.dismiss();
                UtilClass.logD(TAG, "onFailure="+call.toString()+", "+t);
                Toast.makeText(getActivity(), "handleResponse Driver 4",Toast.LENGTH_LONG).show();
            }
        });


    }

    //작성 완료
    public void handleResponse(Response<Datas> response) {
        try {
            String status= response.body().getStatus();
            if(status.equals("success")){
                if(response.body().getCount()==1){
                    Toast.makeText(getActivity(), "등록 하였습니다.", Toast.LENGTH_SHORT).show();
                    getDriverAllocationInfo();
                }else{
                    Toast.makeText(getActivity(), "중복된 데이터가 있습니다.\n 데이터를 확인하세요.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getActivity(), "등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "작업에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    //날짜설정
    @OnClick(R.id.date_button)
    public void getDateDialog() {
        getDialog("D");
    }


    public void getDialog(String gubun) {
        int year = 0, month = 0, day = 0;
        if(tv_date.length()>0){
            String date= tv_date.getText().toString();
            int firstPoint= date.indexOf(".");
            int lastPoint= date.lastIndexOf(".");
            year= Integer.parseInt(date.substring(0,firstPoint));
            month= Integer.parseInt(date.substring(firstPoint+1, lastPoint));
            day= Integer.parseInt(date.substring(lastPoint+1));
            UtilClass.logD(TAG, "==="+year+","+month+","+day);
        }

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), date_listener, year, month-1, day);
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//            Toast.makeText(getActivity(), year + "년" + (monthOfYear+1) + "월" + dayOfMonth +"일", Toast.LENGTH_SHORT).show();
            String month= UtilClass.addZero(monthOfYear+1);
            String day= UtilClass.addZero(dayOfMonth);

            tv_date.setText(year+"."+month+"."+day);
            getDriverAllocationInfo();
        }
    };

}
