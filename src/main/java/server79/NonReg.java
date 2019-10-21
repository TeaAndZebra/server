package server79;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

public interface NonReg {
    void parseId(ChannelHandlerContext ctx) throws Exception;
    void queryRegInfo(ChannelHandlerContext ctx)throws Exception;
    void register(ChannelHandlerContext ctx)throws Exception;
    void updateIp(ChannelHandlerContext ctx)throws Exception;
    void cancel(ChannelHandlerContext ctx)throws Exception;
}
