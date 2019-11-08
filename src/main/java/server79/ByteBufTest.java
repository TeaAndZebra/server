package server79;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class ByteBufTest {
    public static void main(String[] args) throws UnknownHostException {
        byte[] a = new byte[4];
        for (int i = 0; i < a.length; i++) {
            a[i] = (byte)i;
        }
        ByteBuf buf = Unpooled.copiedBuffer(a);
        InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket datagramPacket = new DatagramPacket(a,buf.readableBytes(),address,8945);

        System.out.println(buf.readableBytes());
        ByteBuf buf1 = Unpooled.copiedBuffer(a);
        System.out.println(buf1.readableBytes());

        double num1 = 54565/1000.0;
        System.out.println(num1);
        BigDecimal bg  = new BigDecimal(num1);
        double num = bg.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println(num);





    }
}
