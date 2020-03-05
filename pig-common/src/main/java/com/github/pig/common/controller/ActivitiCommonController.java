package com.github.pig.common.controller;

import com.github.pig.common.bean.Response;
import com.github.pig.common.bean.constants.Constant;
import com.github.pig.common.bean.helper.VarHelper;
import com.github.pig.common.bean.vo.ModelVo;
import com.github.pig.common.exception.ArgsNullException;
import com.github.pig.common.service.ModelHandlerService;
import com.github.pig.common.util.ActivityHelper;
import org.activiti.engine.repository.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @className ActivitiController
 * @Author chenkang
 * @Date 2018/11/26 15:42
 * @Version 1.0
 */
@RestController
@RequestMapping("/activitiCommon")
public class ActivitiCommonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivitiCommonController.class);

    private static final String DEPLOYED = "已发布";
    private static final String UNDEPLOYED = "未发布";

    @Autowired
    private ModelHandlerService modelHandlerService;


    @Autowired
    ActivityHelper activityHelper;

    @GetMapping("/builderActivitiModel")
    public Object buildModel(){
        try {
            // 清空缓存
            VarHelper.setHelperModelVos(null);
            return activityHelper.buildModel();
        }catch (Exception e){
            LOGGER.error("创建Activiti Model 失败 " ,e);
        }
        return null;
    }

    @GetMapping("/getModels")
    public Response<List<ModelVo>> getModels(Integer pageSize, Integer limit){

        Response<List<ModelVo>> response = new Response<>();

        try {
            if(pageSize == null || pageSize < 0){
                throw new ArgsNullException("pageSize is incorrect");
            }

            if(limit == null || limit <= 0){
                throw new ArgsNullException("limit is incorrect");
            }
            //判断是否有缓存
            if( VarHelper.getHelperModelVos() != null && !VarHelper.getHelperModelVos().isEmpty()){
                response.setData(pagination(VarHelper.getHelperModelVos(),pageSize,limit));
                return response;
            }

            return getListAndCache(response,pageSize,limit);

        }catch (Exception e){
            response.setCode(Constant.RespCode.FAIL.getCode());
            response.setMsg(e.getMessage());
            return response;
        }
    }


    private List<ModelVo> pagination( List<ModelVo> modelVos ,int pageSize,int limit){
        List<ModelVo> vos = new ArrayList<>();
        int len = limit;
        //判断长度是否小于limit
        if(modelVos.size() < limit){
            len = modelVos.size();
        }

        int startIndex = (pageSize - 1) * limit;

        for(int i = startIndex ; i < len ; i++){
            vos.add(modelVos.get(i));
        }
        return vos;
    }

    private Response getListAndCache(Response<List<ModelVo>> response ,Integer pageSize, Integer limit){

        List<ModelVo> modelVos = new ArrayList<>();
        List<Model> models = activityHelper.getModels();
        Iterator<Model> iterator = models.iterator();
        while (iterator.hasNext()){
            Model model = iterator.next();
            ModelVo vo = new ModelVo();
            vo.setModelId(model.getId());
            vo.setModelName(model.getName());
            vo.setModelKey(model.getKey());
            vo.setDeploymentId(model.getDeploymentId());
            vo.setDeployStatus(UNDEPLOYED);
            if(model.getDeploymentId() != null && model.getDeploymentId() != ""){
                vo.setDeployStatus(DEPLOYED);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            vo.setCreateTime(sdf.format(model.getCreateTime()));
            modelVos.add(vo);
        }

        //放入缓存
        VarHelper.setHelperModelVos(modelVos);
        response.setData(pagination(modelVos,pageSize,limit));
        return response;
    }


    @GetMapping("/deletModelAndSelectByPage")
    public Response deletModelAndSelectByPage(String modelId ,Integer pageSize, Integer limit){
        activityHelper.deleteModel(modelId);
        Response response = new Response();
        return  getListAndCache(response,pageSize,limit);
    }

    @GetMapping("/deletModelById")
    public Response deletModel(String modelId){
        activityHelper.deleteModel(modelId);
        return  new Response();
    }

    @GetMapping("/getModelProcessImg")
    public Response getModelProcessImg(String modelId, HttpServletResponse response){
        Model model = activityHelper.getModel(modelId);
        try {
            activityHelper.getProcessImgByModelId(model.getId(),response);
        }catch (Exception e){
            LOGGER.error("modelId : {} getModelProcessImg error" ,modelId,e);
        }
        return  new Response();
    }

    @GetMapping("/deployProcessByModelId")
    public Response deployProcessByModelId(String modelId){
        Response resp =  modelHandlerService.deployProcess(modelId);

        if(resp.getCode() ==  Constant.RespCode.SUCCESS.getCode()){
            // 清空缓存
            VarHelper.setHelperModelVos(null);
        }
        return  resp;
    }
}
