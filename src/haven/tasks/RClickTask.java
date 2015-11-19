package haven.tasks;

import haven.*;
/**
 * Created by Niko on 13.11.2015.
 */
public class RClickTask extends FsmTask {
    private final String actionName;
    private final Gob object;

    public RClickTask(Gob object, String actionName) {
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
                            stop("Nothing to harvest");
                        else
                            stop();
                    }
                });
            }
        }
    }
}