package com.jsoup;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class FieldDistr {
	// 1.map
	public static class FieldMapper extends Mapper<Object, Text, Text, IntWritable>{
		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] strs = value.toString().split("\t");
			if(null != strs && strs.length == 8){
				String field = strs[3];
				context.write(new Text(field), new IntWritable(1));
			}
		}
	}
	// 2.reduce
	public static class FieldReduce extends TableReducer<Text, IntWritable, ImmutableBytesWritable>{
		int no = 0;
		@Override
		protected void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable i : values) {
				sum += i.get();
			}
			Put put = new Put(Bytes.toBytes(no+""));
			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("addr "), Bytes.toBytes(key.toString()));
			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("number"), Bytes.toBytes(sum+""));
			context.write(new ImmutableBytesWritable(key.getBytes()), put);
			no++;
		}
	}
	// 3.main
	public static void main(String[] args) throws Exception{
		String tablename = "fieldCount";
		Configuration conf = HBaseConfiguration.create();
	    conf.set("hbase.zookeeper.quorum", "Master:2181");
	    HBaseAdmin admin = new HBaseAdmin(conf);
	    if(admin.tableExists(tablename)){
	        System.out.println("table exists!recreating.......");
	        admin.disableTable(tablename);
	        admin.deleteTable(tablename);
	    }
	    HTableDescriptor htd = new HTableDescriptor(tablename);
	    HColumnDescriptor tcd = new HColumnDescriptor("cf1");
	    htd.addFamily(tcd);//创建列族
	    admin.createTable(htd);//创建表
	    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
	    if (otherArgs.length != 1) {
	      System.err.println("Usage: WordCountHbase <in>");
	      System.exit(2);
	    }
	    Job job = new Job(conf, "FieldDistr");
	    job.setJarByClass(FieldDistr.class);
	    //使用WordCountHbaseMapper类完成Map过程；
	    job.setMapperClass(FieldMapper.class);
	    TableMapReduceUtil.initTableReducerJob(tablename, FieldReduce.class, job);
	    //设置任务数据的输入路径；
	    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	    //设置了Map过程和Reduce过程的输出类型，其中设置key的输出类型为Text；
	    job.setOutputKeyClass(Text.class);
	    //设置了Map过程和Reduce过程的输出类型，其中设置value的输出类型为IntWritable；
	    job.setOutputValueClass(IntWritable.class);
	    //调用job.waitForCompletion(true) 执行任务，执行成功后退出；
	    job.waitForCompletion(true);
	}
}