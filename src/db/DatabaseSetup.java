package db;

import main.Main;
import util.Printer;
import util.Utility;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DatabaseSetup {

    public static void checkSetup() throws Exception {
        Printer.printToLog("Checking database table integrity and creating new tables if necessary...", Printer.LOGTYPE.INFO);

        if(!checkForTable("users"))createMainTable();
        if(!checkForTable("pics"))createPicsTable();
        if(!checkForTable("user_info"))createInfoTable();

        Printer.printToLog("Database is all set up and ready!", Printer.LOGTYPE.INFO);
    }


    private static boolean checkForTable(String tableName) throws Exception {
        ResultSet rs = Main.db.runQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + Main.db_schema + "' AND TABLE_NAME = '" + tableName + "';");
        return Utility.getSize(rs) > 0;
    }

    private static void createMainTable(){
        Main.db.runInsert("CREATE TABLE `" + Main.db_schema + "`.`users` (\n" +
                "  `hash` VARCHAR(100) NOT NULL,\n" +
                "  `m` INT NULL,\n" +
                "  `p` INT NULL,\n" +
                "  `type` VARCHAR(45) NULL,\n" +
                "  `name` VARCHAR(255) NULL,\n" +
                "  `gender` INT NULL,\n" +
                "  `age` INT NULL,\n" +
                "  `lastOnlineTime` VARCHAR(45) NULL,\n" +
                "  `whazzup` VARCHAR(1000) NULL,\n" +
                "  `freetext` VARCHAR(1000) NULL,\n" +
                "  `subscriptions` VARCHAR(1000) NULL,\n" +
                "  `isVIP` INT NULL,\n" +
                "  `flirtInterests` VARCHAR(100) NULL,\n" +
                "  `options` VARCHAR(100) NULL,\n" +
                "  `home_location` VARCHAR(100) NULL,\n" +
                "  `home_country` VARCHAR(45) NULL,\n" +
                "  `home_distance` DOUBLE NULL,\n" +
                "  `current_location` VARCHAR(100) NULL,\n" +
                "  `current_country` VARCHAR(45) NULL,\n" +
                "  `current_distance` VARCHAR(100) NULL,\n" +
                "  `isNew` INT NULL,\n" +
                "  `isOnline` INT NULL,\n" +
                "  `isMobile` INT NULL,\n" +
                "  `isHighlighted` INT NULL,\n" +
                "  `has_liked` INT NULL,\n" +
                "  `has_contact` INT NULL,\n" +
                "  `icebreaker_state` INT NULL,\n" +
                "  `is_match` INT NULL,\n" +
                "  `pic_hash` VARCHAR(45) NULL,\n" +
                "  `isVerified` INT NULL,\n" +
                "  `verifications` VARCHAR(100) NULL,\n" +
                "  `last_checked` VARCHAR(45) NULL, \n" +
                "  PRIMARY KEY (`hash`),\n" +
                "  UNIQUE INDEX `hash_UNIQUE` (`hash` ASC))\n" +
                "ENGINE = InnoDB\n" +
                "DEFAULT CHARACTER SET = utf8mb4\n" +
                "COLLATE = utf8mb4_unicode_ci;");

    }

    private static void createPicsTable(){
        Main.db.runInsert("CREATE TABLE `" + Main.db_schema + "`.`pics` (\n" +
                "  `user_hash` VARCHAR(100) NOT NULL,\n" +
                "  `img_hash` VARCHAR(500) NOT NULL,\n" +
                "  `img_name` VARCHAR(100) NOT NULL, \n" +
                "  `last_seen_at` VARCHAR(25) NULL,\n" +
                "  PRIMARY KEY (`img_hash`),\n" +
                "  UNIQUE INDEX `img_hash_UNIQUE` (`img_hash` ASC))\n" +
                "ENGINE = InnoDB\n" +
                "DEFAULT CHARACTER SET = utf8mb4\n" +
                "COLLATE = utf8mb4_unicode_ci;");
    }

    private static void createInfoTable(){
        Main.db.runInsert("CREATE TABLE `" + Main.db_schema + "`.`user_info` (\n" +
                "  `hash` VARCHAR(100) NOT NULL,\n" +
                "  `ori` VARCHAR(2) NULL,\n" +
                "  `orientation` VARCHAR(45) NULL,\n" +
                "  `hgt` VARCHAR(3) NULL,\n" +
                "  `height` VARCHAR(45) NULL,\n" +
                "  `wgt` VARCHAR(4) NULL,\n" +
                "  `weight` VARCHAR(45) NULL,\n" +
                "  `fig` VARCHAR(3) NULL,\n" +
                "  `figure` VARCHAR(45) NULL,\n" +
                "  `hr` VARCHAR(2) NULL,\n" +
                "  `hair` VARCHAR(45) NULL,\n" +
                "  `hrl` VARCHAR(2) NULL,\n" +
                "  `hairlength` VARCHAR(45) NULL,\n" +
                "  `eye` VARCHAR(6) NULL,\n" +
                "  `eye_color` VARCHAR(45) NULL,\n" +
                "  `tat` VARCHAR(3) NULL,\n" +
                "  `tattoos_piercings` VARCHAR(45) NULL,\n" +
                "  `smk` VARCHAR(3) NULL,\n" +
                "  `smoke` VARCHAR(45) NULL,\n" +
                "  `rt` VARCHAR(2) NULL,\n" +
                "  `root` VARCHAR(45) NULL,\n" +
                "  `rel` VARCHAR(3) NULL,\n" +
                "  `religion` VARCHAR(45) NULL,\n" +
                "  `cld` VARCHAR(2) NULL,\n" +
                "  `child` VARCHAR(45) NULL,\n" +
                "  `lf` VARCHAR(2) NULL,\n" +
                "  `life` VARCHAR(45) NULL,\n" +
                "  `edu` VARCHAR(3) NULL,\n" +
                "  `education` VARCHAR(45) NULL,\n" +
                "  `jb` VARCHAR(2) NULL,\n" +
                "  `job` VARCHAR(45) NULL,\n" +
                "  `inc` VARCHAR(2) NULL,\n" +
                "  `income` VARCHAR(45) NULL,\n" +
                "  `frt` VARCHAR(2) NULL,\n" +
                "  `flirt` VARCHAR(45) NULL,\n" +
                "  `ser` VARCHAR(2) NULL,\n" +
                "  `search` VARCHAR(45) NULL,\n" +
                "  `fai` VARCHAR(2) NULL,\n" +
                "  `faith` VARCHAR(45) NULL,\n" +
                "  `fir` VARCHAR(4) NULL,\n" +
                "  `first` VARCHAR(45) NULL,\n" +
                "  `par` VARCHAR(5) NULL,\n" +
                "  `partner` VARCHAR(45) NULL,\n" +
                "  `ero` VARCHAR(4) NULL,\n" +
                "  `erotic` VARCHAR(45) NULL,\n" +
                "  `tno` VARCHAR(3) NULL,\n" +
                "  `turnoffs` VARCHAR(45) NULL,\n" +
                "  `fre` VARCHAR(5) NULL,\n" +
                "  `freetime` VARCHAR(45) NULL,\n" +
                "  `sat` VARCHAR(2) NULL,\n" +
                "  `saturdayeve` VARCHAR(45) NULL,\n" +
                "  `mus` VARCHAR(5) NULL,\n" +
                "  `music` VARCHAR(45) NULL,\n" +
                "  PRIMARY KEY (`hash`))\n" +
                "ENGINE = InnoDB\n" +
                "DEFAULT CHARACTER SET = utf8mb4\n" +
                "COLLATE = utf8mb4_unicode_ci;\n");
    }
}
