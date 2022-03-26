package network.frostless.mist.services.testing;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import network.frostless.mist.Mist;
import network.frostless.mist.config.model.SelectOptionModel;
import network.frostless.mist.core.service.impl.EventableService;
import network.frostless.mist.services.ServiceManager;
import network.frostless.mist.services.autorole.AutoRoleService;
import network.frostless.mist.services.autorole.model.EmojiModel;

import java.time.Instant;
import java.util.Map;


public class TestingService implements EventableService {

    @SubscribeEvent
    public void onMessage(MessageReceivedEvent evt) {
        if (!evt.getAuthor().getId().equalsIgnoreCase("201825529333153792")) return;

        if(evt.getMessage().getContentRaw().startsWith("!rolesEmbed")) {
            String s = evt.getMessage().getContentRaw().split(" ")[1];
            evt.getChannel().sendMessage(generateRolesEmbed(s)).queue();
        } else if(evt.getMessage().getContentRaw().startsWith("!addReactions")) {
            String s = evt.getMessage().getContentRaw().split(" ")[1];
            if(s == null) return;
            evt.getChannel().retrieveMessageById(s).queue(this::addReactions);
        }
    }

    private void addReactions(Message message) {
        Map<String, EmojiModel> emojis = Mist.get().getConfig().get().getAutoRole().getSelectReactions().get(message.getId());
        if(emojis == null) return;

        for(Map.Entry<String, EmojiModel> entry : emojis.entrySet()) {
            Emoji emoji = entry.getValue().to();
            System.out.println(emoji);
            if(emoji.isUnicode()) {
                message.addReaction(emoji.getName()).queue();
            } else {
                message.addReaction(String.format("%s:%s", emoji.getName(), emoji.getId())).queue();
            }
        }
    }

    private Message generateRolesEmbed(String id) {
        MessageBuilder builder = new MessageBuilder();

        EmbedBuilder embed = new EmbedBuilder();

        StringBuilder desc = new StringBuilder();

        desc.append("Here are the roles you can get in Frostless Network!").append("\n");

        for (SelectOptionModel role : Mist.get().getConfig().get().getAutoRole().getSelectOptions().get(id)) {
            Role roleObj = Mist.getJda().getRoleById(role.getRole());
            if (roleObj == null) continue;

            String emojiSlot = role.getEmoji() != null ? role.getEmoji().to().getAsMention() : "";

            desc.append(emojiSlot).append(" ").append(roleObj.getAsMention()).append(" | ").append(role.getDescription()).append("\n");
        }

        embed.setTitle("Frostless Network | Role Selection");
        embed.setDescription(desc.toString());
        embed.setTimestamp(Instant.now());
        embed.setColor(0x43c4df);
        embed.setFooter("💙 Frostless Network", "https://imgur.com/TkerXsa.png");

        builder.setEmbeds(embed.build());

        ServiceManager.get().getService(AutoRoleService.class).ifPresent((serv) -> builder.setActionRows(ActionRow.of(serv.getRolesAsSelection(id))));

        return builder.build();
    }
}
