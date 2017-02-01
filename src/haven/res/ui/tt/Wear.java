package haven.res.ui.tt;

import haven.ItemInfo.Tip;
import haven.Text;

import java.awt.image.BufferedImage;

public class Wear extends Tip {
    public final int d;
    public final int m;

    public Wear(Owner var1, int d, int m) {
        super(var1);
        this.d = d;
        this.m = m;
    }

    public BufferedImage longtip() {
        return Text.render(String.format("Wear: %,d/%,d", Integer.valueOf(this.d), Integer.valueOf(this.m))).img;
    }
}
