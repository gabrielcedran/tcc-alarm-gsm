package br.fav.alarme.android.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import br.fav.alarme.android.jdbc.bean.Carro;
import br.fav.alarme.android.jdbc.bean.Rastreamento;

public class RastreamentoJDBC {
	public void criarRegistro(Rastreamento rastreamento) {
		try {
			JDBCConnector conn = new JDBCConnector();
			conn.getConnection();
			String sql = " INSERT INTO rastreamento (id_carro, latitude, longitude, data) VALUES (?,?,?,?)";
			PreparedStatement pstmt;
			pstmt = conn.getConnection().prepareStatement(sql);
			pstmt.setInt(1, rastreamento.getIdCarro());
			pstmt.setString(2, rastreamento.getLatitude());
			pstmt.setString(3, rastreamento.getLongitude());
			pstmt.setTimestamp(4, new java.sql.Timestamp(rastreamento.getData()
					.getTime()));
			pstmt.execute();
			conn.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public Rastreamento obterUltimoRegistro(int idCliente, String senha) {
		try {
			JDBCConnector conn = new JDBCConnector();
			conn.getConnection();
			String sql = " SELECT r.* FROM rastreamento r inner join carro c on  " +
					" r.id_carro = c.id  WHERE c.id = ? and c.senha = ? ORDER BY data DESC";
			PreparedStatement pstmt;
			pstmt = conn.getConnection().prepareStatement(sql);
			pstmt.setInt(1, idCliente);
			pstmt.setString(2, senha);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				Rastreamento r = new Rastreamento();
				r.setId(rs.getInt(1));
				r.setIdCarro(rs.getInt(2));
				r.setLatitude(rs.getString(3));
				r.setLongitude(rs.getString(4));
				r.setData(new Date(rs.getTimestamp(5).getTime()));
				conn.closeConnection();
				return r;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Carro obterCarro(int idCliente, String senha) {
		try {
			JDBCConnector conn = new JDBCConnector();
			conn.getConnection();
			String sql = " SELECT * FROM carro c  WHERE c.id = ? and c.senha = ? ";
			PreparedStatement pstmt;
			pstmt = conn.getConnection().prepareStatement(sql);
			pstmt.setInt(1, idCliente);
			pstmt.setString(2, senha);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				Carro r = new Carro();
				r.setId(rs.getInt(1));
				r.setNome(rs.getString(2));
				r.setSenha(rs.getString(3));
				conn.closeConnection();
				return r;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
