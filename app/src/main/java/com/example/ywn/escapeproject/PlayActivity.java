package com.example.ywn.escapeproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by zhangxuan on 2016/12/18.
 */

public class PlayActivity extends Activity implements View.OnClickListener {
    private Button btn_click;
    private Button backBtn;
    private EditText mResultText;
    private ImageView mainWin;

    private int mainImgPath;

    private FiniteStateMachine fsm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play);

        // FSM init
        Intent intent = getIntent();
        int stateNum = intent.getIntExtra("stateNum", 1);
        int roomNum = intent.getIntExtra("roomNum", 1);
        fsm = new FiniteStateMachine();
        fsm.init(stateNum, roomNum);

        //img init
        mainImgPath = intent.getIntExtra("mainImgPath", 0);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mainImgPath);
        mainWin = (ImageView) findViewById(R.id.playImage);
        mainWin.setImageDrawable(new RoundImageDrawable(bitmap));

        // button and textline init
        int clickedRoom = intent.getIntExtra("clickedRoom", 0);
        btn_click = (Button) findViewById(R.id.startBtn);
        mResultText = ((EditText) findViewById(R.id.contentText ));
        mResultText.setFocusable(false);
        backBtn = (Button) findViewById(R.id.backBtn);
        if (clickedRoom != roomNum) {
            mResultText.setText(R.string.wrongRoomInfo);
            btn_click.setVisibility(View.INVISIBLE);
        }
        else {
            mResultText.setText(R.string.rightRoomInfo);
            btn_click.setVisibility(View.VISIBLE);
        }

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=584962eb");

        btn_click.setOnClickListener(this);
        backBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.startBtn)
            btnVoice();
        else if(v.getId() == R.id.backBtn)
            backBtnEvent();
    }

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

    private void printResult(RecognizerResult results) {
        String text = parseIatResult(results.getResultString());
        if(!text.equals(".")) {
            // 自动填写地址
            mResultText.append(text);

            Log.v("debug", "prepare to get fsm str");

            //update fsm
            String fsmRet = fsm.update(text);
            mResultText.append(fsmRet);

            //next action
            int fsmState[] = fsm.getState();
            //update images

            //update voice
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

    private void backBtnEvent() {
        Intent intent = new Intent();
        int fsmState[] = fsm.getState();
        intent.putExtra("roomNum", fsmState[0]);
        intent.putExtra("stateNum", fsmState[1]);
        setResult(1, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public void onBackPressed() {

    }
}
