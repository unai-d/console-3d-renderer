# `console-3d-renderer`
`console-3d-renderer` is a simple program written in Java that draws a 3D scene on the console.
Everything in this program is written in a single file (`ThreeDee.java`) and thus, it must be compiled with `javac`.
That is, without compilation chains like Gradle or Maven.

**Note**: this program doesn't work with Windows unless you run it in top of a POSIX environment (e.g.: WSL, MinGW...).

## History
There's nothing much to say: I started writing a simple proof-of-concept Java program in 2022-10-21 that could draw lines on the terminal.
Then, it quickly became a simple 3D renderer when I started experimenting with matrix-to-vector multiplication (which is necessary for 3D projection and the likes).
Finally, around October 28, I added texture support and it pretty much became the program that is being contained in this repository.
PS: I started writing this program because I was getting bored at programming class 😅️.

## Benchmarks
**Note**: every time the program needs to set the foreground color of the characters, it does so by sending what it's known as [ANSI escape codes](https://en.wikipedia.org/wiki/ANSI_escape_code). 
These escape codes are sent along with the characters that are printed on screen (`stdout`).
That means, the more color changes the program sends to the terminal, the more characters are sent and the less performance it gives.
The number of characters in the Resolution column only takes into account the number of **printable** characters, effectively excluding ANSI escape codes.
<table>
  <thead>
    <tr>
      <th colspan="5">System specs.</th>
      <th colspan="2">Frames per second</th>
    </tr>
    <tr>
      <th>CPU</th>
      <th>OS</th>
      <th>Terminal emulator</th>
      <th>JDK</th>
      <th>Resolution (columns × rows)</th>
      <th>RGB 24-bit</th>
      <th>Grayscale</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td rowspan="2">Intel Core i5-4200U (1.6GHz)</td>
      <td rowspan="2">Arch Linux</td>
      <td rowspan="2">GNOME Terminal 42 3.44.1</td>
      <td rowspan="2">OpenJDK 11.0.16.1</td>
      <td>168×38 <br> 6384 chars.</td>
      <td>30~60</td>
      <td>50~70</td>
    </tr>
    <tr>
      <td>80×25 <br> 2000 chars.</td>
      <td>140~200</td>
      <td>200~250</td>
    </tr>
  </tbody>
</table>
