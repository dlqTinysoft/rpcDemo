

import java.net.InetSocketAddress;

/**
 * Created by 董乐强 on 2018/10/17.
 */
public class RPCTest {


    public static void main(String[] args) {
        new Thread(new Runnable() {
            public void run() {
                try{
                Server serviceServer = new ServiceCenter(8099);
                //注册一个服务，就是将哪些服务暴露给客户端调用
                serviceServer.register(HelloService.class,HelloServiceImpl.class);//注册服务
                serviceServer.start();//开启客户端的服务
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //得到helloService这个服务
        HelloService service = RPCClient.getRemoteProxyObj(HelloService.class,new InetSocketAddress("127.0.0.1",8099));
        System.out.println(service);
        System.out.println("=====================");
        System.out.println(service.sayHi("test"));
    }


}
