import LabConfiguration.FUNC
import LabConfiguration.N
import LabConfiguration.possibleFuncNum
import LabConfiguration.stateNum
import kotlinx.coroutines.channels.Channel
import java.util.Scanner

data class ProgramState(val x: List<Double> = ArrayList(), val y: List<Double> = ArrayList(),
                        var n: Int = N, val func: (Double) -> Double = FUNC,
                        val possibleFunc: List<PossibleFunc> = ArrayList(),
                        val scanner: Scanner, var stateType: StateType = New,
                        val stateChannel: Channel<StateType> = Channel())

fun stateProgressNum(stateType: StateType): Int {
    return when (stateType) {
        is New -> 1
        is Started -> 2
        is ReadingInput -> 3
        is Calculating -> stateType.numOfFunc + 4 - 1
        is Blocked -> stateProgressNum(stateType.prevState)
        is Terminated -> possibleFuncNum + 4
        is Finished -> possibleFuncNum + stateNum
        else -> {
            throw IllegalStateException("Something went wrong.")
        }
    }
}

sealed class StateType
object New: StateType()
object Started: StateType()
object ReadingInput: StateType()
class Calculating(val numOfFunc: Int): StateType()
class Blocked(val prevState: StateType) : StateType()
class Terminated(val ex: Exception): StateType()
class Finished(val resFunc: String): StateType()

data class PossibleFunc(val type: String, val funcString: String, val func: (Double) -> Double, val midEq: Double)