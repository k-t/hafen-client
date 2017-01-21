package haven;

import java.awt.*;
import java.util.List;

public class FepMeter extends Widget {
    private static final Tex bg = Resource.loadtex("gfx/hud/meter/custom/fep");

    private final CharWnd.FoodMeter food;

    private static final Text.Foundry tipF = new Text.Foundry(Text.sans, 10);
    private Tex valueTex = null;
    private double lastFepSum = -1;
    private double lastFepMax = -1;

    public FepMeter(CharWnd.FoodMeter food) {
        super(IMeter.fsz);
        this.food = food;
    }

    private void calcValueText() {
        List<CharWnd.FoodMeter.El> els = food.els;
        double sum = 0.0;
        for (CharWnd.FoodMeter.El el : els) {
            sum += el.a;
        }
        if (food.cap == lastFepMax && lastFepSum == sum)
            return;
        lastFepSum = sum;
        lastFepMax = food.cap;
        valueTex = Text.renderstroked(String.format("%s/%s", Utils.odformat2(sum, 2), Utils.odformat(food.cap, 2)), Color.WHITE, Color.BLACK, tipF).tex();
    }

    @Override
    public void draw(GOut g) {
        Coord isz = IMeter.msz;
        Coord off = IMeter.off;
        g.chcolor(0, 0, 0, 255);
        g.frect(off, isz);
        g.chcolor();
        double x = 0;
        int w = isz.x;
        for(CharWnd.FoodMeter.El el : food.els) {
            int l = (int)Math.floor((x / food.cap) * w);
            int r = (int)Math.floor(((x += el.a) / food.cap) * w);
            try {
                Color col = el.ev().col;
                g.chcolor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 255));
                g.frect(off.add(l, 0), new Coord(r - l, isz.y));
            } catch(Loading e) {
            }
        }
        if (Config.showUserMeterValues.get()) {
            calcValueText();
            if (valueTex != null) {
                g.chcolor();
                g.image(valueTex, sz.div(2).sub(valueTex.sz().div(2)).add(10, -1));
            }
        }
        g.chcolor();
        g.image(bg, Coord.z);
    }

    @Override
    public Object tooltip(Coord c, Widget prev) {
        return food.tooltip(c, prev);
    }
}
