package com.creative.hanjung.app.menu;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.creative.hanjung.app.R;
import com.creative.hanjung.app.fragment.FragMenuActivity;
import com.creative.hanjung.app.gear.GearPopupActivity;
import com.creative.hanjung.app.retrofit.Datas;
import com.creative.hanjung.app.retrofit.RetrofitService;
import com.creative.hanjung.app.safe.SafePopupActivity;
import com.creative.hanjung.app.util.BackPressCloseSystem;
import com.creative.hanjung.app.util.SettingPreference;
import com.creative.hanjung.app.util.UtilClass;
import com.google.firebase.iid.FirebaseInstanceId;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

//    adb shell dumpsys activity activities | findstr "Run"
    private static final String TAG = "MainActivity";
    public static String ipAddress= "http://119.202.60.144:8585";
//    public static String ipAddress= "http://59.11.9.94:9090";
//    public static String ipAddress= "http://192.168.0.4:9191";
    public static String contextPath= "/sjsf_hanjung";
    private ProgressDialog pDlalog = null;

    //FCM 관련
    private String token=null;
    private String phone_num=null;
    public static boolean onAppCheck= false;
    public static String pendingPath= "";
    public static String pendingPathKey= "";

    private BackPressCloseSystem backPressCloseSystem;
    private PermissionListener permissionlistener;

    private SettingPreference pref = new SettingPreference("loginData",this);
    public static String loginSabun;
    public static String loginName;
    public static String jPos;
    public static String part1_cd;
    public static String part2_cd;
    public static String latestAppVer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        backPressCloseSystem = new BackPressCloseSystem(this);

        loginSabun = pref.getValue("sabun_no","").trim();
        loginName = pref.getValue("user_nm","").trim();
        jPos = pref.getValue("j_pos","").trim();
        part1_cd = pref.getValue("part1_cd","");
        part2_cd = pref.getValue("part2_cd","");
        latestAppVer = pref.getValue("LATEST_APP_VER","");

        String currentAppVer= getAppVersion(this);
        UtilClass.logD(TAG, "currentAppVer="+currentAppVer+", latestAppVer="+latestAppVer);

        token = FirebaseInstanceId.getInstance().getToken();
        UtilClass.logD(TAG, "Refreshed token: " + token);

        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(getApplicationContext(), "권한 허가", Toast.LENGTH_SHORT).show();
                phone_num= getPhoneNumber();
                postData();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "권한 거부 목록\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                phone_num="";
                postData();
            }
        };
        new TedPermission(MainActivity.this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("전화번호 정보를 가져오기 위해선 권한이 필요합니다.")
                .setDeniedMessage("권한을 확인하세요.\n\n [설정] > [애플리케이션] [해당앱] > [권한]")
                .setGotoSettingButtonText("권한확인")
                .setPermissions(Manifest.permission.CALL_PHONE)
                .check();

        onAppCheck= true;

    }

    public static String getAppVersion(Context context) {
        // application version
        String versionName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            UtilClass.logD(TAG, "getAppVersion Error");
        }

        return versionName;
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
        Intent intent = new Intent(getBaseContext(),FragMenuActivity.class);
        intent.putExtra("title", "공지사항");
        startActivity(intent);
    }

    @OnClick(R.id.imageView2)
    public void getMenu2() {
        Intent intent = new Intent(getBaseContext(),GearPopupActivity.class);
        intent.putExtra("title", "장비점검");
        startActivity(intent);
    }

    @OnClick(R.id.imageView3)
    public void getMenu3() {
        Intent intent = new Intent(getBaseContext(),FragMenuActivity.class);
        intent.putExtra("title", "동료사랑카드");
        startActivity(intent);
    }

    @OnClick(R.id.imageView4)
    public void getMenu4() {
        Intent intent = new Intent(getBaseContext(),SafePopupActivity.class);
        intent.putExtra("title", "안전관리");
        startActivity(intent);
    }

    @OnClick(R.id.imageView5)
    public void getMenu5() {
        Intent intent = new Intent(getBaseContext(),FragMenuActivity.class);
        intent.putExtra("title", "무재해현황판");
        startActivity(intent);
    }

    @OnClick(R.id.imageView6)
    public void getMenu6() {
        Intent intent = new Intent(getBaseContext(),FragMenuActivity.class);
        intent.putExtra("title", "작업기준");
        startActivity(intent);
    }

    //푸시 데이터 전송
    public void postData() {
        String android_id = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        Map<String, Object> map = new HashMap();
        map.put("Token",token);
        map.put("phone_num",phone_num);
        map.put("sabun_no",loginSabun);
        map.put("and_id",android_id);

        pDlalog = new ProgressDialog(MainActivity.this);
        UtilClass.showProcessingDialog(pDlalog);

        RetrofitService service = RetrofitService.rest_api.create(RetrofitService.class);
        Call<Datas> call = service.sendData("Board","fcmTokenData",map);

        call.enqueue(new Callback<Datas>() {
            @Override
            public void onResponse(Call<Datas> call, Response<Datas> response) {
                UtilClass.logD(TAG, "response="+response);
                if (response.isSuccessful()) {
                    UtilClass.logD(TAG, "isSuccessful="+response.body().toString());
                    String status= response.body().getStatus();

                }else{
                    Toast.makeText(getApplicationContext(), "handleResponse Main",Toast.LENGTH_SHORT).show();
                }
                if(pDlalog!=null) pDlalog.dismiss();
            }

            @Override
            public void onFailure(Call<Datas> call, Throwable t) {
                if(pDlalog!=null) pDlalog.dismiss();
                UtilClass.logD(TAG, "onFailure="+call.toString()+", "+t);
                Toast.makeText(getApplicationContext(), "토큰 생성에 실패하였습니다.",Toast.LENGTH_SHORT).show();
            }
        });

    }

    // 단말기 핸드폰번호 얻어오기
    public String getPhoneNumber() {
        String num = null;
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            num = tm.getLine1Number();
            if(num!=null&&num.startsWith("+82")){
                num = num.replace("+82", "0");
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "오류가 발생 하였습니다!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return num;
    }
}
