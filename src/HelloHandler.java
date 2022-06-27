import BothSide.MsgHandler;

public class HelloHandler implements MsgHandler<HelloMsg> {

    @Override
    public void handle(HelloMsg message) {
        System.out.println("给iO");
    }
}
