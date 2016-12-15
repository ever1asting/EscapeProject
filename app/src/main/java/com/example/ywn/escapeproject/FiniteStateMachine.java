package com.example.ywn.escapeproject;

/**
 * Created by ywn on 2016/12/16.
 */

import android.util.Log;

public class FiniteStateMachine {

    private static final int MaxRoom = 4;
    private static final int MaxState = 20;
    private static final int MaxRet = 20;

    private final String[][][] script = {
            {},
            /****************  ROOM 1  ******************/
            {
                    {},
                    /* state 1 */
                    {},
                    /* state 2 */
                    {"ahead front", "Ok...seems a good path."},
                    /* state 3 */
                    {"right", "Ok...Um, there seems to be obstacles. Shall we try other direction?",
                    "left", "Ok...seems a good path."},
                    /* state 4 */
                    {"ahead front right", "Ok...seems a good path."},
                    /* state 5 */
                    {"ahead front", "Ok...seems a good path. Switch seems to be here... Shall I turn on the light?"},
                    /* state 6 */
                    {"turn light switch", "Ok, I will try to turn on the light. A door here, shall I open it?"},
                    /* state 7 */
                    {"open door", "Ok, next room"}
            },
            /****************  ROOM 2  ******************/
            {

            },
            /***********  ROOM 3 / Corridor  ************/
            {

            },
            /****************  ROOM 4  ******************/
            {

            }

    };

    private int roomNum;
    private int stateNum;

    public void init() {
        roomNum = 1;
        stateNum = 1;
    }

    public String update(String text) {
        String retStr = "";
        String[] vocab = text.split(" ");
        int roomIndex, stateIndex;

        if(roomNum == 1) {
            roomIndex = 1;

            Log.v("", "room num = 1");

            if(stateNum == 1) { // init state, just return settings and hints
                stateNum = 2;
                retStr = "Settings. I can hardly see anything. Help me leave this room.";
            }
            else {
                stateIndex = stateNum;
                for(int i = 0; i < vocab.length; ++i)
                    for(int j = 0; j < script[roomIndex][stateIndex].length; j += 2) {
                        String[] keyword = script[roomIndex][stateIndex][j].split(" ");
                        for(int k = 0; k < keyword.length; ++k) {
                            if(vocab[i].equalsIgnoreCase(keyword[k])) {
                                if(stateNum == 3 && j == 0) {
                                    // wrong but allow route, do not modify state
                                }
                                else if(stateNum == 7) { // next room
                                    roomNum = 2;
                                    stateNum = 1;
                                }
                                else
                                    ++stateNum;
                                return /*" state = " + stateNum + ", " + */ " " + script[roomIndex][stateIndex][j + 1];
                            }
                        }
                    }
                retStr = "Sorry, I did not catch what you said.";
            }
        }



        return "\n update : " /*+ " state = " + stateNum + ", " */+ retStr;
    }

}
