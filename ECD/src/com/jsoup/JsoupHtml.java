package com.jsoup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupHtml {
	
	// 智联招聘网站
	private String url = "http://sou.zhaopin.com/jobs/searchresult.ashx?jl=%E5%8C%97%E4%BA%AC&kw=%E5%A4%A7%E6%95%B0%E6%8D%AE&sm=0&isfilter=0&fl=530&isadv=0&sg=253e507e28f34c9d84b6aba80b9048ee&p="; 


	public void getZhiLianWork() {
		try {
			for (int i = 1; i <= 2; i++) {
				System.out.println("*********开始遍历第" + (i) + "页的求职信息*********");
				Document doc = Jsoup.connect(url + i).get();
				Element content = doc.getElementById("newlist_list_content_table");
				// 职位名称
				Elements zwmcEls = content.getElementsByClass("zwmc");
				// 公司名称
				Elements gsmcEls = content.getElementsByClass("gsmc");
				// 职位月薪
				Elements zwyxEls = content.getElementsByClass("zwyx");
				// 工作地点
				Elements gzddEls = content.getElementsByClass("gzdd");

				Elements xinxi = doc.getElementsByClass("newlist_deatil_two");
				for (int j = 1; j <= zwmcEls.size(); j++) {
					String info = null;
					if (j < zwmcEls.size()) {
						info = zwmcEls.get(j).tagName("a").text() 
								+ "\t" + gsmcEls.get(j).tagName("a").text() 
								+ "\t" + zwyxEls.get(j).tagName("a").text() 
								+ "\t" + gzddEls.get(j).tagName("a").text();
					}
					if (xinxi.size() > j - 1) {
						Elements x = xinxi.get(j - 1).getAllElements();
						for (int k = 1; k < x.size(); k++) {
							info += "\t" + x.get(k).text();
						}
					}
					
					info+="\n";
					
					write("D:/BigData01.txt", info);

				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write(String src, String content) {
    	// 1、建立联系 File对象 目的地
		File dest = new File(src);
		// 2、选择流 文件输出流 OutputStream FileOutputStream
		OutputStream os = null;
		try {
			// 以追加形式 写出文件 必须为true 否则为覆盖
			os = new FileOutputStream(dest, true);
			// 3、操作
			String str = content;
			// 字符串转字节数组
			byte[] data = str.getBytes();
			os.write(data, 0, data.length);

			os.flush(); // 强制刷新出去
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("文件未找到");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("文件写出失败");
		} finally {
			// 4、释放资源 :关闭
			try {
				if (null != os) {
					os.close();
				}
			} catch (Exception e2) {
				System.out.println("关闭输出流失败");
			}
		}
    }

	public static void main(String[] args) {

		JsoupHtml jHtml = new JsoupHtml();
		jHtml.getZhiLianWork();

	}

}

