import LabConfiguration.funcList
import LabConfiguration.solvedList
import org.junit.jupiter.api.Test

internal class MainKtTest {

    @Test
    fun testRungeKuttWithRungeCheck() {
        println("\nМетод Рунге-Кутта")
        val lmd = funcList[1].lmd
        val x = 1.0
        val y = -1.0
        val b = 20.0
        val h = 4.0
        val eps = 0.2
        val ans = rungeKuttaWithRungeCheck(lmd, x, y, b, h, eps)
        println("h = ${ans.h}")
        println("i\tx\t\ty")
        val res = ans.res
        val n = ((b-x)/h).toInt()
        var currX = x - 0.0001
        for (i in 0..n) {
            println("$i\t${currX.prettyRound(2)}\t\t${res[i].second}")
            currX += h
            currX = currX.prettyRound()
        }
    }

    @Test
    fun testMilnWithRungeCheck() {
        val lmd = funcList[1].lmd
        val x = 1.0
        val y = -1.0
        val b = 2.0
        val h = 0.1
        val eps = 0.001
        println("\nМетод Рунге-Кутта 4-го порядка")
        val ans = rungeKuttaWithRungeCheck(lmd, x, y, b, h, eps)
        println("h = ${ans.h}")
        println("Метод Милна")
        val answ2 = ripMiln(x, y, b, h, eps, lmd)
        println("h = ${answ2.h}")
        val res2 = answ2.res
        println("i\tx          y         Рунге-Кутта Милна")
        val res = ans.res
        val n = ((b-x)/h).toInt()
        var currX = x - 0.0001
        val corr = solvedList[1].lmd
        for (i in 0..n) {
            val x = "${currX.prettyRound(2)}".padEnd(11)
            val corrY = "${corr(currX.prettyRound(2), 0.0).prettyRound(6)}".padEnd(11)
            val y = "${res[i].second}".padEnd(11)
            val y2 = "${res2[i].second}".padEnd(11)
            println("$i\t$x$corrY$y$y2")
            currX += h
            currX = currX.prettyRound()
        }
    }
}