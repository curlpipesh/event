package pw.aria.event;

public class EventTest {
    public static void main(String[] args) {
        EventManager.register(
                new Listener<String>() {
                    @Override
                    public void event(final String event) {
                    }
                },
                new Listener<NOPE>() {
                    @Override
                    public void event(final NOPE event) {
                    }
                }
        );

        final long rstart = System.currentTimeMillis();
        for(int i = 0; i < 1_000_000; i++) {
            EventManager.register(new Listener<NOPE>() {
                @Override
                public void event(NOPE event) {
                }
            });
        }
        final long rend = System.currentTimeMillis();
        final long RTIME = rend - rstart;
        System.out.println("Registered 1_000_000 (new) listeners in " + RTIME + " milliseconds!" );
        System.out.println("Average register time: " + ((double)RTIME)/(1_000_000D));


        final int LIMIT = 1_000_000;
        System.out.println("Preparing to fire " + LIMIT * 2 + " pseudoevents");
        final long start = System.currentTimeMillis();
        final NOPE OBJ = new NOPE();
        for(int i = 0; i < LIMIT; i++) {
            EventManager.push("EVENT YAY");
            EventManager.push(OBJ);
        }
        final long end = System.currentTimeMillis();
        final long TIME = end - start;
        System.out.println(String.format("Done! (Took %s milliseconds)", TIME));
        System.out.println(String.format("Average event fire time: %s", ((double)TIME)/((double)LIMIT)));
    }

    public static final class NOPE {}
}
