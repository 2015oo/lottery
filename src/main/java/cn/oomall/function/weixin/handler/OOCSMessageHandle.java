package cn.oomall.function.weixin.handler;

import com.fastweixin.handle.MessageHandle;
import com.fastweixin.message.BaseMsg;
import com.fastweixin.message.CustomeServiceMsg;
import com.fastweixin.message.RespType;
import com.fastweixin.message.req.BaseReqMsg;

/**
 * 转发消息到多客服
 * 
 * @author allen.liu
 * 
 */
public class OOCSMessageHandle implements MessageHandle {

	@Override
	public boolean beforeHandle(BaseReqMsg message) {
		// 当消息是文本 图像 录音 视频 转发多客服
		if (message.getMsgType().equals(RespType.TEXT)
				|| message.getMsgType().equals(RespType.IMAGE)
				|| message.getMsgType().equals(RespType.VOICE)
				|| message.getMsgType().equals(RespType.VIDEO)) {
			return true;
		}
		return false;
	}

	@Override
	public BaseMsg handle(BaseReqMsg message) {
		BaseMsg msg = new CustomeServiceMsg();
		return msg;
	}
}
