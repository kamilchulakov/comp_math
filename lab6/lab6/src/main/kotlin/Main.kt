import LabConfiguration.funcList
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.runBlocking
import java.util.TreeMap
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin

data class Func(val str: String, val lmd: (Double, Double)->Double)

object LabConfiguration {
    val funcList = arrayListOf<Func>(
        Func("x/y") { x, y -> x / y },
        Func("y+(1+x)y^2") {x, y -> y+(1+x)*y.pow(2)},
        Func("sin(x+y)") {x, y -> sin(x+y)}
    )
}

fun askFunc(): Func {
    println("Доступные функции:")
    funcList.forEachIndexed {
        idx,it -> println("${idx+1}) ${it.str}")
    }
    println("Выберете функцию y'")
    val n = readln().toInt()-1
    return funcList[n]
}

fun askDouble(str: String): Double {
    println(str)
    return readln().toDouble()
}

fun <T> repeatTryAndCatch(func: ()->T, isInvalid: (T?)->Boolean = {it->it == null},
                          outOfBoundsMsg: String = "Функции с таким номером нет!",
                          numberFormatMsg: String = "Введите целое число!"
): T {
    var res: T? = null
    while (isInvalid(res)) {
        try {
            res = func()
        } catch (ex: IndexOutOfBoundsException) {
            println(outOfBoundsMsg)
        } catch (ex: NumberFormatException) {
            println(numberFormatMsg)
        }
    }
    return res ?: throw IllegalStateException("Null число!")
}

fun rungeKutt(func: (Double, Double) -> Double, x0: Double, y0: Double,
              b: Double, h: Double, p: Int): TreeMap<Double, Double> {
    var prevX = x0
    var prevY = y0
    val lst = TreeMap<Double, Double>()
    lst[prevX] = prevY
    while (prevX < b) {
        val k1 = h * func(prevX, prevY)
        val x = (prevX+h/2.0).prettyRound(p)
        val k2 = h * func(x, prevY+k1/2.0)
        val k3 = h * func(x, prevY+k2/2.0)
        val k4 = h * func(prevX+h, prevY+k3)
        val y = (prevY + 1.0/6.0 * (k1 + 2*k2 + 2*k3 + k4)).prettyRound(p)

        prevY = y
        prevX += h.prettyRound(p)
        lst[prevX] = prevY
    }
    return lst
}

fun rungeKuttWithRungeCheck(func: (Double, Double) -> Double, x0: Double, y0: Double,
                            b: Double, h: Double, eps: Double): TreeMap<Double, Double> {
    var R = 2 * eps
    var currH = h
    var prevRes: TreeMap<Double, Double>? = null
    var p = 4
    while (R > eps) {
        // try to find better result
        val res = rungeKutt(func, x0, y0, b, currH, p)
        if (prevRes != null) R = abs(prevRes[prevRes.floorKey(b)]!! - res[res.floorKey(b)]!!) / (2.0.pow(p) - 1)
        if (R > eps) prevRes = res
        currH /= 2.0
        p++
    }
    return prevRes ?: throw IllegalStateException("Рунге-Кутт вернул null.")
}

/**
 * Метод Рунге-Кутта 4-го порядка
 * Метод Милна
 */
@OptIn(DelicateCoroutinesApi::class)
fun main() = runBlocking {
    val func = repeatTryAndCatch( {askFunc() })
    println("Выбранная функция: ${func.str}")
    val x = repeatTryAndCatch( {askDouble("Введите x0")}, numberFormatMsg = "Введите число!")
    val y = repeatTryAndCatch( {askDouble("Введите y(x0)")}, numberFormatMsg = "Введите число!")
    val b = repeatTryAndCatch( {askDouble("Введите правую границу интервала")}, numberFormatMsg = "Введите число!")
    val h = repeatTryAndCatch( {askDouble("Введите h > 0")}, isInvalid = {it == null || it <=0},
        numberFormatMsg = "Введите число!")
    val eps = repeatTryAndCatch( {askDouble("Введите e > 0")}, isInvalid = {it == null || it <=0},
        numberFormatMsg = "Введите число!")
    rungeKuttWithRungeCheck(func.lmd, x, y, b, h, eps).forEach {
        println("${it.key} ${it.value}")
    }
}