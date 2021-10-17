package com.king.zxing.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.king.zxing.util.CodeUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author  <a href="1347507120@qq.com">Xiaonan Liang</a>
 */

public class GenerateActivity extends AppCompatActivity {

    private TextView mainTitle;
    private ImageView ivCode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);
        //mainTitle = findViewById(R.id.mainTitle);
        ivCode = findViewById(R.id.imageView1);

        //mainTitle.setText(getIntent().getStringExtra(SecondaryActivity.KEY_TITLE));
        String content = getIntent().getStringExtra("codeContent");
        boolean isQRCode = getIntent().getBooleanExtra(SecondaryActivity.KEY_IS_QR_CODE,false);

        if(isQRCode){
            createQRCode(content);
        }else{
            createBarCode("1234567890");
        }
    }

    /**
     * 生成二维码
     * @param content
     */
    private void createQRCode (String content){
        new Thread(()->{
            //生成二维码放在子线程里面，将图片放到二维码中间
            Bitmap logo = BitmapFactory.decodeResource(getResources(),R.drawable.njupt);
            Bitmap bitmap = CodeUtils.createQRCode(content,600,logo);
            runOnUiThread(()->{
                //显示二维码
               ivCode.setImageBitmap(bitmap);
            });
        }).start();
    }

    /**
     * 生成条形码
     * @param content
     */
    private void createBarCode(String content){
        new Thread(() -> {
            //生成条形码相关放在子线程里面
            Bitmap bitmap = CodeUtils.createBarCode(content, BarcodeFormat.CODE_128,800,200,null,true);
            runOnUiThread(()->{
                //显示条形码
                ivCode.setImageBitmap(bitmap);
            });
        }).start();
    }


    public void onClick(View v){
        switch (v.getId()){
            case R.id.ivLeft:
                finish();
                break;
        }
    }
}