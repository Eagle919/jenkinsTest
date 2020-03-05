package com.caih.cloud.iscs.charge.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caih.cloud.iscs.charge.constants.SmartCommunityConstants;
import com.caih.cloud.iscs.charge.mapper.EBuildingMapper;
import com.caih.cloud.iscs.charge.mapper.ECommunityMapper;
import com.caih.cloud.iscs.charge.mapper.EMemberMapper;
import com.caih.cloud.iscs.charge.mapper.UserLoginMapper;
import com.caih.cloud.iscs.charge.model.entity.EBuilding;
import com.caih.cloud.iscs.charge.model.entity.ECommunity;
import com.caih.cloud.iscs.charge.model.entity.EMember;
import com.caih.cloud.iscs.charge.model.entity.UserLogin;
import com.caih.cloud.iscs.charge.model.vo.ESocketVo;
import com.caih.cloud.iscs.charge.service.SmartCommunityService;
import com.caih.cloud.iscs.common.Pair;
import com.caih.cloud.iscs.charge.utils.SmartCommunityUtils;
import com.github.pig.common.util.sherry.HttpWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class SmartCommunityServiceImpl implements SmartCommunityService {

    @Resource
    private ECommunityMapper communityMapper;

    @Resource
    private EMemberMapper memberMapper;

    @Resource
    private EBuildingMapper buildingMapper;

    @Resource
    private UserLoginMapper userLoginMapper;


    @Override
    public Pair<Object, String> checkUser(String token) {

        Pair<Object, String> pair = new Pair<>();

//        token = "Q2RkSG9kY09sVGtkeFVLa0hvNHc1VExOQmxuQ0VGa25tOTRCQW9qcXdqZmNvQWtHYklRWUV3PT0";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String smartAppId = SmartCommunityConstants.SMART_APP_ID;
        String smartKey = SmartCommunityConstants.SMART_KEY;
        String appSecurityKey = SmartCommunityUtils.MD5EncodeFor16(smartAppId + smartKey + timestamp, "");

        //发送请求获取退款数据
        String url = SmartCommunityConstants.USER_INFO_URL;
        // 拼接参数
        url += "?appId=" + smartAppId;
        url += "&appSecurityKey=" + appSecurityKey;
        url += "&timestamp=" + timestamp;
        url += "&accessToken=" + token;
        String data = HttpWrapper.doPost(url, null);
//        log.info("远端获取数据【data=" + data + "】");

        if (data == null) {
            pair.setFirst(null);
            pair.setSecond("远端获取数据失败");
//            setDefaultMember(pair);
            return pair;
        }
        Map data4map = JSON.parseObject(data, Map.class);
//        log.info("远端获取数据【data4map=" + data4map + "】");
        assert data4map != null;
        Object obj4data = data4map.get("obj");
        if (obj4data == null) {
            pair.setFirst(null);
            pair.setSecond("远端获取数据失败");
//            setDefaultMember(pair);
            return pair;
        }

        if (obj4data.equals("")) {
            pair.setFirst(null);
            pair.setSecond("远端获取数据失败");
//            setDefaultMember(pair);
            return pair;
        }

        Map obj4map = JSON.parseObject(obj4data.toString(), Map.class);

        Object memberNo = obj4map.get("id");

        if (StringUtils.isEmpty(memberNo)) { //返回值,0为成功，其他为失败
            pair.setFirst(null);
            pair.setSecond("远端获取数据失败");
//            setDefaultMember(pair);
            return pair;
        }

        QueryWrapper<EMember> memberQW = new QueryWrapper<>();
        memberQW.eq("member_no", memberNo);
        Integer count = memberMapper.selectCount(memberQW);
        if (count == 1) { //存在用户信息
            pair.setFirst(memberMapper.selectOne(memberQW));
            pair.setSecond("用户信息已存在");
//            setDefaultMember(pair);
            return pair;
        } else if (count > 1) {
            pair.setFirst(null);
            pair.setSecond("用户信息数据异常");
//            setDefaultMember(pair);
            return pair;
        } else {
            {
                EMember member = new EMember();
                member.setMemberNo(memberNo.toString());
                member.setAvatar(obj4map.get("avatar") == null ? "" : obj4map.get("avatar").toString());
                member.setName(obj4map.get("fullName").toString());
                member.setTel(obj4map.get("tel") == null ? "" : obj4map.get("tel").toString());
                member.setMobileTel(obj4map.get("mobileTel") == null ? "" : obj4map.get("mobileTel").toString());
                member.setEmail(obj4map.get("email") == null ? "" : obj4map.get("email").toString());

                memberMapper.insert(member);

                //社区
                Object communityList4data = obj4map.get("communityList");
                List community4list = JSON.parseObject(communityList4data.toString(), List.class);

                //楼栋
                Object buildingList4data = obj4map.get("buildingList");
                List building4list = JSON.parseObject(buildingList4data.toString(), List.class);

                for (int i = 0; i < community4list.size(); i++) {
                    Map map4community = (Map) community4list.get(i);

                    String communityNo = map4community.get("id").toString();
                    QueryWrapper<ECommunity> communityQueryWrapper = new QueryWrapper<>();
                    communityQueryWrapper.eq("community_no", communityNo);
                    Integer count4community = communityMapper.selectCount(communityQueryWrapper);

                    if (count4community == 1) {
                        pair.setFirst(member);
                        pair.setSecond("社区信息已存在");
                    } else if (count4community > 1) {
                        pair.setFirst(null);
                        pair.setSecond("社区信息数据异常");
                    } else {
                        ECommunity community = new ECommunity();
                        community.setName(map4community.get("name").toString());
                        community.setCommunityNo(communityNo);
                        community.setUserNo(memberNo.toString());
                        //获取社区列表指定下标元素的社区编号设置给楼栋的社区编号从而绑定楼栋和社区之间的关系
                        Map map4build = (Map) building4list.get(i);
                        community.setBuildingNo(map4build.get("id").toString());
                        community.setRate("0.01");//费率默认0.01元/小时
                        community.setRemark("电车充电电费收取");//默认收费说明

                        communityMapper.insert(community);
                    }

                }

                for (Object o : building4list) {
                    Map map4building4list = (Map) o;
                    Object buildingObject = map4building4list.get("id");
                    QueryWrapper<EBuilding> buildingQW = new QueryWrapper<>();
                    buildingQW.eq("building_no", buildingObject.toString());
                    Integer count4building = buildingMapper.selectCount(buildingQW);
                    if (count4building == 1) { //存在信息
                        pair.setFirst(member);
                        pair.setSecond("楼栋信息已存在");
                    } else if (count4building > 1) {
                        pair.setFirst(null);
                        pair.setSecond("楼栋信息数据异常");
                    } else {//持久化社区信息
                        EBuilding building = new EBuilding();
                        building.setName(map4building4list.get("name").toString());
                        building.setBuildingNo(map4building4list.get("id").toString());
                        buildingMapper.insert(building);
                        pair.setFirst(member);
                        pair.setSecond("楼栋信息插入成功");
                    }
                }
//                setDefaultMember(pair);

                return pair;

            }
        }

    }

    @Override
    public Pair<Object, String> checkUser4Test(String id) {
        Pair<Object, String> pair = new Pair<>();
        EMember member = memberMapper.selectById(id);
        if (member == null) {
            pair.setFirst(null);
            pair.setSecond("远端获取数据失败");
        } else {
            pair.setFirst(member);
            pair.setSecond("远端获取数据成功");
        }
        return pair;
    }

    @Override
    public Pair<Object, String> checkUser4UserLogin(String token) {
        Pair<Object, String> pair = new Pair<>();

//        token = "Q2RkSG9kY09sVGtkeFVLa0hvNHc1VExOQmxuQ0VGa25tOTRCQW9qcXdqZmNvQWtHYklRWUV3PT0";


        QueryWrapper<UserLogin> uq = new QueryWrapper<>();
        uq.eq("token", token);
        UserLogin userLogin = userLoginMapper.selectOne(uq);

        System.out.println("JSON.toJSONString(userLogin) = " + JSON.toJSONString(userLogin));

        if (userLogin == null) {
            pair.setFirst(null);
            pair.setSecond("获取数据失败");
//            setDefaultMember(pair);
            return pair;
        }

        Map obj4map = JSON.parseObject(JSON.toJSONString(userLogin), Map.class);

        Object memberNo = obj4map.get("id");

        if (StringUtils.isEmpty(memberNo)) { //返回值,0为成功，其他为失败
            pair.setFirst(null);
            pair.setSecond("远端获取数据失败");
//            setDefaultMember(pair);
            return pair;
        }

        QueryWrapper<EMember> memberQW = new QueryWrapper<>();
        memberQW.eq("member_no", memberNo);
        Integer count = memberMapper.selectCount(memberQW);
        if (count == 1) { //存在用户信息
            pair.setFirst(memberMapper.selectOne(memberQW));
            pair.setSecond("用户信息已存在");
//            setDefaultMember(pair);
            return pair;
        } else if (count > 1) {
            pair.setFirst(null);
            pair.setSecond("用户信息数据异常");
//            setDefaultMember(pair);
            return pair;
        } else {
            {
                EMember member = new EMember();
                member.setMemberNo(memberNo.toString());
                member.setAvatar(obj4map.get("avatar") == null ? "" : obj4map.get("avatar").toString());
                member.setName(obj4map.get("fullName").toString());
                member.setTel(obj4map.get("tel") == null ? "" : obj4map.get("tel").toString());
                member.setMobileTel(obj4map.get("mobileTel") == null ? "" : obj4map.get("mobileTel").toString());
                member.setEmail(obj4map.get("email") == null ? "" : obj4map.get("email").toString());

                memberMapper.insert(member);

                //社区
                Object communityList4data = obj4map.get("communityList");
                List community4list = JSON.parseObject(communityList4data.toString(), List.class);

                //楼栋
                Object buildingList4data = obj4map.get("buildingList");
                List building4list = JSON.parseObject(buildingList4data.toString(), List.class);

                for (int i = 0; i < community4list.size(); i++) {
                    Map map4community = (Map) community4list.get(i);

                    String communityNo = map4community.get("id").toString();
                    QueryWrapper<ECommunity> communityQueryWrapper = new QueryWrapper<>();
                    communityQueryWrapper.eq("community_no", communityNo);
                    Integer count4community = communityMapper.selectCount(communityQueryWrapper);

                    if (count4community == 1) {
                        pair.setFirst(member);
                        pair.setSecond("社区信息已存在");
                    } else if (count4community > 1) {
                        pair.setFirst(null);
                        pair.setSecond("社区信息数据异常");
                    } else {
                        ECommunity community = new ECommunity();
                        community.setName(map4community.get("name").toString());
                        community.setCommunityNo(communityNo);
                        community.setUserNo(memberNo.toString());
                        //获取社区列表指定下标元素的社区编号设置给楼栋的社区编号从而绑定楼栋和社区之间的关系
                        Map map4build = (Map) building4list.get(i);
                        community.setBuildingNo(map4build.get("id").toString());
                        community.setRate("1");//费率默认1元/小时
                        community.setRemark("电车充电电费收取");//默认收费说明

                        communityMapper.insert(community);
                    }

                }

                for (Object o : building4list) {
                    Map map4building4list = (Map) o;
                    Object buildingObject = map4building4list.get("id");
                    QueryWrapper<EBuilding> buildingQW = new QueryWrapper<>();
                    buildingQW.eq("building_no", buildingObject.toString());
                    Integer count4building = buildingMapper.selectCount(buildingQW);
                    if (count4building == 1) { //存在信息
                        pair.setFirst(member);
                        pair.setSecond("楼栋信息已存在");
                    } else if (count4building > 1) {
                        pair.setFirst(null);
                        pair.setSecond("楼栋信息数据异常");
                    } else {//持久化社区信息
                        EBuilding building = new EBuilding();
                        building.setName(map4building4list.get("name").toString());
                        building.setBuildingNo(map4building4list.get("id").toString());
                        buildingMapper.insert(building);
                        pair.setFirst(member);
                        pair.setSecond("楼栋信息插入成功");
                    }
                }
//                setDefaultMember(pair);

                return pair;

            }
        }
    }

    @Override
    public List getDeviceStatus(String deviceNo) {
        //发送请求获取退款数据
        String url = SmartCommunityConstants.CHARGE_DEVICE_STATUS;
        // 拼接参数
        url += "?deviceCode=" + deviceNo;
        String data = HttpWrapper.getRequest(url); //{"code":200,"msg":"操作成功","data":null}
        //解析data 封装list
        Map map = JSON.parseObject(data, Map.class);
        if (map == null) {
            return null;
        }
        Object data4data = map.get("data");
        log.info("远端获取的插座data【" + data4data + "】");
        log.info("getDeviceStatus url = 【" + url + "】");
        if (data4data == null) {
            return null;
        }
        return JSON.parseObject(data4data.toString(), List.class);

    }

    @Override
    public String startCharge(String deviceNo, Integer portNo, int timeDifference) {
//        log.info("参数:【deviceNo=" + deviceNo + "】|【portNo=" + portNo + "】|【timer=" + timeDifference);
        if (timeDifference < 0) return null;
//        log.info("参数:【deviceNo=" + deviceNo + "】|【portNo=" + portNo + "】|【timeDifference=" + timeDifference + "】");
        //发送请求获取退款数据
        String url = SmartCommunityConstants.START_CHARGE;
        // 拼接参数
        url += "?port=" + portNo;
        url += "&&deviceCode=" + deviceNo;
        url += "&&timer=" + timeDifference;
        String data = HttpWrapper.getRequest(url);
        log.info("远端获取的startChargeURL【" + url + "】");
        log.info("远端获取的startChargeData【" + data + "】");
        return data;
    }

    @Override
    public String stopCharge(String deviceNo, Integer portNo) {
//        log.info("参数:【deviceNo=" + deviceNo + "】|【portNo=" + portNo + "】");
        //发送请求获取退款数据
        String url = SmartCommunityConstants.STOP_CHARGE;
        // 拼接参数
        url += "?port=" + portNo;
        url += "&&deviceCode=" + deviceNo;
        String data = HttpWrapper.getRequest(url);
        log.info("远端获取的stopChargeURL【" + url + "】");

        //http://admin.godel.vip/sendStopChargeMessage?port=1&&deviceCode=10002998
        //http://admin.godel.vip/sendStopChargeMessage?port=9&&deviceCode=10002998
        log.info("远端获取的stopChargeData【" + data + "】");
        return data;
    }

/*    private void setDefaultMember(Pair<Object, String> pair) {
        //todo 数据库必须有条数据 当获取不到远程用户信息时
        if (pair.getFirst() == null) {
            List<EMember> members = memberMapper.selectList(null);
            EMember member1 = members.get(0);
            if (member1 != null)
                pair.setFirst(member1);
        }
    }*/

    public static void main(String[] args) {
        String json = "{\"code\":200,\"msg\":\"操作成功\",\"data\":[{\"status\":\"1\",\"port\":0},{\"status\":\"1\",\"port\":1},{\"status\":\"1\",\"port\":2},{\"status\":\"1\",\"port\":3},{\"status\":\"1\",\"port\":4},{\"status\":\"1\",\"port\":5},{\"status\":\"1\",\"port\":6},{\"status\":\"1\",\"port\":7},{\"status\":\"1\",\"port\":8},{\"status\":\"1\",\"port\":9}]}";
        Map map = JSON.parseObject(json, Map.class);
        Object data4data = map.get("data");
        List list = JSON.parseObject(data4data.toString(), List.class);
        log.info("远端获取的插座data【" + data4data + "】");
        System.out.println("JSON.parseObject(data4data.toString(), List.class) = " + list);


    }
}
