package network.frostless.fragment.embed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import network.frostless.fragment.embed.model.MessageAuthorModel;
import network.frostless.fragment.embed.model.MessageEmbedModel;
import network.frostless.fragment.embed.model.MessageFieldModel;
import network.frostless.fragment.embed.model.MessageModel;
import network.frostless.mist.Mist;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EmbedUtils {

    private static final Gson gson = new GsonBuilder().create();



    public static CompletableFuture<MessageBuilder> getMessageContents(String link) {
        CompletableFuture<MessageBuilder> future = new CompletableFuture<>();

        System.out.println("MAKING GET REQUEST to " + link);
        HttpRequest req = HttpRequest.newBuilder().GET()
                .uri(URI.create(link))
                .setHeader("User-Agent", "Mist 1.0/Frostless Network")
                .build();

        Mist.get().getHttpClient().sendAsync(req, HttpResponse.BodyHandlers.ofString()).whenComplete((res, t) -> {
            if (t != null) t.printStackTrace();

            MessageBuilder parse = EmbedUtils.parse(res.body());
            System.out.println(parse);
            future.complete(parse);
        });

        return future;
    }
    public static MessageBuilder parse(String string) {
        try {
            MessageModel messageModel = gson.fromJson(string, MessageModel.class);
            return toBuilder(messageModel);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MessageBuilder toBuilder(MessageModel model) {
        MessageBuilder builder = new MessageBuilder();

        builder.setContent(model.getContent());
        builder.setEmbeds(getEmbeds(model.getEmbeds()));

        return builder;
    }

    public static List<MessageEmbed> getEmbeds(List<MessageEmbedModel> models) {
        List<MessageEmbed> embeds = new ArrayList<>();

        for (MessageEmbedModel model : models) {
            EmbedBuilder builder = new EmbedBuilder();

            MessageAuthorModel author = model.getAuthor();
            if (author.getUrl() == null || author.getUrl().isEmpty()) {
                builder.setAuthor(author.getName(), null, author.getIconUrl());
            } else builder.setAuthor(author.getName(), author.getUrl(), author.getIconUrl());

            builder.setDescription(model.getDescription());
            builder.setColor(model.getColor());

            if (model.getImage() != null && !model.getImage().getUrl().isEmpty())
                builder.setImage(model.getImage().getUrl());
            if (model.getThumbnail() != null && !model.getThumbnail().getUrl().isEmpty())
                builder.setThumbnail(model.getThumbnail().getUrl());
            if (model.getTimestamp() != null)
                builder.setTimestamp(Instant.now());
            if (model.getTitle() != null && !model.getTitle().isEmpty())
                builder.setTitle(model.getTitle());

            if (model.getFooter() != null && !model.getFooter().getText().isEmpty())
                builder.setFooter(model.getFooter().getText());
            if (model.getFooter() != null && !model.getFooter().getIcon_url().isEmpty())
                builder.setFooter(model.getFooter().getText(), model.getFooter().getIcon_url());

            for (MessageFieldModel field : model.getFields()) {
                builder.addField(field.getName(), field.getValue(), field.isInline());
            }

            embeds.add(builder.build());
        }

        return embeds;
    }
}
