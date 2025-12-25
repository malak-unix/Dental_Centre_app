package ma.dentalTech.mvc.ui.modules.caisse.table;

import ma.dentalTech.mvc.dto.caisse.CaisseFactureRowDTO;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CaisseFacturesTableModel extends AbstractTableModel {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String[] cols = {
            "N° Facture", "Patient", "Médecin", "Date", "Montant", "Statut", "Actions"
    };

    private final List<CaisseFactureRowDTO> rows = new ArrayList<>();

    public void setRows(List<CaisseFactureRowDTO> list) {
        rows.clear();
        if (list != null) rows.addAll(list);
        fireTableDataChanged();
    }

    public CaisseFactureRowDTO getRowAt(int viewRow) {
        if (viewRow < 0 || viewRow >= rows.size()) return null;
        return rows.get(viewRow);
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }

    @Override
    public Object getValueAt(int r, int c) {
        CaisseFactureRowDTO dto = rows.get(r);

        return switch (c) {
            case 0 -> safe(dto.getNumeroFacture());
            case 1 -> safe(dto.getPatientNom());
            case 2 -> safe(dto.getMedecinNom());
            case 3 -> dto.getDateEmission() == null ? "" : DF.format(dto.getDateEmission());
            case 4 -> money(dto.getMontant()) + " DH";
            case 5 -> safe(dto.getStatut());
            case 6 -> dto; // pour renderer/editor actions
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 6 ? Object.class : String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 6;
    }

    private String safe(String s) { return s == null ? "" : s; }

    private String money(Double v) {
        double x = v == null ? 0.0 : v;
        return String.format("%,.2f", x);
    }
}
