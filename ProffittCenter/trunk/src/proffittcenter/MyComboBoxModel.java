/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author HP_Owner
 */
public class MyComboBoxModel implements ComboBoxModel {

    protected List data;
    private List listeners;
    protected Object selected;
    private String tableSql;
    private Connection connection;

    public MyComboBoxModel(List list) {
        this.listeners = new ArrayList();
        this.data = list;
        if (list.size() > 0) {
            selected = list.get(0);
        }
    }

    public MyComboBoxModel(List list, Connection connection, String sql, boolean isTable) {
        this.listeners = new ArrayList();
        this.data = list;
        this.connection=connection;
        if (list.size() > 0) {
            selected = list.get(0);
        }
        if (isTable) {
            //table is the name of a table with two fields, ID and Description
            tableSql = "SELECT ID,Description FROM " + sql + " ORDER BY Description";
        } else {
            tableSql = sql;
        }
        try {
            PreparedStatement ps = connection.prepareStatement(tableSql);
            ResultSet rs = ps.executeQuery();
            if (data == null) {
                System.exit(0);
            }
            while (rs.next()) {
                data.add(rs.getString("Description"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyComboBoxModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setSelectedItem(Object item) {
        this.selected = item;
    }

    public Object getSelectedItem() {
        return this.selected;
    }

    public int getSize() {
        return data.size();
    }

    public Object getElementAt(int index) {
        return data.get(index);
    }

    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        this.listeners.remove(l);
    }

    //event code
    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals("update")) {
            this.fireUpdate();
        }
    }

    private void fireUpdate() {
        ListDataEvent listDataEvent = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, data.size());
        for (int i = 0; i < listeners.size(); i++) {
            ListDataListener listDataListener = (ListDataListener) listeners.get(i);
            listDataListener.contentsChanged(listDataEvent);
        }
    }
}
