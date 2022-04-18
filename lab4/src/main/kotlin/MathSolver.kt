import Utils.round
import java.lang.Math.log
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

object MathSolver {
    private fun calcSigma(func: (Double) -> Double, st: ProgramState): Double {
        val sigmas = ArrayList<Double>()
        for (i in 0 until st.n) {
            sigmas.add((st.y[i] - func(st.x[i])).pow(2))
        }
        return round(sqrt(sigmas.sum() / st.n))
    }

    private fun solveSystemAndCatch(arr: Array<DoubleArray>): DoubleArray {
        val solver = GaussSeidelSolver(arr)
        if (solver.makeDominant()) {
            throw IllegalStateException("Bad matrix for Gauss-Seidel solver!")
        }
        return solver.solve()
    }

    fun linearApproximate(st: ProgramState) {
        var sx = 0.0
        var sxx = 0.0
        var sy = 0.0
        var sxy = 0.0
        for (i in 0 until st.n) {
            sx += st.x[i]
            sxx += st.x[i].pow(2)
            sy += st.y[i]
            sxy += st.x[i] * st.y[i]
        }
        val midX = sx / st.n
        val midY = sy / st.n
        var s1 = 0.0
        var s2 = 0.0
        var s3 = 0.0
        for (i in 0 until st.n) {
            s1 += (st.x[i] - midX) * (st.y[i] - midY)
            s2 += (st.x[i] - midX).pow(2)
            s3 += (st.y[i] - midY).pow(2)
        }
        val r = s1 / sqrt(s2 * s3) // коэф Пирсона
        val result = solveSystemAndCatch(
            arrayOf(
                arrayOf(sxx, sx, sxy).toDoubleArray(),
                arrayOf(sx, st.n.toDouble(), sy).toDoubleArray()
            )
        )
        val func: (Double) -> Double = { result[0] * it + result[1] }
        val str = "${round(result[0])}*x + ${round(result[1])} Pirson number: ${round(r)}"
        val sigma = calcSigma(func, st)
        st.possibleFunc.add(PossibleFunc("Linear", str, func, sigma))
    }

    fun sqrtApproximate(st: ProgramState) {
        var sx = 0.0
        var sxx = 0.0
        var sxxx = 0.0
        var sxxxx = 0.0
        var sy = 0.0
        var sxy = 0.0
        var sxxy = 0.0
        for (i in 0 until st.n) {
            sx += st.x[i]
            sxx += st.x[i].pow(2)
            sxxx += st.x[i].pow(3)
            sxxxx += st.x[i].pow(4)
            sy += st.y[i]
            sxy += st.y[i] * st.x[i]
            sxxy += (st.x[i].pow(2)) * st.y[i]
        }
        val result = solveSystemAndCatch(
            arrayOf(
                arrayOf(st.n.toDouble(), sx, sxx, sy).toDoubleArray(),
                arrayOf(sx, sxx, sxxx, sxy).toDoubleArray(),
                arrayOf(sxx, sxxx, sxxxx, sxxy).toDoubleArray()
            )
        )
        val func: (Double) -> Double = { result[2] * it.pow(2) + result[1] * it + result[0] }
        val str = "${round(result[2])}*x^2 + ${round(result[1])}*x+${round(result[0])}"
        val sigma = calcSigma(func, st)
        st.possibleFunc.add(PossibleFunc("Square", str, func, sigma))
    }

    fun cubeApproximate(st: ProgramState) {
        var sx = 0.0
        var sxx = 0.0
        var sxxx = 0.0
        var sxxxx = 0.0
        var sxxxxx = 0.0
        var sxxxxxx = 0.0
        var sy = 0.0
        var sxy = 0.0
        var sxxy = 0.0
        var sxxxy = 0.0
        for (i in 0 until st.n) {
            sx += st.x[i]
            sxx += st.x[i].pow(2)
            sxxx += st.x[i].pow(3)
            sxxxx += st.x[i].pow(4)
            sxxxxx += st.x[i].pow(5)
            sxxxxxx += st.x[i].pow(6)
            sy += st.y[i]
            sxy += st.y[i] * st.x[i]
            sxxy += st.x[i].pow(2) * st.y[i]
            sxxxy += st.x[i].pow(3) * st.y[i]

        }
        val result = solveSystemAndCatch(
            arrayOf(
                arrayOf(st.n.toDouble(), sx, sxx, sxxx, sy).toDoubleArray(),
                arrayOf(sx, sxx, sxxx, sxxxx, sxy).toDoubleArray(),
                arrayOf(sxx, sxxx, sxxxx, sxxxxx, sxxy).toDoubleArray(),
                arrayOf(sxxx, sxxxx, sxxxxx, sxxxxxx, sxxxy).toDoubleArray()
            ))
        val func: (Double) -> Double = { result[3] * it.pow(3) + result[2] * it.pow(2) + result[1]*it + result[0] }
        val str = "${round(result[3])}*x^3 + ${round(result[2])}*x^2 ${round(result[1])}*x+${round(result[0])}"
        val sigma = calcSigma(func, st)
        st.possibleFunc.add(PossibleFunc("Cube", str, func, sigma))
    }

