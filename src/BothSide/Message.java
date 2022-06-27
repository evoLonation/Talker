package BothSide;

import java.io.Serializable;
import java.net.Socket;

/**
 * Client通过sendMsg发送的消息基类。
 * 以该基类为父类创建新的Msg。
 * 创建方法：除了extends Msg之外没有任何约束。
 */
public interface Message extends Serializable {

}

