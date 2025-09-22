package com.bingoplugins.bwarps.commands;

import com.bingoplugins.bwarps.api.returncodes.WarpOperationsReturnCode;
import com.bingoplugins.bwarps.api.warps.Warp;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

import static com.bingoplugins.bwarps.Plugin.LOGGER;
import static com.bingoplugins.bwarps.Plugin.PLUGIN;

public class WarpsCommand {
    public static LiteralCommandNode<CommandSourceStack> getNode() {
        return Commands
                .literal("bwarps")
                .requires(source -> source.getSender().isOp())
                .executes(context -> {
                    final CommandSender sender = context.getSource().getSender();

                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<color:#f72c25>[</color><color:#fcf6b1>BingoPlugins</color><color:#f72c25>]</color> <color:#a9e5bb>Written by<color:#2d1e2f>:</color> <color:#f7b32b>Pritam</color></color>"
                    ));
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<color:#f72c25>[</color><color:#fcf6b1>BingoPlugins</color><color:#f72c25>]</color> <color:#a9e5bb>Version<color:#2d1e2f>:</color> <color:#f7b32b><version></color></color>",
                            TagResolver.resolver(
                                    "version", Tag.inserting(Component.text(PLUGIN.getPluginMeta().getVersion()))
                            )
                    ));

                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands
                        .literal("create")
                        .requires(source -> source.getSender() instanceof Player)
                        .then(Commands
                                .argument("id", StringArgumentType.word())
                                .then(Commands
                                        .argument("name", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            final Player player = (Player) context.getSource().getSender();

                                            final String id = StringArgumentType.getString(context, "id");
                                            final String name = StringArgumentType.getString(context, "name");

                                            final WarpOperationsReturnCode returnCode =  PLUGIN.createWarp(id, new Warp(name, player.getLocation()));

                                            switch (returnCode) {
                                                case WARP_ALREADY_EXISTS ->
                                                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                                                            "<color:#f72c25>[</color><color:#fcf6b1>BingoPlugins</color><color:#f72c25>]</color> <color:#a9e5bb>A warp with the id <color:#f7b32b><id></color> already exists<color:#2d1e2f>!</color></color>",
                                                            TagResolver.resolver(
                                                                    "id", Tag.inserting(Component.text(id))
                                                            )
                                                    ));
                                                case SUCCESS ->
                                                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                                                            "<color:#f72c25>[</color><color:#fcf6b1>BingoPlugins</color><color:#f72c25>]</color> <color:#a9e5bb>Created a warp with the id <color:#f7b32b><id></color><color:#2d1e2f>!</color></color>",
                                                            TagResolver.resolver(
                                                                    "id", Tag.inserting(Component.text(id))
                                                            )
                                                    ));
                                            }

                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
                .then(Commands
                        .literal("delete")
                        .then(Commands
                                .argument("id", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    PLUGIN.getWarpIDs().forEach(id -> PLUGIN.getWarp(id).ifPresent(warp -> builder.suggest(id, MessageComponentSerializer.message().serialize(Component
                                                        .text(warp.getName())
                                                        .color(TextColor.fromHexString("#f7b32b"))
                                                ))
                                        )
                                    );

                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    final CommandSender sender = context.getSource().getSender();
                                    final String warpID = StringArgumentType.getString(context, "id");
                                    final WarpOperationsReturnCode returnCode = PLUGIN.deleteWarp(warpID);

                                    switch (returnCode) {
                                        case WARP_DOESNT_EXIST ->
                                            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                                    "<color:#f72c25>[</color><color:#fcf6b1>BingoPlugins</color><color:#f72c25>]</color> <color:#a9e5bb>A warp with the id <color:#f7b32b><id></color> doesn't exist<color:#2d1e2f>!</color></color>",
                                                    TagResolver.resolver(
                                                            "id", Tag.inserting(Component.text(warpID))
                                                    )
                                            ));
                                        case SUCCESS ->
                                            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                                    "<color:#f72c25>[</color><color:#fcf6b1>BingoPlugins</color><color:#f72c25>]</color> <color:#a9e5bb>Deleted warp with the id <color:#f7b32b><id></color><color:#2d1e2f>!</color></color>",
                                                    TagResolver.resolver(
                                                            "id", Tag.inserting(Component.text(warpID))
                                                    )
                                            ));
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands
                        .literal("warp")
                        .then(Commands
                                .argument("id", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    PLUGIN.getWarpIDs().forEach(id -> PLUGIN.getWarp(id).ifPresent(warp -> builder.suggest(id, MessageComponentSerializer.message().serialize(Component
                                                            .text(warp.getName())
                                                            .color(TextColor.fromHexString("#f7b32b"))
                                                    ))
                                            )
                                    );

                                    return builder.buildFuture();
                                })
                                .requires(source -> source.getSender() instanceof Player)
                                .executes(context -> {
                                    final Player player = (Player) context.getSource().getSender();
                                    final String warpID = StringArgumentType.getString(context, "id");
                                    final Optional<Warp> warp = PLUGIN.getWarp(warpID);

                                    if (warp.isEmpty()) {
                                        player.sendMessage(MiniMessage.miniMessage().deserialize(
                                                "<color:#f72c25>[</color><color:#fcf6b1>BingoPlugins</color><color:#f72c25>]</color> <color:#a9e5bb>A warp with the id <color:#f7b32b><id></color> doesn't exist<color:#2d1e2f>!</color></color>",
                                                TagResolver.resolver(
                                                        "id", Tag.inserting(Component.text(warpID))
                                                )
                                        ));

                                        return Command.SINGLE_SUCCESS;
                                    }

                                    PLUGIN.teleport(player, warp.get()).thenAccept(returnCode -> {
                                        switch (returnCode) {
                                            case SUCCESS -> player.sendMessage(MiniMessage.miniMessage().deserialize(
                                                    "<color:#f72c25>[</color><color:#fcf6b1>BingoPlugins</color><color:#f72c25>]</color> <color:#a9e5bb>Teleported to <color:#f7b32b><name></color><color:#2d1e2f>!</color></color>",
                                                    TagResolver.resolver(
                                                            "name", Tag.inserting(Component.text(warp.get().getName()))
                                                    )
                                            ));
                                            case FAILURE -> player.sendMessage(MiniMessage.miniMessage().deserialize(
                                                    "<color:#f72c25>[</color><color:#fcf6b1>BingoPlugins</color><color:#f72c25>]</color> <color:#a9e5bb>Failed to teleport to <color:#f7b32b><name></color><color:#2d1e2f>!</color></color>",
                                                    TagResolver.resolver(
                                                            "name", Tag.inserting(Component.text(warp.get().getName()))
                                                    )
                                            ));
                                            case WORLD_NOT_AVAILABLE -> {
                                                player.sendMessage(MiniMessage.miniMessage().deserialize(
                                                        "<color:#f72c25>[</color><color:#fcf6b1>BingoPlugins</color><color:#f72c25>]</color> <color:#a9e5bb>Failed to teleport to <color:#f7b32b><name></color><color:#2d1e2f>!</color></color>",
                                                        TagResolver.resolver(
                                                                "name", Tag.inserting(Component.text(warp.get().getName()))
                                                        )
                                                ));

                                                LOGGER.severe("World for warp with id %s is not available, players will not be able to warp there!".formatted(warpID));
                                            }
                                        }
                                    });

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands
                        .literal("reload")
                        .executes(context -> {
                            PLUGIN.getMainConfig().reload();
                            PLUGIN.getWarpManager().load();

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }
}