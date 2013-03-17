import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.core.script.job.state.Tree;
import org.powerbot.core.script.methods.Npcs;
import org.powerbot.core.script.methods.Players;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.NPC;

import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

@Manifest(name = "Burthrope Cow Killer", authors = "OrangeJuice", description = "Like cow tipping, but more fun.", version = 1.0)
public class CowKiller extends ActiveScript implements PaintListener {

    private long startTime;
    private int startXPAttack = Skills.getExperience(Skills.ATTACK);
    private int startXPStrength = Skills.getExperience(Skills.STRENGTH);
    private int startXPDefense = Skills.getExperience(Skills.DEFENSE);
    public static int cowsKilled;

    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
            return null;
        }
    }

    private final Color color1 = new Color(255, 255, 0);
    private final Color color2 = new Color(255, 255, 255);

    private final Font font1 = new Font("BatangChe", 1, 17);

    private final Image img1 = getImage("http://images4.wikia.nocookie.net/__cb20100218144231/runescape/images/a/a4/Prized_dairy_cow.png");

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
    public void onRepaint(Graphics g1) {
        int xpGained = 0;
        if(Skills.getExperience(Skills.ATTACK) > startXPAttack) {
            xpGained = Skills.getExperience(Skills.ATTACK)-startXPAttack;
        }
        if(Skills.getExperience(Skills.STRENGTH) > startXPStrength) {
            xpGained = Skills.getExperience(Skills.STRENGTH)-startXPStrength;
        }
        if(Skills.getExperience(Skills.DEFENSE) > startXPDefense) {
            xpGained = Skills.getExperience(Skills.DEFENSE)-startXPDefense;
        }

        long millis = System.currentTimeMillis() - startTime;
        long hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        long minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        long seconds = millis / 1000;

        Graphics2D g = (Graphics2D)g1;
        g.drawImage(img1, 360, 264, null);
        g.setFont(font1);
        g.setColor(color1);
        g.drawString("Time: "+hours+":"+minutes+":"+seconds, 552, 432);
        g.drawString("Cows/Hr: "+getPerHour(cowsKilled), 552, 470);
        g.drawString("XP/Hr: "+getPerHour(xpGained), 552, 506);
    }


    private int getPerHour(final long value) {
        return (int) ((value) * 3600000D / (System.currentTimeMillis() - startTime));
    }

    public class Cow extends Node {
        private NPC cow;
        @Override
        public boolean activate() {
            cow = NPCs.getNearest("Cow");
            return NPCs.getNearest("Cow") != null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void execute() {
            cow = NPCs.getNearest("Cow");
            cowsKilled = 0;
            if(!Players.getLocal().isInCombat() && !Players.getLocal().isMoving() && Players.getLocal().getAnimation() == -1 && Players.getLocal().getInteracting() == null) {
                Camera.turnTo(cow);
                cow.interact("Attack");
                if(cow.getAnimation() == 244) {
                    cowsKilled++;
                }
            }
        }
    }
}