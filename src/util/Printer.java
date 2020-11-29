package util;

import main.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Printer {

    private static String startupLocation;
    private static String logfile;
    private static BufferedWriter output;

    private static int exceptionsThrown = 0;

    private static final String rootPath = "/LovooFarmer/";

    public enum LOGTYPE{
        WARNING,
        ERROR,
        DEBUG,
        INFO,
        SQL,
        IMAGE
    }

    public static void checkSetup() {
        try{
            startupLocation = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            startupLocation = startupLocation.substring(1);
            startupLocation = new File(startupLocation).getParent();

            System.out.println("Startup at " + startupLocation);

            if(Files.notExists(Paths.get(startupLocation + rootPath)))
                if(!new File(startupLocation + rootPath).mkdirs())
                    printError("Failed to create directory " + startupLocation + rootPath);

            //create new log
            logfile = getTimeNow() + ".log";
            if(Files.notExists(Paths.get(startupLocation + rootPath + logfile)))
                if(!new File(startupLocation + rootPath + logfile).createNewFile())
                    printError("Failed to create logfile " + startupLocation + rootPath + logfile);

            output = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(startupLocation + rootPath + logfile, true), StandardCharsets.UTF_8));
            System.out.println(startupLocation + rootPath + logfile);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getLocation(){
        return startupLocation;
    }

    public static String getTimeNow(){
        return DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss").format(ZonedDateTime.now());
    }

    public static void printToLog(String msgToWrite, LOGTYPE logtype) {
        try{
            String msg ="[" + getTimeNow() + "] [" + logtype.toString() + "] " + msgToWrite + "\n";
            output.write(msg);
            output.flush();
            System.out.println(msg);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Fuck me this shouldnt happen");
        }
    }

    public static void printError(String errorMsg){
        printToLog("\u001b[31m" + errorMsg + "\u001b[0m", LOGTYPE.ERROR);
    }

    public static void printException(Exception e){
        exceptionsThrown++;
        String stackTrace = "";
        for(StackTraceElement ste : e.getStackTrace()){
            stackTrace = stackTrace + "\n" + ste.toString();
        }
        printError(e.getMessage() + "\n" + stackTrace);
        if(exceptionsThrown > 3) System.exit(1); //Killswitch
    }
}
