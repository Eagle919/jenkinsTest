package com.github.pig.common.bean.helper;

import com.github.pig.common.bean.vo.ModelVo;

import java.util.List;

/**
 * @className VarHelper  - 参数辅助类 辅助做内存缓存
 * @Author chenkang
 * @Date 2018/12/3 15:53
 * @Version 1.0
 */
public class VarHelper {

    private static List<ModelVo> helperModelVos;

    private VarHelper() {
    }

    public static List<ModelVo> getHelperModelVos(){
        return helperModelVos;
    }

    public static void setHelperModelVos(List<ModelVo> modelVos){
       helperModelVos = modelVos;
    }

}
