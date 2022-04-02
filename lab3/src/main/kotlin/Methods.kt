import java.lang.Double.sum
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.pow

/**
 * WARNING: doesn't work for integral < 0
 */
fun solveRectangleLeft(programState: ProgramState) {
    val h = (programState.b - programState.a) / programState.n
    val k = 2
    var x = programState.a
    var y = programState.function(x)
    var sm = y
    var N = programState.n.toInt()
    var prev = -1.0
    while (((prev - sm) / (2.0.pow(k) - 1) > programState.eps) or (prev < 0.0)) {
        prev = sm
        x = programState.a
        y = programState.function(x)
        sm = y
        for (i in 1 until N) {
            x += h
            y = programState.function(x)
            sm += y
        }
        N *= 2
        sm *= h
    }

    programState.result = sm
}

/**
 * WARNING: doesn't work for integral < 0
 */
fun solveSimpson(programState: ProgramState) {
    val h = (programState.b - programState.a) / programState.n
    val k = 4.0
    var x = programState.a
    var sm = programState.function(x)
    var N = programState.n.toInt()
    var prev = -1.0
    while (((prev - sm) / (2.0.pow(k) - 1) > programState.eps) or (prev < 0.0)) {
        prev = sm
        val mul2 = ArrayList<Double>()
        val mul4 = ArrayList<Double>()
        x = programState.a
        sm = programState.function(x)
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
        sm *= h
        sm /= 3.0
        N *= 2
    }
    programState.result = sm
}