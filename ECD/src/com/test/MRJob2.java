package com.test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class MRJob2 {
	public static class MRmap extends Mapper<Object, Text, Text, IntWritable>
	{
		@Override
		protected void map(Object key, Text values, Mapper<Object, Text, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {
			String [] strs = values.toString().split("\t");
			if(strs.length > 2 && strs != null){
				if(strs[2] != null && !strs[2].trim().equals(""))
				{
					String [] strss = strs[2].split("-");
					
					if(strss.length == 2){
						Integer min = Integer.parseInt(strss[0]);
						Integer max = Integer.parseInt(strss[1]);
						if(max < 5000)
						{
							context.write(new Text("0-5000"), new IntWritable(1));
						}
						if(min >= 5000 && max < 10000)
						{
							context.write(new Text("5000-10000"), new IntWritable(1));
						}
						if(min>=10000 && max < 20000)
						{
							context.write(new Text("10000-20000"), new IntWritable(1));
						}
						if(min >= 20000 && max < 30000)
						{
							context.write(new Text("20000-30000"), new IntWritable(1));
						}
						if(max >=30000)
						{
							context.write(new Text("30000以上"), new IntWritable(1));
						}
						
					}else{
						int result = getNumber(strs[2]);
						if(result != 0 && result<5000 && strs[2].contains("以下"))
						{
							context.write(new Text("0-5000"), new IntWritable(1));
						}
						else if(result>5000 && result<=10000 && strs[2].contains("以上"))
						{
							context.write(new Text("5000-10000"), new IntWritable(1));
						}
						else if(result>10000 && result<=20000 && strs[2].contains("以上"))
						{
							context.write(new Text("10000-20000"), new IntWritable(1));
						}else if(result > 20000 && result<=30000 && strs[2].contains("以上"))
						{
							context.write(new Text("20000-30000"), new IntWritable(1));
						}else if(result > 30000 && strs[2].contains("以上"))
						{
							context.write(new Text("30000以上"), new IntWritable(1));
						}else{
							context.write(new Text(strs[2]), new IntWritable(1));
						}
					}
					
				}
			}
			
		}
		public int getNumber(String str)
		{
			Pattern pattern = Pattern.compile("\\d{2,}");
			Matcher matcher = pattern.matcher(str);
			while(matcher.find())
			{

				return Integer.parseInt(matcher.group().equals("")?"0":matcher.group());
			}
			return 0;
		}
	}
	
	public static class MRreducer extends TableReducer<Text, IntWritable,ImmutableBytesWritable>
	{
		int num = 1;
		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable in : values) {
				sum += in.get();
			}
			Put put = new Put(Bytes.toBytes(num+""));
			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("salary"), Bytes.toBytes(key.toString()));
			put.add(Bytes.toBytes("cf1"), Bytes.toBytes("number"), Bytes.toBytes(sum+""));
			context.write(new ImmutableBytesWritable(key.getBytes()),put);
			num++;
		}
		
		
	}
	public static void main(String[] args) throws IllegalArgumentException, IOException, ClassNotFoundException, InterruptedException {
		String tablename = "job2";
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "master:2181,slave:2181,slave02:2181");
		Job job = new Job(conf);
		HBaseAdmin admin = new HBaseAdmin(conf);
		    if(admin.tableExists(tablename)){
		        System.out.println("table exists!recreating.......");
		        admin.disableTable(tablename);
		        admin.deleteTable(tablename);
		    }
		    HTableDescriptor htd = new HTableDescriptor(tablename);
		    HColumnDescriptor tcd = new HColumnDescriptor("cf1");
		    htd.addFamily(tcd);
		    admin.createTable(htd);
		    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		    if (otherArgs.length != 1) {
		      System.err.println("Usage: WordCountHbase <in>");
		      System.exit(2);
		    }

		//jar
		job.setJarByClass(MRJob2.class);
		//mapper and reducer
		job.setMapperClass(MRmap.class);
		TableMapReduceUtil.initTableReducerJob(tablename, MRreducer.class, job);
	    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);
	    System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
