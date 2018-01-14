package com.itheima.zookeeper;

import org.apache.zookeeper.*;
import java.io.IOException;

public class CRUDZookeeper {
	public static void main(String[] args) throws IOException {
		// 获得zookeeper对象
		ZooKeeper zooKeeper = new ZooKeeper("192.168.16.133:2181", 20000, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				System.out.println(event.getType() + "..." + event.getPath());
			}
		});

		try {
			// 第一个参数：路径；第二个参数：值；第三个参数：权限；第四个参数：类型
			String path = zooKeeper.create("/thanks", "hello".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			// 返回创建好的节点的路径
			System.out.println(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
