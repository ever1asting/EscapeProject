package com.example.ywn.escapeproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_click;
    private EditText mResultText;
    private GridLayout scanWin;
    private ImageView leftUpWin;
    private ImageView rightUpWin;
    private ImageView leftDownWin;
    private ImageView rightDownWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  scanWin = (GridLayout) findViewById(R.id.scanWin);
     //   int scanWinHeight = scanWin.getHeight();
    //    int scanWinWidth = scanWin.getWidth();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.win1);
        leftUpWin = (ImageView) findViewById(R.id.leftUpImg);
        leftUpWin.setImageDrawable(new RoundImageDrawable(bitmap));


        rightUpWin = (ImageView) findViewById(R.id.rightUpImg);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.win2);
        rightUpWin.setImageDrawable(new RoundImageDrawable(bitmap));//.setImageDrawable(getResources().getDrawable(R.drawable.win2));

        leftDownWin = (ImageView) findViewById(R.id.leftDownImg);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.win3);
        leftDownWin.setImageDrawable(new RoundImageDrawable(bitmap));//.setImageDrawable(getResources().getDrawable(R.drawable.win3));

        rightDownWin = (ImageView) findViewById(R.id.rightDownImg);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.win4);
        rightDownWin.setImageDrawable(new RoundImageDrawable(bitmap));//.setImageDrawable(getResources().getDrawable(R.drawable.win4));


        btn_click = (Button) findViewById(R.id.startBtn);
        mResultText = ((EditText) findViewById(R.id.contentText ));
        mResultText.setFocusable(false);

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=584962eb");

        btn_click.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.startBtn)
                btnVoice();
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
        // 自动填写地址
        mResultText.append(text);

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
