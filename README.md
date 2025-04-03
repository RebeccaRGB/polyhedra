# Polyhedra

This is a set of programs to generate, modify, and view various polyhedra. These all use the OFF format as an interchange format which is used by other geometry programs.

### View basic polyhedra

Try:

    java -jar polyhedra.jar cube -c red | java -jar polyhedra.jar view -

Alternatively:

    java -jar polyhedra.jar view -- cube -c red

### Operate on polyhedra

Try:

    java -jar polyhedra.jar cube -c red | java -jar polyhedra.jar ambo -c yellow | java -jar polyhedra.jar view -

This can also be specified using the `construct` generator. Note the order of operations is reversed from the above.

    java -jar polyhedra.jar construct 'ambo -c yellow' 'cube -c red' | java -jar polyhedra.jar view -

Alternatively:

    java -jar polyhedra.jar view -- construct 'ambo -c yellow' 'cube -c red'

### Using Conway polyhedron notation

Try:

    java -jar polyhedra.jar construct aC | java -jar polyhedra.jar view -

Alternatively:

    java -jar polyhedra.jar view -- construct aC

You can pass arguments in curly braces:

    java -jar polyhedra.jar construct 'a{-c yellow}C{-c red}' | java -jar polyhedra.jar view -

Alternatively:

    java -jar polyhedra.jar view -- construct 'a{-c yellow}C{-c red}'

### Reading and writing OFF files

In addition to piping polyhedra from one command to another, you can use OFF files as an intermediary.

    java -jar polyhedra.jar cube -c red | java -jar polyhedra.jar ambo -c yellow > cuboctahedron.off
    java -jar polyhedra.jar view cuboctahedron.off

Alternatively:

    java -jar polyhedra.jar construct 'ambo -c yellow' 'cube -c red' > cuboctahedron.off
    java -jar polyhedra.jar view cuboctahedron.off

Alternatively:

    java -jar polyhedra.jar construct 'a{-c yellow}C{-c red}' > cuboctahedron.off
    java -jar polyhedra.jar view cuboctahedron.off

Alternatively:

    java -jar polyhedra.jar cube -c red > cube.off
    java -jar polyhedra.jar ambo -c yellow < cube.off > cuboctahedron.off
    java -jar polyhedra.jar view cuboctahedron.off

### Topology vs geometry

Note that most operations will result in a unique *topology* but not a unique *geometry*. Many operations have several options to specify how the geometry is created. Compare the following:

    java -jar polyhedra.jar view -- construct 'kis -h 1.3' icosahedron

    java -jar polyhedra.jar view -- construct 'kis -h 0.3' icosahedron

    java -jar polyhedra.jar view -- construct 'kis -s' icosahedron

    java -jar polyhedra.jar view -- construct 'kis -e' icosahedron

Furthermore, the result of any operation is not guaranteed to be convex, non-self-intersecting, or planar. Compare the nonplanar faces of:

    java -jar polyhedra.jar view -- construct dual 'antiprism -n 6'

With the planar faces of:

    java -jar polyhedra.jar view -- trapezohedron -n 6

