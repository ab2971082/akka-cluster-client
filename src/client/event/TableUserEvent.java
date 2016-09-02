package com.tiantian.stranger.akka.event;

/**
 *
 */
public abstract class TableUserEvent implements TableEvent {

    private boolean needRet;

    public TableUserEvent() {

    }
    public TableUserEvent(String event) {
        this(false);
    }
    public TableUserEvent(boolean needRet) {
        this.needRet = needRet;
    }

    public boolean isNeedRet() {
        return needRet;
    }

    public void setNeedRet(boolean needRet) {
        this.needRet = needRet;
    }
}
