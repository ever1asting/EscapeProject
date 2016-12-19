package com.example.ywn.escapeproject;

/**
 * Created by ywn on 2016/12/16.
 */

import android.util.Log;

public class FiniteStateMachine {

//    private static final int MaxRoom = 4;
//    private static final int MaxState = 20;
//    private static final int MaxRet = 20;

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
                    "open+two open+second door+two door+second", "Ok, a new room. We can call it ROOM 3.",
                    },
                    /* state 6 */
                    {},// BAD END
                    /* state 7 */
                    {"open+one open+first door+one door+first", "Ok, I went back to ROOM 1",
                     "light skylight", "(Climbing the ladder...) Oh, a new room, let us call it ROOM 4",
                     "body", "It is in the corride now. We had used it to attract the zombie successfully.",
                     "open+three open+third door+three door+third", "BAD END: The girl was attacked by the zombie.",
                     "open+two open+second door+two door+second", "Back to ROOM 3."}
            },
            /***********  Corridor  ************/
            {
                    {},
                    /* state 1 */
                    {},
                    /* state 2 */
                    {"open+four open+fourth door+four door+fourth", "BAD END: The girl was attacked by the zombie.",
                    "open+three open+third door+three door+third", "I think we can get some ideas in ROOM 2."},

            },
            /****************  ROOM 3  ******************/
            {
                    {},
                    /* state 1 */
                    {},
                    /* state 2 */
                    {"open+four open+fourth door+four door+fourth", "BAD END: The girl was attacked by the zombie.",
                     "open+two open+second door+two door+second", "Back to ROOM 2",
                    "ladder", "Using this ladder, maybe I can access to the skylight in ROOM 2."},
                    /* state 3 */
                    {"open+four open+fourth door+four door+fourth", "BAD END: The girl was attacked by the zombie.",
                    "open+two open+second door+two door+second", "Come into ROOM 2. I have put the ladder here and now I can access to the skylight.",
                     "ladder", "I have put it in ROOM 2 in order to access to the skylight."}
            },
            /****************  ROOM 4  ******************/
            {
                    {},
                    /* state 1 */
                    {},
                    /* state 2 */
                    {"hole obstacle", "Obstacle here...(try to remove it). Oh, A hole here, I think I can escape from this hole.",
                    "mirror", "A dirty mirror here. Can see anything in it before clean it.",
                    "down downstairs", "Back to ROOM 2"},
                    /* state 3 */
                    {"hole obstacle", "A hole here after move the obstales, I think I can escape from this hole.",
                    "mirror", "A dirty mirror here. Can see anything in it before clean it.",
                    "down downstairs", "Back to ROOM 2",
                    "escape leave", "BAD END. Single escape."},
                    /* state 4 */
                    {"hole obstacle", "A hole here after move the obstales, I think I can escape from this hole.",
                    "mirror clean", "Clean it? Um... Ok",
                     "down downstairs", "Back to ROOM 2",
                     "escape leave", "BAD END. Single escape."},
                    /* state 5 */
                    {"hole obstacle", "A hole here after move the obstales, I think I can escape from this hole.",
                    "mirror clean", "I had clean it.",
                    "down downstairs", "Back to ROOM 2",
                    "escape leave", "BAD END. Single escape.",
                    "curtain curtains", "Happy End."},
                    /* state 6 */
                    {},
                    /* state 7 */
                    {},
                    /* state 8 */
                    {"hole obstacle", "Obstacle here...(try to remove it). Oh, A hole here, I think I can escape from this hole.",
                    "mirror clean", "Clean it? Um... Ok",
                     "down downstairs", "Back to ROOM 2"},
                    /* state 9 */
                    {"hole obstacle", "Obstacle here...(try to remove it). Oh, A hole here, I think I can escape from this hole.",
                    "mirror clean", "I had clean it",
                    "down downstairs", "Back to ROOM 2"}
            }
    };

    private int roomNum;
    private int stateNum;

    private int preRoomNum;
    private int preStateNum;
    private int preReact;

    private int preStateOfRoom4;

    public int[] getState() {
        int[] state = new int[5];
        state[0] = roomNum;
        state[1] = stateNum;
        state[2] = preRoomNum;
        state[3] = preStateNum;
        state[4] = preReact;
        return state;
    }

    public void init(int roomNum, int stateNum) {
        /* test ROOM 2 */
        this.roomNum = roomNum;
        this.stateNum = stateNum;
    }

    public String update(String text) {
        String retStr = "";
        String[] vocab = text.split(" ");
        int roomIndex, stateIndex;

        /* ROOM 1 */
        if(roomNum == 1) {
            roomIndex = 1;

            Log.v("", "room num = 1");

            if(stateNum == 1) { // init state, just return settings and hints
                //set pre state
                preRoomNum = 1;
                preStateNum = 1;
                preReact = 1;

                stateNum = 2;
                retStr = "Settings. I can hardly see anything. Help me leave this room.";
            }
            else {
                // set pre state
                preRoomNum = 1;
                preStateNum = stateNum;
                preReact = -1;

                stateIndex = stateNum;
                for(int i = 0; i < vocab.length; ++i)
                    for(int j = 0; j < script[roomIndex][stateIndex].length; j += 2) {
                        String[] keyword = script[roomIndex][stateIndex][j].split(" ");
                        for(int k = 0; k < keyword.length; ++k) {
                            if(vocab[i].equalsIgnoreCase(keyword[k])) {
                                preReact = j + 1;

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
        /* ROOM 2 */
        else if (roomNum == 2) {
            roomIndex = 2;
            if(stateNum == 1) {
                //set pre state
                preRoomNum = 2;
                preStateNum = 1;
                preReact = 1;

                stateNum = 2;
                retStr = "Seems a new room, light, body and 3 doors here.";
            }
            else{
                stateIndex = stateNum;

                //set pre state
                preRoomNum = 2;
                preStateNum = stateNum;
                preReact = -1;

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
                            // set pre state
                            preReact = j + 1;

                            if(stateNum == 2) {
                                switch (j / 2) {
                                    case 0: stateNum = 3; break;
                                    default: break;
                                }
                            }
                            else if (stateNum == 3 || stateNum == 4 || stateNum == 5) {
                                switch (j / 2) {
                                    case 0: roomNum = 1; preStateNum = stateNum; stateNum = 8;  break;
                                    case 2:
                                        if(stateNum == 4)
                                            stateNum = 5;
                                        break;
                                    case 3: stateNum = 6; break; // BE
                                    case 4:
                                        roomNum = (stateNum == 5)? 4: 3;
                                        stateNum = 1;
                                        break;
                                    default: break;
                                }
                            }
                            else if(stateNum == 7) {
                                switch (j / 2) {
                                    case 0: roomNum = 1; preStateNum = stateNum; stateNum = 8;  break;
                                    case 1:
                                        roomNum = 5;
                                        stateNum = preStateOfRoom4;
                                    case 3: stateNum = 6; break; // BE
                                    case 4:
                                        roomNum = 4;
                                        stateNum = 3;
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
        /* Corridor */
        else if(roomNum == 3) {
            roomIndex = 3;

            if (stateNum == 1) {
                //set pre state
                preRoomNum = 3;
                preStateNum = 1;
                preReact = 1;

                stateNum = 2;
                retStr = "Corride. Here are 2 doors in total.";
            } else if (stateNum == 2) {
                stateIndex = stateNum;

                //set pre state
                preRoomNum = 3;
                preStateNum = stateNum;
                preReact = -1;

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
                            preReact = j + 1;

                            roomNum = 2;
                            stateNum = (j == 0) ? 6 : 4; // BE / back to 2-4
                            return /*" state = " + stateNum + ", " + */ " " + script[roomIndex][stateIndex][j + 1];
                        }
                    }
                }
                retStr = "Sorry, I did not catch what you said.";
            }
        }
        /* ROOM 3 */
        else if (roomNum == 4) {
            roomIndex = 4;
            if(stateNum == 1) {
                //set pre state
                preRoomNum = 4;
                preStateNum = 1;
                preReact = 1;

                stateNum = 2;
                retStr = "ROOM 3, a ladder here";
            }
            else{
                stateIndex = stateNum;

                //set pre state
                preRoomNum = 4;
                preStateNum = stateNum;
                preReact = -1;

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
                            // set pre state
                            preReact = j + 1;

                            switch (j / 2) {
                                case 0: roomNum = 2; stateNum = 6; break; // BE
                                case 1:
                                    roomNum = 2;
                                    stateNum = (stateNum == 2)? 5 : 7;
                                    break;
                                default: break;
                            }

                            return " state = " + stateNum + ", " +  " " + script[roomIndex][stateIndex][j + 1];
                        }
                    }
                }
                retStr = "Sorry, I did not catch what you said.";
            }
        }
        /* ROOM 4 */
        else if(roomNum == 5) {
            roomIndex = 5;
            if(stateNum == 1) {
                //set pre state
                preRoomNum = 5;
                preStateNum = 1;
                preReact = 1;

                stateNum = 2;
                retStr = "ROOM 4, girl:\"nothing here\"";
            }
            else{
                stateIndex = stateNum;

                //set pre state
                preRoomNum = 5;
                preStateNum = stateNum;
                preReact = -1;

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
                            // set pre state
                            preReact = j + 1;

                            if(stateNum == 2) {
                                switch (j / 2) {
                                    case 0:
                                        if(stateNum == 2)
                                            stateNum = 3;
                                        else if(stateNum == 8)
                                            stateNum = 4;
                                        else if(stateNum == 9)
                                            stateNum = 5;
                                        break;
                                    case 1:
                                        if(stateNum == 2)
                                            stateNum = 8;
                                        else if(stateNum == 3 || stateNum == 4 || stateNum == 8)
                                            ++stateNum;
                                        break;
                                    case 2: preStateOfRoom4 = stateNum; roomNum = 2; stateNum = 7; break;
                                    case 3: stateNum = 7; break; // BE
                                    case 4: stateNum = 6; break; // HE
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


        return "\n update : " + " state = " + stateNum + ", " + retStr;
    }

}
