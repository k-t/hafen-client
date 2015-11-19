package haven.tasks;

import haven.*;
import haven.minimap.CustomIconGroup;
import haven.minimap.CustomIconMatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Created by Niko on 13.11.2015.
 */
public class ContextTaskFinder {

    private static final String[] LIGHTABLEBUILDINGS = {"gfx/terobjs/smelter",
            "gfx/terobjs/pow", "gfx/terobjs/crucible", "gfx/terobjs/fineryforge", "gfx/terobjs/kiln", "gfx/terobjs/oven",
            "gfx/terobjs/steelcrucible", "gfx/terobjs/tarkiln", "gfx/terobjs/pow", "gfx/terobjs/cauldron"};

    private static final String[] CLOVERABLEKRITTERS = {"gfx/kritter/horse/horse",
            "gfx/kritter/cattle/cattle", "gfx/kritter/sheep/sheep", "gfx/kritter/boar/boar"};

    private static final String[] OPENABLEGATES = {"gfx/terobjs/arch/brickwallgate", "gfx/terobjs/arch/palisadegate",
            "gfx/terobjs/arch/polesgate"};

    private static final List<String> WATERCONTAINER = Arrays.asList("gfx/invobjs/waterflask",
            "gfx/invobjs/waterskin",
            "gfx/invobjs/bucket",
            "gfx/invobjs/bucket-water",
            "gfx/invobjs/kuksa",
            "gfx/invobjs/kuksa-full" );

    public static void findTask(TaskManager tasks, UI ui) {

        if (checkForageables(tasks, ui))
            return;

        if (checkGate(tasks))
            return;

        if (checkQuickHandAction(tasks))
            return;

        if (checkHandToolAction(tasks))
            return;

        if (tryGiddyUp(tasks)) {
            return;
        }
        if (findDreamCatcher(tasks))
            return;

        tasks.getContext().error("Nothing to do");
    }

    private static boolean checkForageables(TaskManager tasks, UI ui) {
        List<String> names = new ArrayList<String>();
        for (CustomIconGroup group : ui.sess.glob.icons.config.groups) {
            if ("Forageables".equals(group.name)) {
                for (CustomIconMatch match : group.matches)
                    if (match.show)
                        names.add(match.value);
                break;
            }
        }
        if (names.size() > 0) {
            Gob obj = tasks.getContext().findObjectByNames(11 * Config.autopickRadius.get(), names.toArray(new String[names.size()]));
            if (obj != null) {
                tasks.add(new RClickTask(obj, "Pick"));
                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean checkGate(TaskManager tasks) {
        Gob obj = tasks.getContext().findObjectByNames(50, OPENABLEGATES);

        if (obj!=null) {
            tasks.getContext().click(obj, 3,0);
            return true;
        }
        return false;
    }
    /*
        Checks only actions possible with items in the quick slot("E")/cursor
     */
    private static boolean checkQuickHandAction(TaskManager tasks) {
        GItem item = tasks.getContext().getItemAtHand();
        if (item==null)
            return false;

        // unlit torch -> light torch at campfire
        if (item.resname().equals("gfx/invobjs/torch"))
        {
            Gob obj = tasks.getContext().findObjectByNames(50, "gfx/terobjs/pow");
            if (obj!=null)
                tasks.getContext().itemact(obj, 0);
            return true;
        }

        // lit torch -> light building
        if (item.resname().equals("gfx/invobjs/torch-l"))
        {
            Gob obj = tasks.getContext().findObjectByNames(50, LIGHTABLEBUILDINGS);
            if (obj!=null)
                tasks.getContext().itemact(obj, 0);
            return true;
        }

        // waterflask or bucket -> rightclick barrel
        if (WATERCONTAINER.contains(item.resname()))
        {
            Gob obj = tasks.getContext().findObjectByName(50, "gfx/terobjs/barrel");
            if (obj!=null)
                tasks.getContext().itemact(obj, 0);
            return true;
        }

        // clover -> horse
        if (item.resname().equals("gfx/invobjs/herbs/clover")) {
            Gob obj = tasks.getContext().findObjectByName(50, "gfx/kritter/horse/horse");
            if (obj!=null)
                tasks.getContext().itemact(obj, 0);
            return true;
        }

        // non interactive item gg
        // remove error later
        tasks.getContext().error(item.resname()+" has no interaction");
        return false;
    }

    /*
    WIP
     */
    private static boolean  checkHandToolAction(TaskManager tasks) {
                return false;
    }

    private static boolean  tryGiddyUp(TaskManager tasks) {
        Gob obj = tasks.getContext().findObjectByName(50, "gfx/kritter/horse/horse");
        if (obj != null) {
            tasks.add(new RClickTask(obj, "Giddyup!"));
            return true;
        }
        return false;
    }

    private static boolean  findDreamCatcher(TaskManager tasks) {
        Gob obj = tasks.getContext().findObjectByName(50, "gfx/terobjs/dreca");
        if (obj != null) {
            tasks.add(new RClickTask(obj, "Harvest"));
            return true;
        }
        return false;
    }

}
