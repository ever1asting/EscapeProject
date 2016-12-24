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
    private TypeTextView mResultText;
    private ImageView mainWin;

    private int mainImgPath;
    private int clickedRoom;
    private Bitmap bitmap;

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
        int []state = intent.getIntArrayExtra("state");
        fsm = new FiniteStateMachine();
        fsm.init(state[0], state[1], state[2], state[3], state[4]);

        //img init
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        mainImgPath = intent.getIntExtra("mainImgPath", 0);
        bitmap = BitmapFactory.decodeResource(getResources(), mainImgPath, options);
        mainWin = (ImageView) findViewById(R.id.playImage);
        mainWin.setImageDrawable(new RoundImageDrawable(bitmap));
        mainWin.setClickable(false);


        // button and textline init
        clickedRoom = intent.getIntExtra("clickedRoom", 0);
        btn_click = (Button) findViewById(R.id.startBtn);
        mResultText = ((TypeTextView) findViewById(R.id.contentText ));
        mResultText.setFocusable(false);
        backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setVisibility(View.VISIBLE);
        if (clickedRoom != state[0]) {
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
        mainWin.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.startBtn)
            btnVoice();
        else if(v.getId() == R.id.backBtn)
            backBtnEvent();
        else if(v.getId() == R.id.playImage)
            resetEvent();
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
            //mResultText.append(text);

            //update fsm
            String fsmRet = text + fsm.update(text);
            mResultText.start(fsmRet);

            //show action
            int fsmState[] = fsm.getState();
            String key = "" + fsmState[2] + "_" + fsmState[3] + "_" + fsmState[4];
            Log.v("action",key);
            int value[] = MainActivity.searchTable.get(key);

            //update images
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
            System.gc();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            mainImgPath = value[clickedRoom];
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mainImgPath, options);
            mainWin.setImageDrawable(new RoundImageDrawable(bitmap));

            //update voice
            MainActivity.pool.load(this, value[0], 1);

            //if room changed
            if (clickedRoom != fsmState[0]) {
                btn_click.setVisibility(View.INVISIBLE);
            }

            //if bad end
            if (fsmState[0] == 2 && fsmState[1] == 6) {
                //mResultText.setText(R.string.BEInfo);
                btn_click.setVisibility(View.INVISIBLE);
                backBtn.setVisibility(View.INVISIBLE);
                mainWin.setClickable(true);
            }
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
        intent.putExtra("state", fsmState);
        setResult(1, intent);
        finish();
    }

    private void resetEvent() {
        Intent intent = new Intent();
        int fsmState[] = new int[]{1,1,0,0,0};
        intent.putExtra("state", fsmState);
        setResult(1, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK )
            backBtnEvent();

        return true;
    }

    @Override
    public void onBackPressed() {

    }
}
