package com.k.matthias.curveassistant.aws;

import com.amazonaws.regions.Regions;

/**
 * Credentials needed to connect to AWS Lambda function
 */

public final class AWSCredentials {
    public static final String AWS_IDENTITY_POOL_ID = "<IDENTITY_POOL_ID>";
    public static final Regions AWS_REGION = Regions.DEFAULT_REGION;

    private AWSCredentials(){}
}
