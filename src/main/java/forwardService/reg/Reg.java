package forwardService.reg;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import forwardService.User;

public interface Reg {
    void sinRoute(ChannelHandlerContext ctx, DatagramPacket msg, User user)throws Exception;
    void multiRoute(ChannelHandlerContext ctx, DatagramPacket msg, User user)throws Exception;
    void reflect(ChannelHandlerContext ctx, DatagramPacket msg)throws Exception;
    void getSpeedOfUser(ChannelHandlerContext ctx, DatagramPacket msg, User user)throws Exception;
    void getBitsOfUser(ChannelHandlerContext ctx, DatagramPacket msg, User user)throws Exception;
    void getNumOfUser(ChannelHandlerContext ctx, DatagramPacket msg, User user)throws Exception;
}
