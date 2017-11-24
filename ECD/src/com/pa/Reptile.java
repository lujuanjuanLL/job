package com.pa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Reptile {
	public static void main(String[] args) {
		String url = "http://sou.zhaopin.com/jobs/searchresult.ashx?jl=北京&kw=大数据&isadv=0&sg=9b4afe6dae224b08aee1037025e51026&p=";
		int i = 0;
		int num = 0;
		HBaseConfiguration conf = new HBaseConfiguration();
		try {
			HBaseAdmin ha = new HBaseAdmin(conf);
			HTableDescriptor htd = new HTableDescriptor("t123");
			HColumnDescriptor hcd = new HColumnDescriptor(Bytes.toBytes("xinxi"));
			htd.addFamily(hcd);
			ha.createTable(htd);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			while(i<20){
				List<String> list = new ArrayList<>();
				Document doc = Jsoup.connect(url+i).get();
				Element ele = doc.getElementById("newlist_list_content_table");
				Elements zwmc = ele.getElementsByClass("zwmc");
				Elements gsmc = ele.getElementsByClass("gsmc");
				Elements zwyx = ele.getElementsByClass("zwyx");
				Elements gzdd = ele.getElementsByClass("gzdd");
				Elements text = ele.getElementsByClass("newlist_deatil_two");
				HTable ht = new HTable(conf, "t123");
				Put put = null;
				for(int j=1;j<zwmc.size();j++){
					num++;
					put = new Put(Bytes.toBytes("r"+num));
					put.add(Bytes.toBytes("xinxi"), Bytes.toBytes("zwmc"), Bytes.toBytes(zwmc.get(j).text()));
					put.add(Bytes.toBytes("xinxi"), Bytes.toBytes("gsmc"), Bytes.toBytes(gsmc.get(j).text()));
					put.add(Bytes.toBytes("xinxi"), Bytes.toBytes("zwyx"), Bytes.toBytes(zwyx.get(j).text()));
					put.add(Bytes.toBytes("xinxi"), Bytes.toBytes("gzdd"), Bytes.toBytes(gzdd.get(j).text()));
					put.add(Bytes.toBytes("xinxi"), Bytes.toBytes("text"), Bytes.toBytes(text.get(j-1).text()));
					ht.put(put);
					System.out.println("已加入"+num+"条数据");
				}
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
