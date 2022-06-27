package BothSide;

public abstract class Request<Req , Res> implements Message{



    public final int reqType = 1;
    public final int resType = 2;
    private int nowType = 1;

    public int getNowType() {
        return nowType;
    }

//    public Request send(Req req, MsgHandler<Res> responseHandler) throws NegotiateFailed {
//        resMsgHandler = responseHandler;
//        oneSide.sendMsg(new RequestMessage<Req, Res>(){
//            @Override
//            public Class getResClass() {
//                return (Class)GenericTypeGetter.getFatherGenericTypes(this)[1];
//            }
//            private final Req request = req;
//            @Override
//            public Req getRequest() {
//                return request;
//            }
//
//            @Override
//            public Class getRequestClass() {
//                return Request.this.getClass();
//            }
//        });
//        return this;
//    }
    private Req req;
    private Res res;
    private String handlerClass;

    /**
     * 发送前使用
     */
    public Request request(Req req){
        // 生成一个id
        this.req = req;
        nowType = 1;
        return this;
    }
    /**
     * 返回前使用
     */
    public Request response(Res res){
        this.res = res;
        this.req = null;
        nowType = 2;
        return this;
    }

    /**
     * 设置该请求对应的响应处理函数类，发送之前务必设置
     * @param handlerClass
     */
    void setResponseHandlerClass(String handlerClass) {
        this.handlerClass = handlerClass;
    }

    String getHandlerClass() {
        return handlerClass;
    }

    public Res getRes() {
        return res;
    }

    public Req getReq() {
        return req;
    }

}