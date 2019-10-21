package server79;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledFuture;

public class Pdp {
    private PdpSocket pdpSocket;
    private long bitsOfDatagram; //接收到的bit数
    private long numOfDatagram;  //接收到的包数
    private long testOfSpeed;
    private long speedOfDatagram;
    private short ipPort;
   // private int state = 0;
    private ChannelHandlerContext ctx;
    private InetSocketAddress ipAdd;
    private String logInTime;
    private String logOffTime;
    private boolean isActive;
    private ScheduledFuture calSpeedFuture;
    private ScheduledFuture timer;

    public ScheduledFuture getTimer() {
        return timer;
    }

    public void setTimer(ScheduledFuture timer) {
        this.timer = timer;
    }

    public ScheduledFuture getCalSpeedFuture() {
        return calSpeedFuture;
    }

    public void setCalSpeedFuture(ScheduledFuture calSpeedFuture) {
        this.calSpeedFuture = calSpeedFuture;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getLogOffTime() {
        return logOffTime;
    }

    public void setLogOffTime(String logOffTime) {
        this.logOffTime = logOffTime;
    }

    public Pdp(PdpSocket pdpSocket){
        this.pdpSocket = pdpSocket;
    }

    public PdpSocket getPdpSocket() {
        return pdpSocket;
    }

    public void setPdpSocket(PdpSocket pdpSocket) {
        this.pdpSocket = pdpSocket;
    }

    public long getBitsOfDatagram() {
        return bitsOfDatagram;
    }

    public void setBitsOfDatagram(long bitsOfDatagram) {
        this.bitsOfDatagram = bitsOfDatagram;
    }

    public long getNumOfDatagram() {
        return numOfDatagram;
    }

    public void setNumOfDatagram(long numOfDatagram) {
        this.numOfDatagram = numOfDatagram;
    }

    public long getTestOfSpeed() {
        return testOfSpeed;
    }

    public void setTestOfSpeed(long testOfSpeed) {
        this.testOfSpeed = testOfSpeed;
    }

    public long getSpeedOfDatagram() {
        return speedOfDatagram;
    }

    public void setSpeedOfDatagram(long speedOfDatagram) {
        this.speedOfDatagram = speedOfDatagram;
    }

    public short getIpPort() {
        return ipPort;
    }

    public void setIpPort(short ipPort) {
        this.ipPort = ipPort;
    }
//
//    public int getState() {
//        return state;
//    }
//
//    public void setState(int state) {
//        this.state = state;
//    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public InetSocketAddress getIpAdd() {
        return ipAdd;
    }

    public void setIpAdd(InetSocketAddress ipAdd) {
        this.ipAdd = ipAdd;
    }

    public String getLogInTime() {
        return logInTime;
    }

    public void setLogInTime(String logInTime) {
        this.logInTime = logInTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pdp other = (Pdp) obj;
        if (!pdpSocket.equals(other.pdpSocket) )
            return false;
        return true;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + pdpSocket.hashCode();
        return result;
    }


}
