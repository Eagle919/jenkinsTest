/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pig.common.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pig.common.exception.MuiltiObjectException;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

/**
 * @author Tijs Rademakers
 */
@RestController
@RequestMapping(value = "/service")
public class ModelSaveRestResource implements ModelDataJsonConstants {

  private static final Logger LOGGER = LoggerFactory.getLogger(ModelSaveRestResource.class);

  @Autowired
  private RepositoryService repositoryService;


  @PutMapping(value="/model/{modelId}/save")
  @ResponseStatus(value = HttpStatus.OK)
  public void saveModel(@PathVariable String modelId, @RequestParam("name") String name, @RequestParam("key") String key,
                        @RequestParam("description") String description ,@RequestParam("jsonData") String jsonData) {
    try {
      //业务名即流程名
      if(StringUtils.isEmpty(key)){
        throw new NullPointerException("流程业务名为空");
      }
      //重复流程KEY
      if(!validKey(key)){
        throw new MuiltiObjectException(String.format("key : %s 已存在 " , key ));
      }

      Model model = repositoryService.getModel(modelId);

      JSONObject modelJson = JSONObject.parseObject(model.getMetaInfo());

      modelJson.put(MODEL_NAME, name);
      modelJson.put(MODEL_DESCRIPTION, description);
      model.setMetaInfo(modelJson.toString());
      model.setName(name);
      model.setKey(key);
      repositoryService.saveModel(model);

      repositoryService.addModelEditorSource(model.getId(), jsonData.getBytes("utf-8"));

    } catch (Exception e) {
      LOGGER.error("Error saving model", e);
      throw new ActivitiException("Error saving model", e);
    }
  }

  @GetMapping(value="/model/{modelId}/saveRresource")
  @ResponseStatus(value = HttpStatus.OK)
  public void saveModel(@PathVariable String modelId,String svgData) {
    try {
      if(StringUtils.isEmpty(svgData)){
        throw new NullPointerException("svgData为空");
      }
      Model model = repositoryService.getModel(modelId);

      InputStream svgStream = new ByteArrayInputStream(svgData.getBytes("utf-8"));
      TranscoderInput input = new TranscoderInput(svgStream);

      PNGTranscoder transcoder = new PNGTranscoder();
      // Setup output
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      TranscoderOutput output = new TranscoderOutput(outStream);

      // Do the transformation
      transcoder.transcode(input, output);
      final byte[] result = outStream.toByteArray();
      repositoryService.addModelEditorSourceExtra(model.getId(), result);
      outStream.close();

    } catch (Exception e) {
      LOGGER.error("Error saving modelResource", e);
      throw new ActivitiException("Error saving modelResource", e);
    }
  }

  private boolean validKey(String key) {

    List<Model> models =  repositoryService.createModelQuery().list();
    Iterator<Model> iterator = models.iterator();

    while (iterator.hasNext()){
      Model model = iterator.next();
      if(key.equals(model.getKey())){
        return Boolean.FALSE;
      }
    }
    return Boolean.TRUE;
  }


}
