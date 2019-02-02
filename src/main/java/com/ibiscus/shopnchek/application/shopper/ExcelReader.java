package com.ibiscus.shopnchek.application.shopper;

import com.ibiscus.shopnchek.domain.util.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;

public class ExcelReader implements Iterator<Row> {

    private final Sheet sheet;

    public ExcelReader(Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Row next() {
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove an item from the file");
    }
}
