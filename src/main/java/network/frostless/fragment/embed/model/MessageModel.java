package network.frostless.fragment.embed.model;

import lombok.Data;

import java.util.List;


@Data
public class MessageModel {

    private String username;

    private String avatarUrl;

    private String content;

    private List<MessageEmbedModel> embeds;
}
