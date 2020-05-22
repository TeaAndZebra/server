package forwardService.nonreg;

import io.netty.channel.ChannelHandlerContext;

public interface NonReg {
    void parseId(ChannelHandlerContext ctx) throws Exception;
    void queryRegInfo(ChannelHandlerContext ctx)throws Exception;
    void register(ChannelHandlerContext ctx)throws Exception;
    void updateIp(ChannelHandlerContext ctx)throws Exception;
    void cancel(ChannelHandlerContext ctx)throws Exception;
}
