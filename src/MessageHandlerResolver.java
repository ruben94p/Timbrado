import java.util.*;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

public class MessageHandlerResolver implements HandlerResolver {

    @Override
    public List<Handler> getHandlerChain(PortInfo portInfo) {
        List<Handler> handlerChain = new ArrayList<Handler>();

        //Debug de mensaje
        LogMessageHandler lmh = new LogMessageHandler();
        
        handlerChain.add(lmh);

        return handlerChain;
    }

}
