package vn.nhu2410.minecraftmpris.overlay;

import net.minecraft.client.Minecraft;
import vn.nhu2410.minecraftmpris.config.ConfigHandler;

public class OverlayAreaHandler {
    public static final int PADDING = 5;
    private static final int ALBUM_ART_TEXT_GAP = 7;
    public static final int PROGRESS_BAR_HEIGHT = 4;

    private static int getTextHeight() {
        return Minecraft.getInstance().font.lineHeight;
    }

    public static int getRealOverlayWidth() {
        int albumArtSpace = ConfigHandler.HANDLER.instance().showAlbumArt
            ? ConfigHandler.HANDLER.instance().albumArtSize + ALBUM_ART_TEXT_GAP
            : 0;
        return albumArtSpace + ConfigHandler.HANDLER.instance().overlayWidth - ConfigHandler.HANDLER.instance().albumArtSize - ALBUM_ART_TEXT_GAP;
    }

    public static int getRealOverlayHeight() {
        return ConfigHandler.HANDLER.instance().overlayHeight;
    }

    public static int getContentStartX(int overlayX) {
        int contentStartX = overlayX + PADDING;
        if (ConfigHandler.HANDLER.instance().showAlbumArt) {
            contentStartX += ConfigHandler.HANDLER.instance().albumArtSize + ALBUM_ART_TEXT_GAP;
        }
        return contentStartX;
    }

    public static int getTextWidth(int overlayX, int actualOverlayWidth) {
        int contentStartX = getContentStartX(overlayX);
        return actualOverlayWidth - (contentStartX - overlayX) - PADDING;
    }

    private static int getEvenSpacing() {
        int visibleElements = 0;
        int totalContentHeight = 0;

        visibleElements++;
        totalContentHeight += getTextHeight();

        if (ConfigHandler.HANDLER.instance().showArtist) {
            visibleElements++;
            totalContentHeight += getTextHeight();
        }

        if (ConfigHandler.HANDLER.instance().showProgressBar) {
            visibleElements++;
            totalContentHeight += getTextHeight();
        }

        if (ConfigHandler.HANDLER.instance().showCurrentTime || ConfigHandler.HANDLER.instance().showPlayingIndicator) {
            visibleElements++;
            totalContentHeight += getTextHeight();
        }

        int availableSpace = ConfigHandler.HANDLER.instance().overlayHeight - (2 * PADDING) - totalContentHeight;
        return Math.max(0, availableSpace / visibleElements);
    }

    public static int getTitleY() {
        return PADDING + getEvenSpacing();
    }

    public static int getArtistY() {
        return getTitleY() + getTextHeight() + getEvenSpacing();
    }

    public static int getProgressBarY() {
        int y = getTitleY() + getTextHeight();
        if (ConfigHandler.HANDLER.instance().showArtist) {
            y = getArtistY() + getTextHeight();
        }
        return y + getEvenSpacing();
    }

    public static int getTimeY() {
        int y = getTitleY() + getTextHeight();
        if (ConfigHandler.HANDLER.instance().showArtist) {
            y = getArtistY() + getTextHeight();
        }
        if (ConfigHandler.HANDLER.instance().showProgressBar) {
            y = getProgressBarY() + getTextHeight();
        }
        return y + getEvenSpacing();
    }

    public static int getAlbumArtX(int overlayX) {
        return overlayX + PADDING;
    }

    public static int getAlbumArtY(int overlayY) {
        return overlayY + PADDING;
    }

    public static int getProgressBarCenterOffset() {
        return (getTextHeight() - PROGRESS_BAR_HEIGHT) / 2;
    }

    public static int getRealX(int screenWidth) {
        int actualOverlayWidth = getRealOverlayWidth();
        return switch (ConfigHandler.HANDLER.instance().anchor) {
            case TOP_RIGHT, BOTTOM_RIGHT ->
                screenWidth - actualOverlayWidth - ConfigHandler.HANDLER.instance().xOffset;
            default -> ConfigHandler.HANDLER.instance().xOffset;
        };
    }

    public static int getRealY(int screenHeight) {
        int actualOverlayHeight = getRealOverlayHeight();
        return switch (ConfigHandler.HANDLER.instance().anchor) {
            case BOTTOM_LEFT, BOTTOM_RIGHT ->
                screenHeight - actualOverlayHeight - ConfigHandler.HANDLER.instance().yOffset;
            default -> ConfigHandler.HANDLER.instance().yOffset;
        };
    }
}
