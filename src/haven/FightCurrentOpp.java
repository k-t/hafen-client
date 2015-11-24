package haven;

import javax.media.opengl.GL2;
import java.awt.*;
/**
 * Created by Niko on 22.11.2015.
 */


public class FightCurrentOpp extends Sprite {
    private static final States.ColState clrstate = new States.ColState(new Color(204, 0, 0, 200));

    public FightCurrentOpp() {
        super(null, null);
    }

    public boolean setup(RenderList rl) {
        rl.prepo(clrstate);
        return true;
    }

    public void draw(GOut g) {
        g.apply();
        BGL gl = g.gl;
        gl.glLineWidth(8f);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3f(-6, -6, 2);
        gl.glVertex3f(6, 6, 2);
        gl.glVertex3f(6, -6, 2);
        gl.glVertex3f(-6, 6, 2);
        gl.glEnd();
    }
}