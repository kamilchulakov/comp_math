import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object LabConfiguration {
    const val N = 10
    const val interpolationMethods = 2
    const val stateNum = 4
    const val progressNum = stateNum + interpolationMethods - 1
    const val uiMult = 100.0 / (progressNum - 1)
    const val badWaiting = 50
    const val sleepTime = 100L
    const val delimiter = " "
    const val roundParam = 3
    val functions = listOf(
        "√x" to {x: Double -> sqrt(x)},
        "sin(x)" to {x: Double -> sin(x)},
        "x^2" to {x: Double -> x.pow(2)}
    )
}