import org.jfree.pdf.PDFDocument;
import org.jfree.pdf.PDFGraphics2D;
import org.jfree.pdf.Page;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Orders {
    private JFormattedTextField ManagerTitle;
    private JButton logOutButton;
    private JButton homeButton;
    private JTable OrderTable;
    private JFormattedTextField supplierFormattedTextField;
    private JComboBox supplierDropDown;
    JPanel OrderScreen;
    private JButton clearOrderButton;
    private JButton submitOrderButton;
    private JCheckBox saveToPDFCheckBox;
    private JScrollPane orderTableForPrinting;


    public void setUpTable(){
        DBConnection con = new DBConnection();
        DefaultTableModel model = new DefaultTableModel() {
            boolean[] canEdit = new boolean[]{
                    false, false, false, false, true
            };
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        OrderTable.setModel(model);
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Price");
        model.addColumn("Stock");
        model.addColumn("# to Order");
        int[] ids = con.getAllProductIds();
        String supplierString = supplierDropDown.getSelectedItem().toString();
        int supplierId = Integer.parseInt(supplierString.substring(supplierString.indexOf('#') + 1, supplierString.indexOf(':')));
        for(int id : ids){
            if(con.getSupplierNameById(supplierId).equals(con.getProductSupplier(id))) {
                String name = con.getProductName(id);
                double price = con.getProductPrice(id);
                int stock = con.getProductStock(id);
                int OrderValue = 0;
                model.insertRow(0, new Object[]{"" + id, name, price, "" + stock, "" + OrderValue});
            }
        }
        con.close();
    }
    public Orders() {
        DBConnection con = new DBConnection();
        int[] supplierIds = con.getAllSuppliers();
        String[] suppliersStrings = new String[supplierIds.length];
        int i = 0;
        for(int id : supplierIds){
            suppliersStrings[i++] = "#" + id + ": " + con.getSupplierNameById(id);
        }
        supplierDropDown.setModel(new DefaultComboBoxModel(suppliersStrings));
        con.close();
        setUpTable();
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.frame.setContentPane(new Manager().ManagerScreen);
                Main.frame.pack();
            }
        });
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.frame.setContentPane(new Login().LoginPanel);
                Main.frame.pack();
            }
        });
        supplierDropDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setUpTable();
            }
        });
        clearOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int j=0;(OrderTable.getModel().getRowCount())>j;j++){
                    OrderTable.getModel().setValueAt("0",j,4);
                }
            }
           });
        submitOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String supplierString = (String)supplierDropDown.getSelectedItem();
                int supplierId = Integer.parseInt(supplierString.substring(supplierString.indexOf('#') + 1, supplierString.indexOf(':')));
                int rows = OrderTable.getModel().getRowCount();
                int[] productIds = new int[rows];
                int[] productAmounts = new int[rows];
                for(int i = 0; i < rows; i++){
                    productIds[i] = Integer.parseInt((String)(OrderTable.getModel().getValueAt(i, 0)));
                    productAmounts[i] = Integer.parseInt((String)(OrderTable.getModel().getValueAt(i, 4)));
                }
                DBConnection con = new DBConnection();
                con.addOrder(productIds, productAmounts, supplierId);
                con.close();

                if(saveToPDFCheckBox.isSelected()){
                    PDFDocument pdfDoc = new PDFDocument();
                    pdfDoc.setTitle("Sales Report");
                    Page page = pdfDoc.createPage(new Rectangle(1123, 794));
                    PDFGraphics2D g2p = page.getGraphics2D();
                    g2p.translate(50, 50);
                    orderTableForPrinting.paint(g2p);

                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("pdf files (*.pdf)", "pdf");
                    fileChooser.addChoosableFileFilter(pdfFilter);
                    fileChooser.setFileFilter(pdfFilter);
                    if(fileChooser.showSaveDialog(Orders.this.OrderScreen) == JFileChooser.APPROVE_OPTION){
                        pdfDoc.writeToFile(fileChooser.getSelectedFile());
                    }
                }

                for(int j=0;(OrderTable.getModel().getRowCount())>j;j++){
                    OrderTable.getModel().setValueAt("0",j,4);
                }
            }
        });
    }

}
