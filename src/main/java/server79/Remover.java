package server79;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Remover implements Runnable {
    private Pdp pdp;

    Remover(Pdp pdp) {
        this.pdp = pdp;
    }
    @Override
    public void run() {
        if (SharedTranMap.pdpPortMap.containsValue(pdp.getPdpSocket().getPdpAdd(), pdp.getPdpSocket().getPdpPort())) {

                SharedTranMap.pdpPortMap.remove(pdp.getPdpSocket().getPdpAdd(), pdp.getPdpSocket().getPdpPort());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                pdp.getTimer().cancel(false);
             //   pdp.setState(-1);
                pdp.setLogOffTime(dateFormat.format(new Date()));
                pdp.getCalSpeedFuture().cancel(false);

            }
        }
    }
//}
