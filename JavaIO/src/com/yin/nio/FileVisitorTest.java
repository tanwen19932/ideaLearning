package com.yin.nio;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileVisitorTest {

	public static void main(String[] args) throws IOException {
		Files.walkFileTree(Paths.get("D:\\"),new SimpleFileVisitor<Path>(){

			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				System.out.println("正在访问的路径="+dir);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				if (file.endsWith("FileVisitorTest.java")) {
					System.out.println("找到目标文件");
					return FileVisitResult.TERMINATE;
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc)
					throws IOException {
				return super.visitFileFailed(file, exc);
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				System.out.println("访问结束");
				return super.postVisitDirectory(dir, exc);
			}
			
		});
	}
}