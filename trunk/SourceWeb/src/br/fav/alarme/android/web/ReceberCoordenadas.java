package br.fav.alarme.android.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.fav.alarme.android.jdbc.bean.Rastreamento;
import br.fav.alarme.android.service.RastreamentoService;

public class ReceberCoordenadas extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)  
    			throws IOException, ServletException {  
		
		Rastreamento r = new Rastreamento();
		r.setLatitude(request.getParameter("latitude"));
		r.setLongitude(request.getParameter("longitude"));
		r.setIdCarro(Integer.parseInt(request.getParameter("idCarro")));
		
		RastreamentoService rs = new RastreamentoService();
		rs.criarRegistro(r);
	}

}
