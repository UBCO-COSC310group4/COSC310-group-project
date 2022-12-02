import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.pdf.PDFDocument;
import org.jfree.pdf.PDFGraphics2D;
import org.jfree.pdf.PDFHints;
import org.jfree.pdf.Page;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.channels.FileChannel;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Manager {
    public JPanel ManagerScreen;
    private JTabbedPane tabbedPane;
    private JTable InventoryTable; // Display Inventory Data from Database
    private JList SalesList;
    private JList WarningsList;
    private JFormattedTextField SalesTitle;
    private JFormattedTextField RecentSalesTitle;
    private JProgressBar SalesProgress;
    private JFormattedTextField managerFormattedTextField;
    private JButton ManageEmployeesButton;
    private JButton suppliersButton;
    private JButton logOutButton;
    private JFormattedTextField ManagerTitle;
    private JTextPane SaleDetails;
    private JButton saveChagesButton;
    private JButton resetButton;
    private JLabel inventoryMessageOutput;
    private JTextField nameTextField;
    private JTextField priceTextField;
    private JTextField stockTextField;
    private JButton addProductButton;
    private JComboBox supplierDropDown;
    private JButton newOrderButton;
    private JTextPane OrderDetails;
    private JList Orders;
    private JSpinner daysNumSpinner;
    private JPanel chartPanel;
    private JButton writeToPdfButton;
    private JTextField totalTextField;

    private JFreeChart chart;
    NumberFormat moneyFormat;

    private void updateCharts(){
        chartPanel.setLayout(new FlowLayout((FlowLayout.CENTER)));

        SpinnerModel value = new SpinnerNumberModel(30, 0, 600, 10);
        daysNumSpinner.setModel(value);
        daysNumSpinner.setBounds(50, 80, 70, 100);
//        ((JSpinner.DefaultEditor) daysNumSpinner.getEditor()).getTextField().setEditable(false);
        daysNumSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                chartPanel.removeAll();
                chartPanel.add(createChart((int)daysNumSpinner.getValue()));
                chartPanel.revalidate();
                chartPanel.repaint();
            }
        });

        chartPanel.removeAll();
        chartPanel.add(createChart((int)daysNumSpinner.getValue()));
    }

    private ChartPanel createChart(int daysNum){
        TimeSeries timeSeries = new TimeSeries("Date");

        LocalDate rangeEnd = LocalDate.now().plusDays(1);
        LocalDate rangeStart = rangeEnd.minusDays(daysNum);
        int days = (int)ChronoUnit.DAYS.between(rangeStart, rangeEnd);
        for(int i = 0; i < days; i++){
            DBConnection con = new DBConnection();
            LocalDate day = rangeStart.plusDays(i);
            timeSeries.add(new Day(day.getDayOfMonth(), day.getMonthValue(), day.getYear()), con.getSalesByDate(day));
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(timeSeries);

        chart = ChartFactory.createXYBarChart(
                "Sales Report",              // title
                "Date",             // x-axis label
                true,               // date axis?
                "Sales ($)",           // y-axis label
                dataset,            // data
                PlotOrientation.VERTICAL,       // orientation
                false,               // create legend?
                true,               // generate tooltips?
                false               // generate URLs?
        );

        XYPlot plot = chart.getXYPlot();
        DateAxis xAxis = (DateAxis) plot.getDomainAxis();
        xAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY,Math.ceilDiv(daysNum + 1, 30)));
        xAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        xAxis.setVerticalTickLabels(true);

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new java.awt.Dimension(550, 400));
        return panel;
    }

    private void updateSales(){
        DBConnection con = new DBConnection();
        SaleDetails.setText("");
        double monthlySales = con.getThisMonthsSales();
        SalesProgress.setString(moneyFormat.format(monthlySales));
        SalesProgress.setValue((int)monthlySales);
        int[] sales = con.getAllSaleIds();
        String[] salesInfo = new String[sales.length];
        int i = 0;
        for(int id : sales){
            salesInfo[i++] = "Sale #" + id + ":  " + con.getSaleInfoById(id);
        }
        SalesList.setListData(salesInfo);
    }

    private void updateInventory(){
        inventoryMessageOutput.setText("");
        DBConnection con = new DBConnection();
        int[] supplierIds = con.getAllSuppliers();
        String[] suppliers = new String[supplierIds.length];
        int i = 0;
        for(int id : supplierIds){
            suppliers[i++] = "#" + id + ": " + con.getSupplierNameById(id);
        }
        supplierDropDown.setModel(new DefaultComboBoxModel(suppliers));
        DefaultTableModel model = new DefaultTableModel() {
            boolean[] canEdit = new boolean[]{
                    false, false, false, true, true
            };
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        InventoryTable.setModel(model);
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Supplier");
        model.addColumn("Price");
        model.addColumn("Stock");
        int[] ids = con.getAllProductIds();
        for(int id : ids){
            String name = con.getProductName(id);
            String supplier = con.getProductSupplier(id);
            double price = con.getProductPrice(id);
            int stock = con.getProductStock(id);
            model.insertRow(0, new Object[] {"" + id, name, supplier, "" + price, "" + stock});
        }
        con.close();
    }

    private void updateWarnings(){
        DBConnection con = new DBConnection();
        int[] ids = con.getLowStockProductIds();
        String[] warnings = new String[ids.length];
        int i = 0;
        for(int id : ids){
            String name = con.getProductName(id);
            int stock = con.getProductStock(id);
            warnings[i++] = "Low Stock: " + name + ", " + stock + " left";
        }
        WarningsList.setListData(warnings);
        con.close();
    }

    private void updateOrders(){
        DBConnection con = new DBConnection();
        OrderDetails.setText("");
        int[] orders = con.getAllOrderIds();
        String[] ordersInfo = new String[orders.length];
        int i = 0;
        for(int id : orders){
            ordersInfo[i++] = "Order #" + id + ":  " + con.getOrderInfoById(id);
        }
        Orders.setListData(ordersInfo);
    }
    public Manager() {
        moneyFormat = NumberFormat.getCurrencyInstance();
        updateSales();
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                switch(index){
                    case 0: updateSales();
                    case 1: updateInventory();
                    case 2: updateWarnings();
                    case 3: updateOrders();
                    case 4: updateCharts();
                }
            }
        });

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.frame.setContentPane(new Login().LoginPanel);
                Main.frame.pack();
            }
        });

        SalesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                JList source = (JList)e.getSource();
                if(source.isSelectionEmpty()) return;
                String selected = source.getSelectedValue().toString();
                int purchaseId = Integer.parseInt(selected.substring(selected.indexOf('#') + 1, selected.indexOf(':')));
                DBConnection con = new DBConnection();
                String items = con.getSaleItemsById(purchaseId);
                SaleDetails.setText(items);
                con.close();
            }
        });

        Orders.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                JList source = (JList)e.getSource();
                if(source.isSelectionEmpty()) return;
                String selected = source.getSelectedValue().toString();
                int orderId = Integer.parseInt(selected.substring(selected.indexOf('#') + 1, selected.indexOf(':')));
                DBConnection con = new DBConnection();
                String items = con.getOrderItemsById(orderId);
                OrderDetails.setText(items);
                con.close();
            }
        });
        ManageEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.frame.setContentPane(new ManageEmployees().ManageEmployeeScreen);
                Main.frame.pack();
            }
        });
        suppliersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.frame.setContentPane(new Suppliers().SuppliersScreen);
                Main.frame.pack();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateInventory();
            }
        });

        saveChagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int row = 0; row < InventoryTable.getRowCount(); row++){
                    try{
                        int id = Integer.parseInt((String)InventoryTable.getValueAt(row, 0));
                        double price = Double.parseDouble((String)InventoryTable.getValueAt(row, 3));
                        int stock = Integer.parseInt((String)InventoryTable.getValueAt(row, 4));
                        if(price < 0 || stock < 0) throw new IllegalArgumentException();
                        DBConnection con = new DBConnection();
                        con.setProductPrice(id, price);
                        con.setProductStock(id, stock);
                        con.close();
                        inventoryMessageOutput.setText("");
                    }catch(Exception error){
                        inventoryMessageOutput.setText("Invalid value");
                    }
                }
            }
        });

        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameTextField.getText();
                    if(name.equals("")) throw new IllegalArgumentException();
                    Double price = Double.parseDouble(priceTextField.getText());
                    int stock = Integer.parseInt(stockTextField.getText());
                    String supplierString = (String)supplierDropDown.getSelectedItem();
                    int supplier_id = Integer.parseInt(supplierString.substring(supplierString.indexOf('#') + 1, supplierString.indexOf(':')));
                    if(price < 0 || stock < 0) throw new IllegalArgumentException();
                    DBConnection con = new DBConnection();
                    con.addNewProduct(name, price, supplier_id, stock);
                    con.close();
                    inventoryMessageOutput.setText("");
                    updateInventory();
                }catch(Exception error){
                    inventoryMessageOutput.setText("Invalid value");
                }
            }
        });
        newOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.frame.setContentPane(new Orders().OrderScreen);
                Main.frame.pack();
            }
        });

        writeToPdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PDFDocument pdfDoc = new PDFDocument();
                pdfDoc.setTitle("Sales Report");
                Page page2 = pdfDoc.createPage(new Rectangle(794, 1123));
                PDFGraphics2D g2p2 = page2.getGraphics2D();
                chart.draw(g2p2, new Rectangle(91, 300, 612, 468));
                g2p2.setBackground(new Color(255,255,255));
                Page page3 = pdfDoc.createPage(new Rectangle(794, 1123));
                PDFGraphics2D g2p3 = page3.getGraphics2D();
                g2p3.translate(100, 100);
                Manager.this.SalesList.paint(g2p3);

                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("pdf files (*.pdf)", "pdf");
                fileChooser.addChoosableFileFilter(pdfFilter);
                fileChooser.setFileFilter(pdfFilter);
                if(fileChooser.showSaveDialog(Manager.this.ManagerScreen) == JFileChooser.APPROVE_OPTION){
                    pdfDoc.writeToFile(fileChooser.getSelectedFile());
                }
            }
        });
    }
}
