package network.frostless.fragment.embed.model;

import lombok.Data;

@Data
public class MessageFieldModel {

    private String name;

    private String value;

    private boolean inline = false;
}
