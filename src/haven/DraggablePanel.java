package haven;

public class DraggablePanel extends Widget {

    private final String name;
    private final Coord minSize;
    private UI.Grab dm = null;
    private Coord doff;
    private boolean isHovered;

    public DraggablePanel(String name, Coord minSize) {
        this.name = name;
        this.minSize = minSize;
        this.c = BeltConfig.getBeltPosition(name, Coord.z);
        this.sz = minSize;
        setHovered(false);
    }

    @Override
    public void draw(GOut g) {
        if (isHovered) {
            g.chcolor(0, 0, 0, 64);
            g.frect(Coord.z, sz);
            g.chcolor();
        }
        super.draw(g);
    }

    @Override
    public <T extends Widget> T add(T child) {
        super.add(child);
        pack();
        if (parent != null)
            presize();
        return (child);
    }

    @Override
    public void cresize(Widget ch) {
        pack();
        if (parent != null)
            presize();
    }

    @Override
    public void resize(Coord sz) {
        super.resize(sz);
        this.sz.x = Math.max(this.sz.x, minSize.x);
        this.sz.y = Math.max(this.sz.y, minSize.y);
    }

    @Override
    protected void added() {
        presize();
    }

    @Override
    public void move(Coord c) {
        super.move(c);
        BeltConfig.setBeltPosition(name, this.c);
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (msg.equals("drag")) {
            drag((Coord)args[0]);
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }

    @Override
    public boolean mousedown(Coord c, int button) {
        if (super.mousedown(c, button))
            return true;
        if (button == 1) {
            wdgmsg("drag", c);
        }
        return true;
    }

    @Override
    public boolean mouseup(Coord c, int button) {
        if(dm != null) {
            dm.remove();
            dm = null;
            BeltConfig.setBeltPosition(name, this.c);
        } else {
            super.mouseup(c, button);
        }
        return true;
    }

    @Override
    public void mousemove(Coord c) {
        if (dm != null) {
            setHovered(true);
            this.c = this.c.add(c.add(doff.inv()));
        } else {
            setHovered(c.isect(Coord.z, sz));
            super.mousemove(c);
        }
    }

    private void setHovered(boolean value) {
        isHovered = value;
    }

    private void drag(Coord c) {
        dm = ui.grabmouse(this);
        doff = c;
        parent.setfocus(this);
        raise();
    }
}
