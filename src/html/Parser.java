package html;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import main.Main;
import org.w3c.dom.Document;
import util.Printer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Parser {

    public ArrayList<LovooProfile> profiles;

    public Parser(){
        profiles = new ArrayList<>();
    }

    public void parseMainPage(int attempts, int ceil) throws Exception {
        for(int i = 1; i <= attempts; i++){
            for(int j = 1; j <= ceil; j++){
                if(parseAPIpage("https://www.lovoo.com/api_web.php/users?resultPage=" + j + "&type=env&userQuality%5B0%5D=pic") == -1) break;
            }
        }
    }


    private int parseAPIpage(String url) throws Exception {
        String page = LovooConnection.getPage(url);
        if(page.contains("\"response\":{\"result\":[],\"allCount\":0}")) return -1; //validate valid response

        page = page.split("}],\"allCount\":12},\"statusMessage\":\"\",\"statusCode\":200,\"updateUser\":\\{\"counts\":\\{")[0]; //trim end

        String[] results = page.split("\\{\"_type\":\"");
        for(int i = 1; i < results.length; i++){
            try{
                profiles = new ArrayList<>();
                parseUser(results[i]);
                Main.db.closeConnections();
                Main.db.openConnection();
                goDB();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return 0;
    }

    private void parseUser(String rs) throws Exception {
        Printer.printToLog("Processing: " + rs, Printer.LOGTYPE.DEBUG);
        LovooProfile p = new LovooProfile();
        p.type = rs.split("\",\"id\":\"")[0];
        p.hash = getPassage(rs, ",\"id\":\"","\",\"name\":\"");
        p.name = getPassage(rs, "\",\"name\":\"", "\",\"gender\":");
        p.gender = getPassage(rs, "\",\"gender\":", ",\"age\":");
        p.age = getPassage(rs, ",\"age\":", ",\"lastOnlineTime\":");
        p.lastOnlineTime = getPassage(rs, ",\"lastOnlineTime\":", ",\"whazzup\":\"");
        p.whazzup = getPassage(rs, ",\"whazzup\":\"", "\",\"freetext\":\"");
        p.freetext = getPassage(rs, "\",\"freetext\":\"", "\",\"subscriptions\":\\[");
        p.subscriptions = getPassage(rs, "\",\"subscriptions\":\\[", "],\"isVip\":");
        p.isVIP = getPassage(rs, "],\"isVip\":", ",\"flirtInterests\":\\[");
        p.flirtInterests = getPassage(rs, ",\"flirtInterests\":\\[", "],\"options\":\\{");
        p.options = getPassage(rs, "],\"options\":\\{", "},\"counts\":\\{");
        p.p = getPassage(rs, "},\"counts\":\\{\"p\":", ",");
        p.m = getPassage(rs, ",\"m\":", "},\"locations\":\\{");

        //location
        String locations = getPassage(rs, ",\"locations\":", "}},\"isNew\":");
        String home = locations.split("\"current\"")[0];
        String current = locations.split("\"current\"")[1];

        p.home_location = getPassage(home, "\"city\":\"", "\",\"country\":\"");
        p.home_country = getPassage(home, "\",\"country\":\"", "\",\"distance\":");
        p.home_distance = getPassage(home, "\",\"distance\":", "},");

        p.current_location = getPassage(current, "\"city\":\"", "\",\"country\":\"");
        p.current_country = getPassage(current, "\",\"country\":\"", "\",\"distance\":");
        p.current_distance = getPassage(current, "\",\"distance\":", "},");



        p.isNew = getPassage(rs, "},\"isNew\":", ",\"isOnline\":");
        p.isOnline = getPassage(rs, ",\"isOnline\":", ",\"isMobile\":");
        p.isMobile = getPassage(rs, ",\"isMobile\":", ",\"isHighlighted\":");
        p.isHighlighted = getPassage(rs, ",\"isHighlighted\":", ",\"picture\":\"");
        p.pic_hash = getPassage(rs, ",\"picture\":\"", "\",\"images\":\\[");
        p.isVerified  = getPassage(rs, ",\"isVerified\":", ",\"verifications\":\\{");
        p.verifications = getPassage(rs, ",\"verifications\":\\{", "}}");

        p.last_checked = Printer.getTimeNow();

        if(p.freetext.length() > 1000) p.freetext = p.freetext.substring(0, 1000);


        String h = getPassage(rs, "\"picture\":\"", "\",\"images\":");
        LovooImage profilePic = new LovooImage(p, h, "0");
        profilePic.downloadImage("https://img.lovoo.com/users/pictures/" + h + "/image.jpg");
        p.images = new ArrayList<>();
        p.images.add(profilePic);





        parseProfile(p);

        profiles.add(p);
    }


    //patternStart has to be unique in the string
    private String getPassage(String strToFilter, String patternStart, String patternEnd){
        String[] tmp = strToFilter.split(patternStart);
        if(tmp.length != 2) {
            Printer.printError("Failed to parse correctly:\nStrToFilter: " + strToFilter + "\nPatternStart: " +
                    patternStart + "\nPatternEnd: " + patternEnd);
            System.exit(3);
        }
        String[] tmp1 = tmp[1].split(patternEnd);
        String result = "";
        if(tmp1.length > 0) result = tmp1[0];
        return reencode(result);
    }

    public String reencode(String str){
        String[] tmp = str.split("\\\\u");
        if(tmp.length == 1) return str;
        ArrayList<String> pieces = new ArrayList<>();
        for(int j = 1; j < tmp.length; j++){
            boolean isHex = true;
            if(tmp[j].length() < 4) continue;
            for(int i = 0; i < 4; i++){
                if(Character.digit(tmp[j].toCharArray()[i], 16) == -1){
                    isHex = false;
                }
            }
            if(isHex){
                int i = Integer.parseInt(tmp[j].substring(0, 4), 16);
                char c = (char)i;
                pieces.add(c + "");
                tmp[j] = tmp[j].substring(4);
            } else {
                pieces.add("\\u");
            }
        }
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < tmp.length; i++){
            result.append(tmp[i]);
            if(i < pieces.size()) result.append(pieces.get(i));
        }
        return result.toString();
    }

    private void goDB() throws Exception {
        ResultSet rs = Main.db.runQuery("SELECT hash FROM " + Main.db_schema + ".users;");
        ArrayList hashes = new ArrayList();
        while(rs.next()){
            hashes.add(rs.getString("hash"));
        }

        for(LovooProfile p : profiles){
            if(hashes.contains(p.hash)){
                Main.db.runInsert("DELETE FROM " + Main.db_schema + ".users WHERE hash='" + p.hash + "';");
            }
            Main.db.runInsertNoDuplicateError("INSERT INTO " + Main.db_schema + ".users VALUES('" +
                    p.hash + "', " +
                    p.m + ", " +
                    p.p + ", '" +
                    p.type + "', '" +
                    p.name + "', " +
                    p.gender + ", " +
                    p.age + ", '" +
                    p.lastOnlineTime + "', '" +
                    p.whazzup + "', '" +
                    p.freetext + "', '" +
                    p.subscriptions + "', " +
                    p.isVIP + ", '" +
                    p.flirtInterests + "', '" +
                    p.options + "', '" +
                    p.home_location + "', '" +
                    p.home_country + "', " +
                    p.home_distance + ", '" +
                    p.current_location + "', '" +
                    p.current_country + "', " +
                    p.current_distance + ", " +
                    p.isNew + ", " +
                    p.isOnline + ", " +
                    p.isMobile + ", " +
                    p.isHighlighted + ", " +
                    p.has_liked + ", " +
                    p.has_contact + ", " +
                    p.icebreaker_state + ", " +
                    p.is_match + ", '" +
                    p.pic_hash + "', " +
                    p.isVerified + ", '" +
                    p.verifications + "', '" +
                    p.lastOnlineTime + "');"
            );
            p.details.addToDB(p.hash);
            for(LovooImage img : p.images){
                img.addToDB();
            }
        }
    }

    public void parseProfile(LovooProfile p) throws Exception{
        parseImages(p);
        parseDetails(p);
        parseConnections(p);
    }

    private void parseConnections(LovooProfile p) throws Exception {
        String page = getResponse(p.hash, "connections");
        p.is_match = getPassage(page, "\"isMatch\":", ",");
        p.has_contact = getPassage(page, "\"hasContact\":", ",");
        p.has_liked = getPassage(page, "\"hasLiked\":", ",");
        p.icebreaker_state = getPassage(page, "\"icebreakerState\":", ",");
    }



    public void parseImages(LovooProfile p) throws Exception {
        String page = getResponse(p.hash, "feed?resultLimit=14");
        if(page.length() <= 25) return;

        String[] tmp = page.split("\"_type\":\"story\",");
        for(int i = 1; i < tmp.length; i++){
            if(!tmp[i].substring(0, 5).equals("\"id\":")) continue;
            String t = tmp[i].split("\",\"")[0];
            String tt = t.substring(6);
            String h  = tt.split("_")[1];
            System.out.println(h);
            LovooImage img = new LovooImage(p, h, i + "");
            img.downloadImage(new String("https://img.lovoo.com/moments/" + h + "/image.jpg").replace("\\", ""));
            p.images.add(img);
        }
    }



    private void parseDetails(LovooProfile p) throws Exception {
        String page = getResponse(p.hash, "details");
        LovooDetails dt = new LovooDetails();
        Printer.printToLog("Processing: " + page, Printer.LOGTYPE.DEBUG);

        dt.setOri(getDetails(page, "ori"));
        dt.setHgt(getDetails(page, "size"));
        dt.setWgt(getDetails(page, "weight"));
        dt.setFig(getDetails(page, "fig"));
        dt.setHr(getDetails(page, "hair"));
        dt.setHrl(getDetails(page, "hairlength"));
        dt.setEye(getDetails(page, "eye"));
        dt.setTat(getDetails(page, "yewel"));
        dt.setSmk(getDetails(page, "smoke"));
        dt.setRt(getDetails(page, "root"));
        dt.setRel(getDetails(page, "religion"));
        dt.setCld(getDetails(page, "child"));
        dt.setLf(getDetails(page, "life"));
        dt.setEdu(getDetails(page, "educ"));
        dt.setJb(getDetails(page, "job"));
        dt.setInc(getDetails(page, "income"));
        dt.setFrt(getDetails(page, "status"));
        dt.setSer(getDetails(page, "search"));
        dt.setFai(getDetails(page, "faith"));
        dt.setFir(getDetails(page, "first"));
        dt.setPar(getDetails(page, "dreampar"));
        dt.setEro(getDetails(page, "erotic"));
        dt.setTno(getDetails(page, "turnsup"));
        dt.setFre(getDetails(page, "freetime"));
        dt.setSat(getDetails(page, "sat"));
        dt.setMus(getDetails(page, "music"));

        p.details = dt;
    }

    private String getDetails(String page, String key){
        if(!page.contains("\"" + key + "\":{")) return null;
        String tmp = getPassage(page, "\"" + key + "\":\\{", "\\},");
        if(tmp.equals("")) return null;
        return getPassage(tmp, "\"id\":\"", "\",\"");
    }



    private String getResponse(String hash, String attr) throws Exception {
        String page = LovooConnection.getPage("https://www.lovoo.com/api_web.php/users/"  + hash + "/" + attr);
        return getPassage(page, "\\{\"response\":\\{", "\\},\"statusMessage\":\"\",\"statusCode\":200,\"updateUser\":\\{");
    }
}
