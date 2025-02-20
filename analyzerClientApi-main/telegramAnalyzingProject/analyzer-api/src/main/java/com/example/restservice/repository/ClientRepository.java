package com.example.restservice.repository;

import com.example.restservice.Entity.Post;
import com.example.restservice.repository.interfaces.AccessAnalyzerRepository;
import com.example.restservice.repository.interfaces.JpaPostRepository;
import io.github.cdimascio.dotenv.Dotenv;
import it.tdlight.Init;
import it.tdlight.client.*;
import it.tdlight.jni.TdApi;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class ClientRepository implements AccessAnalyzerRepository {

    private final JpaPostRepository jpaRepository;
    private final List<Long> targetChannelIds;

    public ClientRepository(JpaPostRepository repository) {
        this.jpaRepository = repository;
        this.targetChannelIds = loadChannelIdsFromEnv();
        startClient();
    }

    private List<Long> loadChannelIdsFromEnv() {
        Dotenv env = Dotenv.configure().load();
        String channelIds = env.get("CHANNEL_IDS");

        if (channelIds == null || channelIds.isEmpty()) {
            throw new IllegalStateException("CHANNEL_IDS environment variable is not set or empty!");
        }

        return List.of(channelIds.split(","))
                .stream()
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    private void startClient() {
        new Thread(() -> {
            try {
                final Dotenv env = Dotenv.configure().load();
                long adminId = Long.parseLong(System.getProperty("it.tdlight.example.adminid", "667900586"));
                final int apiId = Integer.parseInt(env.get("API_ID"));
                final String apiHash = env.get("API_HASH");
                final String apiPhone = env.get("PHONE_NUMBER");

                // Инициализация TDLight
                Init.init();

                try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {
                    var apiToken = new APIToken(apiId, apiHash);
                    TDLibSettings settings = TDLibSettings.create(apiToken);

                    Path sessionPath = Paths.get("tdlight-session");
                    settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
                    settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

                    SimpleTelegramClientBuilder clientBuilder = clientFactory.builder(settings);
                    SimpleAuthenticationSupplier<?> authenticationData = AuthenticationSupplier.user(apiPhone);

                    var app = new ExampleApp(clientBuilder, authenticationData, adminId);
                    app.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private class ExampleApp implements AutoCloseable {

        private final SimpleTelegramClient client;
        private final long adminId;

        public ExampleApp(SimpleTelegramClientBuilder clientBuilder,
                          SimpleAuthenticationSupplier<?> authenticationData,
                          long adminId) throws Exception {
            this.adminId = adminId;
            this.client = clientBuilder.build(authenticationData);
            this.client.addUpdateHandler(TdApi.UpdateNewMessage.class, this::onUpdateNewMessage);

            new Thread(() -> {
                try {
                    client.waitForExit();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        private void onUpdateNewMessage(TdApi.UpdateNewMessage update) {
            TdApi.Message message = update.message;
            long chatId = message.chatId; // ID чата (канала)
            long postDate = message.date;
            String postText = "(Unknown Content)";

            if (!targetChannelIds.contains(chatId)) {
                return ;
            }

            if (message.content instanceof TdApi.MessageText messageText) {
                postText = (messageText.text != null) ? messageText.text.text : "(Empty Text)";
            }

            System.out.println("Received message from channel " + chatId + ": " + postText);


            jpaRepository.save(new Post(postText, postDate));
        }

        public void run() {
            System.out.println("Telegram Client Started!");
        }

        public SimpleTelegramClient getClient() {
            return client;
        }

        @Override
        public void close() throws Exception {
            client.close();
        }
    }

    private boolean isPostToday(long postTime) {
        long currentTime = Instant.now().getEpochSecond();
        return (currentTime - postTime) <= 86400;
    }

    @Override
    public List<Post> getAllPosts() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Post> getDailyPosts() {
        return jpaRepository.findAll().stream()
                .filter(post -> isPostToday(post.getDate()))
                .collect(Collectors.toList());
    }

    @Override
    public Post getLastPost() {
        return jpaRepository.findAll().stream()
                .max((p1, p2) -> Long.compare(p1.getDate(), p2.getDate()))
                .orElse(null);
    }

    @Override
    public void addPost(Post message) {
        jpaRepository.save(message);
    }
}

