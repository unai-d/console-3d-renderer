# `console-3d-renderer`
`console-3d-renderer` is a simple and standalone program written in Java that draws a 3D scene on the console.

Everything in this program is written in a single file (`ThreeDee.java`) and thus, it must be compiled with `javac`.
That is, without compilation chains like Gradle or Maven.

## Instructions
### Build
**Note**: this program works with OpenJDK 11.
- Open a terminal window.
- Make sure you can invoke Java from the command line by entering `java --version` and `javac --version`.
- Go to the directory containing the files of this repository (use commands like `cd`).
- Type `javac ThreeDee.java` and press Enter.

### Usage
It is recommended to start this program from the shell prompt (e.g. from the Windows Console).
Also, make sure you're calling `ThreeDee` from where the generated `.class` file is located at.
You can reuse the terminal that you used in the above build steps.

Syntax:
```
java ThreeDee [OPTIONS]...
```

### Command line arguments
- `--texture=<url>`/`-T=<url>`: changes the cube model's texture to the one specified in the `url` value. You can also use local files.
  
  - E.g.: `-T=https://example.org/path/to/texture.jpg`.

- `--color-mode=<mode>`: indicates which color mode the program should use to render the scene to the console.
  
  - E.g.: `--color-mode=RGB4`.

  The most common values are `RGB4`, `RGB24` and `GrayscaleChars`.

  You can find all possible values inside the `ConsoleRenderMode` enumeration.

- `--show-lines`: renders the cube model's lines.

- `--show-vertices`: renders the cube model's vertices.

### Controls
- <kbd>W</kbd><kbd>A</kbd><kbd>S</kbd><kbd>D</kbd> for camera movement. Use <kbd>Shift</kbd> in combination with <kbd>W</kbd> and <kbd>S</kbd> to move faster.
- <kbd>X</kbd><kbd>Z</kbd> for camera movement along the Y axis (up and down respectively).
- <kbd>Q</kbd> to quit the program.

## Benchmarks
Go to [`BENCHMARKS.md`](BENCHMARKS.md) to see all the details about performance.

## History
I started writing a simple proof-of-concept Java program in 2022-10-21 that could draw lines on the terminal.
Then, it quickly became a simple 3D renderer when I started experimenting with matrix-to-vector multiplication (which is necessary for 3D projection and the likes).
Finally, around October 28, I added texture support and it pretty much became the program that is now in this repository.

PS: I started writing this program because I was getting bored at programming class 😅️.