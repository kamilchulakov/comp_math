import java.util.*
import kotlin.math.abs

class GaussSeidelSolver(private var M: Array<DoubleArray>) {
    companion object {
        const val MAX_ITERATIONS = 100
    }

    // attempting to change a matrix to dominant
    // if proved that it is not
    private fun transformToDominant(
        r: Int, V: BooleanArray,
        R: IntArray
    ): Boolean {
        val n = M.size
        if (r == M.size) {
            val T = Array(n) { DoubleArray(n + 1) }
            for (i in R.indices) {
                for (j in 0 until n + 1) T[i][j] = M[R[i]][j]
            }
            M = T
            return true
        }
        for (i in 0 until n) {
            if (V[i]) continue
            var sum = 0.0
            for (j in 0 until n) sum += Math.abs(M[i][j])
            if (2 * abs(M[i][r]) > sum) {
                // diagonally dominant?
                V[i] = true
                R[r] = i
                if (transformToDominant(r + 1, V, R)) return true
                V[i] = false
            }
        }
        return false
    }

    // method to check whether matrix is
    // diagonally dominant or not
    fun makeDominant(): Boolean {
        val visited = BooleanArray(M.size)
        val rows = IntArray(M.size)
        Arrays.fill(visited, false)
        return transformToDominant(0, visited, rows)
    }

    // method to find the solution of the matrix
    // after all conditions are satisfied
    fun solve(): DoubleArray {
        var iterations = 0
        val n = M.size
        val epsilon = 1e-15
        val X = DoubleArray(n) // Approximations
        var P = DoubleArray(n) // Prev
        Arrays.fill(X, 0.0)
        while (true) {
            for (i in 0 until n) {
                var sum = M[i][n] // b_n
                for (j in 0 until n) if (j != i) sum -= M[i][j] * X[j]
                X[i] = 1 / M[i][i] * sum
            }
            iterations++
            if (iterations == 1) continue
            var stop = true
            var i = 0
            while (i < n && stop) {
                if (abs(X[i] - P[i]) > epsilon) stop = false
                i++
            }
            P = X.clone()
            if (stop || iterations == MAX_ITERATIONS) break
        }
        return P
    }
}