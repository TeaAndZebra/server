package databaseOperation;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * {@code DbHandler}  is the central class in this SettingServer project.Every setting operation is done through
 *  * this class.
 *  <p>
 *      Mainly for clients to config devices.
 *  </p>
 * @author WangYueMing
 *
 * */
@ChannelHandler.Sharable
public class DbHandler extends ChannelHandlerAdapter {
    private static Logger logger = LogManager.getLogger(DbHandler.class.getName());


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf cmdByte = (ByteBuf) msg;
        byte[] str = new byte[cmdByte.readableBytes()];
        cmdByte.getBytes(0, str, 0, str.length);
        System.out.println(new String(str));
        JSONObject cmd =new JSONObject();
        String mode , reply;
        try {
            cmd = JSONObject.parseObject(new String(str));
            if (!cmd.containsKey("mode")) {
                logger.info("mode is null");
                reply = null;
            } else {
                DeviceSetting deviceSetting = new DeviceSetting(cmd);
                mode = cmd.getString("mode");
                switch (mode) {
                    case "AcquireUserDetails": {
                        logger.info(mode, "mode is AcquireUserDetails");
                        reply = deviceSetting.acquireUserDetails(cmd);
                        break;
                    }
                    case "AddUserDev": {
                        logger.info(mode, "mode is " + "AddUserDev");
                        reply = deviceSetting.addUserDev(cmd);
                        break;
                    }
                    case "DelUserDev": {
                        logger.info(mode, "mode is DelUserDev");

                        reply = deviceSetting.delUserDev(cmd);
                        break;
                    }
                    case "ModDevLink": {
                        logger.info(mode, "mode is ModDevLink");

                        reply = deviceSetting.modDevLink(cmd);
                        break;
                    }
                    case "ConfigDev": {
                        logger.info(mode, "mode is ConfigDev");

                        reply = deviceSetting.configDev(cmd);
                        break;
                    }
                    case "LogIn": {
                        logger.info(mode, "mode is LogIn");

                        reply = deviceSetting.logIn(cmd);
                        break;
                    }
                    default: {
                        logger.info("no matching mode");
                        reply = null;
                        break;
                    }
                }
            }
        }catch (RuntimeException e){
            logger.info("can not cast to JSONObject.");
            cmd.put("msg", "error: can not cast to JSONObject.");
            reply = cmd.toString();
        }

        ByteBuf buf = Unpooled.buffer();
        if(reply==null) {
            String info = "error:no matching mode";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", info);
            reply = jsonObject.toString();
        }
        buf.writeBytes(reply.getBytes());
        ctx.writeAndFlush(buf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("error is ", cause);
        String info = "error is:"+cause;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", info);
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(jsonObject.toString().getBytes());
        ctx.writeAndFlush(buf);
    }


}
