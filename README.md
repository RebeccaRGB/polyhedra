# Polyhedra

This is a set of programs to generate, modify, and view various polyhedra. These all use the OFF format as an interchange format which is used by other geometry programs.

### View basic polyhedra

Try:

    java -jar polyhedra.jar cube -c red | java -jar polyhedra.jar view -

### Operate on polyhedra

Try:

    java -jar polyhedra.jar cube -c red | java -jar polyhedra.jar ambo -c yellow | java -jar polyhedra.jar view -

This can also be specified using the `construct` generator. Note the order of operations is reversed from the above.

    java -jar polyhedra.jar construct 'ambo -c yellow' 'cube -c red' | java -jar polyhedra.jar view -

### Reading and writing OFF files

In addition to piping polyhedra from one command to another, you can use OFF files as an intermediary.

    java -jar polyhedra.jar cube -c red | java -jar polyhedra.jar ambo -c yellow > cuboctahedron.off
    java -jar polyhedra.jar view cuboctahedron.off

Alternatively:

    java -jar polyhedra.jar construct 'ambo -c yellow' 'cube -c red' > cuboctahedron.off
    java -jar polyhedra.jar view cuboctahedron.off

Alternatively:

    java -jar polyhedra.jar cube -c red > cube.off
    java -jar polyhedra.jar ambo -c yellow < cube.off > cuboctahedron.off
    java -jar polyhedra.jar view cuboctahedron.off

