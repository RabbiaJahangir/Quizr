package com.rabbiajahangir.android.quizr;

import android.os.AsyncTask;

/**
 * Created by Jahangir on 11/25/2015.
 */
public class ServerRequest extends AsyncTask<Void, Void, Boolean> {
    public interface TaskHandler {
        public boolean task();
    }

    private final TaskHandler taskHandler;

    public ServerRequest(TaskHandler handler) {
        this.taskHandler = handler;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean result = false;
        if (this.taskHandler != null) {
            result = this.taskHandler.task();
        }
        return result;
    }
}

