package server79;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

public interface NonReg {
    void parseId(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception;
    void queryRegInfo(ChannelHandlerContext ctx, DatagramPacket msg)throws Exception;
    void register(ChannelHandlerContext ctx, DatagramPacket msg)throws Exception;
    void updateIp(ChannelHandlerContext ctx, DatagramPacket msg)throws Exception;
    void cancel(ChannelHandlerContext ctx, DatagramPacket msg)throws Exception;
}
