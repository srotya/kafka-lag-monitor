/**
 * Copyright 2017 Ambud Sharma
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.srotya.monitoring.kafka.util;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

import com.srotya.monitoring.kafka.KafkaMonitorConfiguration;
import com.srotya.sidewinder.core.storage.DataPoint;
import com.srotya.sidewinder.core.storage.StorageEngine;

/**
 * @author ambud
 */
public class KafkaMBeanUtil {

	public static final String THROUGHPUT = "throughput";
	public static final String BYTES = "bytes";
	private static KafkaMBeanUtil self;
	private static final Logger log = Logger.getLogger(KafkaMBeanUtil.class.getName());
	private static Map<String, Long> throughputMap = new ConcurrentHashMap<>();

	public static KafkaMBeanUtil getInstance(KafkaMonitorConfiguration conf, StorageEngine engine) {
		if (self == null) {
			self = new KafkaMBeanUtil(conf, engine);
		}
		return self;
	}

	public KafkaMBeanUtil(KafkaMonitorConfiguration conf, StorageEngine engine) {
		ScheduledExecutorService es = Executors.newScheduledThreadPool(1);
		es.scheduleAtFixedRate(new JMXMon(conf.getKafkaBroker(), conf.getJmxPort(), engine), 0, 10, TimeUnit.SECONDS);
	}

	public static class JMXMon implements Runnable {

		private String[] brokers;
		private int port;
		private StorageEngine engine;

		public JMXMon(String[] brokers, int port, StorageEngine engine) {
			this.brokers = brokers;
			this.port = port;
			this.engine = engine;
		}

		@Override
		public void run() {
			for (String broker : brokers) {
				try {
					String url = "service:jmx:rmi:///jndi/rmi://" + broker + ":" + port + "/jmxrmi";
					JMXServiceURL serviceUrl = new JMXServiceURL(url);
					JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceUrl, null);
					MBeanServerConnection conn = jmxConnector.getMBeanServerConnection();
					Object attribute = conn.getAttribute(
							new ObjectName("kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec"), "Count");
					throughputMap.put(broker, (Long) attribute);
					DataPoint dp = new DataPoint("kafka", THROUGHPUT, BYTES, Arrays.asList(broker),
							System.currentTimeMillis(), (Long) attribute);
					engine.writeDataPoint(dp);
					log.info("Broker throughput:" + broker + "\t" + attribute);
					
					
					
				} catch (Exception e) {
					log.warn("JMX fetch failed for:" + broker, e);
				}
			}

		}

	}

}
