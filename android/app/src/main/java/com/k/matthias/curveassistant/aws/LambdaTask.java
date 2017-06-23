package com.k.matthias.curveassistant.aws;

import android.content.Context;
import android.os.*;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;

import org.json.JSONObject;

/**
 * Created by matthias on 31.05.17.
 */

public class LambdaTask extends AsyncTask<RequestClass, Void, String> {

    private static final String TAG = "LambdaTask";
    private final LambdaInterface lambdaInterface;
    private final Context context;
    private final ResultReceiver receiver;

    public LambdaTask(Context context, LambdaInterface lambdaInterface, ResultReceiver receiver) {
        this.lambdaInterface = lambdaInterface;
        this.context = context;
        this.receiver = receiver;
    }

    @Override
    protected String doInBackground(RequestClass... params) {
        try {
            return lambdaInterface.curveDetection(params[0]);
        } catch (LambdaFunctionException lfe) {
            Log.e(TAG, "Failed to invoke curveDetection", lfe);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            return;
        }

        if (receiver != null) {
            receiver.onResultReceived(result);
        }
    }
}
