package BothSide.exception;

public class ConnectionFailed extends NegotiateFailed{
    public ConnectionFailed() {
        super("尝试连接至服务端失败");
    }
}
