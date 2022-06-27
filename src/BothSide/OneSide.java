package BothSide;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

/**
 * 一方。
 * 可以发送Msg并设置处理接收到的Msg的方式。
 * 可以查看连接是否仍然存在。
 */
public class OneSide {

    /**
     * 客户机连接至服务器所用
     */
    public OneSide(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    /**
     * 向对方发送消息
     * 允许异步进行
     */
    public void sendMsg(Message message) throws NegotiateFailed {
        synchronized (mos){
            try {
                mos.writeObject(message);
            }catch (SocketException e){
                throw new NegotiateFailed();
            }catch (Exception e){
                e.printStackTrace();
                throw new NegotiateFailed();
            }
        }
    }

    /**
     * 发送一个请求给另一方。
     * 这里没有为responseHandler的类型和request进行验证，需要用户自行确认。
     * @param request 传入新new的Request对象，用来唯一标识一个类型的request，让对方知道要干啥
     * @param responseHandler 设置收到响应后的处理函数。
     * @throws NegotiateFailed
     */
    public <R1, R2> void sendRequest(Request<R1, R2> request, R1 req, MsgHandler<R2> responseHandler) throws NegotiateFailed {
        //类型检查 废弃，目前无法检查实现了函数式泛型接口的lambda表达式的具体类型
//        Type[] requestClasses = GenericTypeGetter.getFatherGenericTypes(request);
//        Class<?> responseClass = (Class<?>)GenericTypeGetter.getFatherGenericTypes(responseHandler)[0];
//        if(!req.getClass().equals((Class<?>)requestClasses[0]) || !responseClass.equals((Class<?>)requestClasses[1])){
//            throw new NegotiateFailed();
//        }
        String responseHandlerClassName = responseHandler.getClass().getName();
        if(!responseHandlerMap.containsKey(responseHandlerClassName)){
            responseHandlerMap.put(responseHandlerClassName, responseHandler);
        }
        request.request(req);
        request.setResponseHandlerClass(responseHandlerClassName);
        sendMsg(request);
    }

    /**
     * 查看双方连接是否正常，无论我方对方一者关闭则为关闭
     */
    public void isCool() throws NegotiateFailed{
        if(socket.isClosed()) throw new NegotiateFailed();
        synchronized (mos){
            try {
                mos.writeObject(new KeepAliveMsg());
            }catch (SocketException se){
                throw new NegotiateFailed();
            }catch (Exception e){
                e.printStackTrace();
                throw new NegotiateFailed();
            }
        }
    }

    /**
     * 关闭与对方的连接，也就无法再次使用了，可以抛弃了
     */
    public void close() {
        try{
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    public <R> void setMsgHandler(Class<? extends Message> msgClass, MsgHandler<R> msgHandler){
        msgHandlerMap.put(msgClass, msgHandler);
//        msgHandlerMap.put((Class<?>)GenericTypeGetter.getFatherGenericTypes(msgHandler)[0], msgHandler);
    }

    /**
     * 传入请求类型和对应的请求处理函数
     * @param requestHandler
     */
    public <R1, R2> void setRequestHandler(Class<? extends Request<R1, R2>> requestClass ,RequestHandler<R1, R2> requestHandler){
        //类型检查
//        Type[] requestClasses = GenericTypeGetter.getFatherGenericTypes(requestClass);
//        Type[] handlerClasses = GenericTypeGetter.getFatherGenericTypes(requestHandler);
//        for(int i = 0; i < 2; i++){
//            if(!((Class<?>)requestClasses[i]).equals((Class<?>)handlerClasses[i])){
//                throw new RuntimeException();
//            }
//        }
        requestHandlerMap.put(requestClass, requestHandler);
    }

    void setMsgHandler(Map<Class<?>, MsgHandler<?>> msgHandlerMap){
        this.msgHandlerMap.putAll(msgHandlerMap);
//        msgHandlerMap.put((Class<?>)GenericTypeGetter.getFatherGenericTypes(msgHandler)[0], msgHandler);
    }
    void setRequestHandler(Map<Class<?>, RequestHandler<?, ?>> requestHandlerMap){
        this.requestHandlerMap.putAll(requestHandlerMap);
    }

    /**
     * 用来处理一般Message, key是对应Message类型
     */
    private final Map<Class<?>, MsgHandler<?>> msgHandlerMap = new HashMap<>();
    /**
     * 用来处理发过来的请求， key是对应Request类型
     */
    private final Map<Class<?>, RequestHandler<?, ?>> requestHandlerMap = new HashMap<>();

    /**
     * 用来处理响应，key是对应响应函数的类型
     */
    private final Map<String, MsgHandler<?>> responseHandlerMap = new HashMap<>();


    /**
     * 设置收到Message后应该做的事，
     * 设置之后，每当收到一个Message，就会创建一个进程来执行这个Handler的handle
     * 每次设置都会覆盖上次设置的
     * 需要自行考虑异步性
     */
    void setDealer(Dealer dealer){
        myDealer = dealer;
    }
    interface Dealer {
        void deal(Message msg) throws NegotiateFailed;
    }

    private Dealer myDealer;

    /**
     * Serve创建所用
     */
    OneSide (Socket socket) throws IOException {
        this.socket = socket;
        mos = new ObjectOutputStream(socket.getOutputStream());
        new ReceiveMsgLoop().start();
        new KeepAliveMsgSendLoop().start();
        setDealer((message) ->{
            if(message instanceof Request){
                Request<?, Object> req = (Request<?, Object>) message;
                if(req.getNowType() == req.reqType){
                    // 接收请求
                    Object res = ((RequestHandler<Object, ?>)requestHandlerMap.get(message.getClass())).handle(req.getReq());
                    req.response(res);
                    // 将请求对应的响应发送回去
                    sendMsg(req);
                }else{
                    // 接收响应
                    // 寻找响应对应的请求的处理函数并执行
                    ((MsgHandler)responseHandlerMap.get(req.getHandlerClass())).handle(req.getRes());
                }
            }else if(message != null){
                ((MsgHandler<Message>)msgHandlerMap.get(message.getClass())).handle(message);
            }
        });
    }

    private final ObjectOutputStream mos;
    private final Socket socket;

    class ReceiveMsgLoop extends Thread{
        @Override
        public void run() {
            try {
                ObjectInputStream mis = new ObjectInputStream(socket.getInputStream());
                while(!socket.isClosed()) {
                    Message message = (Message) mis.readObject();
                    if(!(message instanceof KeepAliveMsg)){
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    myDealer.deal(message);
                                } catch (NegotiateFailed e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }
            }catch (SocketException se){
                new NegotiateFailed().getMessage();
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    class KeepAliveMsgSendLoop extends Thread{
        @Override
        public void run() {
            try {
                sendMsg(new KeepAliveMsg());
                sleep(500);
            }
            catch (NegotiateFailed e){
                System.out.println(e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    static class KeepAliveMsg implements Message {
        // i am keep alive!
    }
}
