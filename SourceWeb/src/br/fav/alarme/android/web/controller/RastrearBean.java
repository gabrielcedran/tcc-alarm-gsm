package br.fav.alarme.android.web.controller;

import java.text.SimpleDateFormat;

import br.fav.alarme.android.jdbc.bean.Rastreamento;
import br.fav.alarme.android.service.RastreamentoService;

public class RastrearBean {

	private String idCarro;
	private String senha;
	private String latitude;
	private String longitude;
	private String ultimaAtualizacao;

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getIdCarro() {
		return idCarro;
	}

	public void setIdCarro(String idCarro) {
		this.idCarro = idCarro;
	}
	
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(String ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	public String consultarUltimaLocalizacao() {
		RastreamentoService rs = new RastreamentoService();
		Rastreamento r = rs.obterUltimoRegistro(Integer.parseInt(idCarro), senha);
		if(r == null) {
			return "falha";
		}
		latitude = r.getLatitude();
		longitude = r.getLongitude();
		SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		ultimaAtualizacao =  s.format(r.getData());
		return "sucesso";
	}

}
