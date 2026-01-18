package vn.nhu2410.minecraftmpris.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindRegistry {
    public static KeyBinding playPauseKey;
    public static KeyBinding nextTrackKey;
    public static KeyBinding prevTrackKey;
    public static KeyBinding configKey;

    public static void registerKeybinds() {
        playPauseKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "minecraftmpris.key.playpause",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "key.category.minecraftmpris.controls"
            )
        );

        nextTrackKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "minecraftmpris.key.next",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_BRACKET,
                "key.category.minecraftmpris.controls"
            )
        );

        prevTrackKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "minecraftmpris.key.previous",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_BRACKET,
                "key.category.minecraftmpris.controls"
            )
        );

        configKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "minecraftmpris.key.config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_BACKSLASH,
                "key.category.minecraftmpris.controls"
            )
        );
    }
}
