package com.ibiscus.shopnchek.domain.debt;

import java.util.List;

import org.hibernate.transform.ResultTransformer;

import com.ibiscus.shopnchek.domain.util.Row;

public class RowResultTransformer implements ResultTransformer {

	private static final long serialVersionUID = 6952521605693085842L;

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		Row row = new Row();
		for (int i = 0; i < tuple.length; i++) {
			row.addValue(aliases[i], tuple[i]);
		}
		return row;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List transformList(List collection) {
		return collection;
	}

}
