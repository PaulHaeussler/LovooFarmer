package main;

public class Foo {

    private static String s = "        \"no\": \"No response\",\n" +
            "        \"blac\": \"R & B\",\n" +
            "        \"hip\": \"Hip Hop, Soul, Funk\",\n" +
            "        \"dub\": \"Goa, Hardstyle, Dubstep\",\n" +
            "        \"danc\": \"Dance, House, Electro\",\n" +
            "        \"pop\": \"Pop, Chart music\",\n" +
            "        \"part\": \"Party, hits\",\n" +
            "        \"goth\": \"Gothic, Darkwave\",\n" +
            "        \"rock\": \"Rock, Indie\",\n" +
            "        \"metal\": \"Metal\",\n" +
            "        \"quer\": \"Eclectic blend\"";

    public static void main(String[] args){
        String[] t = s.split("\n");
        for(String d : t){
            if(d.equals("")) continue;
            String[] tmp = d.trim().split(":");
            String a = tmp[0];
            String b = tmp[1];
            if(b.substring(b.length() - 1).equals(",")) b = b.substring(0, b.length() - 1);
            System.out.println("    put(" + a + ", " + b + ");");
        }
    }
}
