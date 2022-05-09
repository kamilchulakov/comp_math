import Utils.round

object MathSolver {
    fun lagrangeInterpolation(st: ProgramState) {
        val resPoints = mutableListOf<Double>()
        val n = st.x.size
        var result = 1.0
        for (j in 0 until n) {
            val c0 = st.interpolationParam - st.x[j]
            result *= c0
        }
        for (i in 0 until n) {
            var curr = result
            for (j in 0 until n) {
                curr /= if (i == j) {
                    val c0 = st.interpolationParam - st.x[j]
                    c0
                } else {
                    val c1 = st.x[i] - st.x[j]
                    c1
                }
            }
            resPoints.add(curr)
        }
        for (i in 0 until n) {
            resPoints[i] *= st.y[i]
        }

        st.interpolationValues.add(InterpolationValue("Многочлен Лагранжа", round(resPoints.sum())))
    }

    fun gaussInterpolation(st: ProgramState) {

    }
}

data class ResultFunc(val type: String, val funcString: String, val func: (Double) -> Double)
data class InterpolationValue(val method: String, val result: Double)