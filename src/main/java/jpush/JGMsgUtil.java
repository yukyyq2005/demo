package jpush;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

//import org.apache.commons.lang.StringUtils;
//import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.example.demo2.qn.PropertiesUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.jpush.api.JPushClient;
//import cn.jpush.api.common.resp.APIConnectionException;
//import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

public class JGMsgUtil {
	private static final Logger logger = LoggerFactory.getLogger(JGMsgUtil.class);
	private static String masterSecret = null;
	private static String appKey = null;
	private static boolean apnsProduction = false;
	static {
		masterSecret = PropertiesUtil.getValue("jg_master_secret");
		appKey = PropertiesUtil.getValue("jg_app_key");
		apnsProduction = Boolean.valueOf(PropertiesUtil.getValue("apns_production"));
//
//		ResourceBundle bundle = ResourceBundle.getBundle("conf.rabbit");
//		if (bundle == null)
//			throw new IllegalArgumentException("[rabbit.properties] is not found");
//		masterSecret = bundle.getString("jg_master_secret");
//		appKey = bundle.getString("jg_app_key");
//		apnsProduction = Boolean.valueOf(bundle.getString("apns_production"));
	}
	
	public static void sendMsg(String plat, List<String> audiences, String content, Integer count,
			String msgType) {

		JPushClient jpushClient = new JPushClient(masterSecret, appKey);
		if (plat == null) {
			plat = Platforms.all.getName();
		}
		Platform platform = null;

		Audience audience = null;
		Notification notification = null;
		if (plat.equals(Platforms.all.getName())) {
			platform = Platform.all();
			/*String contents = null;
			if (sendName == null || sendName == null) {
				contents = "您收到一条新消息";
			} else {
				contents = sendName + ":" + content;
			}*/
			// 是否清除提醒数
			if ("10".equals(msgType) || "11".equals(msgType) || "12".equals(msgType) || "13".equals(msgType)
					|| "14".equals(msgType) || "15".equals(msgType) || "4".equals(msgType) || "5".equals(msgType)) {
				if (count != null) {
					notification = Notification.newBuilder().addPlatformNotification(IosNotification.newBuilder()
							.setAlert(content).setSound("default").setBadge(count + 1).build()).build();
				} else {
					notification = Notification.newBuilder()
							.addPlatformNotification(
									IosNotification.newBuilder().setAlert(content).setSound("default").build())
							.build();
				}
			} else {
				if (count != null) {
					notification = Notification.newBuilder().addPlatformNotification(IosNotification.newBuilder()
							.setAlert(content).setSound("default").setBadge(count + 1).build()).build();
				} else {
					notification = Notification.newBuilder()
							.addPlatformNotification(
									IosNotification.newBuilder().setAlert(content).setSound("default").build())
							.build();
				}
				/*notification = Notification.newBuilder().addPlatformNotification(
						IosNotification.newBuilder().setAlert(content).setSound("default").disableBadge().build())
						.build();*/
			}

		}


		if (plat.equals(Platforms.ios.getName())) {
			platform = Platform.ios();
			notification = Notification.newBuilder()
					.addPlatformNotification(IosNotification.newBuilder().setAlert("新消息").disableBadge().build())
					.build();
		}
		if (plat.equals(Platforms.android.getName())) {
			platform = Platform.android();
			notification = Notification.newBuilder()
					.addPlatformNotification(AndroidNotification.newBuilder().setAlert("新消息").build()).build();
		}
		if (audiences == null) {
			audience = Audience.all();
		} else {
			audience = Audience.alias(audiences);
		}
		Options options = Options.newBuilder().setApnsProduction(apnsProduction).build();
		PushPayload pushPayload  = build(platform, audience, options, notification, content);
		try {
			logger.info("开始远程消息推送");
			PushResult result = jpushClient.sendPush(pushPayload);
			logger.info("Got result - " + result);
		} catch (Exception e) {
			logger.error("Should review the error, and fix the request", "推送错误");
		}
	}

	private static PushPayload build(Platform platform, Audience audience, Options options, Notification notification,
			String content) {
		return PushPayload.newBuilder().setPlatform(platform).setAudience(audience).setOptions(options)
				.setNotification(notification).setMessage(Message.content(content)).build();
	}

	public enum Platforms {
		all(1, "all"), ios(2, "ios"), android(3, "android");

		private int plat;
		private String name;

		private Platforms(int plat, String name) {
			this.plat = plat;
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public int getPlat() {
			return this.plat;
		}
	}

	public static JSONObject parseContent(String content) {
		if (!content.endsWith("\"}}")) {
			content = content + "\"}}";
		}
		JSONObject object = JSONObject.parseObject(content);
		return object;
	}

	private final static String QUEUE_NAME = "queue_rewrite";

	private static String exchanger = "rewrite_msg";
	private static String queuename = "queue_rewrite";
	private static String key = "im_to_rewrite";

	public static void main(String[] args) {

//		 sendMsg(null,null,"推送测试数据");

		//rabbitmq("");

		//sendMsg(null,null,"test1d",1,null);

	}

	//接收消息
	//使用RabbitMQ要去220.231.216.88的防火墙规则中添加5672端口
	public static void connectAndRecvRabbitMQMsg(String str){
		QueueingConsumer consumer = null;
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("220.231.216.88");
		factory.setPort(5672);
		factory.setUsername("mqmanager");
		factory.setPassword("123456");
		Connection connection = null;
		try {
			connection = factory.newConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Channel channel = null;
		try {
			channel = connection.createChannel();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// channel.exchangeDeclare(EXCHANGER, TYPE);
		try {
			channel.queueBind(queuename, exchanger, key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("启动MQ接收[*] Waiting for messages. To exit press CTRL+C");
		consumer = new QueueingConsumer(channel);
		// 指定接收者，第二个参数为自动应答，无需手动应答
		try {
			channel.basicConsume(queuename, true, consumer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("RabbitMQ链接成功");

		while (true) {
			QueueingConsumer.Delivery delivery = null;
			String message = null;
			try {
				delivery = consumer.nextDelivery();
				message = new String(delivery.getBody(), "UTF-8");
				logger.info("收到消息：" + message);

				sendMsg(null,null,message,1,null);//极光推送

				JSONObject msgObj = null;
//				JSONObject msgObj = getMessage(message);
				if (msgObj != null) {
					// 极光推送
					if (1 == msgObj.getIntValue("jpush")) {
//						jPushHelper.push(msgObj);
					} else {
						// 存储消息
						JSONObject userObj = msgObj.getJSONObject("user");
						if (userObj != null) {
							logger.debug("保存消息：" + message);
							String userId = userObj.getString("userid");
							String talkid = userObj.getString("talkid");
							String talktype = userObj.getString("talktype");
//							if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(talkid)
//									&& StringUtils.isNotEmpty(talktype))
//								messageService.sendMessage(msgObj, Long.valueOf(userId),
//										Long.valueOf(talkid), Byte.valueOf(talktype));
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					logger.error(e1.toString());
				}
			}
		}
	}
	//发送消息
	public static void SendRabbitMqMsg(String str){
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("220.231.216.88");
		factory.setUsername("mqmanager");
		factory.setPassword("123456");
		factory.setPort(5672);
		Connection connection = null;
		try {
			connection = factory.newConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Channel channel = null;
		try {
			channel = connection.createChannel();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = "Hello World!";
		try {
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(" [x] Sent '" + message + "'");
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
