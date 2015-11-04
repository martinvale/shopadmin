package com.ibiscus.shopnchek.application.debt;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibiscus.shopnchek.domain.debt.Branch;
import com.ibiscus.shopnchek.domain.debt.Client;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.TipoItem;
import com.ibiscus.shopnchek.domain.debt.TipoPago;

public class GetIngematicaPendingDebtCommand extends AbstractGetPendingDebtCommand {

	private static Logger logger = Logger.getLogger(GetIngematicaPendingDebtCommand.class.getName());

	private static final String INGEMATICA_FEED_CODE = "ingematica_items_debt";

	public GetIngematicaPendingDebtCommand() {
	}

	@Override
	protected List<Debt> getDebt(final Long lastProcessedId) {
		List<Debt> items = new LinkedList<Debt>();

		CallableStatement cstmt = null;
		ResultSet rs = null;
		try {
			cstmt = getDataSource().getConnection().prepareCall(
					"{call dbo.itemsDeudaIngematica(?)}");

			cstmt.setLong(1, lastProcessedId);
			rs = cstmt.executeQuery();
			while (rs.next()) {
				TipoPago tipoPago = TipoPago.valueOf(rs.getString("tipoPago"));
				Client client = getClient(rs.getString("client"));
				Branch branch = getBranch(client, rs.getString("branch"));
				Debt debt = new Debt(TipoItem.ingematica, tipoPago, rs.getString("documento"),
						rs.getDouble("importe"), rs.getDate("fecha"), null,
						rs.getString("survey"), client, null, branch, null, rs.getLong("externalId"), null);
				items.add(debt);
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, null, ex);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					logger.log(Level.WARNING, null, ex);
				}
			}
			if (cstmt != null) {
				try {
					cstmt.close();
				} catch (SQLException ex) {
					logger.log(Level.WARNING, null, ex);
				}
			}
		}
		return items;
	}

	@Override
	protected String getCode() {
		return INGEMATICA_FEED_CODE;
	}

}
