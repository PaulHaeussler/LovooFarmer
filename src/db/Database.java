package db;

import com.mysql.cj.jdbc.MysqlDataSource;

import main.Main;
import util.Printer;
import util.Utility;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private MysqlDataSource dataSource;
    private Connection conn;
    private Statement stmt;



    public Database(String user, String password, String databaseServer, String schemaName) throws Exception {
        dataSource = new MysqlDataSource();
        dataSource.setConnectionCollation("utf8mb4_unicode_ci");
        dataSource.setServerTimezone("Europe/Berlin");
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setServerName(databaseServer);
        dataSource.setDatabaseName(schemaName);

        try {
            conn = dataSource.getConnection();
            Printer.printToLog("Database connection established", Printer.LOGTYPE.INFO);
            runInsert("SET NAMES utf8mb4;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet runQuery(String cmd){
        ResultSet rs = null;
        try {
            Printer.printToLog("Executing " + cmd, Printer.LOGTYPE.SQL);
            stmt = conn.prepareStatement(cmd, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.execute(cmd);
            rs = stmt.getResultSet();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(cmd);
            Printer.printException(e);
        }
        return rs;
    }

    public void runBigInsert(String cmd, String param){
        try{
            Printer.printToLog(cmd, Printer.LOGTYPE.DEBUG);
            PreparedStatement ps = conn.prepareStatement(cmd);
            ps.setString(1, param);
            ps.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
            System.err.println(cmd);
            System.err.println(param);
            Printer.printError(cmd);
            Printer.printException(e);
        }
    }

    public void runInsertNoDuplicateError(String cmd) {
        try {
            Printer.printToLog("Executing " + cmd, Printer.LOGTYPE.SQL);
            stmt = conn.createStatement();
            stmt.executeUpdate(cmd);
        } catch (SQLIntegrityConstraintViolationException e){
            Printer.printToLog("Duplicate", Printer.LOGTYPE.SQL);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(cmd);
            Printer.printException(e);
        }
    }


    public void runInsert(String cmd) {
        try {
            Printer.printToLog("Executing " + cmd, Printer.LOGTYPE.SQL);
            stmt = conn.createStatement();
            stmt.executeUpdate(cmd);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(cmd);
            Printer.printException(e);
        }
    }

    public void closeConnections() {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openConnection() throws Exception{
        conn = dataSource.getConnection();
    }


    private String strIfNull(String str){
        if(str == null){
            return "";
        } else {
            return str;
        }
    }



}

