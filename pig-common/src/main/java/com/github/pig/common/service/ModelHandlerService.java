package com.github.pig.common.service;

import com.github.pig.common.bean.Response;
import com.github.pig.common.bean.constants.Constant;
import com.github.pig.common.util.ActivityHelper;
import org.activiti.engine.repository.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @className ModelHandlerService
 * @Author chenkang
 * @Date 2018/11/27 16:18
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/service")
public class ModelHandlerService {

    private static final  Logger  LOGGER = LoggerFactory.getLogger(ModelHandlerService.class);

    @Autowired
    private ActivityHelper activityHelper;

    @PutMapping(value="/model/{modelId}/delete")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteModel(@PathVariable String modelId) {
        activityHelper.deleteModel(modelId);
    }

    @PutMapping(value="/model/{modelId}/deploy")
    @ResponseStatus(value = HttpStatus.OK)
    public Response deployProcess(@PathVariable String modelId) {
        Response response = new Response();
        try {
            Deployment deployment = activityHelper.doDeployByModelId(modelId);
           if(deployment == null){
               response.setCode(Constant.RespCode.FAIL.getCode());
           }
            return response;
       }catch (Exception e){
           LOGGER.error( e.getMessage());
            response.setCode(Constant.RespCode.FAIL.getCode());
            response.setMsg(e.getMessage());
           return response;
       }
    }
}
