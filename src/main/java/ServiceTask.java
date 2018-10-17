import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by 董乐强 on 2018/10/17.
 * 关于rpc思想的核心处理逻辑
 */
public class ServiceTask implements Runnable {
    private Socket client = null;
    //其中key代表接口的名字，大Class是接口的实现
    private static  HashMap<String,Class> serviceRegistry ;
    public ServiceTask(Socket accept,HashMap<String,Class> serviceRegistry) {
        this.client = accept;
        this.serviceRegistry = serviceRegistry;
    }

    public void run() {
        ObjectInputStream input = null;
        ObjectOutputStream output = null;

        try {
            //2.将客户端发送过来的流，反序列化为对象，反射服务实现者，获取执行结果
            input = new ObjectInputStream(client.getInputStream());
            //获取服务的名字
            String serviceName = input.readUTF();
            //获取客户端要调用服务的哪个方法
            String methodName = input.readUTF();

            //调用方法的参数类型
            Class<?>[] parameterTypes = (Class<?>[]) input.readObject();

            //调用方法所传递的参数
            Object[] arguments = (Object[]) input.readObject();
            //得到注册的 服务，可以认为是服务端给暴露 出来的服务，服务端的有的服务可以不暴露哦，不是全部的服务都暴露出来的
            Class serviceClass = serviceRegistry.get(serviceName);

            if (serviceClass == null) {
                throw new ClassNotFoundException(serviceName + " not found");
            }

            //通过参数名和参数类型获得方法，因为方法有重载，所以不能只通过方法名来获得方法，所以要通过方法名和参数类型，来唯一确定方法
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            //调用方法，并得到结果
            Object result = method.invoke(serviceClass.newInstance(), arguments);

            //3.将执行结果反序列化，通过socket发送给客户端
            output = new ObjectOutputStream(client.getOutputStream());
            //将结果发送给客户端
            output.writeObject(result);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(output !=null){
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(input !=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(client !=null){
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

















