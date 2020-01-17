import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.xdevapi.JsonArray;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModeTset extends ChannelHandlerAdapter {
    private static Logger logger = LogManager.getLogger(ModeTset.class.getName());
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("mode", "AcquireUserDetails");
//        jsonObject.put("user_id", "592373157@qq.com");

//        jsonObject.put("mode","AddUserDev");
//        jsonObject.put("user_id", "592373157@qq.com");
//        jsonObject.put("dev_id","TE252DA34DA00014");
//        jsonObject.put("mode","DelUserDev");
//        jsonObject.put("user_id", "592373157@qq.com");
//        jsonObject.put("dev_id","TE252DA34DA00014");


        //"mode":"add","user_id": "132793ututy12063@163.com","src_dev_id":"23454","dest_dev_id":"3","type":"video","channel":"8"
//        jsonObject.put("mode","ModDevLink");
//        jsonObject.put("operate", "delete");
//        jsonObject.put("user_id", "592373157@qq.com");
//        jsonObject.put("src_dev_id","TE1A6DDA4B900001");
//        jsonObject.put("dest_dev_id","TD12DDF0DAE00001");
//        jsonObject.put("type", "audio");
//        //原channel为0
//        jsonObject.put("channel","9");


//        jsonObject.put("mode", "ConfigDev");
////        jsonObject.put("user_id", "592373157@qq.com");
////        jsonObject.put("dev_id","TD12DDF0DAE00001");
////        jsonObject.put("frame", "000");
////        jsonObject.put("resolution","0001");
////        jsonObject.put("codec","HHHH");
////        jsonObject.put("code_rate", "5555");


//        jsonObject.put("mode", "LogIn");
//        jsonObject.put("user_id", "592373157@qq.com");
//        jsonObject.put("password", "123456");
        jsonObject .put("is", "in");

        ByteBuf buf = Unpooled.buffer();
        String reply = jsonObject.toJSONString();
        buf.writeBytes(reply.getBytes());
        ctx.writeAndFlush(buf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        JSONObject jsonObject ;
        ByteBuf buf = (ByteBuf)msg;
        byte[] str = new byte[buf.readableBytes()];
        (buf).getBytes(0,str,0,str.length);
        try {
            jsonObject = JSONObject.parseObject(new String(str));
            logger.debug(jsonObject);
        }catch (RuntimeException e){
            logger.info("cannot cast to JSONObject",e);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().close();
    }
}
