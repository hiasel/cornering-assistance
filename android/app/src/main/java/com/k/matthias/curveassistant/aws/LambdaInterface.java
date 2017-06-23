package com.k.matthias.curveassistant.aws;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

/**
 * Created by matthias on 31.05.17.
 */

public interface LambdaInterface {
    /**
     * Invoke lambda function "curveDetection".
     */
    @LambdaFunction
    String curveDetection(RequestClass request);
}
