package haven;

import java.awt.*;

public class HungerMeter extends Widget {
    private static final Tex bg = Resource.loadtex("gfx/hud/meter/custom/hunger");

    private final CharWnd.GlutMeter glut;

    private static final Text.Foundry tipF = new Text.Foundry(Text.sans, 10);
    private Tex valueTex = null;

    public HungerMeter(CharWnd.GlutMeter glut) {
        super(IMeter.fsz);
        this.glut = glut;
    }

    @Override
    public void draw(GOut g) {
        if (glut.bg == null)
            return;
        Coord isz = IMeter.msz;
        Coord off = IMeter.off;
        g.chcolor(glut.bg);
        g.frect(off, isz);
        g.chcolor(glut.fg);
        g.frect(off, new Coord((int) Math.round(isz.x * (glut.glut - Math.floor(glut.glut))), isz.y));
        if (Config.showUserMeterValues.get()) {
            g.chcolor();
            valueTex = Text.renderstroked(String.format("%d%%/%d%%", Math.round((glut.lglut) * 100),
                    Math.round(glut.gmod * 100)), Color.WHITE, Color.BLACK, tipF).tex();
            g.image(valueTex, sz.div(2).sub(valueTex.sz().div(2)).add(10, -1));
        }
        g.chcolor();
        g.image(bg, Coord.z);
    }

    @Override
    public Object tooltip(Coord c, Widget prev) {
        return glut.tooltip(c, prev);
    }
}
