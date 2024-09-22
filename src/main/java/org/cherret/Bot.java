package org.cherret;

import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.client.*;
import it.tdlight.client.TDLibSettings;
import it.tdlight.Init;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Bot {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    static SimpleTelegramClient client;
    public static void setupClient(int api_id, String hash_code, String phone_number) throws Exception {
        Init.init();
        try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {
            APIToken apiToken = new APIToken(api_id, hash_code);
            TDLibSettings settings = TDLibSettings.create(apiToken);
            Path sessionPath = Paths.get("tdlight-session");
            settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
            settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

            SimpleTelegramClientBuilder clientBuilder = clientFactory.builder(settings);
            SimpleAuthenticationSupplier<?> authenticationData = AuthenticationSupplier.user(phone_number);

            setupHandlers(clientBuilder);
            client = clientBuilder.build(authenticationData);
            client.waitForExit();
        }
    }

    private static void setupHandlers(SimpleTelegramClientBuilder clientBuilder) {
        clientBuilder.addUpdateHandler(UpdateAuthorizationState.class, Bot::onUpdateAuthorizationState);
        clientBuilder.addUpdateHandler(UpdateNewMessage.class, Bot::onUpdateNewMessage);
    }

    private static void onUpdateAuthorizationState(UpdateAuthorizationState update) {
        AuthorizationState authorizationState = update.authorizationState;
        String state = "";
        if (authorizationState instanceof AuthorizationStateReady) state = "Logged in";
        else if (authorizationState instanceof AuthorizationStateClosing) state = "Closing...";
        else if (authorizationState instanceof AuthorizationStateClosed) state = "Closed";
        else if (authorizationState instanceof AuthorizationStateLoggingOut) state = "Logging out...";
        System.out.println(state);
    }

    private static void onUpdateNewMessage(TdApi.UpdateNewMessage update) {
        TdApi.Message message = update.message;
        MessageContent messageContent = message.content;
        if (Main.isStickerIdGet) {
            if (messageContent instanceof TdApi.MessageSticker messageSticker) {
                TdApi.Sticker sticker = messageSticker.sticker;
                String stickerFileId = sticker.sticker.remote.id;
                System.out.println("Sticker ID: " + stickerFileId);
            }
        }
        if (messageContent instanceof TdApi.MessageText messageText) {
            String text = messageText.text.text;
            long chatId = message.chatId;

            if (text.startsWith(Main.command)) {
                if (Main.method.equals("1")) {
                    startSendStickers(chatId, Main.stickerID, Main.cooldownAfterSend);
                }
                else if (Main.method.equals("2")) {
                    startSendMessages(chatId, Main.message, Main.cooldownAfterSend);
                }
            }
        }
    }

    private static void startSendStickers(long chatId, String stickerFileId, Integer cooldownAfterSend) {
        Runnable sendStickerTask = new Runnable() {
            @Override
            public void run() {
                sendSticker(chatId, stickerFileId);
            }
        };
        scheduler.scheduleAtFixedRate(sendStickerTask, 0, cooldownAfterSend, TimeUnit.SECONDS);
    }

    private static void startSendMessages(long chatId, String message, Integer cooldownAfterSend) {
        Runnable sendMessageTask = new Runnable() {
            @Override
            public void run() {
                sendTextMessage(chatId, message);
            }
        };
        scheduler.scheduleAtFixedRate(sendMessageTask, 0, cooldownAfterSend, TimeUnit.SECONDS);
    }

    private static void sendSticker(long chatId, String stickerFileId) {
        TdApi.InputMessageSticker inputMessageSticker = new TdApi.InputMessageSticker();
        inputMessageSticker.sticker = new TdApi.InputFileRemote(stickerFileId);
        TdApi.SendMessage sendMessage = new TdApi.SendMessage();
        sendMessage.chatId = chatId;
        sendMessage.inputMessageContent = inputMessageSticker;
        client.send(sendMessage, response -> {
            if (response.isError()) {
                TdApi.Error error = response.getError();
                System.out.println("Error: " + error.message);
            }
        });
    }

    private static void sendTextMessage(long chatId, String text) {
        TdApi.InputMessageText inputMessageText = new TdApi.InputMessageText();
        inputMessageText.text = new TdApi.FormattedText(text, null);
        TdApi.SendMessage sendMessage = new TdApi.SendMessage();
        sendMessage.chatId = chatId;
        sendMessage.inputMessageContent = inputMessageText;
        client.send(sendMessage, response -> {
            if (response.isError()) {
                TdApi.Error error = response.getError();
                System.out.println("Error: " + error.message);
            }
        });
    }

}
