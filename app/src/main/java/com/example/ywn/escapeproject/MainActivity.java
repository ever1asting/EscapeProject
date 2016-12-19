package com.example.ywn.escapeproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends Activity implements View.OnClickListener {

    private ImageView leftUpWin;
    private ImageView middleUpWin;
    private ImageView rightUpWin;
    private ImageView leftDownWin;
    private ImageView middleDownWin;
    private ImageView rightDownWin;

    public static SoundPool pool;

    private int leftUpImgPath, leftDownImgPath, rightUpImgPath, rightDownImgPath, middleUpImgPath, middleDownImgPath;

    private int roomNum = 1;
    private int stateNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //soundpool init
        pool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 2, 2, 0, 0, 1);
            }
        });

        // init image for scan windows
        imgInit();
        leftUpWin.setOnClickListener(this);
        leftDownWin.setOnClickListener(this);
        rightUpWin.setOnClickListener(this);
        rightDownWin.setOnClickListener(this);
        middleUpWin.setOnClickListener(this);
        middleDownWin.setOnClickListener(this);
    }

    private void imgInit() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        leftUpImgPath = R.drawable.demo1;
        middleUpImgPath = R.drawable.demo3;
        rightUpImgPath = R.drawable.demo5;
        leftDownImgPath = R.drawable.demo2;
        middleDownImgPath = R.drawable.demo4;
        rightDownImgPath = R.drawable.demo6;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), leftUpImgPath);
        leftUpWin = (ImageView) findViewById(R.id.leftUpImg);
        leftUpWin.setImageDrawable(new RoundImageDrawable(bitmap));

        rightUpWin = (ImageView) findViewById(R.id.rightUpImg);
        bitmap = BitmapFactory.decodeResource(getResources(), rightUpImgPath);
        rightUpWin.setImageDrawable(new RoundImageDrawable(bitmap));//.setImageDrawable(getResources().getDrawable(R.drawable.win2));

        leftDownWin = (ImageView) findViewById(R.id.leftDownImg);
        bitmap = BitmapFactory.decodeResource(getResources(), leftDownImgPath);
        leftDownWin.setImageDrawable(new RoundImageDrawable(bitmap));//.setImageDrawable(getResources().getDrawable(R.drawable.win3));

        rightDownWin = (ImageView) findViewById(R.id.rightDownImg);
        bitmap = BitmapFactory.decodeResource(getResources(), rightDownImgPath);
        rightDownWin.setImageDrawable(new RoundImageDrawable(bitmap));//.setImageDrawable(getResources().getDrawable(R.drawable.win4));

        middleUpWin = (ImageView) findViewById(R.id.middleUpImg);
        bitmap = BitmapFactory.decodeResource(getResources(), middleUpImgPath);
        middleUpWin.setImageDrawable(new RoundImageDrawable(bitmap));//.setImageDrawable(getResources().getDrawable(R.drawable.win2));

        middleDownWin = (ImageView) findViewById(R.id.middleDownImg);
        bitmap = BitmapFactory.decodeResource(getResources(), middleDownImgPath);
        middleDownWin.setImageDrawable(new RoundImageDrawable(bitmap));//.setImageDrawable(getResources().getDrawable(R.drawable.win2));

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("roomNum", roomNum);
        intent.putExtra("stateNum", stateNum);

        int id = v.getId();

        if (id == R.id.leftUpImg) {
            intent.putExtra("mainImgPath", leftUpImgPath);
            intent.putExtra("clickedRoom", 1);
        }
        else if (id == R.id.middleUpImg) {
            intent.putExtra("mainImgPath", middleUpImgPath);
            intent.putExtra("clickedRoom", 2);
        }
        else if (id == R.id.rightUpImg) {
            intent.putExtra("mainImgPath", rightUpImgPath);
            intent.putExtra("clickedRoom", 3);
        }
        else if (id == R.id.leftDownImg) {
            intent.putExtra("mainImgPath", leftDownImgPath);
            intent.putExtra("clickedRoom", 4);
        }
        else if (id == R.id.middleDownImg) {
            intent.putExtra("mainImgPath", middleDownImgPath);
            intent.putExtra("clickedRoom", 5);
        }
        else if (id == R.id.rightDownImg) {
            intent.putExtra("mainImgPath", rightDownImgPath);
            intent.putExtra("clickedRoom", 6);
        }
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == 1) {
                roomNum = data.getIntExtra("roomNum", 1);
                stateNum = data.getIntExtra("stateNum", 1);
            }
        }
    }
}
