package haven;

public class MinimapWnd extends Window {

    static final Tex grip = Resource.loadtex("gfx/hud/gripbr");
    static final Coord gzsz = new Coord(19, 18);
    static final Coord minsz = new Coord(150, 125);

    private final MapView map;
    private final LocalMiniMap minimap;
    private IButton vclaimButton;
    private IButton pclaimButton;
    private IButton mapButton;
    private IButton realmButton;
    private IButton centerButton;
    private IButton radiusButton;
    private IButton radarButton;
    private IButton gridButton;
    private Coord doff;
    private boolean folded;
    private UI.Grab resizegrab = null;

    public MinimapWnd(Coord c, Coord sz, MapView map, LocalMiniMap minimap) {
        super(sz, "Minimap");
        this.map = map;
        this.minimap = minimap;
        this.c = c;
        add(minimap, 0, 0);
        initbuttons();
        setMargin(Coord.z);
    }

    public void draw(GOut g) {
        super.draw(g);
        if (!folded) {
            g.image(grip, sz.sub(gzsz));
        }
    }

    public boolean mousedown(Coord c, int button) {
        if(folded)
            return super.mousedown(c, button);
        parent.setfocus(this);
        raise();
        if (button == 1) {
            doff = c;
            if(c.isect(sz.sub(gzsz), gzsz)) {
                resizegrab = ui.grabmouse(this);
                return true;
            }
        }
        return super.mousedown(c, button);
    }

    public boolean mouseup(Coord c, int button) {
        if (resizegrab != null) {
            resizegrab.remove();
            resizegrab = null;
            Config.minimapSize.set(minimap.sz);
        } else {
            super.mouseup(c, button);
        }
        return (true);
    }

    public void mousemove(Coord c) {
        if (resizegrab != null) {
            Coord d = c.sub(doff);
            minimap.sz = minimap.sz.add(d);
            minimap.sz.x = Math.max(minsz.x, minimap.sz.x);
            minimap.sz.y = Math.max(minsz.y, minimap.sz.y);
            doff = c;
            pack();
        } else {
            super.mousemove(c);
        }
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if(sender == cbtn) {
            togglefold();
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }

    public boolean type(char key, java.awt.event.KeyEvent ev) {
        if(key == 27) {
            wdgmsg(cbtn, "click");
            return(true);
        }
        return(super.type(key, ev));
    }

    public void move(Coord c) {
        super.move(c);
        Config.minimapPosition.set(this.c);
    }

    public void togglefold() {
        folded = !folded;
        // TODO: toolbar widget?
        minimap.visible = !folded;
        vclaimButton.visible = !folded;
        pclaimButton.visible = !folded;
        realmButton.visible = !folded;
        centerButton.visible = !folded;
        radiusButton.visible = !folded;
        radarButton.visible = !folded;
        gridButton.visible = !folded;
        if (folded) {
            resize(new Coord(minimap.sz.x, 0));
        } else {
            resize(Config.minimapSize.get());
        }
    }

    private void initbuttons() {
        int x = 10;

        mapButton = add(new IButton("custom/gfx/hud/lbtn-map", "", "-d", "-h") {
            { tooltip = Text.render("Display big map");  }

            public void click() {
                wdgmsg("show-big-map");
            }
        }, x, 3);

        pclaimButton = add(new IButton("custom/gfx/hud/lbtn-vil", "", "-d", "-h") {
            { tooltip = Text.render("Display personal claims");  }

            public void click() {
                if ((map != null) && !map.visol(0))
                    map.enol(0, 1);
                else
                    map.disol(0, 1);
            }
        }, x+=31, 3);

        vclaimButton = add(new IButton("custom/gfx/hud/lbtn-claim", "", "-d", "-h") {
            { tooltip = Text.render("Display village claims"); }

            public void click() {
                if ((map != null) && !map.visol(2))
                    map.enol(2, 3);
                else
                    map.disol(2, 3);
            }
        }, x+=25, 3);

        realmButton = add(new IButton("custom/gfx/hud/lbtn-rlm", "", "-d", "-h") {
            {tooltip = Text.render("Display realms");}
            public void click() {
                if((map != null) && !map.visol(4))
                    map.enol(4, 5);
                else
                    map.disol(4, 5);
            }
        }, x+=25, 3);



        centerButton = add(new IButton("gfx/hud/buttons/center", "-u", "-d", "-d") {
            { tooltip = Text.render("Center map"); }

            public void click() {
                minimap.setOffset(Coord.z);
            }
        }, x+=25, 3);

        radiusButton = add(new IButton("gfx/hud/buttons/dispradius", "", "", "") {
            { tooltip = Text.render("Toggle view radius"); }

            public void click() {
                minimap.toggleRadius();
            }
        }, x+=25, 3);

        radarButton = add(new IButton("gfx/hud/buttons/radar", "", "", "") {
            { tooltip = Text.render("Select icons to display"); }

            public void click() {
                getparent(GameUI.class).iconwnd.toggle();
            }
        }, x+=25, 3);

        gridButton = add(new IButton("gfx/hud/buttons/grid", "", "", "") {
            { tooltip = Text.render("Toggle grid"); }

            public void click() {
                minimap.toggleGrid();
            }
        }, x+=25, 3);
    }
}