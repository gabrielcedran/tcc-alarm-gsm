package br.fav.alarme.android.service;

import java.util.Date;

import br.fav.alarme.android.jdbc.RastreamentoJDBC;
import br.fav.alarme.android.jdbc.bean.Carro;
import br.fav.alarme.android.jdbc.bean.Rastreamento;

public class RastreamentoService {

	public void criarRegistro(Rastreamento rastreamento) {
		RastreamentoJDBC r = new RastreamentoJDBC();
		rastreamento.setData(new Date());
		r.criarRegistro(rastreamento);
	}
	

	public Rastreamento obterUltimoRegistro(int idCliente, String senha) {
		return new RastreamentoJDBC().obterUltimoRegistro(idCliente, senha);
	}
	
	public Carro obterCarro(int idCliente, String senha) {
		return new RastreamentoJDBC().obterCarro(idCliente, senha);
	}
}
