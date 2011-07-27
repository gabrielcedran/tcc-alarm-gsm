package br.fav.alarme.android.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReceberCoordenadas extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)  
    			throws IOException, ServletException {  
		
		PrintWriter print = response.getWriter();
		print.println("Resposta do servidor!!!");
		print.print("Latitude=");
		print.println(request.getParameter("latitude"));
		System.out.println("Latitude="+request.getParameter("latitude"));
		print.print("Longitude=");
		print.println(request.getParameter("longitude"));
		System.out.println("Longitude="+request.getParameter("longitude"));
		print.print("IdCarro=");
		print.println(request.getParameter("idCarro"));
		System.out.println("IdCarro="+request.getParameter("idCarro"));
		
	}

}
