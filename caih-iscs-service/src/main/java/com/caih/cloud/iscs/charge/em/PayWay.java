package com.caih.cloud.iscs.charge.em;

public enum PayWay {
    NONE, WX_PAY, ALI_PAY;

    public static PayWay makePayWay(Integer type) {
        if (type == 2)
            return WX_PAY;
        else if (type == 1)
            return ALI_PAY;
        return null;
    }
}
