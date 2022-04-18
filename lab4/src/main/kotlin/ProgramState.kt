import LabConfiguration.FUNC
import LabConfiguration.N
import LabConfiguration.possibleFuncNum
import LabConfiguration.stateNum
import kotlinx.coroutines.channels.Channel
import java.util.Scanner

data class ProgramState(val x: MutableList<Double> = ArrayList(), val y: MutableList<Double> = ArrayList(),
                        var n: Int = N, val func: (Double) -> Double = FUNC,
                        val possibleFunc: MutableList<PossibleFunc> = ArrayList(),
                        val scanner: Scanner, var stateType: StateType = New,
                        val stateChannel: Channel<StateType> = Channel())

fun stateProgressNum(stateType: StateType): Int {
    return when (stateType) {
        is New -> 0
        is Started -> 1
        is ReadingInput -> 2
        is Calculating -> stateType.numOfFunc + 4
        is Calculated -> 4 + possibleFuncNum
        is Finished -> possibleFuncNum + stateNum - 1
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
object Calculated: StateType()
class Finished(val resFunc: String): StateType()