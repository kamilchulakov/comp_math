import java.lang.Error
import java.lang.StrictMath.abs

class IntMatrix(val xSize: Int, val ySize: Int, private var data: Array<IntArray>) {

    private val OPERATION_LIMIT by lazy {}

    operator fun get(x: Int, y: Int): Int {
        return data[x][y]
    }

    operator fun set(x: Int, y: Int, t: Int) {
        data[x][y] = t
    }

    override fun toString(): String {
        return data.contentDeepToString()
    }

    private fun isArrayDiagonallyDominant(xSize: Int, ySize: Int, data: Array<IntArray>): Boolean {
        var result = true
        var oneWasGood = false
        for (i in 0 until xSize) {
            for (j in 0 until ySize) {
                if (abs(data[i][i]) > data[i].sumOf { abs(it) } - abs(data[i][i]) - abs(data[i][ySize-1])) {
                    oneWasGood = true
                }
                if (abs(data[i][i]) < data[i].sumOf { abs(it) } - abs(data[i][i]) - abs(data[i][ySize-1])) {
                    result = false
                    break
                }
            }
        }
        return result && oneWasGood
    }

    fun isDiagonallyDominant(): Boolean {
        return isArrayDiagonallyDominant(xSize, ySize, data)
    }

    // maybe should use quick sort here
    fun makeDiagonallyDominant() {
        for (i in 0..OPERATION_LIMIT) {
            val list = data.toMutableList().apply { shuffle() }.toTypedArray()
            if (isArrayDiagonallyDominant(xSize, ySize, list)) {
                data = list
                return
            }
        }
        throw Error("Достижения диагонального преобладания невозможно!")
    }
}