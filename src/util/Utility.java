package util;

import main.Main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

public class Utility {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static HashMap<String, String> reqHeaders = new HashMap<>();
    private static byte[] quota_exceeded = null;

    public static void checkStartupArgs(String[] args){
        if(args.length == 0) Printer.printToLog("No startup params given", Printer.LOGTYPE.INFO);
        boolean isPassword = false;
        for (int i = 0; i < args.length; i++){
            if(args[i].startsWith("-")){
                switch(args[i]){
                    case "-p":
                        String path = getNextIfExistent(args, i);
                        File file = new File(path);
                        if(file.exists()) {
                            Main.repositoryPath = path;
                        } else {
                            Printer.printError("Invalid path " + path);
                            System.exit(1);
                        }
                        break;
                    case "-headers":
                        Main.pathToReqHeaders = getNextIfExistent(args, i);
                        File file2 = new File(Main.pathToReqHeaders);
                        if(!file2.exists()) {
                            Printer.printError("Invalid path " + Main.pathToReqHeaders);
                            System.exit(1);
                        }
                        break;
                    case "-user":
                        Main.db_user = getNextIfExistent(args, i);
                        break;
                    case "-pw":
                        Main.db_pass = getNextIfExistent(args, i);
                        args[i + 1] = "********";
                        break;
                    case "-host":
                        Main.db_host = getNextIfExistent(args, i);
                        break;
                    case "-dbname":
                        Main.db_schema = getNextIfExistent(args, i);
                        break;
                    default:
                        break;
                }
            }
            Printer.printToLog("Found startup param " + args[i], Printer.LOGTYPE.INFO);
        }

        if(Main.repositoryPath.equals("")) {
            Printer.printError("No repository path supplied!");
            System.exit(1);
        }
        if(Main.db_host == null || Main.db_pass == null ||Main.db_user == null || Main.db_schema == null){
            Printer.printError("Insufficient database information supplied!");
            System.exit(1);
        }
    }

    public static void readReqHeaders(){
        File file = new File(Main.pathToReqHeaders);


        try (Stream<String> stream = Files.lines(Paths.get(file.getPath()))) {
            stream.forEach(entry -> {
                String[] tmp = entry.split("=====");
                if(tmp.length == 2) {
                    reqHeaders.put(tmp[0], tmp[1]);
                }
            });
        } catch (Exception e){
            Printer.printException(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String getNextIfExistent(String[] args, int index){
        if(index >= args.length - 1) {
            Printer.printError("Invalid params");
            System.exit(1);
            return null;
        } else {
            return args[index+1];
        }
    }

    public static int getSize(String pathToDir) {
        return Objects.requireNonNull(new File(pathToDir).list()).length;
    }

    public static int getSize(ResultSet resultSet) throws SQLException {
        resultSet.last();
        int size = resultSet.getRow();
        resultSet.beforeFirst();
        return size;
    }


    public static String hashSHA256(String strToHash) {
        String hash = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(strToHash.getBytes(StandardCharsets.UTF_8));
            hash = bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            Printer.printException(e);
            e.printStackTrace();
            System.exit(1);
        }
        return hash;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static boolean isQuotaExceeded(File file) throws Exception {
        byte[] f = Files.readAllBytes(Paths.get(file.getPath()));

        if(quota_exceeded == null){
            InputStream is = Utility.class.getClassLoader().getResourceAsStream("util/quota_exceeded.gif");
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            quota_exceeded =  buffer.toByteArray();
        }
        return Arrays.equals(quota_exceeded, f);
    }
}
