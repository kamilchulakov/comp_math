import java.lang.Double.sum
import kotlin.math.pow

enum class Method {
        LEFT, RIGHT, MID
}

/**
 * WARNING: doesn't work for integral < 0
 */
private fun solveRectangle(programState: ProgramState, method: Method) {
    val h = programState.h
    val k = 2
    var sm = 0.0
    var N = programState.n.toInt()
    var prev = -1.0
    while (((prev - sm) / (2.0.pow(k) - 1) > programState.eps) or (prev < 0.0)) {
        programState.operations = N
        prev = sm
        var x = programState.a
        var y = programState.function(x)
        sm = 0.0
        for (i in 1 .. N) {
            x += h
            when (method) {
                Method.LEFT -> {
                    sm += y
                    y = programState.function(x)
                }
                Method.RIGHT -> {
                    y = programState.function(x)
                    sm += y
                }
                else -> {
                    error("Not supported method!")
                }
            }
        }
        N *= 2
        sm *= h
    }

    programState.result = sm
}

fun solveRectangleLeft(programState: ProgramState) {
    solveRectangle(programState, Method.LEFT)
}

fun solveRectangleRight(programState: ProgramState) {
    solveRectangle(programState, Method.RIGHT)
}

fun solveRectangleMid(programState: ProgramState) {
    val h = programState.h
    val k = 2
    var sm = 0.0
    var N = programState.n.toInt()
    var prev = -1.0
    while (((prev - sm) / (2.0.pow(k) - 1) > programState.eps) or (prev < 0.0)) {
        programState.operations = N
        prev = sm
        var x = programState.a
        sm = 0.0
        for (i in 1 .. N) {
            val oldX = x
            x += h
            val midX = (oldX + x) / 2.0
            sm += programState.function(midX)
            // println("$x ${programState.function(x)} $midX ${programState.function(midX)}")
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
        programState.operations = N
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