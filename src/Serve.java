import User.UserSystem;

import java.io.IOException;

public class Serve {
    public static void main(String[] args) throws IOException {
        BothSide.Serve serve = new BothSide.Serve(2077);
//        serve.addDefaultHandler(new MsgHandler<HelloMsg>() {
//            @Override
//            public void handle(HelloMsg msg) {
//                System.out.println("客户端发来了一个消息：" + msg.str);
//            }
//        });
//        serve.addRequestHandler(new RequestHandler<HelloRequest>(){
//            @Override
//            public Request handle(HelloRequest request) {
//                HelloMsg req = request.getReq();
//                return request.response(req);
//            }
//        });
        UserSystem userSystem = new UserSystem(serve);

    }

}
