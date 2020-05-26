package forwardService.reg;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import forwardService.*;
import forwardService.utils.DataChange;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class RegHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        cause.printStackTrace();
        logger.error(cause.getMessage(),cause);
    }
    private int ipPort;
    public RegHandler(int ipPort){
        this.ipPort = ipPort;
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(2);
        service.scheduleAtFixedRate(new calSpeed(),0,10,TimeUnit.SECONDS);
    }
    /**测试端口路由数据量*/
    private long bitOfPort =0;
    private long speedOfPort = 0;
    private long testPortSpeed = 0;
    private static Logger logger = LogManager.getLogger(RegHandler.class.getName());
    public long getSpeedOfPort() {
        return speedOfPort;
    }

    public long getTestPortSpeed() {
        return testPortSpeed;
    }

    public void setBitOfPort(long bitOfPort) {
        this.bitOfPort = bitOfPort;
    }

    public void setSpeedOfPort(long speedOfPort) {
        this.speedOfPort = speedOfPort;
    }

    public void setTestPortSpeed(long testPortSpeed) {
        this.testPortSpeed = testPortSpeed;
    }

    public long getBitOfPort() {
        return bitOfPort;
    }

    class calSpeed implements Runnable{
        @Override
        public void run() {
            /**每6s更新端口路由速率*/
            speedOfPort = testPortSpeed/10;
            testPortSpeed  = 0;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
            logger.info("[{}] channel active",Thread.currentThread().getName());

    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        msg.retain();
        ByteBuf buf =msg.content();
//        System.out.println("reg handler readable bytes is  "+buf.readableBytes());
        byte fByte = buf.getByte(0);
        byte sByte = buf.getByte(1);

        byte[] pdpAddByte = new byte[4];
        buf.getBytes(2,pdpAddByte,0,4);
        int pdpAddInt =  DataChange.bytes2Int(pdpAddByte);

        byte pdpPort =  buf.getByte(6);
        PdpSocket pdpSocket = new PdpSocket(pdpAddInt, pdpPort);

     //   User pdp = new User(pdpSocket);//和SharedTranMap.pdpSocketPdpMap中存的不是同一个pdp，
        if(SharedTranMap.pdpSocketPdpMap.containsKey(pdpSocket)) {
            User user = SharedTranMap.pdpSocketPdpMap.get(pdpSocket);
            //System.out.println("success  " +buf.getByte(0)+"  "+buf.getByte(1));
            if(fByte==(byte)0x55){
                logger.debug(" reg operation");
                RegImpl reg = SharedTranMap.regImplWithObject.get(user);
             //   System.out.println("reg is  "+reg);
               //注册时在map中存入对象及该用户RegImpl
                if(reg!=null) {
                    switch (sByte) {
                        case (byte) 0x00:
                            reg.sinRoute(ctx, msg, user);
                            break;
                        case (byte) 0x01:
                            reg.multiRoute(ctx, msg, user);
                            break;
                        case (byte) 0x03:
                            reg.reflect(ctx, msg);
                            break;
                        case (byte) 0x04:
                            reg.getNumOfUser(ctx, msg, user);
                            break;
                        case (byte) 0x05:
                            reg.getBitsOfUser(ctx, msg, user);
                            break;
                        case (byte) 0x06:
                            reg.getSpeedOfUser(ctx, msg, user);
                            break;
                    }
                }
            }
        }else {
        //    System.out.println("fail "+Integer.toHexString(buf.getByte(0))+"  "+Integer.toHexString(buf.getByte(1)));
            logger.info("reg first two bytes wrong :[{}] [{}]",Integer.toHexString(buf.getByte(0)),Integer.toHexString(buf.getByte(1)));
        }
    }
}
