package User;

import BothSide.*;

import java.util.HashSet;
import java.util.Set;

public class UserSystem {
    private Set<String> loginUsers = new HashSet<>();
    private Serve serve;

    public UserSystem(Serve serve){
        this.serve = serve;
//        serve.addRequestHandler(new RequestHandler<LoginRequest>(){
//            @Override
//            public Request handle(LoginRequest request) {
//                String name = request.getReq();
//                if(loginUsers.contains(name)){
//                    return request.response("用户已登录，无法再次登录");
//                }
//                else loginUsers.add(name);
//                return request.response("登陆成功！");
//            }
//        });
        serve.addRequestHandler(LoginRequest.class, (String name) -> {
            if(loginUsers.contains(name)){
                return "用户已登录，无法再次登录";
            }
            else loginUsers.add(name);
            return "登陆成功！";
        });
    }

    OneSide oneSide;
    public UserSystem(OneSide oneSide){
       this.oneSide = oneSide;
    }
    public void login(String userName) throws NegotiateFailed {
        oneSide.sendRequest(new LoginRequest(), userName, (String res) ->
                System.out.println(res)
        );
    }
}
