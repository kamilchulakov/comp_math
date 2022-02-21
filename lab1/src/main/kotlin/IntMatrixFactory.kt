import java.io.BufferedReader
import java.io.FileReader
import java.util.*


class IntMatrixFactory {
    private fun produceMatrixFromScanner(sc: Scanner): IntMatrix {
        val rows = sc.nextInt()
        val columns = rows + 1
        sc.skip("\n")
        val myArray = Array(rows) { IntArray(columns) }
        var cnt = 0
        while (sc.hasNextLine() && cnt < myArray.size) {
            for (i in 0..myArray.size-1) {
                val line: List<String> = sc.nextLine().trim().split(" ")
                for (j in 0..line.size-1) {
                    myArray[i][j] = line[j].toInt()
                }
                cnt += 1
            }
        }
        println("Начальная матрица:")
        println(myArray.contentDeepToString())
        return IntMatrix(rows, columns, myArray);
    }

    fun produceIntMatrixFromFile(filename: String = "input.txt"): IntMatrix {
        val sc = Scanner(BufferedReader(FileReader(filename)))
        return produceMatrixFromScanner(sc)
    }
    fun produceIntMatrixFromCLI(): IntMatrix {
        val sc = Scanner(System.`in`)
        println("Введите размерность, а затем сами коэффициенты")
        return produceMatrixFromScanner(sc)
    }

    fun produceIntMatrixEmpty(rows: Int, columns: Int): IntMatrix {
        val myArray = Array(rows) { IntArray(columns) }
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                myArray[i][j] = 0
            }
        }
        return IntMatrix(rows, columns, myArray)
    }
}