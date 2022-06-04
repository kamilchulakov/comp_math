import LabConfiguration.funcList
import LabConfiguration.solvedList
import UIManager.draw
import jetbrains.datalore.plot.config.asMutable
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.math.*

data class Func(val str: String, val lmd: (Double, Double)->Double)

object LabConfiguration {
    val funcList = arrayListOf<Func>(
        Func("x/y") { x, y -> x / y },
        Func("y+(1+x)y^2") {x, y -> y+(1+x)*y.pow(2)},
        Func("cos(x)") {x, y -> cos(x)}
    )
    val solvedList = arrayListOf<Func>(
        Func("x/y") { x, y -> -sqrt(x.pow(2)) },
        Func("y+(1+x)y^2") {x, _ -> - (exp(x) / (0.0+exp(x)*x))},
        Func("cos(x)") {x, y -> sin(x)-1-sin(1.0)}
    )
}

data class Answer(val res: MutableList<Pair<Double, Double>>, val h: Double)

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

fun rungeKutta(func: (Double, Double) -> Double, x0: Double, y0: Double,
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
        prevX += h
        lst[prevX] = prevY
    }
    return lst
}

fun rungeKuttaWithRungeCheck(func: (Double, Double) -> Double, x0: Double, y0: Double,
                            b: Double, h: Double, eps: Double): Answer {
    var R = 2 * eps
    var currH = h
    var prevRes: TreeMap<Double, Double>? = null
    var p = 3
    while (R > eps) {
        // try to find better result
        val res = rungeKutta(func, x0, y0, b, currH, p)
        if (prevRes != null) R = abs(prevRes[prevRes.lastKey()]!! - res[res.lastKey()]!!) / (2.0.pow(p) - 1)
        if (R.isNaN() || R.isInfinite()) R = Double.MAX_VALUE
        if (R > eps) prevRes = res
        currH /= 2.0
        p++
    }
    val res = prevRes ?: throw IllegalStateException("Метод Рунге-Кутта вернул null.")
    val n = ((b-x0)/h).toInt()
    var currX = x0
    val realRes = mutableListOf<Pair<Double, Double>>()
    for (i in 0..n) {
        if (res.ceilingKey(currX) != null) realRes.add(Pair(currX.prettyRound(),res[res.ceilingKey(currX)]!!))
        currX += h
        currX = currX.prettyRound()
    }
    return Answer(realRes, currH*2)
}

fun miln(func: (Double, Double) -> Double, x0: Double, y0: Double,
         b: Double, h: Double, eps: Double, p: Int): List<Pair<Double, Double>> {
    val lst = rungeKuttaWithRungeCheck(func, x0, y0, b, h, eps).res.take(4).asMutable()
    var prevX = (x0+3*h).prettyRound(p)
    var idx = 4
    val n = ((b-x0) / h).toInt()
    while (idx <= n) {
        val f3i = func(lst[idx-3].first, lst[idx-3].second)
        val f2i = func(lst[idx-2].first, lst[idx-2].second)
        val f1i = func(lst[idx-1].first, lst[idx-1].second)
        val y = lst[idx-4].second+4*h/3.0 * (2*f3i-f2i+2*f1i)
        prevX += h.prettyRound(p)
        lst.add(Pair(prevX.prettyRound(p), y.prettyRound(p)))
        idx++
    }
    return lst
}

fun generateArray(n: Int, h: Double, x0: Double): Array<Double?> {
    val array = arrayOfNulls<Double>(n)
    for (i in 0 until n) {
        array[i] = x0 + i * h
    }
    return array
}
fun ripMiln(realA: Double, y0: Double, realB: Double, suggestedH: Double, eps: Double, f: (Double, Double) -> Double): Answer {
    var h = suggestedH
    var n = ((realB - realA) / h).toInt()
    var x: Array<Double?>
    val goodX = generateArray(n + 1, h, realA)
    var y: MutableList<Double>
    var y1: Double
    var y2: Double
    var e: Double
    var p = 3
    while (true) {
        x = generateArray(n + 1, h, realA)
        y = rungeKuttaWithRungeCheck(f,realA,y0, realB, h, eps).res.map { it.second }.asMutable()
        var k = 4
        while (k <= n + 1) {
            // прогноз
            y1 = y[k - 4] + 4 * h / 3 * (2 * f(x[k - 3]!!, y[k - 3]) - f(x[k - 2], y[k - 2]) + 2 * f(x[k - 1]!!, y[k - 1]))
            // корректор
            y2 = y[k - 2] + h / 3 * (f(x[k - 2]!!, y[k - 2]) + 4 * f(x[k - 1]!!, y[k - 1]) + f(x[k]!!, y1))
            e = Math.abs(y2 - y1)
            if (e < eps) {
                y[k] = y1
            } else {
                y[k] = y2
                k--;
            }
            if (k == n) {
                val res = mutableListOf<Pair<Double, Double>>()
                for (j in 0 until n + 1) {
                    if (goodX.contains(x[j])) res.add(Pair(x[j]!!, y[j]!!.prettyRound(p)))
                }
                return Answer(res, h)
            }
            k++
        }
        p++
    }
}

/**
 * Метод Рунге-Кутта 4-го порядка
 * Метод Милна
 */
//@OptIn(DelicateCoroutinesApi::class)
fun main() = runBlocking {
    val func = repeatTryAndCatch( {askFunc() })
    val x = 1.0
    val y = -1.0
    println("Выбранная функция: ${func.str}. y(${x})=$y")
//    val x = repeatTryAndCatch( {askDouble("Введите x0")}, numberFormatMsg = "Введите число!")
//    val y = repeatTryAndCatch( {askDouble("Введите y(x0)")}, numberFormatMsg = "Введите число!")
    val b = repeatTryAndCatch( {askDouble("Введите b")}, numberFormatMsg = "Введите число!")
    val h = repeatTryAndCatch( {askDouble("Введите h > 0")}, isInvalid = {it == null || it <=0},
        numberFormatMsg = "Введите число!")
    val eps = repeatTryAndCatch( {askDouble("Введите e > 0")}, isInvalid = {it == null || it <=0},
        numberFormatMsg = "Введите число!")

    println("\nМетод Рунге-Кутта 4-го порядка")
    val ans = rungeKuttaWithRungeCheck(func.lmd, x, y, b, h, eps)
    println("h = ${ans.h}")
    println("Метод Милна")
    val answ2 = ripMiln(x, y, b, h, eps, func.lmd)
    println("h = ${answ2.h}")
    val res2 = answ2.res
    println("i\tx          y         ${"Рунге-Кутта"} Милна")
    val res = ans.res
    val n = ((b-x)/h).toInt()
    var currX = x - 0.0001
    val corr = solvedList.first { it.str == func.str }.lmd
    for (i in 0..n) {
        val x = "${currX.prettyRound(2)}".padEnd(11)
        val corrY = "${corr(currX.prettyRound(2), 0.0).prettyRound(6)}".padEnd(11)
        val y = "${res[i].second}".padEnd(11)
        val y2 = "${res2[i].second}".padEnd(11)
        println("$i\t$x$corrY$y$y2")
        currX += h
        currX = currX.prettyRound()
    }


    draw(res, res2, x, b, h, func)

}