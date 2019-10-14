package server79;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

public class RegImpl implements Reg {
    private int port;
    RegImpl(int port){
        this.port = port;
    }

    /**普通路由：Type = 0x00用户发送0x55	0x00	源地址（40bit）	目的地址（40bit）	用户数据*/
    @Override
    public void sinRoute(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        msg.retain();
        ByteBuf buf =msg.content();
        int rBytesOfBuf= buf.readableBytes();
        byte[] pdpAddByte = new byte[4];
        buf.getBytes(2,pdpAddByte,0,4);
        int pdpAddInt = DataChange.bytes2Int(pdpAddByte);
        byte pdpPort =buf.getByte(6);
        PdpSocket pdpSocket = new PdpSocket(pdpAddInt, pdpPort);
        Pdp pdp = SharedTranMap.objectWithSocket.get(pdpSocket);
        RegHandler regHandler = null;
        switch (port){
            case 5467:
                regHandler=ServerTest.b;
                break;
            case 5468:
                regHandler=ServerTest.c;
                break;
            case 5469:
                regHandler=ServerTest.d;
                break;
        }

        if(regHandler!=null&&pdp!=null) {

            /**计算IP端口对应的数据量及路由速率*/
            regHandler.setBitOfPort(regHandler.getBitOfPort() + rBytesOfBuf);
            regHandler.setTestPortSpeed(regHandler.getTestPortSpeed() + rBytesOfBuf);

            /**计算每个pdp地址对应的数据量及路由速率*/
            pdp.setBitsOfDatagram(pdp.getBitsOfDatagram() + rBytesOfBuf);
            pdp.setTestOfSpeed(pdp.getTestOfSpeed() + rBytesOfBuf);
            /**计算每个pdp地址对应的数据包数*/
            pdp.setNumOfDatagram(pdp.getNumOfDatagram() + 1);

            byte[] desAddByte = new byte[4];
            byte desPort;
            buf.getBytes(7, desAddByte, 0, 4);
            desPort = buf.getByte(11);
            int pdpSocketIntDes = DataChange.bytes2Int(desAddByte);
            //
            PdpSocket pdpSocketDes = new PdpSocket(pdpSocketIntDes, desPort);
            if (SharedTranMap.pdpPortMap.containsValue(pdpSocketIntDes, desPort)) {
                System.out.println(pdpAddInt+  " singleCast desAdd is : "+pdpSocketIntDes+" port is： "+desPort);
                Pdp dest = SharedTranMap.objectWithSocket.get(pdpSocketDes);
                dest.getCtx().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(buf), dest.getIpAdd()));
            }
        }else {
            System.out.println(pdpAddInt+  " singleCast " +" ipPort is "+port +" fail");

        }
    }
    /**多播路由：Type = 0x01
     *用户发送0x55	0x01	源地址（40bit）	目的地址数（8bit）	目的地址0（40bit）	目的地址……（40bit）用户数据*/
    @Override
    public void multiRoute(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        msg.retain();
        ByteBuf buf =msg.content();
        long rBytesOfBuf= buf.readableBytes();
        byte[] pdpAddByte = new byte[4];
        buf.getBytes(2,pdpAddByte,0,4);
        int pdpAddInt = DataChange.bytes2Int(pdpAddByte);
        byte pdpPort =buf.getByte(6);
        PdpSocket pdpSocket = new PdpSocket(pdpAddInt, pdpPort);
        Pdp pdp = SharedTranMap.objectWithSocket.get(pdpSocket);

        RegHandler regHandler = null;
        switch (port){
            case 5467:
                regHandler=ServerTest.b;
                break;
            case 5468:
                regHandler=ServerTest.c;
                break;
            case 5469:
                regHandler=ServerTest.d;
                break;
        }
        if(regHandler!=null&&pdp!=null) {
            System.out.println(pdpAddInt + " multiCast");

            byte numOfDesAdd = buf.getByte(7);
            System.out.println("multicast:  num is:" + numOfDesAdd);
            /**计算IP端口对应的数据量*/
            regHandler.setBitOfPort(regHandler.getBitOfPort() + rBytesOfBuf);
            regHandler.setTestPortSpeed(regHandler.getTestPortSpeed() + rBytesOfBuf);

            /**计算每个pdp地址对应的数据量*/
            pdp.setBitsOfDatagram(pdp.getBitsOfDatagram() + rBytesOfBuf);

            pdp.setTestOfSpeed(pdp.getTestOfSpeed() + rBytesOfBuf);

            /**计算每个pdp地址对应的数据包数*/
            pdp.setNumOfDatagram(pdp.getNumOfDatagram() + 1);

            /**添加*/
            for (int i = 0; i < numOfDesAdd; i++) {
                byte[] dest = new byte[4];
                buf.getBytes(8 + i * 5, dest, 0, 4);
                byte destPort = buf.getByte(12 + 5 * i);
                int pdpSocketIntDes = DataChange.bytes2Int(dest);
                // System.out.println("multicast: "+" pdpAdd:"+DataChange.bytes2Int(dest)+"port:"+destPort);
                PdpSocket pdpSocketDes = new PdpSocket(pdpSocketIntDes, destPort);
                if (SharedTranMap.pdpPortMap.containsValue(pdpSocketIntDes, destPort)) {
                    System.out.println(pdpAddInt+  " multiCast desAdd is "+pdpSocketIntDes+" port is"+destPort);
                    Pdp pdpDest = SharedTranMap.objectWithSocket.get(pdpSocketDes);
                    pdpDest.getCtx().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(buf), pdpDest.getIpAdd()));
                }
            }
        }
    }
    /**回射 = 0x03
     *用户发送0x55	0x03	源地址（40bit）	用户数据
     *服务器返回0x55 0x03	源地址（40bit）	用户数据*/
    @Override
    public void reflect(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        msg.retain();
        ByteBuf buf =  msg.content();
        ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(buf), msg.sender()));
    }
    /**获取路由速率 = 0x07
    *用户发送0x55	0x07	源地址（40bit）
    *服务器返回0x55	0x07	速率（64bit）*/
    @Override
    public void getSpeedOfUser(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        msg.retain();
        ByteBuf buf =msg.content();
        byte[] pdpAddByte = new byte[4];
        buf.getBytes(2,pdpAddByte,0,4);
        int pdpAddInt = DataChange.bytes2Int(pdpAddByte);
        byte pdpPort =buf.getByte(6);
        PdpSocket pdpSocket = new PdpSocket(pdpAddInt, pdpPort);
        Pdp pdp = SharedTranMap.objectWithSocket.get(pdpSocket);
            System.out.println(  "get  speed");
            //  System.out.println(obj+"获取路由速率bit/s:"+obj.getSpeedOfDatagram());
            byte[] echo = new byte[10];
            echo[0] = (byte) 0x55;
            echo[1] = (byte) 0x06;
            byte[] speedOfSocket = DataChange.longToBytes(pdp.getSpeedOfDatagram());
            if (speedOfSocket.length == 8) {
                System.arraycopy(speedOfSocket, 0, echo, 2, 8);
                ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(echo), msg.sender()));
            }
        }

    /**获取路由报文数据量 = 0x06
     *用户发送0x55	0x06	源地址（40bit）
     *服务器返回0x55	0x06	数据量（64bit）*/
    @Override
    public void getBitsOfUser(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
// System.out.println(obj+"获取路由报文数据量byte:"+obj.getBitsOfDatagram());
        msg.retain();
        ByteBuf buf =msg.content();
        byte[] pdpAddByte = new byte[4];
        buf.getBytes(2,pdpAddByte,0,4);
        int pdpAddInt = DataChange.bytes2Int(pdpAddByte);
        byte pdpPort =buf.getByte(6);
        PdpSocket pdpSocket = new PdpSocket(pdpAddInt, pdpPort);
        Pdp pdp = SharedTranMap.objectWithSocket.get(pdpSocket);
            byte[] echo = new byte[10];
            echo[0] = (byte) 0x55;
            echo[1] = (byte) 0x05;
            System.out.println(pdpAddInt + "get  bits");
            byte[] bitsOfDatagram = DataChange.longToBytes(pdp.getBitsOfDatagram());
            if (bitsOfDatagram.length == 8) {
                System.arraycopy(bitsOfDatagram, 0, echo, 2, 8);
                ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(echo), msg.sender()));
            }
        }
    /**获取路由报文数 = 0x05
     *用户发送 0x55	0x05	源地址（40bit）
     *服务器返回   0x55	0x05	计数值（64bit）*/
    @Override
    public void getNumOfUser(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        msg.retain();
        ByteBuf buf =msg.content();
        byte[] pdpAddByte = new byte[4];
        buf.getBytes(2,pdpAddByte,0,4);
        int pdpAddInt = DataChange.bytes2Int(pdpAddByte);
        byte pdpPort =buf.getByte(6);
        PdpSocket pdpSocket = new PdpSocket(pdpAddInt, pdpPort);
        Pdp pdp = SharedTranMap.objectWithSocket.get(pdpSocket);
        System.out.println(pdpAddInt+  "get  num");
        System.out.println(pdp + "get num"+pdp.getNumOfDatagram());
        //  System.out.println(obj + "获取路由报文数:"+obj.getNumOfDatagram());
            byte[] echo = new byte[10];
            echo[0] = (byte) 0x55;
            echo[1] = (byte) 0x04;
            byte[] numOfDatagram = DataChange.longToBytes(pdp.getNumOfDatagram());
            if (numOfDatagram.length == 8) {
                System.arraycopy(numOfDatagram, 0, echo, 2, 8);
                ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(echo), msg.sender()));
            }
    }
}
