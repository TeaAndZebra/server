
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.TcpGetInfo;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

public class DbTcpTest {
    private static Logger logger = LogManager.getLogger(TcpGetInfo.class.getName());

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup(5);
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress("localhost",45690))
                .handler(new ChannelInitializer< SocketChannel >() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ModeTset());
                    }});
        try {
            b.bind(45691);
            b.connect().sync().channel().closeFuture().await();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }

    }
}
