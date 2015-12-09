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

    private static final String[] OPENABLEGATES = {"gfx/terobjs/arch/brickwallgate", "gfx/terobjs/arch/palisadegate"};

    private static final String[] MILESTONES = {"gfx/terobjs/road/milestone-stone-e", "gfx/terobjs/road/milestone-wood-e"};

    private static final List<String> WATERCONTAINER = Arrays.asList("gfx/invobjs/waterflask",
            "gfx/invobjs/waterskin",
            "gfx/invobjs/bucket",
            "gfx/invobjs/bucket-water",
            "gfx/invobjs/kuksa",
            "gfx/invobjs/kuksa-full" );
    private static final String KRITTER = "gfx/kritter/";
    private static final String[] LIVESTOCK = {KRITTER+"cattle/cattle",KRITTER+"cattle/calf", KRITTER+"sheep/sheep",
            KRITTER+"sheep/lamb", KRITTER+"pig/piglet", KRITTER+"pig/sow", KRITTER+"pig/hog"};

    private static final String TREES = "gfx/terobjs/trees/appletree";

    /*
        Hotkey R priority list
     */
    public static void findHandTask(TaskManager tasks, UI ui) {
        if (checkQuickHandAction(tasks))
            return;

        if (checkRClick(tasks, 30, "Giddyup!", CLOVERABLEKRITTERS))
            return;
        tasks.getContext().error("Nothing to do");
    }
    /*
        Hotkey F priority list
     */
    public static void findBuilding(TaskManager tasks, UI ui) {

        if (checkGate(tasks))
            return;

        if (checkMileStone(tasks))
            return;

        // shoo livestock
        if (checkRClick(tasks, 30, "Shoo", LIVESTOCK))
            return;

        // pick leaves from mulberry
        if (checkRClick(tasks, 30, "Pick leaf", "gfx/terobjs/trees/mulberry"))
            return;

        // harvest wax from beehive

        if (checkRClick(tasks, 30, "Harvest wax", "gfx/terobjs/beehive"))
            return;

        // harvest dreamcatcher
        if (checkRClick(tasks, 25, "Harvest", "gfx/terobjs/dreca"))
            return;

        if (checkTreeChop(tasks))
            return;

    }

    /*
        Hotkey space priority list TODO hand tool actions, chop trees, dig on tile, maybe mine, butcher animal with cutting tool
        TODO checkbox to enable/disable tasks
     */

    /*
        Hotkey Q foraging only
     */
    public static boolean checkForageables(TaskManager tasks, UI ui) {
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
            Gob obj = tasks.getContext().findObjectByNames(11 * Config.autopickRadius.get(), false, names.toArray(new String[names.size()]));
            if (obj != null) {
                tasks.add(new RClickTask(obj, "Pick"));
                return true;
            }
        }
        Gob obj = tasks.getContext().findObjectByName(70, false, KRITTER+"dragonfly/dragonfly");
        if (obj!=null) {
            tasks.getContext().click(obj, 3,0);
            return true;
        }

        return false;
    }

    private static boolean checkGate(TaskManager tasks) {
        Gob obj = tasks.getContext().findObjectByNames(35, false, OPENABLEGATES);

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
            Gob obj = tasks.getContext().findObjectByNames(50, false, "gfx/terobjs/pow");
            if (obj!=null)
                tasks.getContext().itemact(obj, 0);
            return true;
        }

        // lit torch -> light building
        if (item.resname().equals("gfx/invobjs/torch-l"))
        {
            Gob obj = tasks.getContext().findObjectByNames(50, false, LIGHTABLEBUILDINGS);
            if (obj!=null)
                tasks.getContext().itemact(obj, 0);
            return true;
        }

        // waterflask or bucket -> rightclick barrel
        if (WATERCONTAINER.contains(item.resname()))
        {
            Gob obj = tasks.getContext().findObjectByName(50, false, "gfx/terobjs/barrel");
            if (obj!=null)
                tasks.getContext().itemact(obj, 0);
            return true;
        }

        // clover -> horse
        if (item.resname().equals("gfx/invobjs/herbs/clover")) {
            Gob obj = tasks.getContext().findObjectByName(50, false, "gfx/kritter/horse/horse");
            if (obj!=null)
                tasks.getContext().itemact(obj, 0);
            return true;
        }

        // non interactive item gg
        tasks.getContext().error(item.resname()+" has no interaction");
        return false;
    }

    /*
    TODO
     */
    private static boolean  checkHandToolAction(TaskManager tasks) {
        GItem left = tasks.getContext().getItemLeftHand()!=null?tasks.getContext().getItemLeftHand().item:null;
        GItem right = tasks.getContext().getItemRightHand()!=null?tasks.getContext().getItemRightHand().item:null;

        //  tanning fluid first
        if (left != null && left.resname().contains("bucket-tanfluid")
                || right!= null && right.resname().contains("bucket-tanfluid") ) {
            if (tasks.getContext().getItemAtHand()!=null) {
                tasks.getContext().error("Temporary slot needs to be empty");
                return false;
            }
            Gob obj = tasks.getContext().findObjectByNames(50, false, "gfx/terobjs/ttub");
            if (obj!= null) {
                if (left != null && left.resname().contains("bucket-tanfluid"))
                    Utils.takeItem(tasks.getContext().getItemLeftHand().item);
                else
                    Utils.takeItem(tasks.getContext().getItemRightHand().item);
                tasks.add(new FluidTask(obj));
                return true;}
        }

        // fill empty bucket with water from barrel
        if ((left != null && left.resname().equals("gfx/invobjs/bucket"))
                || (right!= null && right.resname().equals("gfx/invobjs/bucket"))) {
            if (tasks.getContext().getItemAtHand()!=null) {
                tasks.getContext().error("Temporary slot needs to be empty");
                return false;
            }
            Gob obj = tasks.getContext().findObjectByNames(50, false, "gfx/terobjs/barrel");
            if (obj!= null) {
                if (left != null && left.resname().equals("gfx/invobjs/bucket"))
                    Utils.takeItem(tasks.getContext().getItemLeftHand().item);
                else if (right != null && right.resname().equals("gfx/invobjs/bucket"))
                    Utils.takeItem(tasks.getContext().getItemRightHand().item);
                tasks.add(new FluidTask(obj));
                return true;}
        }

        return false;
    }

    private static boolean checkTreeChop(TaskManager tasks) {
        Gob obj = tasks.getContext().findObjectByNames(20, true, TREES);
        if (obj!=null) {
            tasks.add(new ChopTask(obj, "Chop"));
            return true;
        }
        return false;
    }

    private static boolean checkMileStone(TaskManager tasks) {
        Gob obj = tasks.getContext().findObjectByNames(30, false, MILESTONES);
        if (obj!=null) {
            tasks.getContext().click(obj, 3,0, obj.sc);
            return true;
        }
        return false;
    }

    private static boolean checkRClick(TaskManager tasks, int radius, String action, String... objects) {
        Gob obj = tasks.getContext().findObjectByNames(radius, false, objects);
        if (obj != null) {
            tasks.add(new RClickTask(obj, action));
            return true;
        }
        return false;
    }
}
