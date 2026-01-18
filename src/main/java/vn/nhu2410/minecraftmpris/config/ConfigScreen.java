package vn.nhu2410.minecraftmpris.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen {
    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
        .title(Component.translatable("minecraftmpris.config.title"))
        .category(ConfigCategory.createBuilder()
                .name(Component.translatable("minecraftmpris.config.category.position"))
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("minecraftmpris.config.option.x_offset"))
                        .description(OptionDescription.of(Component.translatable("minecraftmpris.config.description.x_offset")))
                        .binding(ConfigHandler.DefaultConfigOptions.xOffset,
                                () -> ConfigHandler.HANDLER.instance().xOffset,
                                newVal -> ConfigHandler.HANDLER.instance().xOffset = newVal)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("minecraftmpris.config.option.y_offset"))
                        .description(OptionDescription.of(Component.translatable("minecraftmpris.config.description.y_offset")))
                        .binding(ConfigHandler.DefaultConfigOptions.yOffset,
                                () -> ConfigHandler.HANDLER.instance().yOffset,
                                newVal -> ConfigHandler.HANDLER.instance().yOffset = newVal)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1))
                        .build())
                .option(Option.<ConfigHandler.AnchorPoints>createBuilder()
                        .name(Component.translatable("minecraftmpris.config.option.anchor_point"))
                        .description(OptionDescription.of(Component.translatable("minecraftmpris.config.description.anchor_point")))
                        .binding(ConfigHandler.DefaultConfigOptions.anchor,
                                () -> ConfigHandler.HANDLER.instance().anchor,
                                newVal -> ConfigHandler.HANDLER.instance().anchor = newVal)
                        .controller(opt -> EnumControllerBuilder.create(opt)
                                .enumClass(ConfigHandler.AnchorPoints.class)
                                .formatValue(v -> Component.translatable(
                                        "minecraftmpris.config.anchor."
                                        + v.name().toLowerCase())))
                        .build())
                .build())
        .category(ConfigCategory.createBuilder()
                .name(Component.translatable("minecraftmpris.config.category.visual"))
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("minecraftmpris.config.option.show_background"))
                        .description(OptionDescription.of(Component.translatable("minecraftmpris.config.description.show_background")))
                        .binding(ConfigHandler.DefaultConfigOptions.showBackground,
                                () -> ConfigHandler.HANDLER.instance().showBackground,
                                newVal -> ConfigHandler.HANDLER.instance().showBackground = newVal)
                        .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("minecraftmpris.config.option.overlay_width"))
                        .description(OptionDescription.of(Component.translatable("minecraftmpris.config.description.overlay_width")))
                        .binding(ConfigHandler.DefaultConfigOptions.overlayWidth,
                                () -> ConfigHandler.HANDLER.instance().overlayWidth,
                                newVal -> ConfigHandler.HANDLER.instance().overlayWidth = newVal)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(50, 300)
                                .step(1))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("minecraftmpris.config.option.overlay_height"))
                        .description(OptionDescription.of(Component.translatable("minecraftmpris.config.description.overlay_height")))
                        .binding(ConfigHandler.DefaultConfigOptions.overlayHeight,
                                () -> ConfigHandler.HANDLER.instance().overlayHeight,
                                newVal -> ConfigHandler.HANDLER.instance().overlayHeight = newVal)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 110)
                                .step(1))
                        .build())
                .build())
        .category(ConfigCategory.createBuilder()
                .name(Component.translatable("minecraftmpris.config.category.content"))
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("minecraftmpris.config.option.album_art_size"))
                        .description(OptionDescription.of(Component.translatable("minecraftmpris.config.description.album_art_size")))
                        .binding(ConfigHandler.DefaultConfigOptions.albumArtSize,
                                () -> ConfigHandler.HANDLER.instance().albumArtSize,
                                newVal -> ConfigHandler.HANDLER.instance().albumArtSize = newVal)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("minecraftmpris.config.option.show_album_art"))
                        .description(OptionDescription.of(Component.translatable("minecraftmpris.config.description.show_album_art")))
                        .binding(ConfigHandler.DefaultConfigOptions.showAlbumArt,
                                () -> ConfigHandler.HANDLER.instance().showAlbumArt,
                                newVal -> ConfigHandler.HANDLER.instance().showAlbumArt = newVal)
                        .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("minecraftmpris.config.option.show_artist"))
                        .description(OptionDescription.of(Component.translatable("minecraftmpris.config.description.show_artist")))
                        .binding(ConfigHandler.DefaultConfigOptions.showArtist,
                                () -> ConfigHandler.HANDLER.instance().showArtist,
                                newVal -> ConfigHandler.HANDLER.instance().showArtist = newVal)
                        .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("minecraftmpris.config.option.show_current_time"))
                        .description(OptionDescription.of(Component.translatable("minecraftmpris.config.description.show_current_time")))
                        .binding(ConfigHandler.DefaultConfigOptions.showCurrentTime,
                                () -> ConfigHandler.HANDLER.instance().showCurrentTime,
                                newVal -> ConfigHandler.HANDLER.instance().showCurrentTime = newVal)
                        .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("minecraftmpris.config.option.show_playing_indicator"))
                        .description(OptionDescription.of(Component.translatable("minecraftmpris.config.description.show_playing_indicator")))
                        .binding(ConfigHandler.DefaultConfigOptions.showPlayingIndicator,
                                () -> ConfigHandler.HANDLER.instance().showPlayingIndicator,
                                newVal -> ConfigHandler.HANDLER.instance().showPlayingIndicator = newVal)
                        .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("minecraftmpris.config.option.show_progress_bar"))
                        .description(OptionDescription.of(Component.translatable("minecraftmpris.config.description.show_progress_bar")))
                        .binding(ConfigHandler.DefaultConfigOptions.showProgressBar,
                                () -> ConfigHandler.HANDLER.instance().showProgressBar,
                                newVal -> ConfigHandler.HANDLER.instance().showProgressBar = newVal)
                        .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("minecraftmpris.config.option.show_text_shadow"))
                        .description(OptionDescription.of(Component.translatable("minecraftmpris.config.description.show_text_shadow")))
                        .binding(ConfigHandler.DefaultConfigOptions.showTextShadow,
                                () -> ConfigHandler.HANDLER.instance().showTextShadow,
                                newVal -> ConfigHandler.HANDLER.instance().showTextShadow = newVal)
                        .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true))
                        .build())
                .build())
        .save(ConfigHandler.HANDLER::save)
        .build()
        .generateScreen(parent);
    }
}
