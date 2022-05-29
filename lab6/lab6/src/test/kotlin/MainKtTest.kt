import LabConfiguration.funcList
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MainKtTest {

    @Test
    fun testRungeKuttWithRungeCheck() {
        println("\nМетод Рунге-Кутта")
        val lmd = funcList[1].lmd
        val x = 1.0
        val y = -1.0
        val b = 1.5
        val h = 0.1
        val eps = 0.0000001

        val res = rungeKuttaWithRungeCheck(lmd, x, y, b, h, eps)
        val n = ((b-x)/h).toInt()
        var currX = x - 0.001
        for (i in 0..n) {
            println("$i ${currX.prettyRound(2)} ${res[i].second}")
            currX += h
            currX = currX.prettyRound()
        }
    }

    @Test
    fun testMilnWithRungeCheck() {
        println("\nМетод Милна")
        val lmd = funcList[1].lmd
        val x = 1.0
        val y = -1.0
        val b = 1.5
        val h = 0.1
        val eps = 0.00001
        val res = milnWithRungeCheck(lmd, x, y, b, h, eps)
        res.forEachIndexed {
                idx, it -> println("$idx ${it.first.prettyRound()} ${it.second}")
        }
    }
}