package com.sfmap.api.location.demo.bean;

/*
 *  @author Dylan
 */
public class MsgEvent {
    private int type;
    private boolean isLoc;
    private String msg;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public MsgEvent(boolean isLoc, String msg) {
        this.isLoc = isLoc;
        this.msg = msg;
    }

    public MsgEvent(String msg) {
        this.msg = msg;
    }
}
