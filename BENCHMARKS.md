# `console-3d-renderer` benchmarks
**Note**: every time the program needs to set the foreground color of the following characters, it does so by sending what it's known as [ANSI escape codes](https://en.wikipedia.org/wiki/ANSI_escape_code).
These escape codes are sent along with the characters that are printed on screen (they're also sent through the standard output).
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
      <td rowspan="2">Arch Linux (Linux 6.0.2)</td>
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
    <tr>
      <td rowspan="8">Intel Core i5-9500 (3GHz)</td>
      <td rowspan="3">Linux 5.4.0</td>
      <td rowspan="3">KDE Konsole</td>
      <td rowspan="3">OpenJDK 11.0.15</td>
      <td>282×76 <br> 21432 chars.</td>
      <td>19~31</td>
      <td>29~31</td>
    </tr>
    <tr>
      <td>160×50 <br> 8000 chars.</td>
      <td>58~83</td>
      <td>76~83</td>
    </tr>
    <tr>
      <td>80×25 <br> 2000 chars.</td>
      <td>200~333</td>
      <td>250~333</td>
    </tr>
    <tr>
      <td rowspan="3">WSL2 on top of Windows 10 21H2</td>
      <td rowspan="5"><code>conhost.exe</code></td>
      <td rowspan="3">OpenJDK 11.0.16</td>
      <td>284×69 <br> 19596 chars.</td>
      <td>1</td>
      <td>1</td>
    </tr>
    <tr>
      <td>177×52 <br> 9204 chars.</td>
      <td>2</td>
      <td>3</td>
    </tr>
    <tr>
      <td>80×25 <br> 2000 chars.</td>
      <td>10~16</td>
      <td>14~16</td>
    </tr>
    <tr>
      <td rowspan="2">Windows 10 21H2</td>
      <td rowspan="2">Java 17.0.3.1</td>
      <td>177×52 <br> 9204 chars.</td>
      <td>2~4</td>
      <td>3~4</td>
    </tr>
    <tr>
      <td>80×25 <br> 2000 chars.</td>
      <td>12~21</td>
      <td>16~21</td>
    </tr>
  </tbody>
</table>