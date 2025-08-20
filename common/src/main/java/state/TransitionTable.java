package state;

import pieces.EPieceEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the transition table for piece states.
 * Loads transitions from a CSV resource in the format:
 * FROM_STATE,EVENT,TO_STATE
 */
public class TransitionTable {

    /** Map of current state → (event → next state) */
    private final Map<EState, Map<EPieceEvent, EState>> table = new HashMap<>();

    /**
     * Constructs a TransitionTable from a CSV resource.
     *
     * @param csvResourcePath path to the CSV file in resources
     */
    public TransitionTable(String csvResourcePath) {
        try (InputStream is = getClass().getResourceAsStream(csvResourcePath)) {
            if (is == null) {
                throw new RuntimeException("CSV resource not found: " + csvResourcePath);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                // Skip header line
                String line = br.readLine();

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    EState from = EState.valueOf(parts[0].trim().toUpperCase());
                    EPieceEvent event = EPieceEvent.valueOf(parts[1].trim().toUpperCase());
                    EState to = EState.valueOf(parts[2].trim().toUpperCase());

                    add(from, event, to);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load transition table from " + csvResourcePath, e);
        }
    }

    /** Adds a transition to the table */
    private void add(EState from, EPieceEvent event, EState to) {
        table.computeIfAbsent(from, k -> new HashMap<>()).put(event, to);
    }

    /**
     * Gets the next state given a current state and event.
     *
     * @param from  Current state
     * @param event Triggering event
     * @return The next state
     * @throws IllegalStateException if the transition is invalid
     */
    public EState next(EState from, EPieceEvent event) {
        Map<EPieceEvent, EState> map = table.get(from);
        if (map == null || !map.containsKey(event)) {
            throw new IllegalStateException("Illegal transition: " + from + " + " + event);
        }
        return map.get(event);
    }
}
