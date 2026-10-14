package com.ccm.gateway.constants;

public class GatewayConstants {

    private GatewayConstants(){} // Prevent instantiation

    public static final String X_GATEWAY_HEADER = "X-Gateway";
    public static final String GATEWAY_NAME = "CCM-APIGateway";

    public static final String BASE_PATH_SECURITY = "/ccm/security/**";
    public static final String BASE_PATH_CLIENT = "/ccm/client/**";
    public static final String BASE_PATH_CUSTOMER = "/ccm/customer/**";

    public static final String REWRITE_SECURITY = "/security/${remaining}";
    public static final String REWRITE_CLIENT = "/client/${remaining}";
    public static final String REWRITE_CUSTOMER = "/customer/${remaining}";
}
