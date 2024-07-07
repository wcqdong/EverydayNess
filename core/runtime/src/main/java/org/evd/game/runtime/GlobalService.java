package org.evd.game.runtime;

/**
 *
 */
public class GlobalService extends Service{
    public GlobalService(Node node, String name, String scheduledName, long tickInterval) {
        super(node, name, scheduledName, tickInterval);
    }

    public GlobalService(Node node, String name, String scheduledName) {
        super(node, name, scheduledName);
    }
}
