package com.cdeer.client.starter;

import org.apache.log4j.PropertyConfigurator;

import com.cdeer.client.AIOClientSingle;

public class IMClientStarter {

	public static void main(String[] args) {

		// 配置日志
		PropertyConfigurator.configure("cdeer_log4j.properties");

		AIOClientSingle client = new AIOClientSingle();
		client.createSocket(1);

		try {
			Thread.sleep(1000000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
