package com.yin.nio;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class WatchServiceTest {
	public static void main(String[] args) {
		try {
			WatchService watchService = FileSystems.getDefault().newWatchService();
			Paths.get("D:\\").register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE,StandardWatchEventKinds.ENTRY_MODIFY);
			while(true){
				WatchKey key = watchService.take();
				for (WatchEvent<?> event : key.pollEvents()) {
					System.out.println(event.context()+ "发生了"+event.kind()+"事件");
				}
				boolean vaild = key.reset();//重设WatchKey
				if (!vaild) {//如果重设失败，退出监听
					break;
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
