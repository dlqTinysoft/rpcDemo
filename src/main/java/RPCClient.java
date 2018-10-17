import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by 董乐强 on 2018/10/17.
 */
public class RPCClient<T> {

    //通过流的方式，请求远程的服务。rpc的思想，远程过程方调用
    public static <T> T getRemoteProxyObj(final Class<?> serviceInterface, final InetSocketAddress addr) {
        T t = (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[]{serviceInterface}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = null;
                ObjectOutputStream output = null;
                ObjectInputStream input = null;

                //创建socket客户端，根据指定的地址链接到远程的服务者
                socket = new Socket();
                socket.connect(addr);
                //3.将远程服务需要调用的接口类、方法名、参数列表等编码后发送给服务提供者
                output = new ObjectOutputStream(socket.getOutputStream());
                //请求远程服务的接口名字，也可称呼服务的名字
                output.writeUTF(serviceInterface.getName());
                System.out.println("serviceInterface name: "+ serviceInterface.getName());
                //请求远程服务的哪个方法
                output.writeUTF(method.getName());
                System.out.println("method name is "+method.getName());
                //请求到远程服务的参数名称
                output.writeObject(method.getParameterTypes());
                System.out.println("method parameterTypes is : "+method.getParameterTypes());
                //请求到远程服务的参数
                output.writeObject(args);
                System.out.println("method args is : "+args);
                //4.同步阻塞等服务器返回应答，获取应答后返回
                input = new ObjectInputStream(socket.getInputStream());
                return input.readObject();//得到方法的应答结果
            }
        });
        //返回的是代理对象
        return t;
    }
}
