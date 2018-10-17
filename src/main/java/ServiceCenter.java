import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 董乐强 on 2018/10/17.
 */
public class ServiceCenter implements Server {

    //标准中定义线程池，开启线程的大小，就是cpu的核数+1
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    //用于保存服务的，大Class就是代表一个服务
    //其中key代表接口的名字，大Class是接口的实现,其实key就是服务名称，value是服务（就是服务的实现）
    private static final HashMap<String,Class> serviceRegistry = new HashMap<String,Class>();

    private  static int port;

    private static boolean isRunning = false;

    public ServiceCenter(int port){
        this.port = port;
    }
    public void stop() {
       isRunning = false;
       //关闭线程池
       executor.shutdown();
    }

    public void start() throws Exception {
        ServerSocket server = new ServerSocket();

        server.bind(new InetSocketAddress(port));

        System.out.println("start server");

        try{
            while(true){
                //1.监听到客户端的tcp连接，接到tcp连接后将其封装成task,由线程池执行
                executor.execute(new ServiceTask(server.accept(),serviceRegistry));
                System.out.println("======start work  from server======");
            }
        }finally {
            //必须关闭stock连接，以免造成资源浪费
            server.close();
        }



    }

    public void register(Class serviceInterface, Class impl) {
        //key服务的名字和key服务的实现
        serviceRegistry.put(serviceInterface.getName(),impl);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());
    }
}
