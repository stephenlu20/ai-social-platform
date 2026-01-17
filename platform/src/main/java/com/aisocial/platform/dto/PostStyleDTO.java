package com.aisocial.platform.dto;

import java.util.Set;

/**
 * PostStyle JSON schema (#68)
 * Defines the style options for customizing post appearance.
 */
public class PostStyleDTO {

    // Available font options
    public static final Set<String> VALID_FONTS = Set.of(
        "default",      // Plus Jakarta Sans (system default)
        "serif",        // Georgia, serif
        "mono",         // Fira Code, monospace
        "handwritten",  // Caveat, cursive
        "bold",         // Plus Jakarta Sans, bold
        "condensed"     // Roboto Condensed
    );

    // Available text color options
    public static final Set<String> VALID_COLORS = Set.of(
        "default",  // white/90
        "pink",     // veritas-pink
        "purple",   // veritas-purple
        "blue",     // veritas-blue
        "green",    // green-400
        "orange"    // orange-400
    );

    // Available background gradient options
    public static final Set<String> VALID_BACKGROUNDS = Set.of(
        "none",         // transparent (default)
        "pink-purple",  // pink to purple gradient
        "blue-purple",  // blue to purple gradient
        "green-blue",   // green to blue gradient
        "orange-pink",  // orange to pink gradient
        "dark",         // dark gradient
        "sunset"        // orange to pink to purple
    );

    // Available text size options
    public static final Set<String> VALID_SIZES = Set.of(
        "default",  // text-[15px] (normal)
        "small",    // text-sm
        "large",    // text-xl
        "xlarge"    // text-2xl
    );

    private String font = "default";
    private String textColor = "default";
    private String background = "none";
    private String size = "default";

    public PostStyleDTO() {}

    public PostStyleDTO(String font, String textColor, String background, String size) {
        this.font = font;
        this.textColor = textColor;
        this.background = background;
        this.size = size;
    }

    // Validation method (#69)
    public boolean isValid() {
        return (font == null || VALID_FONTS.contains(font))
            && (textColor == null || VALID_COLORS.contains(textColor))
            && (background == null || VALID_BACKGROUNDS.contains(background))
            && (size == null || VALID_SIZES.contains(size));
    }

    public String getFont() { return font; }
    public void setFont(String font) { this.font = font; }

    public String getTextColor() { return textColor; }
    public void setTextColor(String textColor) { this.textColor = textColor; }

    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
}
