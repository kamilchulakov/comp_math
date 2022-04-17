import kotlin.math.pow
import kotlin.properties.Delegates

object LabConfiguration {
    const val N = 10
    val FUNC: (Double) -> Double = { (4*it) / (it.pow(4)+4)}
    const val possibleFuncNum = 6
    const val stateNum = 6
    const val progressNum = stateNum + possibleFuncNum - 1
    const val uiMult = 100.0 / (progressNum - 1)
    const val badWaiting = 50
    const val sleepTime = 100L
    const val delimiter = " "
}