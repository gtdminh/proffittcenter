/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;

/**
 *
 * @author Dave
 */
public class CashUp {

    ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/CashUp");
    private String cashupSales = SQL.cashupSales;
    private String saveCashupSQL = SQL.saveCashupSQL;
    private String paidOutTransactions = SQL.paidOutTransactions;
    private String findReceived = "SELECT Sales.*,Customers.* FROM Sales LEFT JOIN Customers ON Sales.Customer=Customers.ID WHERE TillID=? AND Sales.WhenCreated>=? AND Sales.WhenCreated<=? AND Waste="
            + SaleType.RECEIVEDONACCOUNT.valueString();
    int taxTotal;
    int chargedTotalIncTax;
    int ownUseAgencyTotal;
    int rentalTotal;
    int netTotal;
    int ownUseTotal;
    private int takings;
    private int billPayment;
    private int charged;
    private int debits;
    private int tillId;
    private int operator;
    private int cashupId;
    private String opertorName;
    Timestamp startTimestamp, endTimestamp;
    private int paidOuts;
    private int chequeTotal;
    private String lastCashupSQL = "SELECT ID,WhenCreated FROM CashUps WHERE TillID=? ORDER BY ID DESC LIMIT 1";
    private int fixedFloat;
    private int tax;
    private int debit;
    private Regime rr;
    private JOptionPane optionPane;
    private int saleTotal;
    private int billPaymentTotal;
    private int chargedTotal;
    private int couponTotal;
    private int debitTotal;
    private int option;
    private int cash;
    private int receivedTotal;
    private boolean isReceived;
    private boolean transactions;

