package haven;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.List;

public class ItemQuality {
    private static final DecimalFormat format = new DecimalFormat("#.##");

    public final Element quality;

    public ItemQuality(double quality) {
        this.quality = new Element(quality, Color.WHITE);
    }

    public boolean equals(ItemQuality other) {
        if (other != null) {
            return Utils.equals(quality.value, other.quality.value);
        }
        return false;
    }

    public static ItemQuality fromItemInfo(List<ItemInfo> infos) {
        double quality = 0;
        try {
            for (ItemInfo info : infos) {
                if ("QBuff".equals(info.getClass().getSimpleName())) {
                    quality = (Double)info.getClass().getDeclaredField("q").get(info);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return new ItemQuality(quality);
    }

    public class Element {
        public final double value;
        public final Color color;
        private Tex tex;

        public Element(double value, Color color) {
            this.value = value;
            this.color = color;
        }

        public Tex tex() {
            if (tex == null) {
                String text = format.format(value);
                BufferedImage img = Text.render(text, color).img;
                tex = new TexI(Utils.outline2(img, Color.BLACK));
            }
            return tex;
        }
    }
}
