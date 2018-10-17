/**
 * Created by 董乐强 on 2018/10/17.
 *
 * 服务中心代码实现
 */
public interface Server {
    void stop();

    void start() throws Exception;

    void register(Class serviceInterface,Class impl);

    boolean isRunning();

    int getPort();

}
