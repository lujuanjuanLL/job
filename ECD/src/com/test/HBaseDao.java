package com.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseDao {
	private static HBaseConfiguration conf = new HBaseConfiguration();

	public static HBaseConfiguration getConf() {
		if (conf == null) {
			conf = new HBaseConfiguration();
		}
		return conf;
	}

	public static Map getResultScann(String tableName) throws IOException {
		Scan scan = new Scan();
		ResultScanner rs = null;
		Map<String, String> hm = new HashMap<String,String>();
		String adress ="";
		String number ="";
		HTable table = new HTable(conf, Bytes.toBytes(tableName));
			rs = table.getScanner(scan);
			for (Result r : rs) {
				for (KeyValue kv : r.list()) {
					if(Bytes.toString(kv.getQualifier()).equals("adress"))
					{
						adress = Bytes.toString(kv.getValue());
					}
					if(Bytes.toString(kv.getQualifier()).equals("number"))
					{
						number = Bytes.toString(kv.getValue());
					}
					hm.put(adress, number);
				}
			}
			return hm;
		 
	}
	public static Map getResultScann1(String tableName) throws IOException {
		Scan scan = new Scan();
		ResultScanner rs = null;
		Map<String, String> hm = new TreeMap<String,String>();
		String adress ="";
		String number ="";
		HTable table = new HTable(conf, Bytes.toBytes(tableName));
			rs = table.getScanner(scan);
			for (Result r : rs) {
				for (KeyValue kv : r.list()) {
					if(Bytes.toString(kv.getQualifier()).equals("salary"))
					{
						adress = Bytes.toString(kv.getValue());
					}
					if(Bytes.toString(kv.getQualifier()).equals("number"))
					{
						number = Bytes.toString(kv.getValue());
					}
					hm.put(adress, number);
				}
			}
			return hm;
		 
	}
	public static void getResultScann2(String tableName) throws IOException {
		Scan scan = new Scan();
		ResultScanner rs = null;
		Map<String, String> hm = new HashMap<String,String>();
		String adress ="";
		String number ="";
		HTable table = new HTable(conf, Bytes.toBytes(tableName));
			rs = table.getScanner(scan);
			for (Result r : rs) {
				for (KeyValue kv : r.list()) {
					System.out.println("列名:" + Bytes.toString(kv.getQualifier()));
					System.out.println("row:" + Bytes.toString(kv.getRow()));
					System.out.println("family:" + Bytes.toString(kv.getFamily()));
					System.out.println("value:" + Bytes.toString(kv.getValue()));
					System.out.println("timestamp:" + kv.getTimestamp());
					System.out.println("-------------------------------------------");
				}
			}

		 
	}
	public static void main(String[] args) {
//		HBaseDao hb = new HBaseDao();
//		try {
//			hb.getResultScann2("BigData02.txt");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		HBaseDao hb = new HBaseDao();
		try {
			Map<String,String> map = hb.getResultScann1("job2");
			Set<String> set = map.keySet();
			for (String str : set) {
				System.out.println("salary: "+str+" value:"+map.get(str));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
