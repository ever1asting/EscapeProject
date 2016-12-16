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
                    {"open door", "Ok, next room"},
                    /* state 8 */
                    {"open door", "Ok, back to ROOM 2"}
            },
            /****************  ROOM 2  ******************/
            {
                    {},
                    /* state 1 */
                    {},
                    /* state 2 */
                    {"open door", "Something dangerous may be behind those doors, be careful.",
                    "light", "Strange light here. Wow, a skylight here, but I can not access to the skylight. A ladder may help.",
                    "body", "OMG, a body here. I am scared because zombies may be attracted by it."},
                    /* state 3 */
                    {"open+one open+first door+one door+first", "Ok, I went back to ROOM 1",
                    "light", "Light comes from the skylight, but I can not access to the skylight. A ladder may help.",
                    "body", "A body here. It may attract zombies.",
                    "open+two open+second door+two door+second", "BAD END: The girl was attacked by the zombie.",
                     "open+three open+third door+three door+third", "Oh, Corride. "},
                    /* state 4 */
                    {"open+one open+first door+one door+first", "Ok, I went back to ROOM 1",
                     "light", "Light comes from the skylight, but I can not access to the skylight. A ladder may help.",
                     "body", "Um...I think we can use this to attract the zombie and let " +
                            "them go into corride, so that I can come into ROOM 4. Let me try...Good, I succeed.",
                    "open+two open+second door+two door+second", "BAD END: The girl was attacked by the zombie.",
                     "open+three open+third door+three door+third", "Come to corride. "},
                    /* state 5 */
                    {"open+one open+first door+one door+first", "Ok, I went back to ROOM 1",
                    "light", "Light comes from the skylight, but I can not access to the skylight. A ladder may help.",
                    "body", "It is in the corride now. We had used it to attract the zombie successfully.",
                    "open+three open+third door+three door+third", "BAD END: The girl was attacked by the zombie.",
                    "open+two open+second door+two door+second", "Ok, a new room. We can call it ROOM 4.",
                    },
                    /* state 6 */
                    {} // BAD END
            },
            /***********  ROOM 3 / Corridor  ************/
            {
                    {},
                    /* state 1 */
                    {},
                    /* state 2 */
                    {"open+four open+fourth door+four door+fourth", "BAD END: The girl was attacked by the zombie.",
                    "open+three open+third door+three door+third", "I think we can get some ideas in ROOM 2."},

            },
            /****************  ROOM 4  ******************/
            {

            }

    };

    private int roomNum;
    private int stateNum;
    private int preStateNum;

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
                                else if (stateNum == 8) {// back
                                    roomNum = 2;
                                    stateNum = preStateNum;
                                }
                                else
                                    ++stateNum;
                                return " state = " + stateNum + ", " +  " " + script[roomIndex][stateIndex][j + 1];
                            }
                        }
                    }
                retStr = "Sorry, I did not catch what you said.";
            }
        }
        else if (roomNum == 2) {
            roomIndex = 2;
            if(stateNum == 1) {
                stateNum = 2;
                retStr = "Seems a new room, light, body and 3 doors here.";
            }
            else{
                stateIndex = stateNum;

                for(int j = 0; j < script[roomIndex][stateIndex].length; j += 2) {
                    String[] keyword = script[roomIndex][stateIndex][j].split(" ");
                    for(int k = 0; k < keyword.length; ++k) {
                        String[] combo = keyword[k].split("\\+");
                        boolean isCompitable = true;
                        for(int c = 0; c < combo.length; ++c) {
                            boolean mark = false;
                            for(int i = 0; i < vocab.length; ++i) {
                                if(vocab[i].equalsIgnoreCase(combo[c])) {
                                    mark = true;
                                    break;
                                }
                            }
                            if(!mark) {
                                isCompitable = false;
                                break;
                            }
                        }
                        if(isCompitable) { // change states
                            if(stateNum == 2) {
                                switch (j / 2) {
                                    case 0: stateNum = 3; break;
                                    default: break;
                                }
                            }
                            else if (stateNum == 3 || stateNum == 4 || stateNum == 5) {
                                switch (j / 2) {
                                    case 0: roomNum = 1; preStateNum = stateNum; stateNum = 8;  break;
                                    case 3: stateNum = 6; break; // BE
                                    case 4:
                                        roomNum = (stateNum == 5)? 4: 3;
                                        stateNum = 1;
                                        break;
                                    default: break;
                                }
                            }

                            return " state = " + stateNum + ", " +  " " + script[roomIndex][stateIndex][j + 1];
                        }
                    }
                }
                retStr = "Sorry, I did not catch what you said.";
            }
        }
        else if(roomNum == 3) {
            roomIndex = 3;

            if (stateNum == 1) {
                stateNum = 2;
                retStr = "Corride. Here are 2 doors in total.";
            } else if (stateNum == 2) {
                stateIndex = stateNum;

                for (int j = 0; j < script[roomIndex][stateIndex].length; j += 2) {
                    String[] keyword = script[roomIndex][stateIndex][j].split(" ");
                    for (int k = 0; k < keyword.length; ++k) {
                        String[] combo = keyword[k].split("\\+");
                        boolean isCompitable = true;
                        for (int c = 0; c < combo.length; ++c) {
                            boolean mark = false;
                            for (int i = 0; i < vocab.length; ++i) {
                                if (vocab[i].equalsIgnoreCase(combo[c])) {
                                    mark = true;
                                    break;
                                }
                            }
                            if (!mark) {
                                isCompitable = false;
                                break;
                            }
                        }
                        if (isCompitable) { // change states
                            roomNum = 2;
                            stateNum = (j == 0) ? 6 : 4; // BE / back to 2-4
                            return /*" state = " + stateNum + ", " + */ " " + script[roomIndex][stateIndex][j + 1];
                        }
                    }
                }
                retStr = "Sorry, I did not catch what you said.";
            }
        }


        return "\n update : " + " state = " + stateNum + ", " + retStr;
    }

}
