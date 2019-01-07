package org.markjay;

import com.google.gson.Gson;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageAuthor;
import org.markjay.domain.RaidInfo;
import org.markjay.services.raidinfoextractor.RaidInfoExtractor;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class Application {

    private static final Logger log = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) throws Exception {
        String token = getToken();

//        jda(token);

        DiscordApi api = new DiscordApiBuilder()
                .setWaitForServersOnStartup(false)
                .setToken(token)
                .login()
                .join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            log.info("an event caught ");
            if (event.getMessageAttachments().size() == 1) {
                log.info("processing a message with a single attachment");
                MessageAttachment attach = event.getMessageAttachments().get(0);
                event.getChannel().type();
                log.info("typing sent");
                attach.downloadAsByteArray().thenAccept(bytes -> {
                    RaidInfo result = extractInfo(attach, bytes);
                    log.info("info extracted : " + new Gson().toJson(result));
                    String author = event.getMessage().getAuthor().getName();
                    event.getChannel().sendMessage(author + "\n```\n" + result.toReport() + "\n```");
                }).exceptionally(exception -> {
                    log.log(Level.SEVERE, "failed to parse image ", exception);
                    event.getChannel().sendMessage("Failed, you're on your own");
                    return null;
                });
            }
        });

        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }

    private static RaidInfo extractInfo(MessageAttachment attach, byte[] bytes) {
        BufferedImage image = createImageFromBytes(bytes);
        try {
            return new RaidInfoExtractor().extractFromImage(image, attach.getFileName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void jda(String token) throws LoginException, InterruptedException {
        JDA api = new JDABuilder(AccountType.BOT)
                .setToken(token)
//                .addEventListener()
                .buildBlocking();
//        JDA api = new JDABuilder()
//                .setPassword(token)
//                .build();

        System.out.println("api = " + api);
    }

    private static String getToken() throws IOException {
        String home = System.getenv("HOME");
        String tokenPath = home + "/.discord/token";
        log.info("using token path = " + tokenPath);
        if (!new File(tokenPath).exists()) {
            throw new RuntimeException("expected to find token at " + tokenPath);
        }
        return new String(Files.readAllBytes(Paths.get(tokenPath)));
    }

}
