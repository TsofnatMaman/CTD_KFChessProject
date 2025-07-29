# config.json Documentation

This file describes the structure and meaning of the `config.json` files found under each piece's state directory (e.g., `pieces/<PIECE>/states/<STATE>/config.json`).

## Structure

Each `config.json` contains two main sections:
- `physics`: Defines movement and state transition properties.
- `graphics`: Defines animation properties.

### Example

```json
{
  "physics": {
    "speed_m_per_sec": 20,
    "next_state_when_finished": "long_rest"
  },
  "graphics": {
    "frames_per_sec": 12,
    "is_loop": true
  }
}
```

---

## Fields

### physics

| Field                  | Type    | Description                                                                 |
|------------------------|---------|-----------------------------------------------------------------------------|
| speed_m_per_sec        | number  | The movement speed in meters per second. Use `0.0` for non-moving states.   |
| next_state_when_finished | string | The name of the next state after this one finishes (e.g., `"idle"`).        |

### graphics

| Field           | Type    | Description                                                                 |
|-----------------|---------|-----------------------------------------------------------------------------|
| frames_per_sec  | number  | Number of animation frames per second.                                      |
| is_loop         | boolean | If `true`, the animation loops; if `false`, it plays once and stops.        |

---

## Usage

- Each piece and state has its own `config.json` file.
- The game engine loads these files to determine how pieces move and animate in each state.
- State transitions are controlled by `next_state_when_finished`.

---

## Example States

- **idle**: No movement, looping idle animation.
- **move**: Fast movement, looping move animation, transitions to rest state.
- **jump**: Jump movement, non-looping animation, transitions to short rest.
- **long_rest / short_rest**: No movement, non-looping rest animation, transitions to idle.

---

## Notes

- All state names in `next_state_when_finished` must match the directory names under `states/`.
- Animation frames are loaded from `sprites/` subdirectory for each state.

