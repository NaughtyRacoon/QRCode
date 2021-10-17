package com.king.zxing.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.king.zxing.CameraScan;
import com.king.zxing.CaptureActivity;
import com.king.zxing.util.CodeUtils;
import com.king.zxing.util.LogUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity {
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";
    public static final int REQUEST_CODE_SCAN = 0X01;
    public static final int REQUEST_CODE_PHOTO = 0X02;

    public static final int RC_CAMERA = 0X01;
    //public static final String MAIN_TITLE = "mian_title";
    private Button button_generate;//跳转按钮
    private Button button_scanning;//扫描按钮
    private boolean isUri;//判断是否为网址
    private Toast toast;
    private boolean isContinuousScan;
    private Class<?> cls;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button_generate = (Button)findViewById(R.id.button_generate);//安卓编程的定位函数，主要是引用.R文件里的引用名。
        button_generate.setOnClickListener(new View.OnClickListener() {  //监听器
            @Override
            public void onClick(View view) {
                //跳转到第二个Activity
                gotoSecondaryActivity();
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case REQUEST_CODE_SCAN:
                    String result = CameraScan.parseScanResult(data);
                    showToast(result);
                    openWithBrowser(result);
                    break;
                case REQUEST_CODE_PHOTO:
                    parsePhoto(data);
                    break;
            }
        }
    }
    //toast 显示文本内容
    private void showToast(String text){
        if(toast == null){
            toast = Toast.makeText(this,text,Toast.LENGTH_SHORT);
        }else{
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(text);
        }
        toast.show();
    }



    private void parsePhoto(Intent data){

//            final String path = UriUtils.getImagePath(this,data);
//            LogUtils.d("path:" + path);
//            if(TextUtils.isEmpty(path)){
//                return;
//            }
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
            //异步解析
            asyncThread(() -> {
                final String result = CodeUtils.parseCode(bitmap);
                runOnUiThread(() -> {
                    LogUtils.d("result:" + result);
                    Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Context getContext(){
        return this;
    }

    /**
     * 扫码
     * @param cls
     * @param title
     */
    private void startScan(Class<?> cls,String title){
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this,R.anim.in,R.anim.out);
        Intent intent = new Intent(this, cls);
        //intent.putExtra(KEY_TITLE,title);
        intent.putExtra(KEY_IS_CONTINUOUS,isContinuousScan);
        ActivityCompat.startActivityForResult(this,intent,REQUEST_CODE_SCAN,optionsCompat.toBundle());
    }
    private void gotoSecondaryActivity(){
        Intent toSecondary = new Intent();         //创建一个意图
        toSecondary.setClass(this, SecondaryActivity.class);//指定跳转SecondaryActivity
        startActivity(toSecondary);
    }
    /**
     * 检测是否是网址
     * @param isUrl
     */
    private boolean isValidUrl(String isUrl) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(isUrl.toLowerCase());
        return m.matches();
    }
    /**
    * 用默认浏览器打开
    * @param result
    */
    private void openWithBrowser(String result) {
        isUri = isValidUrl(result);
        if(isUri==true){
            Uri uri = Uri.parse(result);
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                //指定默认浏览器
                intent.setClassName("com.android.browser",
                        "com.android.browser.BrowserActivity");
                startActivity(intent);
            }catch (Exception e){
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        }
    }

    /**
     * 检测拍摄权限
     */
    @AfterPermissionGranted(RC_CAMERA)
    private void checkCameraPermissions(){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            startScan(cls,title);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.permission_camera),
                    RC_CAMERA, perms);
        }
    }

    private void asyncThread(Runnable runnable){
        new Thread(runnable).start();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    public void onClick(View v){
        isContinuousScan = false;
        switch (v.getId()){
            case R.id.button_scanning:
                this.cls = CaptureActivity.class;
//                this.title = ((Button)v).getText().toString();
                checkCameraPermissions();

                break;

        }

    }

}