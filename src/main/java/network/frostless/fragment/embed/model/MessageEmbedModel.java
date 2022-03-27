package network.frostless.fragment.embed.model;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;


@Data
public class MessageEmbedModel {

    private String title;

    private int color;

    private String description;

    private String timestamp;

    private String url;

    private MessageAuthorModel author;

    private MessageImageModel image;

    private MessageThumbnailModel thumbnail;

    private MessageFooterModel footer;

    private List<MessageFieldModel> fields;
}
