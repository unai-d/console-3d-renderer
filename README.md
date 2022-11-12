# `console-3d-renderer`
`console-3d-renderer` is a simple and standalone program written in Java that draws a 3D scene on the console.
Everything in this program is written in a single file (`ThreeDee.java`) and thus, it must be compiled with `javac`.
That is, without compilation chains like Gradle or Maven.

## Benchmarks
Go to [`BENCHMARKS.md`](BENCHMARKS.md) to see all the details about performance.

## History
There's nothing much to say: I started writing a simple proof-of-concept Java program in 2022-10-21 that could draw lines on the terminal.
Then, it quickly became a simple 3D renderer when I started experimenting with matrix-to-vector multiplication (which is necessary for 3D projection and the likes).
Finally, around October 28, I added texture support and it pretty much became the program that is now in this repository.

PS: I started writing this program because I was getting bored at programming class 😅️.