package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息编码器
 */
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {

    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg == null || !(msg instanceof GeneratedMessageV3)) {
            super.write(ctx, msg, promise);
            return;
        }

//        int msgCode = -1;
//        if (msg instanceof GameMsgProtocol.UserEntryResult) {
//            msgCode = GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
//        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereResult) {
//            msgCode = GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;
//        } else if (msg instanceof GameMsgProtocol.UserMoveToResult) {
//            msgCode = GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE;
//        }else if (msg instanceof GameMsgProtocol.UserQuitResult){
//            msgCode = GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE;
//        }else {
//
//            return;
//        }
        // 获取消息编码
        int msgCode = GameMsgRecognizer.getMsgCodeByClazz(msg.getClass());
        if (msgCode <= -1){
            LOGGER.error("无法识别消息类型，msgClazz = " + msg.getClass().getName());
            return;
        }

        byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeShort((short) 0); // 写出消息长度，0只是为了占位
        byteBuf.writeShort((short) msgCode); // 写出消息编号
        byteBuf.writeBytes(msgBody); // 写出消息体

        // 编码后的消息写出
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
        super.write(ctx, frame, promise);
    }
}
