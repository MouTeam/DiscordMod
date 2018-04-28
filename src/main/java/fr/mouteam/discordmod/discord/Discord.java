package fr.mouteam.discordmod.discord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class Discord {

    private String discordTokenSecret;

    private JDA jda;

    public Discord(String discordTokenSecret) {
        this.discordTokenSecret = discordTokenSecret;
    }

    public void connect() {
        if (this.discordTokenSecret == null || this.discordTokenSecret.isEmpty()) {
            return;
        }

        try {
            jda = new JDABuilder(AccountType.BOT).setToken(this.discordTokenSecret).buildBlocking();
            System.out.println(jda.getStatus());

        } catch (LoginException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid Discord token. Please verify your token in the DiscordChat config file.", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addEventListener(EventListener eventListener) {
        this.jda.addEventListener(eventListener);
    }

    public JDA getJda() {
        return this.jda;
    }

    public void stop() {
        this.jda.shutdown();
    }
}
