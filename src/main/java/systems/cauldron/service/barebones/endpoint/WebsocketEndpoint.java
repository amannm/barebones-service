package systems.cauldron.service.barebones.endpoint;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * Created by amannmalik on 1/15/17.
 */
@ServerEndpoint("/")
public class WebsocketEndpoint {

    @OnOpen
    public void connect(Session session) throws IOException { }

    @OnMessage
    public void message(String message, Session session) {  }

    @OnClose
    public void close(CloseReason closeReason, Session session) { }

}
