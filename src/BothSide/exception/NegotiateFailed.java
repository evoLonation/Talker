package BothSide.exception;

import java.io.IOException;

/**
 * 本包专属异常，交涉失败时抛出~ ~ ~
 */
public class NegotiateFailed extends IOException {
    private String msg = "哎呀，双方的交涉发生了一点小摩擦，谈判失败了呢";
    public NegotiateFailed(){}
    public NegotiateFailed(String msg){
        this.msg = msg;
    }

    @Override
    public String toString() {
        return msg;
    }
}
