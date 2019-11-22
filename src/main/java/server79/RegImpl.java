package server79;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegImpl implements Reg {
    private int port;
    private PdpSocket pdpSocket;
    private static Logger logger = LogManager.getLogger(RegImpl.class.getName());
    RegImpl(PdpSocket pdpSocket,int port){
        this.port = port;
        this.pdpSocket = pdpSocket;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RegImpl other = (RegImpl)obj;
        if (port!=other.port)
            return false;
        if (pdpSocket!=other.pdpSocket)
            return false;
        return true;

    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + port;
        result = prime * result + pdpSocket.hashCode();
        return result;
    }
    /**普通路由：Type = 0x00用户发送0x55	0x00	源地址（40bit）	目的地址（40bit）	用户数据*/
    @Override
    public void sinRoute(ChannelHandlerContext ctx, DatagramPacket msg,Pdp pdp) throws Exception {

        msg.retain();
        ByteBuf buf =msg.content();
        int rBytesOfBuf= buf.readableBytes();
//        System.out.println("reg impl readable bytes is  "+rBytesOfBuf);
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
            logger.debug("singleCast bits of data is [{}]",rBytesOfBuf);
            /**计算IP端口对应的数据量及路由速率*/
            regHandler.setBitOfPort(regHandler.getBitOfPort() + rBytesOfBuf);
            regHandler.setTestPortSpeed(regHandler.getTestPortSpeed() + rBytesOfBuf);

            /**计算每个pdp地址对应的数据量及路由速率*/
            pdp.setBitsOfDatagram(pdp.getBitsOfDatagram() + rBytesOfBuf);
//            System.out.println(pdp.getPdpSocket()+":"+pdp.getPdpSocket()+" num is  "+pdp.getBitsOfDatagram());
            pdp.setTestOfSpeed(pdp.getTestOfSpeed() + rBytesOfBuf);
            /**计算每个pdp地址对应的数据包数*/
            pdp.setNumOfDatagram(pdp.getNumOfDatagram() + 1);

            byte[] desAddByte = new byte[4];
            byte desPort;
            buf.getBytes(7, desAddByte, 0, 4);
            desPort = buf.getByte(11);
            int pdpSocketIntDes = DataChange.bytes2Int(desAddByte);
          //  System.out.println("single cast dest pdp "+pdpSocketIntDes);
            PdpSocket pdpSocketDes = new PdpSocket(pdpSocketIntDes, desPort);
            if (SharedTranMap.pdpSocketPdpMap.containsKey(pdpSocketDes)) {
                //System.out.println(pdp.getPdpSocket()+  " singleCast desAdd is : "+pdpSocketIntDes+" port is： "+desPort);
                Pdp dest = SharedTranMap.pdpSocketPdpMap.get(pdpSocketDes);
                logger.debug("[{}]:[{}] send packages to [{}]:[{}]",pdpSocket.getPdpAdd(),pdpSocket.getPdpPort(),pdpSocketDes.getPdpAdd(),pdpSocketDes.getPdpPort());
                dest.getCtx().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(buf), dest.getIpAdd()));
            }
        }else {
//            System.out.println(pdp+  " singleCast " +" ipPort is "+port +" fail");
            logger.info("singleCast fail : regHandler=null || pdp:{[{}]}=null ",pdp);
        }
    }
    /**多播路由：Type = 0x01
     *用户发送0x55	0x01	源地址（40bit）	目的地址数（8bit）	目的地址0（40bit）	目的地址……（40bit）用户数据*/
    @Override
    public void multiRoute(ChannelHandlerContext ctx, DatagramPacket msg,Pdp pdp) throws Exception {
        msg.retain();
        ByteBuf buf =msg.content();
        int rBytesOfBuf= buf.readableBytes();
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
          //  System.out.println(pdp.getPdpSocket() + " multiCast");
            logger.debug("multiCast bits of data is [{}]",rBytesOfBuf);
            byte numOfDesAdd = buf.getByte(7);
           // System.out.println("multicast:  num is:" + numOfDesAdd);
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
                if (SharedTranMap.pdpSocketPdpMap.containsKey(pdpSocketDes)) {
                   // System.out.println(pdp.getPdpSocket()+  " multiCast desAdd is "+pdpSocketIntDes+" port is"+destPort);
                    logger.debug("[{}]:[{}] send packages to [{}]:[{}]",pdpSocket.getPdpAdd(),pdpSocket.getPdpPort(),pdpSocketDes.getPdpAdd(),pdpSocketDes.getPdpPort());
                    Pdp pdpDest = SharedTranMap.pdpSocketPdpMap.get(pdpSocketDes);
                    pdpDest.getCtx().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(buf), pdpDest.getIpAdd()));
                }else{
                    logger.info("multiCast dest pdpAdd:[{}] error ",pdpSocketDes);
                }
            }
        }else {
            logger.info("multiCast fail : regHandler=null || pdp:{[{}]}=null ",pdp);
        }
    }
    /**回射 = 0x03
     *用户发送0x55	0x03	源地址（40bit）	用户数据
     *服务器返回0x55 0x03	源地址（40bit）	用户数据*/
    @Override
    public void reflect(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
       // System.out.println("reflect");
        logger.debug("reflect");
        msg.retain();
        ByteBuf buf =  msg.content();
        ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(buf), msg.sender()));
    }
    /**获取路由速率 = 0x07
    *用户发送0x55	0x07	源地址（40bit）
    *服务器返回0x55	0x07	速率（64bit）*/
    @Override
    public void getSpeedOfUser(ChannelHandlerContext ctx, DatagramPacket msg,Pdp pdp) throws Exception {
           // System.out.println(  "get  speed");
        logger.debug("get speed of [{}]:[{}]",pdp.getPdpSocket().getPdpAdd(),pdp.getPdpSocket().getPdpPort());
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
    public void getBitsOfUser(ChannelHandlerContext ctx, DatagramPacket msg,Pdp pdp) throws Exception {
// System.out.println(obj+"获取路由报文数据量byte:"+obj.getBitsOfDatagram());
        logger.debug("get bits of [{}]:[{}]",pdp.getPdpSocket().getPdpAdd(),pdp.getPdpSocket().getPdpPort());
        byte[] echo = new byte[10];
        echo[0] = (byte) 0x55;
        echo[1] = (byte) 0x05;
       // System.out.println(pdp.getPdpSocket() + "get  bits");
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
    public void getNumOfUser(ChannelHandlerContext ctx, DatagramPacket msg,Pdp pdp) throws Exception {
//        System.out.println(pdp + "get num"+pdp.getNumOfDatagram());
        logger.debug("get num of [{}]:[{}]",pdp.getPdpSocket().getPdpAdd(),pdp.getPdpSocket().getPdpPort());

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
