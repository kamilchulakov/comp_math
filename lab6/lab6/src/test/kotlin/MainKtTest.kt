import LabConfiguration.funcList
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MainKtTest {

    @Test
    fun testRungeKuttWithRungeCheck() {
        val lmd = funcList[1].lmd
        val x = 1.0
        val y = -1.0
        val b = 1.5
        val h = 0.1
        val eps = 0.00001

        val res = rungeKuttWithRungeCheck(lmd, x, y, b, h, eps)
        val n = ((b-x)/h).toInt()
        var currX = x - 0.001
        for (i in 0..n) {
            println("$i ${currX.prettyRound(2)} ${res[res.higherKey(currX)]}")
            currX += h
            currX = currX.prettyRound()
        }
    }
}