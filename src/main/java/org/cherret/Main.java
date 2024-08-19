package org.cherret;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static String method;
    private static ConfigManager configManager = new ConfigManager();
    public static boolean isStickerIdGet = false;
    public static String stickerID;
    public static String message;
    public static Integer cooldownAfterSend;
    public static String command;
    public static void main(String[] args) throws Exception {
        File configFile = new File("config.conf");
        if (!configFile.exists()) {
            createConfig();
            startBot();
        }
        else {
            check();
        }
    }

    private static void check() throws Exception {
        String[] config = getConfig();
        System.out.println("Are the details correct?");
        System.out.printf("Api Id: %s%n", config[0]);
        System.out.printf("Api Hash: %s%n", config[1]);
        System.out.printf("Telegram Phone Number: %s%n", config[2]);
        System.out.print("Enter y to continue or e to edit: ");
        String check = new Scanner(System.in).nextLine().trim();
        if (check.equals("y")) {
            continue_start();
        }
        else if(check.equals("e")) {
            createConfig();
            continue_start();
        }
        else {
            check();
        }
    }

    public static void continue_start() throws Exception {
        method = selectSpamMethod();
        if (method.equals("1")) {
            if (spamStickerMethod().equals("1")) {
                isStickerIdGet = true;
                startBot();
                System.out.println("Send the desired sticker to favorites, its ID will be displayed here");
                System.out.println("To exit press CTRL+C");
            }
            else if (spamStickerMethod().equals("2")) {
                System.out.print("Enter the command that will be used to start the crash: ");
                command = new Scanner(System.in).nextLine().trim();
                System.out.print("Enter the sticker ID: ");
                stickerID = new Scanner(System.in).nextLine().trim();
                System.out.print("Delay between sending main stickers (in seconds): ");
                cooldownAfterSend = new Scanner(System.in).nextInt();
                startBot();
            }
        }
        else if (method.equals("2")) {
            System.out.print("Enter the command that will be used to start the crash: ");
            command = new Scanner(System.in).nextLine().trim();
            System.out.print("Enter the message: ");
            message = new Scanner(System.in).nextLine().trim();
            System.out.print("Delay between sending main messages (in seconds): ");
            cooldownAfterSend = new Scanner(System.in).nextInt();
            startBot();
        }
        else {
            continue_start();
        }
    }

    private static String[] getConfig() {
        Integer api_id = configManager.getApi_Id();
        String api_hash = configManager.getApi_Hash();
        String phone_number = configManager.getPhone_Number();
        return new String[]{api_id.toString(), api_hash, phone_number};
    }

    private static void startBot() throws Exception {
        String[] config = getConfig();
        Bot.setupClient(Integer.parseInt(config[0]), config[1], config[2]);
    }

    private static String selectSpamMethod() {
        System.out.print("Choose a spamming method\n1. Stickers\n2. Messages\nEnter the method you prefer: ");
        String method = new Scanner(System.in).nextLine().trim();
        return method;
    }

    private static String spamStickerMethod() {
        System.out.print("1. Get the sticker ID\n2. Enter the sticker ID\nChoose an option: ");
        String answer = new Scanner(System.in).nextLine().trim();
        return answer;
    }


    private static void createConfig() {
        System.out.print("Enter api id: ");
        String api_id = new Scanner(System.in).nextLine().trim();
        System.out.print("Enter api hash: ");
        String api_hash = new Scanner(System.in).nextLine().trim();
        System.out.print("Enter the phone number from your Telegram account: ");
        String phone_number = new Scanner(System.in).nextLine().trim();
        configManager.setApi_Id(api_id);
        configManager.setApi_Hash(api_hash);
        configManager.setPhone_Number(phone_number);
        configManager.saveConfig();
    }
}