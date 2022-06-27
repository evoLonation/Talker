import BothSide.*;
import User.UserSystem;

import java.io.IOException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        OneSide oneSide = OneSide.newClientSide("localhost", 2077);
//        System.out.println("新的服务端建立，" + oneSide.getIdentifier().ip + ", " + oneSide.getIdentifier().port);
        Scanner scanner = new Scanner(System.in);
        UserSystem userSystem = new UserSystem(oneSide);
        while(true){
            String str = scanner.nextLine();
//            oneSide.sendRequest(new HelloRequest(), new HelloMsg(str), (String res)->
//                    System.out.println("服务端相应信息：" + res)
//            );
            userSystem.login(str);
        }
    }
}
