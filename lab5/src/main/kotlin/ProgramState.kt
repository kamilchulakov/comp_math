import LabConfiguration.N
import LabConfiguration.interpolationMethods
import LabConfiguration.stateNum
import kotlinx.coroutines.channels.Channel
import java.util.*

data class ProgramState(val x: MutableList<Double> = ArrayList(), val y: MutableList<Double> = ArrayList(),
                        var n: Int = N,
                        val fileInput: Boolean = true,
                        var interpolationParam: Double = 0.0,
                        val interpolationValues: MutableList<InterpolationValue> = ArrayList(),
                        val resultFuncs: MutableList<ResultFunc> = ArrayList(),
                        val scanner: Scanner, var stateType: StateType = New,
                        val stateChannel: Channel<StateType> = Channel())

fun stateProgressNum(stateType: StateType): Int {
    return when (stateType) {
        is New -> 0
        is Started -> 1
        is ReadingInput -> 2
        is Calculating -> stateType.numOfFunc + 3
        is Finished -> interpolationMethods + stateNum - 1
        else -> {
            throw IllegalStateException("Something went wrong.")
        }
    }
}

sealed class StateType
object New: StateType()
object Started: StateType()
object ReadingInput: StateType()
class Calculating(val numOfFunc: Int = 0): StateType()
object Finished: StateType()