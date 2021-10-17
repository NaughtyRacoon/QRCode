package com.king.zxing.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.king.zxing.myapplication.R;
import com.king.zxing.util.CodeUtils;
import com.king.zxing.util.LogUtils;

import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SecondaryActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_IS_QR_CODE = "key_code";
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";

    public static final int REQUEST_CODE_PHOTO = 0X02;
    public static final int RC_READ_PHOTO = 0X02;

    private Button button3;
    private boolean isUri;//判断textView是否为网址
    private EditText show_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);



        //传递intent，并且显示文本编辑框
        button3 = (Button)findViewById(R.id.produce);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String content = show_edit.getText().toString().trim(); //trim去除空格
                beginCode(true);
            }
        });
    }



    /**
     * 生成二维码
     * @param isQRCode
     */
    private void beginCode(boolean isQRCode){
        Intent intent = new Intent();
        intent.setClass(this,GenerateActivity.class);
        //intent.putExtra(QRContent,content);
        EditText editText =(EditText) findViewById(R.id.show_edit);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setEnabled( true );
        String content = editText.getText().toString().trim();
        intent.putExtra("codeContent",content);
        intent.putExtra(KEY_IS_QR_CODE,isQRCode);
        intent.putExtra(KEY_TITLE,isQRCode ? getString(R.string.qr_code) : getString(R.string.bar_code));
        startActivity(intent);
    }
    private void startPhotoCode(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
    }

    @AfterPermissionGranted(RC_READ_PHOTO)
    private void checkExternalStoragePermissions(){
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            startPhotoCode();
        }else{
            EasyPermissions.requestPermissions(this, getString(R.string.permission_external_storage),
                    RC_READ_PHOTO, perms);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}