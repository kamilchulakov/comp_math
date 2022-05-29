import LabConfiguration.funcList
import jetbrains.datalore.plot.config.asMutable
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.TreeMap
import kotlin.math.abs
import kotlin.math.min
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
        prevX += h.prettyRound(p)
        lst[prevX] = prevY
    }
    return lst
}

fun rungeKuttaWithRungeCheck(func: (Double, Double) -> Double, x0: Double, y0: Double,
                            b: Double, h: Double, eps: Double): List<Pair<Double, Double>> {
    var R = 2 * eps
    var currH = h
    var prevRes: TreeMap<Double, Double>? = null
    var p = 3
    while (R > eps) {
        // try to find better result
        val res = rungeKutta(func, x0, y0, b, currH, p)
        if (prevRes != null) R = abs(prevRes[prevRes.lastKey()]!! - res[res.lastKey()]!!) / (2.0.pow(p) - 1)
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
    return realRes
}

fun miln(func: (Double, Double) -> Double, x0: Double, y0: Double,
         b: Double, h: Double, eps: Double, p: Int): List<Pair<Double, Double>> {
    val lst = rungeKuttaWithRungeCheck(func, x0, y0, b, h, eps).take(4).asMutable()
    var prevX = (x0+3*h).prettyRound(p)
    var idx = 4
    val n = ((b-x0) / h).toInt()
    while (idx <= n) {
        val f3i = func(lst[idx-3].first, lst[idx-3].second)
        val f2i = func(lst[idx-2].first, lst[idx-2].second)
        val f1i = func(lst[idx-1].first, lst[idx-1].second)
        val y = lst[idx-4].second+4*h/3.0 * (2*f3i-f2i+2*f1i)
        prevX += h.prettyRound(p)
        lst.add(Pair(prevX.prettyRound(p), y))
        idx++
    }
    return lst
}

fun milnWithRungeCheck(func: (Double, Double) -> Double, x0: Double, y0: Double,
                            b: Double, h: Double, eps: Double): List<Pair<Double, Double>> {
    var R = 2 * eps
    var currH = h
    var prevRes: List<Pair<Double, Double>>? = null
    var p = 3
    // прогноз
    while (R > eps) {
        // try to find better result
        val prevR = R
        val res = miln(func, x0, y0, b, currH, eps, p)
        if (prevRes != null) R = abs(prevRes.last().second - res.last().second) / (2.0.pow(p) - 1)
        if (prevRes != null && R == prevR) break
        if (R > eps) prevRes = res
        if (res.size > 100) break
        currH /= 2.0
        p++
    }
    // (map)reduce
    val original = mutableListOf<Pair<Double, Double>>()
    val reduced = mutableListOf<Pair<Double, Double>>()
    var srch = x0.prettyRound(p)
    if (prevRes != null) {
        for (pair: Pair<Double, Double> in prevRes) {
            if (pair.first.prettyRound(p) - srch < eps) {
                reduced.add(pair)
                original.add(pair)
                srch += h.prettyRound(p)
            }
        }
    } else throw IllegalStateException("Метод Милна вернул null.")

    // коррекция
    val n = reduced.size
    for (i in 2 until n) {
        val f2i = func(reduced[i-2].first, reduced[i-2].second)
        val f1i = func(reduced[i-1].first, reduced[i-1].second)
        val f0pr = func(reduced[i].first, reduced[i].second)
        reduced[i] = Pair(reduced[i].first, (reduced[i-2].second+h/3.0 * (f2i+4*f1i+f0pr)).prettyRound(p))
    }
    return reduced
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
    val b = repeatTryAndCatch( {askDouble("Введите xn")}, numberFormatMsg = "Введите число!")
    val h = repeatTryAndCatch( {askDouble("Введите h > 0")}, isInvalid = {it == null || it <=0},
        numberFormatMsg = "Введите число!")
    val eps = repeatTryAndCatch( {askDouble("Введите e > 0")}, isInvalid = {it == null || it <=0},
        numberFormatMsg = "Введите число!")
    launch {
        println("\nМетод Милна")
        println("i\tx\ty")
        val res = milnWithRungeCheck(func.lmd, x, y, b, h, eps)
        res.forEachIndexed {
            idx, it -> println("$idx ${it.first.prettyRound(2)} ${it.second}")
        }
    }
    println("\nМетод Рунге-Кутта 4-го порядка")
    println("i\tx\ty")
    val res = rungeKuttaWithRungeCheck(func.lmd, x, y, b, h, eps)
    val n = ((b-x)/h).toInt()
    var currX = x - 0.0001
    for (i in 0..n) {
        println("$i\t${currX.prettyRound(2)}\t${res[i].second}")
        currX += h
        currX = currX.prettyRound()
    }
}