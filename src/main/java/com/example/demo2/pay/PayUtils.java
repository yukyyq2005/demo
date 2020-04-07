package com.example.demo2.pay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class PayUtils {
	private Logger logger = LoggerFactory.getLogger(getClass());
	public String pay(String product_type, Double price, int num, String remotePath,String receiver) {
		try {
			String appsecret     = "160130736b1ac0d54ed7abe51e44840b";//测试账户，微信支付的secret
//			String appsecret     = "27a8ea66ba311bb5eeab30f998ae4041";//测试账户，支付宝支付的secret
			Double total_fee = price*num;
			Map<String, String> sortParams = new HashMap<String, String>();
			String trade_order_id = UUID.randomUUID().toString().replace("-", "");
			sortParams.put("version", "1.1");//固定值，api 版本，目前暂时是1.1
//			sortParams.put("lang", "zh-cn");//必须的，zh-cn或en-us 或其他，根据语言显示页面
			sortParams.put("plugins", "");//可选。备注字段，可以传入一些备注数据，回调时原样返回
//			sortParams.put("appid", "xxx");
			sortParams.put("appid", "2147483647");//微信支付的appid
//			sortParams.put("appid", "201906122733");//支付宝支付的appid
//			sortParams.put("redirect", "Y");
			sortParams.put("trade_order_id", trade_order_id);//必须的，网站订单ID，唯一的，匹配[a-zA-Z\d\-_]+
			sortParams.put("payment", "alipay");//必须的，支付接口标识：wechat(微信接口)|alipay(支付宝接口)
			sortParams.put("total_fee", String.valueOf(total_fee));//人民币，单位精确到分(测试账户只支持0.1元内付款)
			sortParams.put("title", product_type+"_账号");//必须的，订单标题，长度32或以内
			sortParams.put("time", new Date().getTime()/1000+"");//必须的，当前时间戳，根据此字段判断订单请求是否已超时，防止第三方攻击服务器
			sortParams.put("callback_url","https://www.xunhupay.com");//必须的，支付发起地址（未支付或支付失败，系统会会跳到这个地址让用户修改支付信息）

			//必填。用户支付成功后，我们服务器会主动发送一个post消息到这个网址(注意：当前接口内，SESSION内容无效)
//			sortParams.put("notify_url", "https://www.xunhupay.com/autoSend/returnPay");
			sortParams.put("notify_url", "http://yw.smartlife-go.com/warehouse/payCallback");

			//可选。用户支付成功后，我们会让用户浏览器自动跳转到这个网址
			sortParams.put("return_url", "https://www.xunhupay.com");

			sortParams.put("nonce_str", "124876359");//必须的，随机字符串，必填。作用：1.避免服务器页面缓存，2.防止安全密钥被猜测出来
			sortParams.put("modal", "full");//可空，支付模式 ，可选值( full:返回完整的支付网页; qrcode:返回二维码; 空值:返回支付跳转链接)
			sortParams.put("hash", createSign(sortParams,appsecret));
			logger.info("开始调三方接口:\n {}",JSON.toJSONString(sortParams));
			String response = HttpUtils.httppostjson("https://api.xunhupay.com/payment/do.html", JSON.toJSONString(sortParams));
			logger.info("调三方接口结束");
			JSONObject jsonObject = JSONObject.parseObject(response);

			if (jsonObject != null) {
				Integer errorcode = jsonObject.getInteger("errcode");
				String errmsg = jsonObject.getString("errmsg");
				if (errorcode == 0 && StringUtils.equals(errmsg, "success!")) {
					logger.info("返回跳转连接成功");
//					String sql = "insert into rm_order ( trade_order_id, product_type,product_price,product_path,product_cnt,receiver,state) " +
//											" values " +
//								 			"(?,?,?,?,?,?,?)";
//					List<Object[]> batchArgs = new ArrayList<Object[]>();
//					batchArgs.add(new Object[]{trade_order_id,product_type,price,remotePath,num,receiver,0});
//					jdbcTemplate.batchUpdate(sql, batchArgs);
					logger.info(trade_order_id+"---订单已存入");
					jsonObject.put("trade_order_id",trade_order_id);
//					return jsonObject.getString("url_qrcode");
					return jsonObject.toString();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public String createSign(Map<String, String> params, String privateKey) {
		StringBuilder sb = new StringBuilder();
		// 将参数以参数名的字典升序排序
		Map<String, String> sortParams = sortMapByKey(params);
		// 遍历排序的字典,并拼接"key=value"格式
		for (Map.Entry<String, String> entry : sortParams.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().trim();
			if (StringUtils.isNotEmpty(value)) {
				sb.append("&").append(key).append("=").append(value);
			}
		}

		String stringA = sb.toString().replaceFirst("&", "");
		System.out.println("stringA: " + stringA);
//		byte[] stringSignTemp = (stringA + privateKey).getBytes();
		String stringSignTemp = stringA + privateKey;
		String signValue = "";
		try {
			signValue = MD5Encoder.encode(stringSignTemp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return signValue;
	}

	public Map<String, String> sortMapByKey(Map<String, String> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		Map<String, String> sortMap = new TreeMap(new MapKeyComparator());
		sortMap.putAll(map);
		return sortMap;
	}

	public class MapKeyComparator implements Comparator<String> {
		public int compare(String str1, String str2) {
			return str1.compareTo(str2);
		}
	}


}
