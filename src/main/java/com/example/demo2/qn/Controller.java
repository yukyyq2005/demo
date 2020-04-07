package com.example.demo2.qn;

import com.example.demo2.pay.PayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
public class Controller {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@GetMapping("/test")
    public Object test() {
		String string = PropertiesUtil.getValue("server.interface");
		System.out.println(string);
		return "hello world 8802";
	}
	//json 和表单请求都支持
	@RequestMapping(value = "/payCallback", method = RequestMethod.POST)
	private Object test3(@RequestBody String jsonStr) {
		try {
			logger.info("调三方回调---支付结果");
			logger.info(jsonStr);
			return "{\"errcode\":0,\"errmsg\":\"success!\"}\n";
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"errcode\":-1,\"errmsg\":\"failure!\"}\n";
		}
	}
	@ResponseBody
	@RequestMapping(value = "/pay", method = RequestMethod.POST)
	public Object pay(@RequestBody String jsonStr, HttpEntity<Object> entity, HttpServletRequest request) throws Exception {
		PayUtils pay = new PayUtils();
		String string = pay.pay("1",0.02,1,"1","d");
		return string;
	}


	//不支持表单
	@ResponseBody
	@RequestMapping(value = "/payCallback11", method = RequestMethod.POST)
	public Object entrance(@RequestBody String jsonStr, HttpEntity<Object> entity, HttpServletRequest request) throws Exception {
		JSONObject params = JSONObject.parseObject(jsonStr);
		logger.info(params.toJSONString());
//		handleJson(jsonStr, request);
//		return JSON.toJSONString(JSON.parseObject("{\"code\":100,\"msg\":\"成功\"}"));
		return jsonStr;
	}


	private Map<String, String> getParamString(HttpServletRequest request){
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
			}
		}
		return res;
	}
}
