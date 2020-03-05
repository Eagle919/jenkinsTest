package com.github.pig.common.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pig.common.bean.vo.ActivitiModelVo;
import com.github.pig.common.util.exception.NotExistException;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.zip.ZipInputStream;

/**
 * @className ActivityHelper - 工作流辅助类
 * @Author chenkang
 * @Date 2018/11/6 16:12
 * @Version 1.0
 */
@Component
public class ActivityHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityHelper.class);


    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ProcessEngine processEngine;

    /**
     * 默认byte大小
     */
    private static final  int DEFAULT_SIZE = 1024;


    private static final  String USER_TASK = "userTask";

    /**
     * 模型默认流程名称
     */
    private static final String DEFAULT_NAME = "new_process";

    /**
     * 新模型默认版本
     */
    private static final Integer DEFAULT_VERSION = 1;

    /**
     * 默认流程KeyName
     */
    private static final String DEFAULT_KEY = "default_key";

    private static final String IMG_CONTENT_TYPE = "img/png";


    /**
     * 根据流程名获取Deployment
     *
     * @param processName
     * @return
     */
    public Deployment getDeploymentByName(String processName) {
        return  repositoryService.createDeploymentQuery().deploymentName(processName).singleResult();
    }


    /**
     * 根据deployId获取Deployment
     *
     * @param deployId
     * @return
     */
    public Deployment getDeploymentById(String deployId) {
        return repositoryService.createDeploymentQuery().deploymentId(deployId).singleResult();
    }

    /**
     * 根据deployId删除部署流程  -- 发布新版本之前需要删除旧的
     *
     * @param deployId
     */
    public void delDeploymentById(String deployId) {
        repositoryService.deleteDeployment(deployId);
    }


    /**
     * 根据zip压缩包获取流程定义
     *
     * @param zipUrl
     * @return
     */
    public Deployment doDeployByZip(Class<?> claszz, String zipUrl, String processName) {

        InputStream in = claszz.getClassLoader().getResourceAsStream(zipUrl);
        ZipInputStream zipInputStream = new ZipInputStream(in);
        return repositoryService.createDeployment().key(processName).name(processName).addZipInputStream(zipInputStream).deploy();

    }

    /**
     * 启动流程 - 设置业务ID
     *
     * @param deployId
     * @param bizId
     * @return
     */
    public ProcessInstance startProcess(String deployId, String bizId) {
        //根据部署流程获取定义
        ProcessDefinition processDefinition = getProcessDefinition(deployId);
        LOGGER.info("deployId ：{} 启动",deployId);
        return runtimeService.startProcessInstanceById(processDefinition.getId(), bizId);
    }


    /**
     * 根据deployId 获取流程定义
     *
     * @param deployId
     * @return
     */
    public ProcessDefinition getProcessDefinition(String deployId) {
        //根据部署流程获取定义
        return repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
    }

    /**
     * 根据流程定义ID - definitionKey 和 用户ID获取代办列表
     *
     * @param userId
     * @return
     */
    public List<Task> getTaskListTaskByCndidateUser(String userId) {
        return taskService.createTaskQuery().taskCandidateUser(userId).orderByTaskCreateTime().desc().list();
    }

    /**
     * 查询任务列表通过CndidateUserOrAssign
     * @param userId
     * @return
     */
    public List<Task> getTaskListTaskByCndidateUserOrAssign(String userId) {
        return taskService.createTaskQuery().taskCandidateOrAssigned(userId).orderByTaskCreateTime().desc().list();
    }

    /**
     * 根据分配人员查询
     *
     * @param assignee
     * @return
     */
    public List<Task> getTaskListByUserId(String assignee) {
        return processEngine.getTaskService().createTaskQuery().taskAssignee(assignee).orderByTaskCreateTime().desc().list();
    }

    /**
     * 任务审批
     *
     * @param taskId
     * @param userId
     */
    public void completeTask(String taskId, String userId, Map<String, Object> vars) {

        //获取流程实例
        taskService.claim(taskId, userId);
        taskService.complete(taskId, vars);
    }

    /**
     * 根据TaskId 获取指定任务
     *
     * @param taskId
     * @return
     */
    public Task getTaskByTaskId(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }


    /**
     * 根据processInstanceId获取ProcessInstance
     *
     * @param
     * @return
     */
    public ProcessInstance getProcessInstanceByProcessInstanceId(String processInstanceId) {
        return runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    /**
     * 根据流程ID生成流程图
     *
     * @param
     */
    public void getImgByDeployId(String deployId ,HttpServletResponse response) throws IOException {

        Deployment deployment = getDeploymentById(deployId);
        // 根据流程部署ID和资源名称获取输入流
        InputStream inputStream = processEngine.getRepositoryService().getResourceAsStream(deployId, deployment.getName() + ".png");

        //接口测试的时候注释掉 -- 用于页面显示
        response.setContentType(IMG_CONTENT_TYPE);

        OutputStream os = response.getOutputStream();

        int bytesRead = 0;

        byte[] buffer = new byte[DEFAULT_SIZE * 8];

        while ((bytesRead = inputStream.read(buffer, 0, DEFAULT_SIZE * 8)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        inputStream.close();

    }

    /**
     * 根据procInstId 获取指定任务
     *
     * @param
     * @return
     */
    public Task getTaskByTaskProcInstId(String procInstId) {
        return taskService.createTaskQuery().processInstanceId(procInstId).singleResult();
    }

    /**
     * 根据processInstanceId获取历史流程实例
     *
     * @param
     * @return
     */
    private HistoricProcessInstance getHistoricProcessInstance(String processInstanceId) {
        return  historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

    }

    /**
     * 根据流程ID生成流程图
     *
     * @param
     */
    public void getProcessImg(String processInstanceId ,HttpServletResponse response) throws IOException {
        HistoricProcessInstance historicProcessInstance = getHistoricProcessInstance(processInstanceId);

        Deployment deployment = getDeploymentById(historicProcessInstance.getDeploymentId());

        // 根据流程部署ID和资源名称获取输入流
        InputStream inputStream = processEngine.getRepositoryService().getResourceAsStream(historicProcessInstance.getDeploymentId(), deployment.getName() + ".png");

        //接口测试的时候注释掉 -- 用于页面显示
        response.setContentType(IMG_CONTENT_TYPE);

        OutputStream os = response.getOutputStream();

        int bytesRead = 0;

        byte[] buffer = new byte[DEFAULT_SIZE * 8];

        while ((bytesRead = inputStream.read(buffer, 0, DEFAULT_SIZE * 8)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        inputStream.close();
    }

    /**
     * 读取zip
     *
     * @throws Exception
     */
    public void readResource(String processDefinitionId, String processInstanceId, HttpServletResponse response) throws IOException {

        // 设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);


        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();

        String resourceName = processDefinition.getResourceName() + ".png";

        if (!StringUtils.isEmpty(processInstanceId)) {
            getHisProcessImg(processInstanceId, response);
        } else {

            InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);

            // 输出资源内容到相应对象
            byte[] b = new byte[DEFAULT_SIZE];
            int len = -1;
            while ((len = inputStream.read(b, 0, DEFAULT_SIZE)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }

        }
    }

    /**
     * 获取流程图
     *
     * @param processInstanceId
     * @param response
     */
    public void getHisProcessImg(String processInstanceId, HttpServletResponse response) throws IOException {

        //获取历史流程实例
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();


        /**
         *获取历史流程节点 并升序排列 （执行先后）
         */
        List<HistoricActivityInstance> historicProcessInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceId().asc().list();

        List<String> completeList = new ArrayList<>();

        for (int i = 0; i < historicProcessInstances.size() - 1; i++) {
            completeList.add(historicProcessInstances.get(i).getActivityId());
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

        //已执行的线
        List<String> actedIds = getHightLightIds(bpmnModel, historicProcessInstances);

        //获取流程图图像字符流
        ProcessDiagramGenerator pdg = new DefaultProcessDiagramGenerator();
        //生成Diagram 设置格式 转为流输出
        InputStream inputStream = pdg.generateDiagram(bpmnModel, "png", completeList, actedIds, "宋体 ", "微软雅黑", "黑体", null, 2.0);


        //接口测试的时候注释掉 -- 用于页面显示
        response.setContentType(IMG_CONTENT_TYPE);

        OutputStream os = response.getOutputStream();

        int bytesRead = 0;

        byte[] buffer = new byte[DEFAULT_SIZE * 8];

        while ((bytesRead = inputStream.read(buffer, 0, DEFAULT_SIZE * 8)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        inputStream.close();
    }

    /**
     * 获取需要高亮的线ID
     *
     * @param bpmnModel
     * @param historicActivityInstances
     * @return
     */
    private List<String> getHightLightIds(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances) {

        List<String> lines = new ArrayList<>();

        //遍历历史实例
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            //获取节点信息
            FlowNode node = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(i).getActivityId());
            //存放相同时间节点
            List<FlowNode> sameStartTimeNodes = new ArrayList<>();

            FlowNode flowNode = getRealNextNode(i,bpmnModel,historicActivityInstances);

            sameStartTimeNodes.add(flowNode);

            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                // 后续第一个节点
                HistoricActivityInstance historicActivityInstance1 = historicActivityInstances.get(j);
                // 后续第二个节点
                HistoricActivityInstance historicActivityInstance2 = historicActivityInstances.get(j + 1);

                // 如果第一个节点和第二个节点开始时间相同保存
                if (historicActivityInstance1.getStartTime().equals(historicActivityInstance2.getStartTime())) {
                    FlowNode saveNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance2.getActivityId());
                    sameStartTimeNodes.add(saveNode);
                } else {// 有不相同跳出循环
                    break;
                }
            }
            // 取出节点的所有出去的线
            List<SequenceFlow> transitionLines = node.getOutgoingFlows();

            for (SequenceFlow sequenceFlow : transitionLines) {
                //遍历集合 将Id放入返回集合中
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                FlowNode targetNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef());
                if (sameStartTimeNodes.contains(targetNode)) {
                    lines.add(sequenceFlow.getId());
                }
            }
        }
        return lines;
    }

    /**
     * 获取下一个真正节点
     * @param index
     * @param bpmnModel
     * @param historicActivityInstances
     * @return
     */
    private FlowNode getRealNextNode(int index ,BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances ){

        //历史第一个点
        HistoricActivityInstance historicActivityInstance1 = historicActivityInstances.get(index);
        HistoricActivityInstance historicActivityInstance2;

        for (int k = index + 1; k <= historicActivityInstances.size() - 1; index++) {
            //下一个节点
            historicActivityInstance2 = historicActivityInstances.get(k);
            //都是usertask，且主节点与后续节点的开始时间相同，说明不是真实的后继节点
            if (!(historicActivityInstance1.getActivityType().equals(USER_TASK) && historicActivityInstance2.getActivityType().equals(USER_TASK) &&
                    historicActivityInstance1.getStartTime().equals(historicActivityInstance2.getStartTime()))) {
                //找到紧跟在后面的一个节点
                return (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(k).getActivityId());
            }
        }
        return  null;
    }
    /**
     * 给任务节点用户组添加用户
     *
     * @param taskId
     * @param userId
     */
    public void addCandidateUser(String taskId, String userId) {

        processEngine.getTaskService().addCandidateUser(taskId, userId);

        LOGGER.info("任务 id ：{}  添加用户 ：{} 添加成功！",taskId,userId);
    }

    /**
     * 获取正在任务的用户组
     *
     * @param taskId
     * @return
     */
    public Map<String, String> getCandidateUsers(String taskId) {

        List<IdentityLink> links = processEngine.getTaskService().getIdentityLinksForTask(taskId);

        if (links == null) {
            return null;
        }
        Map<String, String> ids = new HashMap<>();
        for (IdentityLink identityLink : links) {
            ids.put(identityLink.getUserId(), identityLink.getUserId());
        }
        return ids;
    }


    /**
     * 根据流程定义ID -
     *
     * @param userId
     * @return
     */
    public List<Task> getListByUserIdAndDeptId(String userId, String deptId) {
        TaskQuery query = taskService.createTaskQuery();

        if(StringUtil.isNotEmpty(userId)){
            query = query.taskCandidateUser(userId);
        }
        return  query.taskCandidateUser(deptId).orderByTaskCreateTime().desc().list();
    }

    /**
     * 根据业务ID
     *
     * @param bizId
     * @return
     */
    public List<Task> getTaskListTaskByBizId(String bizId) {
        return taskService.createTaskQuery().processInstanceBusinessKey(bizId).list();
    }
    public String getCurrentActivityId(String processInstanceId) {
        Task task = getTaskByTaskProcInstId(processInstanceId);
        return task.getTaskDefinitionKey();
    }


    /**
     * 根据processInstanceId 获取当前节点名字
     *
     * @param processInstanceId
     * @return
     */
    public String getCurrentActivityName(String processInstanceId) {

        Task task = getTaskByTaskProcInstId(processInstanceId);

        return task.getName();
    }


    /**
     * 开启流程 - 指定工作人和业务表Id
     *
     * @param deployId
     * @param bizId
     * @param vars
     */
    public void startAssginUser(String deployId, String bizId, Map<String, Object> vars) {
        //根据部署流程获取定义
        ProcessDefinition processDefinition = getProcessDefinition(deployId);
        runtimeService.startProcessInstanceById(processDefinition.getId(), bizId, vars);


    }

    /**
     * 根据 流程ID启动
     *
     * @param key   -processId
     * @param bizId
     * @param vars
     */
    public ProcessInstance startAssginUserByKey(String key, String bizId, Map vars) {
        ProcessInstance processInstance = null;
        if (CollectionUtils.isEmpty(vars)) {
            processInstance = runtimeService.startProcessInstanceByKey(key, bizId);
        } else {
            processInstance = runtimeService.startProcessInstanceByKey(key, bizId, vars);
        }
        return processInstance;
    }


    /**
     * 任务审批 指定下一个用户组
     *
     * @param taskId
     * @param userId
     */
    public void completeTask(String taskId, String userId, String users, Integer extendFlag) {
        //获取流程实例
        taskService.claim(taskId, userId);
        Map<String, Object> vars = new HashMap<>();
        vars.put("candidateUser", users);
        if (extendFlag != null) {
            vars.put("flag", extendFlag);
        }
        taskService.complete(taskId, vars);
    }

    /**
     * 根据processInstanceId 获取下个节点名
     *
     * @param processInstanceId
     * @return
     */
    public String getNextNode(String processInstanceId) {
        ProcessInstance processInstance = getProcessInstanceByProcessInstanceId(processInstanceId);

        ProcessDefinition processDefinition = getProcessDefinition(processInstance.getDeploymentId());

        //获得流程模型
        BpmnModel model = repositoryService.getBpmnModel(processDefinition.getId());

        Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();

        Task task = getTaskByTaskProcInstId(processInstanceId);

        String currentNode = task.getTaskDefinitionKey();

        Iterator<FlowElement> iterator = flowElements.iterator();

        String nextNode = "";

        while (iterator.hasNext()) {
            FlowElement e = iterator.next();
            if (e instanceof UserTask && e.getId().equals(currentNode)) {
                    nextNode = iterator.next().getName();
            }
        }


        return nextNode;
    }


    /**
     *  model方法
     */

    /**
     * @param
     * @param
     * @throws Exception
     */
    public ActivitiModelVo buildModel() throws UnsupportedEncodingException {
        //初始化一个空模型
        Model model = repositoryService.newModel();

        //模型属性定义
        JSONObject vars = new JSONObject();
        vars.put(ModelDataJsonConstants.MODEL_NAME, DEFAULT_NAME);
        vars.put(ModelDataJsonConstants.MODEL_DESCRIPTION, "");
        vars.put(ModelDataJsonConstants.MODEL_REVISION, DEFAULT_VERSION);

        model.setName(DEFAULT_NAME);
        model.setVersion(DEFAULT_VERSION);
        model.setKey(DEFAULT_KEY);
        model.setMetaInfo(vars.toString());

        //新建model
        repositoryService.saveModel(model);
        String id = model.getId();

        //完善ModelEditorSource
        JSONObject editSource = new JSONObject();
        editSource.put("id", "");
        editSource.put("resourceId", "");
        JSONObject stencilSet = new JSONObject();
        stencilSet.put("namespace",
                "http://b3mn.org/stencilset/bpmn2.0#");
        editSource.put("stencilset", stencilSet);
        repositoryService.addModelEditorSource(id, editSource.toString().getBytes("utf-8"));
        ActivitiModelVo vo  = new ActivitiModelVo();
        vo.setDispatchUrl("/modeler.html?modelId=" + id);

        return  vo;

    }


    /**
     * 根据模型ID部署
     *
     * @param
     * @return
     */
    public Deployment doDeployByModelId(String modeId) throws  NotExistException,IOException {
        //判断是否存在模型

        Model model = repositoryService.getModel(modeId);
        if (model == null) {
            throw new NotExistException(String.format("modelId : %s 不存在", modeId));
        }

        byte[] bytes = repositoryService.getModelEditorSource(model.getId());

        JsonNode modelNode = new ObjectMapper().readTree(bytes);

        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(modelNode);

        if (bpmnModel.getProcesses().isEmpty()) {
            throw new NotExistException(String.format("modelId : %s  未设计流程 , 请先设计流程", modeId));
        }

        byte[] modelBytes = new BpmnXMLConverter().convertToXML(bpmnModel);

        String modelKey = model.getKey();
        String editSource = model.getKey() + ".bpmn";
        Deployment deployment = repositoryService
                .createDeployment()
                .key(modelKey)
                .name(modelKey)
                .addString(editSource, new String(modelBytes,"UTF-8"))
                .deploy();

        //更新部署ID
        model.setDeploymentId(deployment.getId());
        repositoryService.saveModel(model);
        return deployment;
    }

    /**
     * 获取所有的模型
     * @return
     */
    public List<Model> getModels(){
        return repositoryService.createModelQuery().list();
    }

    /**
     * 删除模型
     * @param modelId
     */
    public void deleteModel(String modelId){
        repositoryService.deleteModel(modelId);
    }

    /**
     * 通过modelId 启动 流程
     * @param modelId
     * @param bizId -- 业务ID 根据项目业务设置
     */
    public void startProcessByModelId(String modelId,String bizId){
        Model model  = getModel(modelId);
        String deployId = model.getDeploymentId();
        startProcess(deployId,bizId);
    }

    /**
     * 根据modelId获取 model
     * @param modelId
     * @return
     */
    public Model getModel(String modelId){
        return repositoryService.getModel(modelId);
    }


    /**
     * 根据modelId获取流程图
     *
     * @param
     */
    public void getProcessImgByModelId(String modelId ,HttpServletResponse response) throws IOException {
        //获取model
        Model model = repositoryService.getModel(modelId);

        byte[] bytes = repositoryService.getModelEditorSource(model.getId());

        JsonNode modelNode = new ObjectMapper().readTree(bytes);

        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(modelNode);

        //获取流程图图像字符流
        ProcessDiagramGenerator pdg = new DefaultProcessDiagramGenerator();
        //生成Diagram 设置格式 转为流输出
        InputStream inputStream = pdg.generateDiagram(bpmnModel, "png", ListUtils.EMPTY_LIST, ListUtils.EMPTY_LIST, "宋体 ", "微软雅黑", "黑体", null, 2.0);

        //接口测试的时候注释掉 -- 用于页面显示
        response.setContentType(IMG_CONTENT_TYPE);

        OutputStream os = response.getOutputStream();

        for (int b ; (b = inputStream.read()) != -1; ) {
            os.write(b);
        }
        os.close();
        inputStream.close();
    }

}
