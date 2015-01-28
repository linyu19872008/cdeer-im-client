package com.cdeer.client.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cdeer.utils.ThreeDES;

public class AIOConnectHandler implements CompletionHandler {

	/**
	 * 日志
	 */
	private final Logger Log = LoggerFactory.getLogger(getClass());

	private int userId;

	private String pwd;

	private ThreeDES des = new ThreeDES();

	public AIOConnectHandler(int userId, String pwd) {
		this.userId = userId;
		this.pwd = pwd;
	}

	public void startRead(AsynchronousSocketChannel socket) {
		try {
			ByteBuffer clientBuffer = ByteBuffer.allocate(10 * 1024);
			socket.read(clientBuffer, clientBuffer, new AIOReadHandler(socket,
					userId, des));
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
		}
	}

	@Override
	public void completed(Object result, Object attachment) {
		try {
			AsynchronousSocketChannel socket = (AsynchronousSocketChannel) attachment;
			
			AIORouterManager.routeLoginRequest(des, socket, userId, pwd);

			startRead(socket);
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
		}
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		Log.error(exc.getMessage(), exc);
	}

}
