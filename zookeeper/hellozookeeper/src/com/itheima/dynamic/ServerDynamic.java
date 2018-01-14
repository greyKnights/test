package com.itheima.dynamic;

import org.apache.zookeeper.*;

/**
 * 分布式服务器上下线动态感知程序的服务端的开发
 *
 * @author asus 张建博
 * @version 1.0 ， 2017-12-03 20:15:48
 *
 */
public class ServerDynamic {

	ZooKeeper zk = null;
	private static final String parent_path = "/servers";
	private static final String connectString = "192.168.16.133:2181";
	private static final int sessionTimeout = 20000;


	/**
	 * 创建连接的方法
	 *
	 * @return
	 */
	public void getConnection() throws Exception {
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				// process相当于一个回调函数，注册监听器后，当子节点发生变化，回调函数会执行相应的业务处理
				System.out.println(event.getType() + "..." + event.getPath());
				try {
					// 当回调函数执行完毕，会重新注册一个新的监听器，实现服务器的高可用性，因为上一个监听器监听一次之后就会消失
					zk.getChildren("/", true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 注册服务的方法
	 *
	 */
	public void registerServer(String hostname) throws Exception {
		String s = zk.create(parent_path + "/server", hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(hostname + "is online" + s);
	}

	/**
	 * 执行业务逻辑的方法
	 */
	public void handleBussiness(String hostname) {
		System.out.println(hostname + "start working...");
		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// 获得连接
		ServerDynamic server = new ServerDynamic();
		server.getConnection();

		// 注册服务信息
		server.registerServer("mini4");

		// 处理业务逻辑的方法
		server.handleBussiness("mini4");

	}
}
