package com.caih.cloud.iscs.charge.aasecretkey;

import com.alibaba.fastjson.JSONObject;
import com.caih.cloud.iscs.charge.utils.MD5Util;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.*;

@Data
@Component
@ConfigurationProperties(prefix = "wxpay")
public class MyWXPayConfig {

    private String appid;

    private String mch_id;

    private String key;

    private String certPath;

    private String callbackUrl;  // 回调地址用https nginx要特殊配置

    private Set<String> msgToClientParams = new TreeSet<String>() {{
        add("appid");
        add("noncestr");
        add("package");
        add("partnerid");
        add("prepayid");
        add("timestamp");
    }};

    public String createUnifiedOrder(String outTradeNo, Integer chargeMoney,
                                     String chargeMsg,String productId,String spbillCreateIp) {
        Map<String, String> keys = new TreeMap<>();
        keys.put("appid", appid);
        keys.put("product_id", productId);
        keys.put("mch_id", mch_id);
        long key = System.currentTimeMillis() * new Random().nextLong() + new Random().nextLong();
        String nonce_str = MD5Util.makeMD5(key + "");
        keys.put("nonce_str", nonce_str);                   // 5K8264ILTKCH16CQ2502SI8ZNMTM67VS
        keys.put("body", chargeMsg);                        // 自定义 腾讯充值中心-QQ会员充值
        keys.put("out_trade_no", outTradeNo);
        keys.put("total_fee", String.valueOf(chargeMoney)); // 单位是分
        keys.put("spbill_create_ip", spbillCreateIp);            // 127.0.0.1 ?
        keys.put("notify_url", callbackUrl);                // 回调地址
        keys.put("trade_type", "MWEB");                      // H5
        keys.put("sign", createSign(keys));
        return buildXML(keys);
    }


    public Map<String, String> parseWxXML(String ratXml) {
        Map<String, String> m = new TreeMap<>();
        try {
            DocumentBuilder db = createSafeBuilder();
            if (db == null)
                return Collections.emptyMap();

            Document parse = db.parse(new ByteArrayInputStream(ratXml.getBytes()));
            NodeList childNodes = parse.getChildNodes();
            if (childNodes.getLength() != 1)
                return Collections.emptyMap();

            Node xml = childNodes.item(0);
            NodeList keys = xml.getChildNodes();
            for (int i = 0; i < keys.getLength(); ++i) {
                Node key = keys.item(i);
                NodeList value = key.getChildNodes();
                if (value.getLength() != 1)
                    continue;
                Node item = value.item(0);
                m.put(key.getNodeName(), item.getNodeValue());
            }
        } catch (Exception e) {
            return Collections.emptyMap();
        }
        return m;
    }

    public String createMsgToClient(String wxResult) {
        if (StringUtils.isEmpty(wxResult))
            return null;
        Map<String, String> m = parseWxXML(wxResult);
        Map<String, String> result = new TreeMap<>();
        for (Map.Entry<String, String> it : m.entrySet()) {
            String key = it.getKey();
            switch (key) {
                case "mch_id":
                    key = "partnerid";
                    break;
                case "prepay_id":
                    key = "prepayid";
                    break;
                case "nonce_str":
                    key = "noncestr";
                    break;
            }
            if (!msgToClientParams.contains(key))
                continue;
            result.put(key, it.getValue());
        }
        if (!result.get("appid").equals(appid) || !result.get("partnerid").equals(mch_id))
            return null;
        result.put("package", "Sign=WXPay");
        result.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        if (result.size() != msgToClientParams.size())
            return null;
        result.put("sign", createSign(result));
        JSONObject js = new JSONObject();
        for (Map.Entry<String, String> kv : result.entrySet())
            js.put(kv.getKey(), kv.getValue());
        return js.toString();
    }

    public String createSign(Map<String, String> keys) {
        StringBuilder stringA = new StringBuilder();
        for (Map.Entry<String, String> it : keys.entrySet()) {
            stringA.append(it.getKey());
            stringA.append("=");
            stringA.append(it.getValue());
            stringA.append("&");
        }
        stringA.append("key=");
        stringA.append(key);
        return MD5Util.makeMD5(stringA.toString());
    }

    public String buildXML(Map<String, String> keys) {
        StringBuilder xml = new StringBuilder();
        xml.append("<xml>");
        for (Map.Entry<String, String> it : keys.entrySet()) {
            xml.append("<").append(it.getKey()).append(">");
            xml.append(it.getValue());
            xml.append("</").append(it.getKey()).append(">");
        }
        xml.append("</xml>");
        return xml.toString();
    }

    private static DocumentBuilder createSafeBuilder() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
            // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
            String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            dbf.setFeature(FEATURE, true);

            // If you can't completely disable DTDs, then at least do the following:
            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
            // JDK7+ - http://xml.org/sax/features/external-general-entities
            FEATURE = "http://xml.org/sax/features/external-general-entities";
            dbf.setFeature(FEATURE, false);

            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
            // JDK7+ - http://xml.org/sax/features/external-parameter-entities
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            dbf.setFeature(FEATURE, false);

            // Disable external DTDs as well
            FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            dbf.setFeature(FEATURE, false);

            // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);

            // And, per Timothy Morgan: "If for some reason support for inline DOCTYPEs are a requirement, then
            // ensure the entity settings are disabled (as shown above) and beware that SSRF attacks
            // (http://cwe.mitre.org/data/definitions/918.html) and denial
            // of service attacks (such as billion laughs or decompression bombs via "jar:") are a risk."

            // remaining parser logic
            return dbf.newDocumentBuilder();
        } catch (Exception e) {
            // This should catch a failed setFeature feature
            e.printStackTrace();
        }
        return null;
    }
}
