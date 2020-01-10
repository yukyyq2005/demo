package com.example.demo2.qn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

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
	@ResponseBody
	@RequestMapping(value = "/entrance", method = RequestMethod.POST)
	public Object entrance(@RequestBody String jsonStr, HttpEntity<Object> entity, HttpServletRequest request) throws Exception {
		JSONObject params = JSONObject.parseObject(jsonStr);
		logger.info(params.toJSONString());
//		handleJson(jsonStr, request);
//		return JSON.toJSONString(JSON.parseObject("{\"code\":100,\"msg\":\"成功\"}"));
		return jsonStr;
	}
}
