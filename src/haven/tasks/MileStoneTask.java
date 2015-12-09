package haven.tasks;

import com.sun.corba.se.spi.orbutil.fsm.FSM;
import haven.Callback;
import haven.Gob;

/**
 * Created by Niko on 15.11.2015.
 */
public class MileStoneTask extends FsmTask{
    private static final String[] MILESTONES = {"gfx/terobjs/road/milestone-stone-e", "gfx/terobjs/road/milestone-wood-e"};

    private final int actionNumber;

    public MileStoneTask(int actionNumber) {
        this.actionNumber = ((actionNumber+1)*2)-1;
    }

    @Override
    protected State getInitialState() {
        return new FindObject();
    }

    private class FindObject extends State {
        @Override
        public void tick(double dt) {
            Gob object = context().findObjectByNames(30, false, MILESTONES);
            if (object != null) {
                context().click(object, 3, 0);
                waitMenu(1, actionNumber, new Callback<Boolean>() {
                    @Override
                    public void done(Boolean success) {
                        if (!success)
                            stop("No such route");
                        else
                            stop();
                    }
                });
            } else {
                stop("No crossroad in range");
            }
        }
    }
}
