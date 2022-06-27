package BothSide;

import java.io.IOException;

/**
 * 本包专属异常，交涉失败时抛出~ ~ ~
 */
public class NegotiateFailed extends IOException {
    @Override
    public String getMessage() {
        return "哎呀，双方的交涉发生了一点小摩擦，谈判失败了呢";
    }
}
