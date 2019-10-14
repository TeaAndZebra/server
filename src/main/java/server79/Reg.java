package server79;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

public interface Reg {
    void sinRoute(ChannelHandlerContext ctx, DatagramPacket msg)throws Exception;
    void multiRoute(ChannelHandlerContext ctx, DatagramPacket msg)throws Exception;
    void reflect(ChannelHandlerContext ctx, DatagramPacket msg)throws Exception;
    void getSpeedOfUser(ChannelHandlerContext ctx, DatagramPacket msg)throws Exception;
    void getBitsOfUser(ChannelHandlerContext ctx, DatagramPacket msg)throws Exception;
    void getNumOfUser(ChannelHandlerContext ctx, DatagramPacket msg)throws Exception;
}
