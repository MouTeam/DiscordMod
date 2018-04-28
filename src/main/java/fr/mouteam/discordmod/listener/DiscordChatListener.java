package fr.mouteam.discordmod.listener;

import fr.mouteam.discordmod.DiscordMod;
import fr.mouteam.discordmod.config.DiscordModConfig;
import fr.mouteam.discordmod.discord.Discord;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class DiscordChatListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        if (!event.getChannel().getId().equals(DiscordModConfig.global.discordTokenChannel)) {
            return;
        }

        Message message = event.getMessage();

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        if (server != null) {
            server.getPlayerList().sendMessage(ForgeHooks.newChatWithLinks("[Discord] [" + message.getAuthor().getName() + "] " + message.getContentRaw()));
            DiscordMod.logger.info("[Discord]" + message.getContentRaw());
        }

        if (message.getContentRaw().equals(DiscordModConfig.global.discordPrefixCommand + "ping")) {
            MessageChannel channel = event.getChannel();

            channel.sendMessage("pong from minecraft server").queue();
        }
    }
}
