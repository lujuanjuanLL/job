package com.test;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MyServlet_2
 */
@WebServlet("/MyServlet_2")
public class MyServlet_2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MyServlet_2() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF-8");
		String s1 = "";
		String s2 = "";
		HBaseDao hb = new HBaseDao();
		Map<String, String> map = hb.getResultScann1("job2");
		Set<String> set = map.keySet();
		int count = 1;
		for (String str : set) {
			if (!"".equals(str)) {
				if (count < set.size()) {
					s1 += map.get(str) + ",";
					s2 += "'" + str + "',";
				} else {
					s1 += map.get(str);
					s2 += "'" + str + "'";
				}
			}
			count++;

		}
		String strs = "[" + s1 + "]" + "\t" + "[" + s2 + "]";
		response.getWriter().write(strs);
		response.getWriter().close();
	}

}
