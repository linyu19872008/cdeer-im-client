package com.cdeer.client;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cdeer.client.aio.AIOConnectHandler;
import com.cdeer.client.manager.ConstantManager;

public class AIOClientSingle {

	public static int totalCount = 0;// 创建的总客户端数量

	/**
	 * 日志
	 */
	private final Logger Log = LoggerFactory.getLogger(getClass());

	private AsynchronousChannelGroup asyncChannelGroup;

	private String localIP;

	public AIOClientSingle() {
		try {
			ExecutorService executor = Executors
					.newFixedThreadPool(40);
			asyncChannelGroup = AsynchronousChannelGroup
					.withThreadPool(executor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开启连接
	 */
	public void createSocket(int userId) {

		try {
			AsynchronousSocketChannel connector = null;
			if (connector == null || !connector.isOpen()) {
				connector = AsynchronousSocketChannel.open(asyncChannelGroup);
				// connector.bind(localAddress);
				connector.setOption(StandardSocketOptions.TCP_NODELAY, true);
				connector.setOption(StandardSocketOptions.SO_REUSEADDR, true);
				connector.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

				AIOConnectHandler connection = new AIOConnectHandler(52,
						"abcssssssssssssssss2344");
				connector.connect(new InetSocketAddress(
						ConstantManager.SERVER_HOST,
						ConstantManager.SERVER_PORT), connector, connection);
			}
		} catch (Exception e) {
			Log.error(e.getMessage() + "[localIP:" + localIP + "]", e);
		}

	}
	
}
