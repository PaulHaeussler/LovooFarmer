package html;

import main.Main;
import util.OverseerThread;
import util.Printer;

import java.util.ArrayList;

public class LovooImage {

    public String user_hash;
    public String img_hash;
    public String last_seen;
    public String img_name;

    public LovooImage(LovooProfile p, String hash, String name){
        user_hash = p.hash;
        img_hash = hash;
        last_seen = Printer.getTimeNow();
        img_name = name;
    }


    public void downloadImage(String url){
        String[] tmp = url.split("\\.");
        String fileEnd = "";
        try{
            fileEnd = "." + tmp[tmp.length-1].split("\\?")[0];
        } catch(Exception e){
            e.printStackTrace();
            fileEnd = ".png";
        }

        System.out.println("Starting overseer thread");

        ArrayList<Object> al = new ArrayList<>();
        al.add(url);
        al.add(img_name);
        al.add(Main.repositoryPath + "/" + user_hash);


        Runnable r = new OverseerThread(al);
        Thread t = new Thread(r);
        t.start();
        System.out.println("Going to sleep");

        long start = System.currentTimeMillis();

        while(System.currentTimeMillis() - start < 30000){
            if(!t.isAlive()) break;
        }

        if(t.isAlive()){
            System.out.println("Thread still alive, restarting");
            t.stop();
            downloadImage(url);
        }

    }

    public void addToDB(){
        Main.db.runInsertNoDuplicateError("INSERT INTO " + Main.db_schema + ".pics VALUES('" + user_hash + "', '" + img_hash + "', '" + img_name + "', '" + last_seen + "');");
    }
}
