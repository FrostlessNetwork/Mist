package network.frostless.mist.services.autorole;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ComponentLayout;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import network.frostless.mist.Application;
import network.frostless.mist.Mist;
import network.frostless.mist.config.MistConfig;
import network.frostless.mist.config.model.AutoRoleModel;
import network.frostless.mist.config.model.SelectOptionModel;
import network.frostless.mist.core.service.impl.EventableService;
import network.frostless.mist.services.autorole.model.ButtonModel;
import network.frostless.mist.config.model.common.EmojiModel;
import org.spongepowered.configurate.ConfigurateException;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

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

    public SelectionMenu getRolesAsSelection(String customId) {
        AutoRoleModel autoRole = config.get().getAutoRole();
        List<SelectOption> collect = autoRole.getSelectOptions().get(customId).stream().map(SelectOptionModel::to).collect(Collectors.toList());

        SelectionMenu.Builder builder = SelectionMenu.create("autorole-" + customId);
        builder.addOptions(collect);
        builder.setMinValues(0);
        builder.setMaxValues(collect.size());

        return builder.build();
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
    public void onChooseSelection(SelectionMenuEvent event) {
        if(!event.getComponentId().startsWith("autorole-")) return;

        event.deferReply(true).queue();

        InteractionHook hook = event.getHook();

        String roleIdentifier = event.getComponentId().split("autorole-")[1];
        List<SelectOptionModel> configuredRoles = config.get().getAutoRole().getSelectOptions().get(roleIdentifier);
        if (configuredRoles == null) {
            hook.editOriginal("No roles configured for this selection menu.").queue();
            return;
        }
        Member member = event.getMember();
        if (member == null) {
            hook.editOriginal("You must be a member to use this selection menu.").queue();
            return;
        }

        List<Map.Entry<Role, Boolean>> rolesTheyWant = configuredRoles
                .stream()
                .map(r -> {
                    Role roleById = member.getGuild().getRoleById(r.getRole());
                    if (roleById == null) return null;
                    return event.getValues().contains(r.getValue()) ? Map.entry(roleById, true) : Map.entry(roleById, false);
                }).toList();

        for (Map.Entry<Role, Boolean> rolesToChange : rolesTheyWant) {
            if (rolesToChange.getValue() && !member.getRoles().contains(rolesToChange.getKey())) {
                // Add to user
                member.getGuild().addRoleToMember(member, rolesToChange.getKey()).queue();
            } else if (!rolesToChange.getValue()) {
                // remove this
                member.getGuild().removeRoleFromMember(member, rolesToChange.getKey()).queue();
            }
        }

        event.getHook().sendMessage("Your roles have been updated!").setEphemeral(true).queue();
    }


    @SubscribeEvent
    public void onReact(GuildMessageReactionAddEvent evt) {
        if (evt.getUser().isBot()) return;

        getReaction(evt.getMessageId(), evt.getReactionEmote()).ifPresent(r -> {
            Role roleById = evt.getJDA().getRoleById(r.getKey());
            if (roleById == null) return;
            evt.getGuild().addRoleToMember(evt.getMember(), roleById).queue();
        });
    }

    @SubscribeEvent
    public void onUnReact(GuildMessageReactionRemoveEvent evt) {
        if (evt.getUser().isBot()) return;

        if (evt.getMember() == null) return;

        getReaction(evt.getMessageId(), evt.getReactionEmote()).ifPresent(r -> {
            Role roleById = evt.getJDA().getRoleById(r.getKey());
            if (roleById == null) return;
            evt.getGuild().removeRoleFromMember(evt.getMember(), roleById).queue();
        });
    }

    private Optional<Map.Entry<String, EmojiModel>> getReaction(String messageId, MessageReaction.ReactionEmote emote) {
        Map<String, EmojiModel> emoji = Mist.get().getConfig().get().getAutoRole().getSelectReactions().get(messageId);

        if (emoji == null) return Optional.empty();

        return emoji.entrySet().stream().filter(e -> e.getValue().getIcon().equals(emote.getEmoji())).findFirst();
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
