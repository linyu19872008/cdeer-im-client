package com.cdeer.client.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cdeer.protobuf.CdeerMsg.Message;
import com.cdeer.utils.ThreeDES;

public class AIOReadHandler implements CompletionHandler {

	/**
	 * 日志
	 */
	private final Logger Log = LoggerFactory.getLogger(AIOReadHandler.class);

	// public JsonProcessorAIO jsonProcessorAIO = new JsonProcessorAIO();

	private AsynchronousSocketChannel socket;

	private ThreeDES des;

	private long userId;

	public AIOReadHandler(AsynchronousSocketChannel socket, long userId,
			ThreeDES des) {
		this.socket = socket;
		this.userId = userId;
		this.des = des;
	}

	public void cancelled(ByteBuffer attachment) {
		Log.error("cancelled");
	}

	@Override
	public void completed(Object result, Object attachment) {
		try {
			Integer i = (Integer) result;
			ByteBuffer buf = (ByteBuffer) attachment;

			if (i > 0) {
				buf.flip();
				try {
					// String msg = decoder.decode(buf).toString();
					// jsonProcessorAIO.handleMsg(socket, msg);
					buf.mark();
					// 读取传送过来的消息的长度。
					int length = buf.getShort();

					// 长度如果小于0
					if (length < 0) {// 非法数据，关闭连接
						return;
					}

					if (length > buf.remaining()) {// 读到的消息体长度如果小于传送过来的消息长度
						// 重置读取位置
						buf.reset();
						return;
					}

					byte[] msgByte = new byte[length];
					buf.get(msgByte);
					buf.compact();

					try {
						byte[] bareByte = des.decrypt(msgByte);

						Message hahaMsg = Message.parseFrom(bareByte);

						Log.info("[RECV][total length:" + msgByte.length
								+ "][bare length:"
								+ hahaMsg.getSerializedSize() + "]:\r\n"
								+ hahaMsg.toString());

						if (hahaMsg != null) {
							handleMsg(hahaMsg);
						}
						// }
					} catch (Exception ex) {
						Log.error(
								socket.getRemoteAddress() + "decode Failure.",
								ex);
					}

				} catch (Exception e) {
					Log.error(e.getMessage(), e);
				}
				socket.read(buf, buf, this);
			} else if (i == -1) {
				// 服务器断开连接
				Log.error("对端断线:" + socket.getRemoteAddress().toString());
				buf = null;

			}
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
		}

	}

	/**
	 * 处理消息
	 * 
	 * @param hahaMsg
	 */
	private void handleMsg(Message hahaMsg) {
		
		if (hahaMsg.getHeader()== 200) {
			// 收到心跳请求,返回心跳响应
			AIORouterManager.routePong(des, socket);
		}
		
	}

	@Override
	public void failed(Throwable e, Object attachment) {
		Log.error(e.getMessage(), e);
		// 服务器断开连接
		// StatsManager.USER_ONLINE.getAndDecrement();

	}
}
