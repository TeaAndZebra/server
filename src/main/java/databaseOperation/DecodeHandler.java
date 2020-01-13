package databaseOperation;



import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class DecodeHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf cmdByte = (ByteBuf)msg;
        byte[] str = new byte[cmdByte.readableBytes()];
        JSONObject cmd = JSONObject.parseObject(new String(str));
        String mode = new Decoder(cmd).getMode();
        DataBase dataBase = new DataBase();
        JSONObject reply = new JSONObject();
        switch (mode){
            case "AcquireUserDetails":{
                reply = dataBase.acquireUserDetails();
            }
            case "AddUserDev":{
                reply = dataBase.addUserDev();
            }
            case "DelUserDev":{
                reply =dataBase.delUserDev();
            }
            case "ModDevLink":{
                reply =dataBase.modDevLink();
            }
            case "ConfigDev":{
                reply =dataBase.configDev();
            }
            case "LogIn":{
                reply =dataBase.logIn();
            }
            default:{
            }
        }
    }
}
