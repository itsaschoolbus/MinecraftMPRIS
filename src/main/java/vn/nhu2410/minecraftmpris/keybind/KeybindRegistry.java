package vn.nhu2410.minecraftmpris.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import vn.nhu2410.minecraftmpris.MinecraftMprisClient;

public class KeybindRegistry {
    public static KeyMapping playPauseKey;
    public static KeyMapping nextTrackKey;
    public static KeyMapping prevTrackKey;
    public static KeyMapping configKey;

    public static void registerKeybinds() {
        final KeyMapping.Category MPRIS_BINDS = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(
                MinecraftMprisClient.MOD_ID, "controls"
            )
        );

        playPauseKey = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "minecraftmpris.key.playpause",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                MPRIS_BINDS
            )
        );

        nextTrackKey = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "minecraftmpris.key.next",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_BRACKET,
                MPRIS_BINDS
            )
        );

        prevTrackKey = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "minecraftmpris.key.previous",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_BRACKET,
                MPRIS_BINDS
            )
        );

        configKey = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "minecraftmpris.key.config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_BACKSLASH,
                MPRIS_BINDS
            )
        );
    }
}
