package com.cdeer.client.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cdeer.protobuf.CdeerMsg.LoginInfo;
import com.cdeer.protobuf.CdeerMsg.Message;
import com.cdeer.utils.ThreeDES;

/**
 * AIO消息发送管理器
 * 
 * @author jacklin
 * 
 */
public class AIORouterManager {

	/**
	 * 日志
	 */
	private static final Logger Log = LoggerFactory
			.getLogger(AIORouterManager.class);

	/**
	 * 发送心跳响应
	 * 
	 * @param des
	 * @param socket
	 */
	public static void routePong(ThreeDES des, AsynchronousSocketChannel socket) {

		Message.Builder msg = Message.newBuilder();
		msg.setHeader(201);

		Message msgSend = msg.build();

		routeDirect(des, socket, msgSend);

	}

	/**
	 * 发送登录消息
	 * 
	 * @param des
	 * @param socket
	 * @param userId
	 * @param token
	 */
	public static void routeLoginRequest(ThreeDES des,
			AsynchronousSocketChannel socket, int userId, String token) {

		LoginInfo.Builder loginInfo = LoginInfo.newBuilder();
		loginInfo.setUserId(userId);
		loginInfo.setToken(token);
		loginInfo.setPlatform("ios");
		loginInfo.setAppVersion("3.2.2");

		Message.Builder msg = Message.newBuilder();
		msg.setHeader(101);
		msg.setLoginInfo(loginInfo);

		Message msgSend = msg.build();

		routeDirect(des, socket, msgSend);

	}

	/**
	 * 直接发送消息
	 * 
	 * @param socket
	 * @param msgSend
	 */
	public static void routeDirect(ThreeDES des,
			AsynchronousSocketChannel socket, Message msgSend) {
		try {
			byte[] bareByte = msgSend.toByteArray();

			// 消息加密
			byte[] encryptByte = des.encrypt(bareByte);
			int length = encryptByte.length;

			ByteBuffer buf = ByteBuffer.allocate(2 + length);
			short hand = (short) (length);
			buf.putShort(hand);
			buf.put(encryptByte);
			buf.flip();
			socket.write(buf);

			Log.info("[SEND][total length:" + length + "][bare length:"
					+ msgSend.getSerializedSize() + "]:\r\n"
					+ msgSend.toString());

		} catch (Exception e) {
			Log.error(e.getMessage(), e);
		}
	}

}