    fun powApproximate(st: ProgramState) {
        val points = ArrayList<Int>()
        for (i in 0 until st.n) {
            if (st.x[i] > 0 && st.y[i] > 0) points.add(i)
        }
        if (points.size < 2) {
            throw IllegalStateException("Must have at least 2 points.")
        }
        var sx = 0.0
        var sxx = 0.0
        var sy = 0.0
        var sxy = 0.0
        for (i in points) {
            sx += ln(st.x[i])
            sxx += ln(st.x[i]).pow(2)
            sy += ln(st.y[i])
            sxy += ln(st.x[i]) * ln(st.y[i])
        }
        val result = solveSystemAndCatch(
            arrayOf(
                arrayOf(sxx, sx, sxy).toDoubleArray(),
                arrayOf(sx, st.n.toDouble(), sy).toDoubleArray()
            )
        )
        val func: (Double) -> Double = { exp(result[1]) * it.pow(result[0]) }
        val str = "e^${round(result[1])}*x^${round(result[0])}"
        val sigma = calcSigma(func, st)
        st.possibleFunc.add(PossibleFunc("Pow", str, func, sigma))
    }
    fun expApproximate(st: ProgramState) {
        val points = ArrayList<Int>()
        for (i in 0 until st.n) {
            if (st.y[i] > 0) points.add(i)
        }
        if (points.size < 2) {
            throw IllegalStateException("Must have at least 2 points.")
        }
        var sx = 0.0
        var sxx = 0.0
        var sy = 0.0
        var sxy = 0.0
        for (i in points) {
            sx += st.x[i]
            sxx += st.x[i].pow(2)
            sy += ln(st.y[i])
            sxy += st.x[i] * ln(st.y[i])
        }
        val result = solveSystemAndCatch(
            arrayOf(
                arrayOf(sxx, sx, sxy).toDoubleArray(),
                arrayOf(sx, st.n.toDouble(), sy).toDoubleArray()
            )
        )
        val func: (Double) -> Double = { exp(result[1]) * exp(result[0]*it) }
        val str = "e^${round(result[1])}*e^(${round(result[0])}*x)"
        val sigma = calcSigma(func, st)
        st.possibleFunc.add(PossibleFunc("Exp", str, func, sigma))
    }
    fun lnApproximate(st: ProgramState) {
        val points = ArrayList<Int>()
        for (i in 0 until st.n) {
            if (st.x[i] > 0) points.add(i)
        }
        if (points.size < 2) {
            throw IllegalStateException("Must have at least 2 points.")
        }
        var sx = 0.0
        var sxx = 0.0
        var sy = 0.0
        var sxy = 0.0
        for (i in points) {
            sx += ln(st.x[i])
            sxx += ln(st.x[i]).pow(2)
            sy += st.y[i]
            sxy += ln(st.x[i] * st.y[i])
        }
        val result = solveSystemAndCatch(
            arrayOf(
                arrayOf(sxx, sx, sxy).toDoubleArray(),
                arrayOf(sx, st.n.toDouble(), sy).toDoubleArray()
            )
        )
        val func: (Double) -> Double = { result[0]*ln(it) + result[1] }
        val str = "${round(result[0])}*ln(x)+${round(result[1])}"
        val sigma = calcSigma(func, st)
        st.possibleFunc.add(PossibleFunc("Log", str, func, sigma))
    }
}

data class PossibleFunc(val type: String, val funcString: String, val func: (Double) -> Double, val midEq: Double)