import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.core.script.job.state.Tree;
import org.powerbot.core.script.methods.Npcs;
import org.powerbot.core.script.methods.Players;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.wrappers.interactive.NPC;

import java.awt.*;

@Manifest(name = "Burthrope Cow Killer", authors = "OrangeJuice", description = "Like cow tipping, but more fun.", version = 1.0)
public class CowKiller extends ActiveScript implements PaintListener {

    private long startTime;

    private Tree script = new Tree(new Node[] {
            new Cow()
    });

    public void onStart() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public int loop() {
        final Node stateNode = script.state();
        if(stateNode != null && Game.isLoggedIn()) {
            script.set(stateNode);
            final Node setNode = script.get();
            if(setNode != null) {
                getContainer().submit(setNode);
                setNode.join();
            }
        }
        return 0;
    }

    @Override
    public void onRepaint(Graphics graphics) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private int getPerHour(final long value) {
        return (int) ((value) * 3600000D / (System.currentTimeMillis() - startTime));
    }

    public class Cow extends Node {

        NPC cow = NPCs.getNearest("Cow");

        @Override
        public boolean activate() {
            return cow.isOnScreen();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void execute() {
            if(!Players.getLocal().isInCombat() && !Players.getLocal().isMoving()) {
                cow.interact("Attack");
            }
        }
    }
}