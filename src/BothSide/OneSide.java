package BothSide;

import BothSide.exception.NegotiateFailed;

public interface OneSide {
    /**
     * 建立一个新的客户方
     */
    static OneSideImp newClientSide(String ip, int port) throws NegotiateFailed {
        return new OneSideImp(ip, port);
    }

    /**
     * 获得该方的唯一标识符，由对方的ip和port组成
     * @return
     */
    Identifier getIdentifier();
    class Identifier{
        public Identifier(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }
        public String ip;
        public int port;
    }

    /**
     * 发送消息给另一方
     * @param message 要发送的消息
     * @throws NegotiateFailed 如果连接断开，则抛出该异常
     */
    void sendMsg(Message message) throws NegotiateFailed;

    /**
     * 发送请求，同时设置请求相应处理函数
     * @param request 要发送的请求的类型。关于请求类的创建要求，参见请求类文件
     * @param req 要发送的请求信息。
     * @param responseHandler 响应处理函数。
     * @throws NegotiateFailed 连接断开无法发送时抛出
     */
    <R1, R2> void sendRequest(Request<R1, R2> request, R1 req, MsgHandler<R2> responseHandler) throws NegotiateFailed;

    /**
     * 检查连接是否仍然存在
     * @throws NegotiateFailed 连接不存在则抛出该异常
     */
    void isCool() throws NegotiateFailed;

    /**
     * 关闭与对方的连接
     */
    void close();

    /**
     * 设置消息处理函数
     * @param msgClass 要处理的消息的class
     * @param msgHandler 处理函数，可以是lambda表达式
     */
    <R> void setMsgHandler(Class<? extends Message> msgClass, MsgHandler<R> msgHandler);
    /**
     * 设置请求处理函数
     * @param requestClass 要处理的请求的class
     * @param requestHandler 请求处理函数，可以是lambda表达式
     */
    <R1, R2> void setRequestHandler(Class<? extends Request<R1, R2>> requestClass ,RequestHandler<R1, R2> requestHandler);

}
