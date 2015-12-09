package haven.tasks;

import haven.*;

/**
 * Created by Niko on 16.11.2015.
 */
public class FluidTask extends FsmTask{
    Gob object;
    public FluidTask(Gob object) {
        this.object = object;
    }
    @Override
    protected State getInitialState() {
        return new WaitHandSwap();
    }

    private class WaitHandSwap extends State {
        @Override
        public void tick(double dt) {
            if (object != null) {
                waitMenu(2, new Callback<Boolean>() {
                    @Override
                    public void done(Boolean success) {
                        if (success) {
                            context().itemact(object, 0);
                            stop();
                        }
                    }
                });
            }
        }
    }

    protected final void waitMenu(double timeout, Callback<Boolean> callback) {
        setState(new WaitMenu(timeout, callback));
    }

    private class WaitMenu extends State {
        private final double timeout;
        private final Callback<Boolean> callback;
        private double t;


        public WaitMenu(double timeout, Callback<Boolean> callback) {
            this.timeout = timeout;
            this.callback = callback;
        }


        @Override
        public void tick(double dt) {
            if (context().getItemAtHand()!=null){

                callback.done(true);}
            else {
                t += dt;
                if (t > timeout) {
                    callback.done(false);
                }
            }
        }
    }
}
