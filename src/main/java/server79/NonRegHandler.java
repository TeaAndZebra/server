package server79;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class NonRegHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    static DataBase dataBase= null;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        dataBase = new DataBase();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        msg.retain();
        ByteBuf buf = msg.content();

        byte fByte = buf.getByte(0);
        byte sByte = buf.getByte(1);
        NonRegImpl nonReg = new NonRegImpl(msg);

        if (fByte == (byte) 0xd6) {
            switch (sByte) {
                case (byte) 0x01:
                    nonReg.parseId(ctx, msg);
                    break;
                case (byte) 0x02:
                    nonReg.queryRegInfo(ctx, msg);
                    break;
                case (byte) 0x03:
                    nonReg.register(ctx, msg);
                    break;
                case (byte) 0x04:
                    nonReg.cancel(ctx, msg);
                    break;
                case (byte) 0x05:
                    nonReg.updateIp(ctx, msg);
                    break;
            }
        }
        nonReg = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
