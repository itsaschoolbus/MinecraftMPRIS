package vn.nhu2410.minecraftmpris.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import vn.nhu2410.minecraftmpris.config.ConfigHandler;
import vn.nhu2410.minecraftmpris.metadata.MetadataHandler;
import vn.nhu2410.minecraftmpris.metadata.MetadataTransformer;

public class MediaOverlay {
    private String lastArtUrl = null;

    public void renderMediaOverlay(GuiGraphics gui, Minecraft mc) {
        if (MetadataHandler.title == null ||
            MetadataHandler.title.isEmpty() ||
            MetadataHandler.length <= 0
        ) return;

        if (ConfigHandler.HANDLER.instance().showAlbumArt && MetadataHandler.artUrl != null && !MetadataHandler.artUrl.equals(lastArtUrl)) {
            MetadataTransformer.loadAlbumArt(mc, MetadataHandler.artUrl);
            lastArtUrl = MetadataHandler.artUrl;
        } else if (!ConfigHandler.HANDLER.instance().showAlbumArt) {
            MetadataTransformer.clearAlbumArt(mc);
        }

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        int x = OverlayAreaHandler.getRealX(width);
        int y = OverlayAreaHandler.getRealY(height);

        int realOverlayWidth = OverlayAreaHandler.getRealOverlayWidth();
        int realOverlayHeight = OverlayAreaHandler.getRealOverlayHeight();

        if (ConfigHandler.HANDLER.instance().showBackground) {
            gui.fill(x, y, x + realOverlayWidth, y + realOverlayHeight, 0x88000000);
        }

        if (ConfigHandler.HANDLER.instance().showAlbumArt) {
            int albumArtX = OverlayAreaHandler.getAlbumArtX(x);
            int albumArtY = OverlayAreaHandler.getAlbumArtY(y);

            if (MetadataTransformer.albumArtTexture != null && MetadataTransformer.albumArtId != null) {
                gui.blit(
                    RenderPipelines.GUI_TEXTURED,
                    MetadataTransformer.albumArtId,
                    albumArtX, albumArtY,
                    0, 0,
                    ConfigHandler.HANDLER.instance().albumArtSize,
                    ConfigHandler.HANDLER.instance().albumArtSize,
                    ConfigHandler.HANDLER.instance().albumArtSize,
                    ConfigHandler.HANDLER.instance().albumArtSize
                );
            } else {
                gui.fill(albumArtX, albumArtY,
                    albumArtX + ConfigHandler.HANDLER.instance().albumArtSize,
                    albumArtY + ConfigHandler.HANDLER.instance().albumArtSize,
                    0xFF333333
                );
            }
        }

        int textX = OverlayAreaHandler.getContentStartX(x);
        int textWidth = OverlayAreaHandler.getTextWidth(x, realOverlayWidth);
        String title = truncateText(mc, MetadataHandler.title, textWidth);;
        String artist = truncateText(mc, MetadataHandler.artist, textWidth);
        gui.drawString(mc.font, title, textX, y + OverlayAreaHandler.getTitleY(), 0xFFFFFFFF, ConfigHandler.HANDLER.instance().showTextShadow);

        if (ConfigHandler.HANDLER.instance().showArtist) {
            gui.drawString(mc.font, artist, textX, y + OverlayAreaHandler.getArtistY(), 0xFFAAAAAA, ConfigHandler.HANDLER.instance().showTextShadow);
        }

        if (ConfigHandler.HANDLER.instance().showProgressBar) {
            int barY = y + OverlayAreaHandler.getProgressBarY() + OverlayAreaHandler.getProgressBarCenterOffset();
            int barStart = textX;
            int barEnd = x + realOverlayWidth - OverlayAreaHandler.PADDING;
            int barWidth = barEnd - barStart;

            float progress = 0f;
            if (MetadataHandler.length > 0 && MetadataHandler.position >= 0) {
                progress = Math.min(1f, (float) MetadataHandler.position / (float) MetadataHandler.length);
                if (Float.isNaN(progress)) progress = 0f;
                if (progress < 0) progress = 0;
            }

            int filled = (int) (barWidth * progress);

            int barHeight = OverlayAreaHandler.PROGRESS_BAR_HEIGHT;
            gui.fill(barStart, barY, barEnd, barY + barHeight, 0xFF333333);
            if (filled > 0) {
                gui.fill(barStart, barY, barStart + filled, barY + barHeight, 0xFFFFFFFF);
            }
        }

        int timeY = y + OverlayAreaHandler.getTimeY();
        if (ConfigHandler.HANDLER.instance().showCurrentTime) {
            String timeStr = formatTime(MetadataHandler.position) + " / " + formatTime(MetadataHandler.length);
            gui.drawString(mc.font, timeStr, textX, timeY, 0xFFCCCCCC, ConfigHandler.HANDLER.instance().showTextShadow);
        }

        if (ConfigHandler.HANDLER.instance().showPlayingIndicator) {
            String playStatus = MetadataHandler.playing ? "▶" : "⏸";
            int indicatorX = x + realOverlayWidth - 15;
            gui.drawString(mc.font, playStatus, indicatorX, timeY, 0xFFFFFFFF, ConfigHandler.HANDLER.instance().showTextShadow);
        }
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
