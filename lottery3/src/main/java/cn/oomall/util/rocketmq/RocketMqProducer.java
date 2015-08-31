/*
 * @(#) test.java Create on 2015年8月7日 下午5:58:25
 *
 * Copyright 2015 .ooyanjing
 */

package cn.oomall.util.rocketmq;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.message.Message;

@Component
@SuppressWarnings("all")
public class RocketMqProducer {

	@Value("${mq.producer.uri}")
	private String rocketMqURI;
	
	@Value("${mq.producer.group}")
	private String producerName;
	
	@Value("${mq.producer.topic}")
	private String topic;
	
	@Value("${mq.producer.tags}")
	private String tags;

	private DefaultMQProducer producer;
	
	private static boolean flag = false;

	@PostConstruct
	public void init() {
		if(flag){
			return;
		}
		flag = true;
		System.out.println("mq producer init...");
		System.out.println(rocketMqURI);
		System.out.println(producerName);
		System.out.println(topic);
		System.out.println(tags);
		this.producer = new DefaultMQProducer(producerName);
		producer.setNamesrvAddr(rocketMqURI);
		try {
			producer.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				producer.shutdown();
			}
		});
	}

	public void rocketMqSend(String content) {
		try {
			if(producer!=null){
				Message msg = new Message(topic, tags,content.getBytes());
	//			SendResult result = producer.send(msg);
				producer.sendOneway(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
