package com.caih.cloud.iscs.charge.service;

import com.caih.cloud.iscs.common.Pair;

import java.util.List;

public interface SmartCommunityService {

    Pair<Object, String> checkUser(String token);

    Pair<Object, String> checkUser4Test(String id);

    Pair<Object, String> checkUser4UserLogin(String token);

    List getDeviceStatus(String deviceNo);

    String startCharge(String deviceNo, Integer portNo, int timeDifference);

    String stopCharge(String deviceNo, Integer portNo);
}
