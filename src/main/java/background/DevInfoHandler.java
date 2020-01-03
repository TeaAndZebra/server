package background;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DevInfoHandler extends ChannelHandlerAdapter {
    private static Logger logger = LogManager.getLogger(DevInfoHandler.class.getName());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            logger.debug("get");
            Dev dev = new Dev();
            String info = dev.getInfo();
            ByteBuf buf1 =Unpooled.copiedBuffer(info.getBytes());
            ctx.writeAndFlush(buf1);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
