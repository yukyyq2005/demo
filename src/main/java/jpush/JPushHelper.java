package jpush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
//import djoa.app.server.core.cache.RedisPoolManager;
//import djoa.app.server.core.utils.MessageService;
//import redis.clients.jedis.Jedis;

public class JPushHelper {
	//private MessageService messageService;
	private static final Logger logger = LoggerFactory.getLogger(JPushHelper.class);
	public static Map<String, Integer> msgCount = new HashMap<String, Integer>();
	public static boolean isUserMq = true; 
	public void push(JSONObject obj) {
		logger.debug(obj.toJSONString());

		JSONObject user = JSON.parseObject(obj.getString("user"));
		String content = null;
		String m_user_id = obj.getString("to");
		String msgType = obj.getString("msg_type");
		String talkid = user.getString("talkid");
		String talkType = user.getString("talktype");
		if (("2".equals(talkType) || "3".equals(talkType)) && talkid != null) {
			//Jedis jedis = RedisPoolManager.createInstance();
//			try {
//				// 查找群，讨论组推送设置
//				if (jedis != null) {
//					String res = jedis.get(m_user_id + "_" + user.getString("talkid"));
//					if (res == null) {
//						res = messageService.getUserConf(m_user_id, Long.valueOf(talkid), talkType);
//						if (res == null)
//							res = "0";
//						jedis.set(m_user_id + "_" + user.getString("talkid"), res);
//					}
//					if ("1".equals(res))
//						return;
//				}
//			} catch (Exception e) {
//				logger.error(e.getMessage());
//			}finally {
//				if(jedis!=null)RedisPoolManager.returnResource(jedis);
//			}
			
		}
		
		if ("10".equals(msgType)) {
			content = obj.getString("body");
			//content = content.replaceAll("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]","[emoji表情]"); // 过滤emoji表情
			content = content.replaceAll("\\{[0-9][0-9][0-9]\\}", "[表情]");
		} else if ("13".equals(msgType) || "11".equals(msgType) || "15".equals(msgType)) {
			if ("13".equals(msgType)) {
				content = "[文件]";
			} else {
				content = "[图片]";
			}
		} else if ("12".equals(msgType)) {
			content = "[语音]";
		} else if ("14".equals(msgType)) {
			content = "[定位]";
		} else if ("16".equals(msgType)) {
			content = "[审批]";
		}
		String sendName = null;
		if ("10".equals(msgType) || "11".equals(msgType) || "12".equals(msgType) || "13".equals(msgType)
				|| "14".equals(msgType) || "15".equals(msgType)|| "16".equals(msgType)) {
			if (user != null)
				sendName = user.getString("nick");
			try {
				logger.info("====开始远程推送====content=" + obj.toJSONString() + ",m_user_id=" + m_user_id);
				List<String> audiences = new ArrayList<String>();
				if (m_user_id != null) {
					audiences.add(m_user_id);
				}
				Integer count = null;
				if (msgCount.get(m_user_id) != null) {
					count = msgCount.get(m_user_id);
					msgCount.remove(m_user_id);
				}
				String contents;
				if (sendName == null || sendName == null) {
					contents = "您收到一条新消息";
				} else {
					contents = sendName + ":" + content;
				}
				JGMsgUtil.sendMsg("all", audiences, contents, count, msgType);
				logger.info("====远程推送结束====");
			} catch (Exception e) {
				logger.error("远程推送发生错误：", e);
			}
		}
	}
	public static void serverPush(String receveId,String content) {

		List<String> audiences = new ArrayList<String>();
		if (receveId != null) {
			audiences.add(receveId);
		}
		Integer count = null;
		if (msgCount.get(receveId) != null) {
			count = msgCount.get(receveId);
			msgCount.remove(receveId);
		}
		JGMsgUtil.sendMsg("all", audiences, content, count, null);
	}
	//public MessageService getMessageService() {
		//return messageService;
	//}
	//public void setMessageService(MessageService messageService) {
	//	this.messageService = messageService;
	//}
}
