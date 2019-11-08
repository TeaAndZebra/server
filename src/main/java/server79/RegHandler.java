package server79;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class RegHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
    private int ipPort;
    RegHandler(int ipPort){
        this.ipPort = ipPort;
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleAtFixedRate(new calSpeed(),0,6,TimeUnit.SECONDS);
    }
    /**测试端口路由数据量*/
    private long bitOfPort =0;
    private long speedOfPort = 0;
    private long testPortSpeed = 0;

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
            speedOfPort = testPortSpeed/6;
            testPortSpeed  = 0;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");

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

     //   Pdp pdp = new Pdp(pdpSocket);//和SharedTranMap.pdpSocketPdpMap中存的不是同一个pdp，
        if(SharedTranMap.pdpSocketPdpMap.containsKey(pdpSocket)) {
            Pdp pdp = SharedTranMap.pdpSocketPdpMap.get(pdpSocket);
            //System.out.println("success  " +buf.getByte(0)+"  "+buf.getByte(1));
            if(fByte==(byte)0x55){
                RegImpl reg = SharedTranMap.regImplWithObject.get(pdp);
             //   System.out.println("reg is  "+reg);
               //注册时在map中存入对象及该用户RegImpl
                if(reg!=null) {
                    switch (sByte) {
                        case (byte) 0x00:
                            reg.sinRoute(ctx, msg, pdp);
                            break;
                        case (byte) 0x01:
                            reg.multiRoute(ctx, msg, pdp);
                            break;
                        case (byte) 0x03:
                            reg.reflect(ctx, msg);
                            break;
                        case (byte) 0x04:
                            reg.getNumOfUser(ctx, msg,pdp);
                            break;
                        case (byte) 0x05:
                            reg.getBitsOfUser(ctx, msg,pdp);
                            break;
                        case (byte) 0x06:
                            reg.getSpeedOfUser(ctx, msg,pdp);
                            break;
                    }
                }
            }
        }else {
            System.out.println("fail "+Integer.toHexString(buf.getByte(0))+"  "+Integer.toHexString(buf.getByte(1)));
        }
    }
}
