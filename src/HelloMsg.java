import BothSide.Message;

public class HelloMsg implements Message {
    public HelloMsg(String str) {
        this.str = str;
    }
    public String str;
}
