import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

fun main(args: Array<String>) {
    val ROUND_ACCURACY = 4.0
    val ROUND_VALUE = 10.0.pow(ROUND_ACCURACY)

    // Чтение
    val intMatrixFactory = IntMatrixFactory()
    var matrix = intMatrixFactory.produceIntMatrixEmpty(0, 0)
    matrix = if (args.isNotEmpty()) {
        intMatrixFactory.produceIntMatrixFromFile(args[0])
    }
    else {
        intMatrixFactory.produceIntMatrixFromCLI()
    }

        println("Введите точность: ")
        val criteria = Scanner(System.`in`).nextDouble()
        val rows = matrix.xSize
        val columns = matrix.ySize

        // Условие преобладания диагональных элементов
        val diagonalizable = matrix.isDiagonallyDominant()

        // Перестановки
        if (!diagonalizable) {
            matrix.makeDiagonallyDominant()
            println("\nС выполненным условием преобладания диагональных элементов:")
            println(matrix)
        }

        // Выражаем иксы
        val transformed = Array(rows) { DoubleArray(columns) }
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                transformed[i][j] = -1 * matrix[i, j].toDouble()
                if (j == columns - 1) {
                    transformed[i][j] = transformed[i][j] * -1
                }
            }
            transformed[i][i] = 0.0
            val mn = matrix[i, i].toDouble()
            for (l in 0 until matrix.ySize) {
                transformed[i][l] = transformed[i][l] / mn
            }
        }
        println("\nВектор неизвестных:")
        println(transformed.contentDeepToString())

        // Проверка условия сходимости
        var norm = 0.0
        for (i in 0 until rows) {
            var absoluteSum = 0.0
            for (j in 0 until columns-1) {
                absoluteSum += abs(transformed[i][j])
            }
            if (norm < absoluteSum) norm = absoluteSum
        }
        println("\nНорма: $norm")
        if (norm >= 1.0) throw Error("Плохая норма! Условие сходимости не выполнено!")

        // Начальное приближение
        var x_vector = DoubleArray(rows)
        for (i in 0 until rows) {
            x_vector[i] = transformed[i][columns-1]
        }

        // Итерируем
        var curr_cr = 1.0
        var iterations = 0
        var pog_vector = DoubleArray(rows)
        while (curr_cr > criteria) {
            val new_x_vector = DoubleArray(rows)
            for (i in 0 until rows) {
                new_x_vector[i] = 0.0
                for (j in 0 until columns-1) {
                    new_x_vector[i] += transformed[i][j]*x_vector[j]
                }
                new_x_vector[i] += transformed[i][columns-1]
            }
            var max = 0.0
            for (i in 0 until rows) {
                val tmp = abs(x_vector[i]-new_x_vector[i])
                if (tmp > max) {
                    max = tmp;
                }
                pog_vector[i] = tmp
            }
            curr_cr = max
            x_vector = new_x_vector
            iterations += 1
        }
        println("\nКоличество итераций: $iterations")
        println("\nПриближенное решение задачи:")
        for (i in 0 until rows) {
            print("${(x_vector[i] * ROUND_VALUE).roundToInt() / ROUND_VALUE} ")
        }
        println("\n\nВектор погрешностей:")
        println(pog_vector.contentToString())
}