    public boolean execute(int fixedFloat) {
        this.fixedFloat = fixedFloat;
        opertorName = Main.operator.operatorName;
        tillId = Main.shop.getTillId();
        startTimestamp = Timestamp.valueOf("2000-01-01 00:00:00");
        operator = Main.operator.getOperator();
        //find last entry in cashup table.
        try {
            PreparedStatement lcs = Main.getConnection().prepareStatement(lastCashupSQL);
            lcs.setInt(1, Main.shop.getTillId());
            ResultSet rs = lcs.executeQuery();
            if (rs.next()) {
                startTimestamp = rs.getTimestamp("WhenCreated");
            } else {
                startTimestamp = Timestamp.valueOf("2000-01-01 00:00:00");
            }
            lcs.close();
        } catch (SQLException ex) {
            Audio.play("Ring");
            return false;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String endDate = dateFormat.format(date);
        String startDate = dateFormat.format(startTimestamp);
        if (calculateSales(startTimestamp, endDate)) {
            Main.pole.execute(Main.customerMessages.getString("Sorry"), Main.customerMessages.getString("OutOfService"));
            //need to show same data on a panel, in case printer not working
            rr = Main.shop.regimeIs();
            if (rr == Regime.NONE || rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
                tax = 0;//so not tax added
            } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
                //add tax to amounts
            }
            //above needs to be Internationalized
            if (!Main.hardware.isInvoicePrinter()) {
                do {
                    Audio.play("Tada");
                    if (Main.operator.isOwnerManagerOnly()) {//hide
                        if (JOptionPane.showConfirmDialog(null,
                                "Start Date = " + startDate
                                + "\nEnd Date = " + endDate
                                + "\nOperator = " + opertorName
                                + "\nTill ID = " + tillId
                                + "\nFloat = " + (new Money(fixedFloat)).toString()
                                + "\nTakings inc. any tax = " + (new Money(takings + tax)).toString()
                                + "\nCharged inc. any tax = " + (new Money(charged + tax)).toString()
                                //                            + "\nAgency = " + (new Money(billPayment)).toString()
                                + "\nDebits = " + (new Money(debitTotal)).toString()
                                + "\nCheques = " + (new Money(chequeTotal)).toString()
                                + "\nCoupons = " + (new Money(couponTotal)).toString()
                                + "\nPaid outs = " + (new Money(paidOuts)).toString()
                                + "\nReceived on account = " + (new Money(receivedTotal)).toString()
                                + "\nCash in drawer = " + (new Money(cashInDrawer())).toString(),
                                "Proceed", JOptionPane.YES_OPTION) != JOptionPane.YES_OPTION) {
                            return false;
                        }
                        Main.receiptPrinter.cashDrawerOpen();
                        Audio.play("CashReg");
                        //now write data to receipt printer
                        Main.receiptPrinter.startReceipt();
                        Main.receiptPrinter.printLine("Cashup ID = " + cashupId);
                        Main.receiptPrinter.printLine("Start Date = " + startDate);
                        Main.receiptPrinter.printLine("End date = " + endDate);
                        Main.receiptPrinter.printLine("Operator = " + opertorName);
                        Main.receiptPrinter.printLine("Till ID = " + tillId);
                        Main.receiptPrinter.printLine("Float = " + (new Money(fixedFloat)).toString());
                        Main.receiptPrinter.printLine("Takings = " + (new Money(takings + tax)).toString());
                        Main.receiptPrinter.printLine("Charge = " + (new Money(charged + tax)).toString());
//                    Main.receiptPrinter.printLine("Agency = " + (new Money(billPayment)).toString());
                        Main.receiptPrinter.printLine("Debits = " + (new Money(debitTotal)).toString());
                        Main.receiptPrinter.printLine("Cheques = " + (new Money(chequeTotal)).toString());
                        Main.receiptPrinter.printLine("Coupons = " + (new Money(couponTotal)).toString());
                        Main.receiptPrinter.printLine("Paid outs = " + (new Money(paidOuts)).toString());
                        Main.receiptPrinter.printLine("Received on account = " + (new Money(receivedTotal)).toString());
                        if (Main.operator.isOwnerManager()) {
                            Main.receiptPrinter.printLine("Cash in drawer = "
                                    + (new Money(cashInDrawer())).toString());
                        }
                        Main.receiptPrinter.endReceipt();
                        try {
                            Thread.sleep(1000); // do nothing for 1000 miliseconds (1 second)
                            //was this necessary?
                        } catch (InterruptedException e) {
                            Audio.play("Ring");
                            return false;
                        }
                        option = JOptionPane.showConfirmDialog(null,
                                bundle.getString("PrintOK"),
                                bundle.getString("ReceiptPrinted"),
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        if (JOptionPane.showConfirmDialog(null,
                                "Start Date = " + startDate
                                + "\nEnd Date = " + endDate
                                + "\nOperator = " + opertorName
                                + "\nTill ID = " + tillId
                                + "\nFloat = " + (new Money(fixedFloat)).toString()
                                + "\nTakings inc. any tax = " + (new Money(takings + tax)).toString()
                                + "\nCharged inc. any tax = " + (new Money(charged + tax)).toString()
                                //                            + "\nAgency = " + (new Money(billPayment)).toString()
                                + "\nDebits = " + (new Money(debitTotal)).toString()
                                + "\nCheques = " + (new Money(chequeTotal)).toString()
                                + "\nCoupons = " + (new Money(couponTotal)).toString()
                                + "\nPaid outs = " + (new Money(paidOuts)).toString()
                                + "\nReceived on account = " + (new Money(receivedTotal)).toString(),
                                //                                + "\nCash in drawer = " + (new Money(cashInDrawer())).toString(),
                                "Proceed", JOptionPane.YES_OPTION) != JOptionPane.YES_OPTION) {
                            return false;
                        }
                        Main.receiptPrinter.cashDrawerOpen();
                        Audio.play("CashReg");
                        //now write data to receipt printer
                        Main.receiptPrinter.startReceipt();
                        Main.receiptPrinter.printLine("Cashup ID = " + cashupId);
                        Main.receiptPrinter.printLine("Start Date = " + startDate);
                        Main.receiptPrinter.printLine("End date = " + endDate);
                        Main.receiptPrinter.printLine("Operator = " + opertorName);
                        Main.receiptPrinter.printLine("Till ID = " + tillId);
                        Main.receiptPrinter.printLine("Float = " + (new Money(fixedFloat)).toString());
                        Main.receiptPrinter.printLine("Takings = " + (new Money(takings + tax)).toString());
                        Main.receiptPrinter.printLine("Charge = " + (new Money(charged + tax)).toString());
//                    Main.receiptPrinter.printLine("Agency = " + (new Money(billPayment)).toString());
                        Main.receiptPrinter.printLine("Debits = " + (new Money(debitTotal)).toString());
                        Main.receiptPrinter.printLine("Cheques = " + (new Money(chequeTotal)).toString());
                        Main.receiptPrinter.printLine("Coupons = " + (new Money(couponTotal)).toString());
                        Main.receiptPrinter.printLine("Paid outs = " + (new Money(paidOuts)).toString());
                        Main.receiptPrinter.printLine("Received on account = " + (new Money(receivedTotal)).toString());
                        if (Main.operator.isOwnerManager()) {
                            Main.receiptPrinter.printLine("Cash in drawer = "
                                    + (new Money(cashInDrawer())).toString());
                        }
                        Main.receiptPrinter.endReceipt();
                        try {
                            Thread.sleep(1000); // do nothing for 1000 miliseconds (1 second)
                            //was this necessary?
                        } catch (InterruptedException e) {
                            Audio.play("Ring");
                            return false;
                        }
                        option = JOptionPane.showConfirmDialog(null,
                                bundle.getString("PrintOK"),
                                bundle.getString("ReceiptPrinted"),
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } while (option == JOptionPane.NO_OPTION);
                if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
                    return false;
                }
                saveCashup();
                option = JOptionPane.showConfirmDialog(null,
                        bundle.getString("Reconcile"),
                        bundle.getString("ReconcileOrCloseDown"),
                        JOptionPane.INFORMATION_MESSAGE);
                if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION || option == JOptionPane.NO_OPTION) {
                    return true;
                } else {
                    if (Main.cashupReconciliation.execute(cashupId)) {
                        return true;
                    } else {
                        Audio.play("Ring");
                    }
                    return true;
                }
            } else {
                do {
                    Audio.play("Tada");
                    if (JOptionPane.showConfirmDialog(null,
                            "Start Date = " + startDate
                            + "\nEnd Date = " + endDate
                            + "\nOperator = " + opertorName
                            + "\nTill ID = " + tillId
                            + "\nFloat = " + (new Money(fixedFloat)).toString()
                            + "\nTakings inc. any tax = " + (new Money(takings + tax)).toString()
                            + "\nCharged inc. any tax = " + (new Money(charged + tax)).toString()
                            //                    + "\nAgency = " + (new Money(agency)).toString()
                            + "\nDebits = " + (new Money(debitTotal)).toString()
                            + "\nCheques = " + (new Money(chequeTotal)).toString()
                            + "\nCoupons = " + (new Money(couponTotal)).toString()
                            + "\nPaid outs = " + (new Money(paidOuts)).toString()
                            + "\nReceived on account = " + (new Money(receivedTotal)).toString(),
                            "Proceed", JOptionPane.YES_OPTION) != JOptionPane.YES_OPTION) {
                        return false;
                    }
                    Main.receiptPrinter.cashDrawerOpen();
                    Audio.play("CashReg");
                    File fc = Main.salesScreen.getJasper("CashUp");
                    //need to write to another printer
                    Map<String, Object> parameters = new HashMap<String, Object>();
                    parameters.put("Cashup_ID", cashupId);
                    parameters.put("startDate", startTimestamp);
                    parameters.put("endDate", endDate);
                    parameters.put("Operator", opertorName);
                    parameters.put("Till_ID", tillId);
                    parameters.put("fixedFloat", (new Money(fixedFloat)).toString());
                    parameters.put("Takings", (new Money(takings + tax)).toString());
                    parameters.put("Agency", (new Money(billPayment)).toString());
                    parameters.put("Charge", (new Money(charged + tax)).toString());
                    parameters.put("Agency", (new Money(billPayment)).toString());
                    parameters.put("startDate", startDate);
                    parameters.put("debitTotal", (new Money(debitTotal)).toString());
                    parameters.put("chequeTotal", (new Money(chequeTotal)).toString());
                    parameters.put("couponTotal", (new Money(debitTotal)).toString());
                    parameters.put("paidOuts", (new Money(paidOuts)).toString());
                    parameters.put("received", (new Money(receivedTotal)).toString());
//                    File defaultDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory();
//                    File fc = new File(Main.salesScreen.defaultDirectory + "ProffittCenterReports/CashUp.jasper");
                    Connection con = Main.getConnection();
                    try {
                        JasperPrint jasperPrint = null;
                        jasperPrint = JasperFillManager.fillReport(fc.getAbsolutePath(), parameters, con);
//                        JasperViewer.viewReport(jasperPrint);
                        JasperPrintManager.printReport(jasperPrint, true);
                    } catch (JRException ex) {
                        Audio.play("Ring");
                        return false;
                    }
                    option = JOptionPane.showConfirmDialog(null,
                            bundle.getString("PrintOK"),
                            bundle.getString("ReceiptPrinted"),
                            JOptionPane.INFORMATION_MESSAGE);
                } while (option == JOptionPane.NO_OPTION);
                if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
                    return false;
                }
                saveCashup();
                try {
                    Thread.sleep(1000); // do nothing for 1000 miliseconds (1 second)
                    //was this necessary?
                } catch (InterruptedException e) {
                    Audio.play("Ring");
                }
                option = JOptionPane.showConfirmDialog(null,
                        bundle.getString("Reconcile"),
                        bundle.getString("ReconcileOrCloseDown"),
                        JOptionPane.INFORMATION_MESSAGE);
                if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION || option == JOptionPane.NO_OPTION) {
                    return true;
                } else {
                    if (Main.cashupReconciliation.execute(cashupId)) {
                        return true;
                    } else {
                        Audio.play("Ring");
                    }
                    return true;
                }
            }
        } else {
            Audio.play("Tada");
            JOptionPane.showMessageDialog(null,
                    bundle.getString("NoSales"),
                    bundle.getString("Warning"), JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }

    private boolean calculateSales(java.sql.Timestamp startWhen, String endWhen) {
        int total;
        int tax;
        int cheque;
        int coupon;
        short waste;
        boolean isCharged;
        boolean isOwnUse;
        boolean isWaste;
        int quantity;
        int charge;
        int rate;
        int aDepartment;
        //used by cahup to calculate taking since last cashup on this till
        try {
            transactions = true;//true if any transactions in period
            saleTotal = 0;
            billPaymentTotal = 0;
            chargedTotal = 0;
            couponTotal = 0;
            debit = 0;
            debitTotal = 0;
            chequeTotal = 0;
            paidOuts = 0;
            receivedTotal = 0;
            isCharged = false;
            //now scan though sales
            PreparedStatement salesQuery =
                    Main.getConnection().prepareStatement(cashupSales);
            salesQuery.setInt(1, tillId);
            salesQuery.setInt(2, tillId);
            salesQuery.setTimestamp(3, startWhen);
            salesQuery.setString(4, endWhen);
            ResultSet rs = salesQuery.executeQuery();
            if (rs.first()) {//not if there are none
                do {
                    total = rs.getInt("Total");
                    tax = rs.getInt("Sales.Tax");
                    cheque = rs.getInt("Cheque");
                    debit = rs.getInt("Debit");
                    coupon = rs.getInt("Coupon");
                    waste = rs.getShort("Waste");
                    isCharged = (waste == SaleType.CHARGED.value());
                    isOwnUse = (waste == SaleType.OWNUSE.value());
                    isReceived = (waste == SaleType.RECEIVEDONACCOUNT.value());
                    //(waste == 3) || (waste == 4)
                    isWaste = (waste == SaleType.WASTE.value()) || (waste == SaleType.RETURN.value()) || (waste == SaleType.LOSS.value());
                    if (waste == SaleType.CHARGED.value()) {
                        taxTotal += tax;
                        chargedTotal += total;
                    }
                    if (waste == 0 || isCharged) {
                        saleTotal += total;
                        taxTotal += tax;
                    }
                    if (isReceived) {
                        receivedTotal += total;
                    }
                    debitTotal += debit;
                    couponTotal += coupon;
                    chequeTotal += cheque;
                } while (rs.next());
                if (rr == Regime.REGISTERED || rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
                    netTotal = saleTotal - taxTotal - rentalTotal + ownUseTotal;
                } else {
                    netTotal = saleTotal - rentalTotal + ownUseTotal;
                }
            } else {
                transactions = false;
            }
            takings = saleTotal;
            billPayment = billPaymentTotal;//not used
            charged = chargedTotal;
            PreparedStatement paidOutQuery =
                    Main.getConnection().prepareStatement(paidOutTransactions);
            paidOutQuery.setInt(1, tillId);
            paidOutQuery.setInt(2, tillId);
            paidOutQuery.setTimestamp(3, startWhen);
            paidOutQuery.setString(4, endWhen);
            rs = paidOutQuery.executeQuery();
            while (rs.next()) {
                paidOuts += rs.getInt("Amount");
                transactions = true;
            }
            return transactions;
        } catch (SQLException ex) {
            Audio.play("Ring");
            return false;
        }
    }

    private boolean saveCashup() {
        PreparedStatement sc;
        try {
            sc = Main.getConnection().prepareStatement(saveCashupSQL);
            sc.setNull(1, Types.INTEGER); //ID
            sc.setInt(2, tillId);
            sc.setInt(3, operator);
            sc.setInt(4, takings + tax);//is this right?
            sc.setInt(5, charged + tax);
            sc.setInt(6, billPayment);
            sc.setTimestamp(7, startTimestamp);
            sc.setNull(8, Types.DATE);
            sc.setInt(9, 0);//reconciled = 0
            sc.setInt(10, fixedFloat);
            sc.setInt(11, receivedTotal);
            sc.execute();
            PreparedStatement np1 = Main.getConnection().prepareStatement(
                    SQL.lastCashup);
            ResultSet rs = np1.executeQuery();
            rs.first();
            cashupId = rs.getInt(1);
            rs.close();
        } catch (SQLException ex) {
            Audio.play("Ring");
//            ex.printStackTrace();
            return false;
        }
        Main.shop.setShiftFloat(0);
        return true;
    }

    public int cashInDrawer() {
        try {
            tillId = Main.shop.getTillId();
            PreparedStatement lcs = Main.getConnection().prepareStatement(lastCashupSQL);
            lcs.setInt(1, tillId);
            ResultSet rs = lcs.executeQuery();
            if (rs.next()) {
                startTimestamp = rs.getTimestamp("WhenCreated");
            } else {
                startTimestamp = Timestamp.valueOf("2000-01-01 00:00:00");
            }
            lcs.close();
        } catch (SQLException ex) {
            Audio.play("Ring");
            return -1;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String endDate = dateFormat.format(date);
        String startDate = dateFormat.format(startTimestamp);
        if (calculateSales(startTimestamp, endDate)) {

            rr = Main.shop.regimeIs();
            if (rr == Regime.NONE || rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
                tax = 0;//so not tax added
            } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
                //add tax to amounts
            }
        }
        fixedFloat = Main.salesScreen.getFixedFloat();
        cash = fixedFloat + saleTotal - debitTotal - chequeTotal - couponTotal - chargedTotal + receivedTotal - paidOuts;
        return cash;
    }
}
