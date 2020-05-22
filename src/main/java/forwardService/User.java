package forwardService;

import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledFuture;

public class User implements Serializable {
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
  //  private boolean isActive;
    private ScheduledFuture calSpeedFuture;
    private ScheduledFuture timer;
    private Double currentFlow;
    public User(PdpSocket pdpSocket){
        this.pdpSocket = pdpSocket;
    }
    @Override
    public String toString() {
        return pdpSocket.toString();
    }

    public Double getCurrentFlow() {
        return currentFlow;
    }

    public void setCurrentFlow(Double currentFlow) {
        this.currentFlow = currentFlow;
    }

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

//    public boolean isActive() {
//        return isActive;
//    }
//
//    public void setActive(boolean active) {
//        isActive = active;
//    }

    public String getLogOffTime() {
        return logOffTime;
    }

    public void setLogOffTime(String logOffTime) {
        this.logOffTime = logOffTime;
    }


    public PdpSocket getPdpSocket() {
        return pdpSocket;
    }

    public  long getBitsOfDatagram() {
        return this.bitsOfDatagram;
    }

    public  void setBitsOfDatagram(long bitsOfDatagram) {//synchronized  锁住的是对象：非静态方法锁住的是this对象
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
        User other = (User) obj;
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
