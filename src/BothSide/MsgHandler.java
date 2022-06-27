package BothSide;

/**
 * 使用方法：
 * 1、以该基类为父类创建新的处理类。可以是匿名类。
 * 2、需要重写handle方法。编写过程可以使用getClient获得该处理类所在的Client
 *
 */
public interface MsgHandler<Msg>{
    void handle(Msg message);
}
