package br.fav.alarme.android.jdbc;

import br.fav.alarme.android.jdbc.bean.Rastreamento;

public class TesteBanco {

	public static void main(String[] args) {

		
		RastreamentoJDBC rr = new RastreamentoJDBC();
		Rastreamento r = rr.obterUltimoRegistro(1, "2");
		System.out.println(r.getLatitude());
		System.out.println(r.getData());

		rr.criarRegistro(r);
	}
}
