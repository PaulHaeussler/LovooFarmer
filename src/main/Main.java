package main;

import db.Database;
import db.DatabaseSetup;
import html.LovooConnection;
import html.LovooImage;
import html.LovooProfile;
import html.Parser;
import util.Printer;
import util.Utility;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static String pathToReqHeaders = "";
    public static String repositoryPath = "";
    public static String db_user;
    public static String db_pass;
    public static String db_host;
    public static String db_schema;

    public static Database db;
    public static Parser parser;




    public static void main(String[] args){
        Printer.checkSetup();
        Utility.checkStartupArgs(args);
        Utility.readReqHeaders();
        try{
            db = new Database(db_user, db_pass, db_host, db_schema);
            parser = new Parser();
            DatabaseSetup.checkSetup();

            /*
            LovooProfile lf = new LovooProfile();
            lf.hash = "5fa6e910c4564f57e528b466";
            lf.images = new ArrayList<LovooImage>();
            parser.parseImages(lf);
            */


            parser.parseMainPage(3, 100);

            Printer.printToLog("Process exited with code 0", Printer.LOGTYPE.INFO);
        } catch (Exception e){
            e.printStackTrace();
            Printer.printException(e);
        }
    }
}

