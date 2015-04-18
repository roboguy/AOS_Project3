package edu.utdallas.aos.core;

import java.util.concurrent.ConcurrentHashMap;

import edu.utdallas.aos.p3.filesystem.FileSystemHandler;

/*
 * Shared application context
 */
public class Context {
	
	public static volatile Object lock = new Object();
	public static volatile FileSystemHandler fsHandler;
	public static volatile ConcurrentHashMap<String, String> nodeInfo;
	
}
