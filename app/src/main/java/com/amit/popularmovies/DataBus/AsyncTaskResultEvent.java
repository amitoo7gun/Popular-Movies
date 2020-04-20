package com.amit.popularmovies.DataBus;


public class AsyncTaskResultEvent {
    private boolean result;
    private String className;

    public AsyncTaskResultEvent(boolean result, String className) {
        this.className = className;
        this.result = result;
    }

    public boolean getResult() {
        return result;
    }

    public String getName(){
        return className;
    }
}
