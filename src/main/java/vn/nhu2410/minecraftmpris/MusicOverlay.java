package vn.nhu2410.minecraftmpris;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

public class MusicOverlay {

    public static String title = "Unknown Title";
    public static String artist = "Unknown Artist";
    public static int position = 0;
    public static int length = 1;
    public static boolean playing = false;
    public static String artUrl = null;
    
    private static DynamicTexture albumArtTexture = null;
    private static Identifier albumArtId = null;
    private static String lastArtUrl = null;

    public static void register() {
        Identifier overlayId = Identifier.fromNamespaceAndPath(
            MinecraftMpris.MOD_ID,
            "music_overlay"
        );
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            overlayId,
            (gui, delta) -> renderOverlay(gui, delta)
        );
    }

    private static void renderOverlay(GuiGraphics gui, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;
        if (title == null || title.isEmpty() || length <= 0) return;

        // update album art if url changed
        if (artUrl != null && !artUrl.equals(lastArtUrl)) {
            loadAlbumArt(mc, artUrl);
            lastArtUrl = artUrl;
        }

        int width = mc.getWindow().getGuiScaledWidth();

        int albumSize = 48;
        int boxWidth = 200;
        int boxHeight = 58;

        int x = width - boxWidth - 10;
        int y = 10;

        // background
        gui.fill(x, y, x + boxWidth, y + boxHeight, 0x88000000);

        // album art
        if (albumArtTexture != null && albumArtId != null) {
            gui.blit(RenderPipelines.GUI_TEXTURED, albumArtId, x + 5, y + 5, 0, 0, albumSize, albumSize, albumSize, albumSize);
        } else {
            gui.fill(x + 5, y + 5, x + 5 + albumSize, y + 5 + albumSize, 0xFF333333);
        }

        // text after album art
        int textX = x + albumSize + 12;
        int textWidth = boxWidth - albumSize - 17;

        String displayTitle = truncateText(mc, title, textWidth);
        String displayArtist = truncateText(mc, artist, textWidth);

        // text
        gui.drawString(mc.font, displayTitle, textX, y + 8, 0xFFFFFFFF, true);
        gui.drawString(mc.font, displayArtist, textX, y + 20, 0xFFAAAAAA, true);

        // progress bar
        int barStart = textX;
        int barEnd = x + boxWidth - 5;
        int barWidth = barEnd - barStart;

        float progress = 0f;
        if (length > 0 && position >= 0) {
            progress = Math.min(1f, (float) position / (float) length);
            if (Float.isNaN(progress)) progress = 0f;
            if (progress < 0) progress = 0;
        }

        int filled = (int) (barWidth * progress);

        // bar
        gui.fill(barStart, y + 35, barEnd, y + 39, 0xFF333333);
        if (filled > 0) {
            gui.fill(barStart, y + 35, barStart + filled, y + 39, 0xFF00DD00);
        }

        // time
        String timeStr = formatTime(position) + " / " + formatTime(length);
        gui.drawString(mc.font, timeStr, textX, y + 43, 0xFFCCCCCC, false);
        
        // playing indicator
        String playStatus = playing ? "▶" : "⏸";
        gui.drawString(mc.font, playStatus, x + boxWidth - 15, y + 43, 0xFFFFFFFF, false);
    }

    private static void loadAlbumArt(Minecraft mc, String url) {
        new Thread(() -> {
            try {
                BufferedImage image = ImageIO.read(new URL(url));
                if (image == null) return;

                // resize to 48x48
                BufferedImage resized = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
                java.awt.Graphics2D g2d = resized.createGraphics();
                g2d.drawImage(image, 0, 0, 48, 48, null);
                g2d.dispose();

                // convert to NativeImage
                NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, 48, 48, true);
                for (int y = 0; y < 48; y++) {
                    for (int x = 0; x < 48; x++) {
                        int argb = resized.getRGB(x, y);
                        nativeImage.setPixel(x, y, argb);
                    }
                }

                mc.execute(() -> {
                    try {
                        if (albumArtTexture != null) {
                            albumArtTexture.close();
                        }
                        albumArtId = Identifier.fromNamespaceAndPath(MinecraftMpris.MOD_ID, "albumart");
                        albumArtTexture = new DynamicTexture(() -> "albumart", nativeImage);
                        mc.getTextureManager().register(albumArtId, albumArtTexture);
                    } catch (Exception e) {
                        MinecraftMpris.LOGGER.error("Failed to register album art texture", e);
                    }
                });
            } catch (Exception e) {
                MinecraftMpris.LOGGER.error("Failed to load album art", e);
            }
        }).start();
    }

    private static String truncateText(Minecraft mc, String text, int maxWidth) {
        if (mc.font.width(text) <= maxWidth) {
            return text;
        }
        
        String ellipsis = "...";
        int ellipsisWidth = mc.font.width(ellipsis);
        
        StringBuilder truncated = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String current = truncated.toString() + text.charAt(i);
            if (mc.font.width(current) + ellipsisWidth > maxWidth) {
                return truncated.toString() + ellipsis;
            }
            truncated.append(text.charAt(i));
        }
        
        return text;
    }
    
    private static String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", mins, secs);
    }
}