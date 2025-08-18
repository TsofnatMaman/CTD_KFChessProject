package state;

import pieces.EPieceEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class TransitionTable {
    private final Map<EState, Map<EPieceEvent, EState>> table = new HashMap<>();

    public TransitionTable(String csvResourcePath) {
        try (InputStream is = getClass().getResourceAsStream(csvResourcePath)) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                // The first line is a header, skip it
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

    private void add(EState from, EPieceEvent event, EState to) {
        table.computeIfAbsent(from, k -> new HashMap<>()).put(event, to);
    }

    public EState next(EState from, EPieceEvent event) {
        Map<EPieceEvent, EState> map = table.get(from);
        if (map == null || !map.containsKey(event)) {
            throw new IllegalStateException("Illegal transition: " + from + " + " + event);
        }
        return map.get(event);
    }
}
