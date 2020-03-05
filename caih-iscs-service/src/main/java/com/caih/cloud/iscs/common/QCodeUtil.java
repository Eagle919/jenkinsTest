package com.caih.cloud.iscs.common;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class QCodeUtil {
    /*
     * 定义二维码的宽高
     */
    private static int WIDTH = 300;
    private static int HEIGHT = 300;
    private static String FORMAT = "png";//二维码格式

    //生成二维码（保存至OutputStream）
    public static ByteArrayOutputStream createZxingqrCode(String content, ByteArrayOutputStream baop) {
        //定义二维码参数
        Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");//设置编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//设置容错等级
        hints.put(EncodeHintType.MARGIN, 2);//设置边距默认是5

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
            MatrixToImageWriter.writeToStream(bitMatrix, FORMAT, baop);
            /**
            //写到指定路径下
            Path path = new File("D:\\qr.png").toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, FORMAT, path);
            **/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baop;
    }

    //读取二维码（从文件）
    public static void readZxingQrCode() {
        MultiFormatReader reader = new MultiFormatReader();
        File file = new File("E:\\qr.png");
        try {
            BufferedImage image = ImageIO.read(file);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");//设置编码
            Result result = reader.decode(binaryBitmap, hints);
            System.out.println("解析结果:" + result.toString());
            System.out.println("二维码格式:" + result.getBarcodeFormat());
            System.out.println("二维码文本内容:" + result.getText());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Map treeMap = new TreeMap();
        treeMap.put("cusid","990611048166001");
        treeMap.put("appid","00009447");
        treeMap.put("appkey","allinpay1234");
        treeMap.put("trxamt","1");
        treeMap.put("reqsn","2019080800001");
        treeMap.put("charset","UTF-8");
        treeMap.put("returl","http://www.baidu.com");
        treeMap.put("notify_url","http://xtest.allinpaygx.com/wx/cash/notifytest");
        treeMap.put("body","orderTitleName");
        treeMap.put("remark","orderRemark");

        //http://xtest.allinpaygx.com/wx/cash/externalorder/unionorder?cusid=990611048166001&appid=00009447&appkey=allinpay1234&trxamt=1&reqsn=2019080800001&charset=UTF-8&returl=http://www.baidu.com&notify_url=http://xtest.allinpaygx.com/wx/cash/notifytest&body=orderTitleName &remark=orderRemark&sign=DA592D21EB41D903F93B810DD9578820
    }
}
