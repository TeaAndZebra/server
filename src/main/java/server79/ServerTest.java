package server79;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import redis.clients.jedis.Jedis;
import redisSave.MysqlHandler;
import redisSave.RedisHandler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerTest {
     static RegHandler b,c,d;

    private void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(4);
        try {

            Bootstrap b0 = new Bootstrap();
            b0.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer< NioDatagramChannel >() {
                        @Override
                        public void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast( new NonRegHandler());
                        }});
            b0.bind(45685).sync().channel();



            Bootstrap b1 = new Bootstrap();
            b1.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer< NioDatagramChannel >() {
                        @Override
                        public void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(b = new RegHandler(5467));
                        }});
            b1.bind(5467).sync().channel();




            Bootstrap b2 = new Bootstrap();
            b2.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer< NioDatagramChannel >() {
                        @Override
                        public void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(c = new RegHandler(5468));
                        }});
            b2.bind(5468).sync().channel();



            Bootstrap b3 = new Bootstrap();
            b3.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer< NioDatagramChannel >() {
                        @Override
                        public void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(d=new RegHandler(5469));
                        }});
            b3.bind(5469).sync().channel().closeFuture().await();
           // System.out.println(Thread.currentThread()+"  "+group.next()+"   "+group.next().next()+"   "+group.next().next().next()+"   "+group.next().next().next().next()+"     ");

        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.println("start");
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(2);
        Jedis jedis = new Jedis("localhost");
        service.scheduleAtFixedRate(new RedisHandler(jedis), 0,20, TimeUnit.SECONDS);
        new MysqlHandler().insertData();
//        Thread thread = new Thread(new Monitor());
//        thread.start();
        new ServerTest().run();




    }
}
