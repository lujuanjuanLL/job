package com.ecd.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jsoup.GetAllData;


/**
 * Servlet implementation class FieldCount
 */
@WebServlet("/FieldCount")
public class FieldCount extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FieldCount() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		String s="";
		GetAllData gData = new GetAllData();
		Map<String, String> maps = null;
		try {
			maps = gData.getResult("fieldCount");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Set<String> sets = maps.keySet();
		for (String str : sets) {
			s += "{value:"+maps.get(str)+",name:'"+str+"'},";
		}
		String strs = "["+s+"]";
		response.getWriter().write(strs);
		response.getWriter().close();
	}

}
