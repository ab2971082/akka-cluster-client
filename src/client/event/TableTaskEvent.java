package com.tiantian.stranger.akka.event;

import com.alibaba.fastjson.JSONObject;

/**
 *
 */
public class TableTaskEvent implements TableEvent {
    private String event;
    private JSONObject params;

    public TableTaskEvent() {

    }

    public TableTaskEvent(String event, JSONObject params) {
        this.event = event;
        this.params = params;
    }

    public void addParam(String name, Object value) {
        if (params == null) {
            params = new JSONObject();
        }
        params.put(name, value);
    }

    @Override
    public String event() {
        return event;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }

    @Override
    public String tableId() {
        return params.getString("tableId");
    }
}
