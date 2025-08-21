package state;

import pieces.EPieceEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a state transition table for chess pieces.
 * Transitions are loaded from a CSV file with format:
 * FROM_STATE,EVENT,TO_STATE
 */
public class TransitionTable {

    /** Map of current state → (event → next state) */
    private final Map<EState, Map<EPieceEvent, EState>> table = new HashMap<>();

    /**
     * Constructs a TransitionTable by reading a CSV resource.
     *
     * @param csvResourcePath path to CSV in resources
     */
    public TransitionTable(String csvResourcePath) {
        try (InputStream is = getClass().getResourceAsStream(csvResourcePath)) {
            if (is == null) {
                throw new RuntimeException("CSV resource not found: " + csvResourcePath);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                // Skip header
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

    /** Adds a transition from one state to another via an event */
    private void add(EState from, EPieceEvent event, EState to) {
        table.computeIfAbsent(from, k -> new HashMap<>()).put(event, to);
    }

    /**
     * Returns the next state given a current state and event.
     *
     * @param from  Current state
     * @param event Event that triggers the transition
     * @return Next state
     */
    public EState next(EState from, EPieceEvent event) {
        Map<EPieceEvent, EState> map = table.get(from);
        if (map == null || !map.containsKey(event)) {
            throw new IllegalStateException("Illegal transition: " + from + " + " + event);
        }
        return map.get(event);
    }
}
