package edu.utdallas.aos.core;

import info.siyer.aos.clock.VectorClock;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import com.google.api.client.util.ExponentialBackOff;

import edu.utdallas.aos.p3.config.Node;
import edu.utdallas.aos.p3.filesystem.FileSystemHandler;

/*
 * Shared application context
 */
public class Context {
	
	public static volatile Object lock = new Object();
	public static volatile FileSystemHandler fsHandler;
	public static volatile ExponentialDistribution requestDelay;
	public static volatile Node myInfo;
	public static volatile ConcurrentHashMap<Integer, Node> nodeInfos = new ConcurrentHashMap<>();
	public static volatile ExponentialBackOff backoff;
	public static volatile VectorClock clock;
	
}
