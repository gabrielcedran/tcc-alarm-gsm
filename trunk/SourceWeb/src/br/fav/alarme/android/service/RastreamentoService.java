package br.fav.alarme.android.service;

import java.util.Date;

import br.fav.alarme.android.jdbc.RastreamentoJDBC;
import br.fav.alarme.android.jdbc.bean.Rastreamento;

public class RastreamentoService {

	public void criarRegistro(Rastreamento rastreamento) {
		RastreamentoJDBC r = new RastreamentoJDBC();
		rastreamento.setData(new Date());
		r.criarRegistro(rastreamento);
	}
	

	public Rastreamento obterUltimoRegistro(int idCliente) {
		return new RastreamentoJDBC().obterUltimoRegistro(idCliente);
	}
}
