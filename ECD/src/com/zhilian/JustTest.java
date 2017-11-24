package com.zhilian;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

public class JustTest {
	private static HBaseConfiguration conf = new HBaseConfiguration();

	public static HBaseConfiguration getConf() {
		if (conf == null) {
			conf = new HBaseConfiguration();
		}
		return conf;
	}
	public void getALLData(String tableName) {
		try {
			@SuppressWarnings("resource")
			HTable hTable = new HTable(conf, tableName);
			Scan scan = new Scan();
			ResultScanner scanner = hTable.getScanner(scan);
			for (Result result : scanner) {
				if (result.raw().length == 0) {
					System.out.println(tableName + " 表数据为空！");
				} else {
					for (KeyValue kv : result.raw()) {
						System.out.println(new String(kv.getKey()) + "\t" + new String(kv.getValue()));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		
	}
}
