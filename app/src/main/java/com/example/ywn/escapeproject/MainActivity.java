package com.example.ywn.escapeproject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btn_click;
    private Button backBtn;
    private Button rstBtn;
    private EditText mResultText;
    private ImageView mainWin;
    private GridLayout scanWin;
    private ImageView leftUpWin;
    private ImageView rightUpWin;
    private ImageView leftDownWin;
    private ImageView rightDownWin;

    private int mainImgPath;
    private int leftUpImgPath, leftDownImgPath, rightUpImgPath, rightDownImgPath;

    private FiniteStateMachine fsm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // FSM init
        fsm = new FiniteStateMachine();
        fsm.init();

        Log.v("debug", "init fsm");

        // init image path
        mainImgPath = R.drawable.mainwin;
        leftUpImgPath = R.drawable.win1;
        rightUpImgPath = R.drawable.win2;
        leftDownImgPath = R.drawable.win3;
        rightDownImgPath = R.drawable.win4;

        // init image for scan windows
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mainImgPath);
        mainWin = (ImageView) findViewById(R.id.mainWinImg);
        mainWin.setImageDrawable(new RoundImageDrawable(bitmap));

        bitmap = BitmapFactory.decodeResource(getResources(), leftUpImgPath);
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

        // button and textline init
        btn_click = (Button) findViewById(R.id.startBtn);
        mResultText = ((EditText) findViewById(R.id.contentText ));
        mResultText.setFocusable(false);
        backBtn = (Button) findViewById(R.id.backBtn);
        rstBtn = (Button) findViewById(R.id.rstBtn);

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=584962eb");

        btn_click.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        rstBtn.setOnClickListener(this);
        leftUpWin.setOnClickListener(this);
        leftDownWin.setOnClickListener(this);
        rightUpWin.setOnClickListener(this);
        rightDownWin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.startBtn)
            btnVoice();
        else if(v.getId() == R.id.backBtn)
            backBtnEvent();
        else if(v.getId() == R.id.rstBtn) {
            fsm.init();
            mResultText.setText("Restart.");
        }
        else
            setMainWin(v.getId());
    }

    private void setMainWin(int id) {
        if(id == R.id.leftUpImg)
            mainImgPath = leftUpImgPath;
        else if(id == R.id.rightUpImg)
            mainImgPath = rightUpImgPath;
        else if(id == R.id.leftDownImg)
            mainImgPath = leftDownImgPath;
        else if(id == R.id.rightDownImg)
            mainImgPath = rightDownImgPath;

        mResultText.setText(",height = " + mainWin.getHeight() + ", width = " + mainWin.getWidth());

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mainImgPath);
        mainWin.setImageDrawable(new RoundImageDrawable(bitmap));
        mainWin.setVisibility(View.VISIBLE);
    }

    private void backBtnEvent() {
        mainWin.setVisibility(View.GONE);
    }


    //TODO 开始说话：
    private void btnVoice() {
        RecognizerDialog dialog = new RecognizerDialog(this,null);
        dialog.setParameter(SpeechConstant.LANGUAGE, "en_us");
        dialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        mResultText.setText("");

        dialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                printResult(recognizerResult);
            }
            @Override
            public void onError(SpeechError speechError) {

            }
        });
        dialog.show();
        Toast.makeText(this, "请开始说话", Toast.LENGTH_SHORT).show();
    }

    //回调结果：
    private void printResult(RecognizerResult results) {
        String text = parseIatResult(results.getResultString());
        if(!text.equals(".")) {
            // 自动填写地址
            mResultText.append(text);

            Log.v("debug", "prepare to get fsm str");

            //update fsm
            String fsmRet = fsm.update(text);
            mResultText.append(fsmRet);
        }

    }

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }


}
