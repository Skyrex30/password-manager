package table;

import model.PasswordEntry;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordTableModel extends AbstractTableModel {
    private List<PasswordEntry> passwordList;
    private List<PasswordEntry> filteredList;

    public PasswordTableModel(List<PasswordEntry> passwordList) {
        this.passwordList = passwordList;
        this.filteredList = passwordList;
    }

    @Override
    public int getRowCount() {
        return filteredList.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PasswordEntry entry = filteredList.get(rowIndex);
        if (columnIndex == 0) {
            return entry.getWebsite();
        } else {
            return entry.getPassword();
        }
    }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? "Website" : "Password";
    }

    public void filter(String query) {
        if (query.isEmpty()) {
            filteredList = passwordList;
        } else {
            filteredList = passwordList.stream()
                    .filter(entry -> entry.getWebsite().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    public void setPasswordList(List<PasswordEntry> passwordList) {
        this.passwordList = passwordList;
        this.filteredList = passwordList;
        fireTableDataChanged();
    }

}