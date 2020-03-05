package com.caih.cloud.iscs.charge.utils;

public class PcaCode {

    public PcaCode(Integer provinceCode, Integer cityCode, Integer areaCode)
    {
        this.provinceCode = provinceCode;
        this.cityCode = cityCode;
        this.areaCode = areaCode;
    }

    public Integer provinceCode;
    public Integer cityCode;
    public Integer areaCode;
}