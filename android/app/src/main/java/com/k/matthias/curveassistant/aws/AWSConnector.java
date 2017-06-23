package com.k.matthias.curveassistant.aws;

import android.content.Context;

import com.amazonaws.auth.*;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

/**
 * Created by matthias on 31.05.17.
 */
public final class AWSConnector {

    private static LambdaInvokerFactory factory = null;
    private static LambdaInterface lambdaInterface = null;

    private AWSConnector() {

    }

    private static void initConnection(Context context) {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                AWSCredentials.AWS_IDENTITY_POOL_ID, // Identity Pool ID
                AWSCredentials.AWS_REGION // Region
        );

        factory = new LambdaInvokerFactory(
                context,
                AWSCredentials.AWS_REGION,
                credentialsProvider);

        lambdaInterface = factory.build(LambdaInterface.class);

    }

    public static void callAWSLambda(Context context, RequestClass request, ResultReceiver receiver) {
        if (factory == null || lambdaInterface == null) {
            initConnection(context);
        }
        LambdaTask lambdaTask = new LambdaTask(context, lambdaInterface, receiver);
        lambdaTask.execute(request);
    }


}
