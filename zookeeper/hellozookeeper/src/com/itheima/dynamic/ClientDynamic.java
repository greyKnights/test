package com.itheima.dynamic;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import java.util.ArrayList;
import java.util.List;

/**
 * 分布式服务器上下线动态感知程序的客户端的开发
 *
 * @author asus 张建博
 * @version 1.0 ， 2017-12-03 20:38:49
 *
 * 业务端整体的业务流程就是：如果服务器端某个机器荡掉，客户端需要知道，因为服务端的子节点是一个短暂的节点对象，如果
 * 服务器掉线，那么节点会消失，也就是节点中存储的数据会丢失，我们可以对父节点进行监听，当数据产生变化，回调函数执行，
 * 在回调函数中，对正在工作的服务器重新进行存储
 *
 */
public class ClientDynamic {
	ZooKeeper zk = null;
	private static final String parent_path = "/servers";
	private static final String connectString = "192.168.16.133:2181";
	private static final int sessionTimeout = 20000;
	// 这里使用volitile修饰变量，能够保证多线程访问同一个变量的时候不会出现线程安全的问题
	private volatile List<String> serverList = null;

	/**
	 * 创建连接的方法
	 *
	 * @return
	 */
	public void getConnection() throws Exception {
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				try {
					// 这里是收到子节点变化事件后的回调函数，在此执行业务逻辑
					// 重新更新服务器列表，并且重新注册监听
					getServerList();
				} catch (Exception e) {

				}
			}
		});
	}

	/**
	 * 获取服务器节点信息
	 *
	 * @throws Exception
	 */
	public void getServerList() throws Exception {
		// 获取服务器子节点，并对父节点进行监听,当父节点下面的子节点发生变化，就能监听到
		List<String> children = zk.getChildren(parent_path, true);
		// 遍历子节点，获取服务器信息进行存储
		List<String> servers = new ArrayList<>();
		for (String child : children) {
			byte[] data = zk.getData(parent_path + "/" + child, false, null);
			servers.add(new String(data));
		}
		serverList = servers;
		// 显示服务器列表
		System.out.println(serverList);
	}

	/**
	 * 执行业务逻辑的方法
	 */
	public void handleBussiness() {
		System.out.println("client start working...");
		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// 创建对象
		ClientDynamic clientDynamic = new ClientDynamic();
		// 创建连接
		clientDynamic.getConnection();
		// 获取服务器子节点的信息，并进行监听
		clientDynamic.getServerList();
		clientDynamic.handleBussiness();
	}
}
