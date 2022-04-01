import java.lang.Double.sum

fun solveRectangleLeft(programState: ProgramState) {
    val h = (programState.b - programState.a) / programState.n
    var x = programState.a
    var y = programState.function(x)
    var sm = y
    for (i in 1..(programState.n).toInt()) {
        x += h
        y = programState.function(x)
        sm += y
    }

    programState.result = sm * h
}

fun solveSimpson(programState: ProgramState) {
    val h = (programState.b - programState.a) / programState.n
    var x = programState.a
    var sm = programState.function(x)
    val N = programState.n.toInt()
    val mul2 = ArrayList<Double>()
    val mul4 = ArrayList<Double>()
    x += h
    for (i in 1 until N) {
        if (i % 2 == 1) {
            mul4.add(programState.function(x))
        } else mul2.add(programState.function(x))
        x += h
    }
    sm += programState.function(x)
    sm += mul4.stream().reduce(::sum).get() * 4
    sm += mul2.stream().reduce(::sum).get() * 2
    programState.result = sm * h / 3.0
}