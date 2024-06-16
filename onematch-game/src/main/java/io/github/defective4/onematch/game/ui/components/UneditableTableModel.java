package io.github.defective4.onematch.game.ui.components;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class UneditableTableModel implements TableModel {
    private final TableModel delegate;

    public UneditableTableModel(TableModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        delegate.addTableModelListener(l);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return delegate.getColumnClass(columnIndex);
    }

    @Override
    public int getColumnCount() {
        return delegate.getColumnCount();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return delegate.getColumnName(columnIndex);
    }

    @Override
    public int getRowCount() {
        return delegate.getRowCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return delegate.getValueAt(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        delegate.removeTableModelListener(l);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        delegate.setValueAt(aValue, rowIndex, columnIndex);
    }

}
