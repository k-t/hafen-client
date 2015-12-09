package haven.tasks;

import haven.Callback;
import haven.Gob;
import haven.IMeter;

/**
 * Created by Niko on 30.11.2015.
 */
public class ChopTask extends FsmTask {
    private final String actionName;
    private final Gob object;
    private final int threshHold=25;

    public ChopTask(Gob object, String actionName) {
        this.object = object;
        this.actionName = actionName;
    }

    @Override
    protected State getInitialState() {
        return new FindObject();
    }

    private class FindObject extends State {
        @Override
        public void tick(double dt) {
            if (object != null) {
                context().click(object, 3, 0);
                waitMenu(2, actionName, new Callback<Boolean>() {
                    @Override
                    public void done(Boolean success) {
                        if (!success)
                            stop("No action possible");
                        else
                            setState(new CheckStam());
                    }
                });
            }
        }
    }

    private class CheckStam extends State {
        private final int threshHold=20;
        private double t;
        private final double timer = 3;

        @Override
        public void tick(double dt) {
            t+=dt;
            IMeter.Meter stam = context().gui().getmeter("stam", 0);
            if (stam != null && stam.a < threshHold) {
                context().gui().lowStam=true;
                setState(new Wait());
                return;
            }
            if (t>timer && context().gui().prog<0) {
                // drink after chopping to prevent deadlock when chopping new tree immediately
                context().gui().lowStam=true;
                stop("Done");
            }
        }
    }

    private class Wait extends State {
        private final double timeout = 6;
        private double t;
        private final int minThreshhold = 50;
        @Override
        public void tick(double dt) {
            t+=dt;
            if (t>timeout) {
                IMeter.Meter stam = context().gui().getmeter("stam", 0);
                if (stam != null && stam.a < minThreshhold) {
                    stop("Not enough to drink");
                }
                setState(new FindObject());
            }

        }
    }
}