package br.fav.alarme.android.service;

import br.fav.alarme.android.jdbc.RastreamentoJDBC;
import br.fav.alarme.android.jdbc.bean.Rastreamento;

public class RastreamentoService {

	public void criarRegistro(Rastreamento rastreamento) {
		RastreamentoJDBC r = new RastreamentoJDBC();
		r.criarRegistro(rastreamento);
	}
	
}
