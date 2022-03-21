package network.frostless.mist.services.autorole;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ComponentLayout;
import network.frostless.mist.Application;
import network.frostless.mist.config.MistConfig;
import network.frostless.mist.core.service.impl.EventableService;
import network.frostless.mist.services.autorole.model.ButtonModel;
import network.frostless.mist.services.autorole.model.EmojiModel;
import org.spongepowered.configurate.ConfigurateException;

import java.util.*;

public class AutoRoleService implements EventableService {

    private final MistConfig config;

    private final Map<String, List<ComponentLayout>> autoRoles = new HashMap<>();


    private final List<ButtonModel> buttonModels = new ArrayList<>();

    public AutoRoleService() {
        this.config = Application.config;
    }


    public void refreshRoles() {
        try {
            config.reload();
            loadRoles();
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    private void loadRoles() {
        buttonModels.clear();
        Map<String, Map<Integer, Map<Integer, ButtonModel>>> buttons = config.get().getAutoRole().getButtons();

        for (Map.Entry<String, Map<Integer, Map<Integer, ButtonModel>>> typesToComponentLayout : buttons.entrySet()) {
            Map<Integer, Map<Integer, ButtonModel>> rowsToColumns = typesToComponentLayout.getValue();

            List<ComponentLayout> actionRows = new ArrayList<>();
            for (Map<Integer, ButtonModel> value : rowsToColumns.values()) {
                List<Button> rowButtons = new ArrayList<>();

                for (ButtonModel buttonModel : value.values()) {
                    rowButtons.add(parseButton(buttonModel));
                }

                buttonModels.addAll(value.values());
                actionRows.add(ActionRow.of(rowButtons));
            }


            autoRoles.put(typesToComponentLayout.getKey(), actionRows);
        }
    }

    public List<ComponentLayout> getAutoRoleComponents(String id) {
        return autoRoles.get(id);
    }

    private Button parseButton(ButtonModel button) {
        Button btn = Button.of(button.getButtonStyle(), button.getIdOrUrl(), button.getLabel());

        if (button.getEmoji() != null && button.getEmoji().getIcon() != null) {
            final EmojiModel emoji = button.getEmoji();
            btn = btn.withEmoji(emoji.isCustom() ? Emoji.fromEmote(emoji.getIcon(), emoji.getId(), emoji.isAnimated()) : Emoji.fromUnicode(emoji.getIcon()));
        }

        return btn;
    }

    @SubscribeEvent
    public void onButtonClick(ButtonClickEvent event) {
        InteractionHook interactionHook = event.getHook().setEphemeral(true);
        if (event.getGuild() == null || event.getButton() == null || event.getMember() == null) return;

        Optional<ButtonModel> first = buttonModels.stream().filter(b -> b.getIdOrUrl().equals(event.getButton().getId())).findFirst();


        first.ifPresent(btnModel -> {
            if (btnModel.getRole() == null) {
                interactionHook.sendMessage("This button has no role assigned to it.").queue();
                return;
            }

            Role role = event.getGuild().getRoleById(btnModel.getRole());
            if (role == null) {
                interactionHook.sendMessage("Role not found").queue();
                return;
            }

            event.getGuild().addRoleToMember(event.getMember(), role).queue((s) -> interactionHook.sendMessage("You have been given the role " + event.getButton().getLabel()).setEphemeral(true).queue());
        });


    }
}
