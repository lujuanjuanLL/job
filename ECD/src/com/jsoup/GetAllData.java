package com.jsoup;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class GetAllData {
	static Configuration conf = null;
	private static final String ZK_CONNECT_STR = "master:2181";
	static {
	conf = HBaseConfiguration.create();
	conf.set("hbase.zookeeper.quorum", ZK_CONNECT_STR);
	}
	/**
	*   根据 rwokey 查询
	* @rowKey rowKey 
	* @tableName  表名
	*/
	public  Map<String, String> getResult(String tableName) throws Exception {
		Scan scan = new Scan();
		ResultScanner rs = null;
		Map<String, String> map = new HashMap<String,String>();
		String adress ="";
		String number ="";
		HTable table = new HTable(conf, Bytes.toBytes(tableName));
			rs = table.getScanner(scan);
			for (Result r : rs) {
				for (KeyValue kv : r.list()) {
					if(Bytes.toString(kv.getQualifier()).equals("addr"))
					{
						adress = Bytes.toString(kv.getValue());
					}
					if(Bytes.toString(kv.getQualifier()).equals("number"))
					{
						number = Bytes.toString(kv.getValue());
					}
					map.put(adress, number);
				}
			}

	
	return map;
	}
	public static void main(String[] args) throws Exception {
		GetAllData hbd = new GetAllData();
		Map<String, String> map = null;
		try {
			map = hbd.getResult("fieldCount");
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (String in : map.keySet()) {
			System.out.println(map.get(in));
			System.out.println(in);
		}
	}
}
