package io.github.defective4.onematch.game.ui.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class JHoverableTable extends JTable {

    private int hoverColumn = -1;
    private int hoverHeaderColumn = -1;
    private int hoverRow = -1;

    public JHoverableTable() {
        register();
    }

    public JHoverableTable(int numRows, int numColumns) {
        super(numRows, numColumns);
        register();
    }

    public JHoverableTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        register();
    }

    public JHoverableTable(TableModel dm) {
        super(dm);
        register();
    }

    public JHoverableTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        register();
    }

    public JHoverableTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        register();
    }

    public JHoverableTable(Vector<? extends Vector<?>> rowData, Vector<?> columnNames) {
        super(rowData, columnNames);
        register();
    }

    public int getHoverColumn() {
        return hoverColumn;
    }

    public int getHoverHeaderColumn() {
        return hoverHeaderColumn;
    }

    public int getHoverRow() {
        return hoverRow;
    }

    private void register() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int localHoverColumn = columnAtPoint(e.getPoint());
                int localHoverRow = rowAtPoint(e.getPoint());
                if (localHoverColumn != hoverColumn || localHoverRow != hoverRow) {
                    repaint();
                }
                hoverColumn = localHoverColumn;
                hoverRow = localHoverRow;
            }
        });

        getTableHeader().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int localHoverHeaderColumn = getTableHeader().columnAtPoint(e.getPoint());
                if (localHoverHeaderColumn != hoverHeaderColumn) {
                    repaint();
                }
                hoverHeaderColumn = localHoverHeaderColumn;
            }
        });

        getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverHeaderColumn = -1;
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverColumn = -1;
                hoverRow = -1;
                repaint();
            }
        });
    }

}
