package com.example.restservice.services;

import com.example.restservice.Entity.Message;
import com.example.restservice.repository.botRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class analyzerBotService extends TelegramLongPollingBot {

    private botRepository repository;
    private final long MY_ID = 6284830603L;
    private final long CHANAL_ID = 1730363637L;
    private static final Dotenv env = Dotenv.configure().load();
    private final String token = env.get("TOKEN");
    private final String botName = env.get("BOT_NAME");
    private String messageDate;
    private LocalDate lastCollectionDate = LocalDate.now();

    public analyzerBotService() {
        super();
        System.out.println("AnalyzerBot initialized.");
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasChannelPost() && update.getChannelPost().getChatId() == CHANAL_ID) {
            String messageText = update.getChannelPost().getText();
            String channelName = update.getChannelPost().getForwardSenderName();

            System.out.println("Message from channel " + channelName + ": " + "/n" + messageText);
              System.out.println("Message from channel " + update.getMessage().getForwardSenderName() + ": " + "\n" + update.getMessage().getText());
              Instant instant = Instant.ofEpochSecond(update.getMessage().getDate());
              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yy", Locale.ENGLISH).withZone(ZoneId.systemDefault());
              String messageDate = formatter.format(instant);
              System.out.println(messageDate);
              repository.addMessage(new Message(messageText, update.getChannelPost().getDate()));


            if (isNewDay()) {
                sandingDailyMessages();
            }
        }
    }

    private boolean isNewDay() {
        LocalDate today = LocalDate.now();
        if (!today.equals(lastCollectionDate)) {
            lastCollectionDate = today;
            return true;
        }
        return false;
    }


    private void sandingDailyMessages() {
        System.out.println("...All massages for a day...");
        for (Message message : repository.getAllMessages()) {
            sendText(MY_ID, message.getContent());
            System.out.println(message.getContent());
        }
    }

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new analyzerBotService());
    }
}
