package forwardService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Remover implements Runnable {
    private User user;
    private static Logger logger = LogManager.getLogger(Remover.class.getName());
    public Remover(User user) {
        this.user = user;
    }
    @Override
    public void run() {
        if (SharedTranMap.pdpSocketPdpMap.containsValue(user)) {
                logger.info("[{}] is removed", user.toString());
//                System.out.println( user.getPdpSocket().getPdpAdd()+" :  "+user.getPdpSocket().getPdpPort()+" is removed");
                SharedTranMap.pdpPortMap.remove(user.getPdpSocket().getPdpAdd(), user.getPdpSocket().getPdpPort());
                SharedTranMap.pdpSocketPdpMap.remove(user.getPdpSocket(), user);
                SharedTranMap.regImplWithObject.remove(user);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                user.getTimer().cancel(false);
                user.setLogOffTime(dateFormat.format(new Date()));
                user.getCalSpeedFuture().cancel(false);

            }
        }
    }
//}
