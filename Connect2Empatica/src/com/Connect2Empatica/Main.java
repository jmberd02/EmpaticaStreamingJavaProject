package com.Connect2Empatica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

public class Main {

    public static void main(String[] args) throws IOException {
        try {
            connect2Empatica("2138CD");
        }catch (Exception e){
            out.println(e.toString());
        }
    }

    public static void connect2Empatica(String deviceId) throws IOException{
        Socket pingSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            pingSocket = new Socket("127.0.0.1", 28000);
            out = new PrintWriter(pingSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));

            out.println("device_connect "+ deviceId);

            String regex="([E4_Bvp|E4_Gsr|E4_Acc|E4_Bvp|E4_Temperature]+)\\s{1}(\\d+\\.*\\d+)\\s{1}(\\-*\\d+\\.*\\d+)\\s*(\\-*\\d*\\.*\\d*)\\s*(\\-*\\d*\\.*\\d*).*";

            Pattern pattern=Pattern.compile(regex);

            out.println("device_subscribe acc ON");
            TimeUnit.MILLISECONDS.sleep(100);
            out.println("device_subscribe bvp ON");
            TimeUnit.MILLISECONDS.sleep(100);
            out.println("device_subscribe gsr ON");
            TimeUnit.MILLISECONDS.sleep(100);
            out.println("device_subscribe ibi ON");
            TimeUnit.MILLISECONDS.sleep(100);
            out.println("device_subscribe tmp ON");
            TimeUnit.MILLISECONDS.sleep(100);

            while(true){
                String text=in.readLine();
                Matcher matcher=pattern.matcher(text);
                if(matcher.matches()){
                    if(matcher.group(1).equalsIgnoreCase("E4_Acc")){
                        System.out.println(matcher.group(1)+"_x,"+matcher.group(2)+","+matcher.group(3));
                        System.out.println(matcher.group(1)+"_y,"+matcher.group(2)+","+matcher.group(4));
                        System.out.println(matcher.group(1)+"_z,"+matcher.group(2)+","+matcher.group(5));
                    }else{
                        System.out.println(matcher.group(1)+","+matcher.group(2)+","+matcher.group(3));
                    }
                }

            }


        } catch (IOException | InterruptedException e) {
            return;
        }

//        out.println("device_list");
//        System.out.println(in.readLine());
//        out.close();
//        in.close();
//        pingSocket.close();
    }
}
