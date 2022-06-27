package BothSide;

import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

/**
 * 作为一个聊天室，应该有服务器和客户端两大运行方式。
 * 作为一个聊天室的客户端，要做的事情是：
 * 1、向服务器进行请求，以建立一个和服务端的连接
 * 2、向服务器发送一个请求来实现某些行为：
 *      1）登录：需要向服务端发送登录的请求，服务端在之后会返回适当信息
 *      2）注册、修改个人信息、
 *      3）反正大多数请求都是要求有返回的，即存在请求——响应关系。
 * 3、接收服务器发送来的请求，该信息大概率是其他用户发送来的消息。
 * 作为一个聊天室的服务端，要做的事情是：
 * 1、实时接收客户端的Socket请求并建立链接
 * 2、接收已连接的客户端的请求并分析，之后响应该请求
 * 3、向已连接的某个客户端发送消息
 *
 * 基于上面的分析，可以设计出客户端与服务端的交互流程：
 * 1、建立连接：客户端与服务端建立可以长期维持的连接。建立后，创建一个线程用于定时接收对方传来的信息
 * 2、解除连接：客户端与服务端解除连接。
 * 3、发送信息：发送后由建立连接时创建的线程接收。
 */
public class Serve {
    private final ServerSocket serverSocket;

    public Serve(int port)throws IOException{
        serverSocket = new ServerSocket(port);
        AcceptLoop acceptLoop = new AcceptLoop();
        acceptLoop.start();
    }

    public void close() {
        try{
            serverSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 新的OneSide建立时，加入该默认队列。
     */
    public void addDefaultHandler(Class<? extends Message> msgClass, MsgHandler<?> msgHandler){
        msgHandlerMap.put(msgClass, msgHandler);
    }
    public <R1, R2> void addRequestHandler(Class<? extends Request<R1, R2>> requestClass ,RequestHandler<R1, R2> requestHandler){
        requestHandlerMap.put(requestClass, requestHandler);
    }

    /**
     * 用来处理一般Message, key是对应Message类型
     */
    private final Map<Class<?>, MsgHandler<?>> msgHandlerMap = new HashMap<>();
    /**
     * 用来处理发过来的请求， key是对应Request类型
     */
    private final Map<Class<?>, RequestHandler<?, ?>> requestHandlerMap = new HashMap<>();


    class AcceptLoop extends Thread {
        @Override
        public void run() {
            while (true){
                try{
                    OneSideImp oneSide;
                    synchronized (serverSocket){
                        oneSide = new OneSideImp(serverSocket.accept());
//                        System.out.println("新的客户端连接建立，" + oneSide.getIdentifier().ip + ", " + oneSide.getIdentifier().port);
                        oneSide.setMsgHandler(msgHandlerMap);
                        oneSide.setRequestHandler(requestHandlerMap);
                    }
                }catch (SocketException ignored){
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

}
