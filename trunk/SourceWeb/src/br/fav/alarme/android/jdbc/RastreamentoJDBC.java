package br.fav.alarme.android.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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

	public Rastreamento obterUltimoRegistro(int idCliente) {
		try {
			JDBCConnector conn = new JDBCConnector();
			conn.getConnection();
			String sql = " SELECT * FROM rastreamento WHERE id_carro = ? ORDER BY data DESC";
			PreparedStatement pstmt;
			pstmt = conn.getConnection().prepareStatement(sql);
			pstmt.setInt(1, idCliente);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				Rastreamento r = new Rastreamento();
				r.setId(rs.getInt(1));
				r.setIdCarro(rs.getInt(2));
				r.setLatitude(rs.getString(3));
				r.setLongitude(rs.getString(4));
				r.setData(new Date(rs.getTimestamp(5).getTime()));
				return r;
			}
			conn.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
