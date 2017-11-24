package com.zhilian;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class HdfsToHBase {
    public static void main(String[] args) throws Exception{
    	System.setProperty("hadoop.home.dir", "D:\\hadoop-2.7.1\\hadoop-2.7.1");
    	//初始化conf文件
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "master:2181");
        conf.set("hbase.rootdir", "hdfs://master:9000/hbase");
        conf.set(TableOutputFormat.OUTPUT_TABLE, args[1]);
        Job job = Job.getInstance(conf, HdfsToHBase.class.getSimpleName());
        TableMapReduceUtil.addDependencyJars(job);
        job.setJarByClass(HdfsToHBase.class);
        
        job.setMapperClass(HdfsToHBaseMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        
        job.setReducerClass(HdfsToHBaseReducer.class);
        
        FileInputFormat.addInputPath(job, new Path(args[0]));
        job.setOutputFormatClass(TableOutputFormat.class);
        job.waitForCompletion(true);
    }
    
    public static class HdfsToHBaseMapper extends Mapper<LongWritable, Text, Text, Text>{
        private Text outKey = new Text();
        private Text outValue = new Text();
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] splits = value.toString().split("\t");
            outKey.set(splits[0]);
            outValue.set(splits[1]+"\t"+splits[2]+"\t"+splits[3]+"\t"+splits[4]+"\t"+splits[5]+"\t"+splits[6]+"\t"+splits[7]+splits[8]+"\t"+splits[9]);
            context.write(outKey, outValue);
        }
    }
    
    public static class HdfsToHBaseReducer extends TableReducer<Text, Text, NullWritable>{
        @Override
        protected void reduce(Text k2, Iterable<Text> v2s, Context context) throws IOException, InterruptedException {
            Put put = new Put(k2.getBytes());
            for (Text v2 : v2s) {
                String[] splis = v2.toString().split("\t");
                if(splis[0]!=null && !"NULL".equals(splis[0])){
                    put.add("f1".getBytes(), "company".getBytes(),splis[0].getBytes());
                }
                if(splis[1]!=null && !"NULL".equals(splis[1])){
                    put.add("f1".getBytes(), "salary".getBytes(),splis[1].getBytes());
                }
                if(splis[2]!=null && !"NULL".equals(splis[2])){
                    put.add("f1".getBytes(), "addr".getBytes(),splis[2].getBytes());
                }
                if(splis[3]!=null && !"NULL".equals(splis[3])){
                    put.add("f1".getBytes(), "addr1".getBytes(),splis[3].getBytes());
                }
                if(splis[4]!=null && !"NULL".equals(splis[4])){
                    put.add("f1".getBytes(), "cNature".getBytes(),splis[4].getBytes());
                }
                if(splis[5]!=null && !"NULL".equals(splis[5])){
                    put.add("f1".getBytes(), "cSize".getBytes(),splis[5].getBytes());
                }
                if(splis[6]!=null && !"NULL".equals(splis[6])){
                    put.add("f1".getBytes(), "exper".getBytes(),splis[6].getBytes());
                }
                if(splis[7]!=null && !"NULL".equals(splis[7])){
                    put.add("f1".getBytes(), "degree".getBytes(),splis[7].getBytes());
                }
            }
            context.write(NullWritable.get(),put);
        }
    }
}
