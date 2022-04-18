import CLIManager.printPossibleFunc
import GUIManager.draw
import LabConfiguration.delimiter
import LabConfiguration.sleepTime
import MathSolver.cubeApproximate
import MathSolver.expApproximate
import MathSolver.linearApproximate
import MathSolver.lnApproximate
import MathSolver.powApproximate
import MathSolver.sqrtApproximate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections.min

object ExecutionManager {
    private suspend fun updateState(st: ProgramState, stateType: StateType) {
        withContext(Dispatchers.IO) {
            Thread.sleep(sleepTime)
        }
        st.stateType = stateType
        st.stateChannel.send(stateType)
    }

    private fun readInput(st: ProgramState) {
        var setN = false
        var i = 0
        while (st.scanner.hasNextLine() && i < st.n) {
            val line = st.scanner.nextLine()
            if (line.isNotBlank()) {
                val args = line.split(delimiter)
                if (args.size > 1 && !setN) {
                    setN = true
                    st.x.add(args[0].toDouble())
                    st.y.add(args[1].toDouble())
                } else if (args.size == 1 && !setN) {
                    setN = true
                    st.n = args[0].toInt()
                } else {
                    st.x.add(args[0].toDouble())
                    st.y.add(args[1].toDouble())
                }
            }
            i++
        }
    }

    private suspend fun peekCalculatingAndUpdate(st: ProgramState) {
        when ((st.stateType as Calculating).numOfFunc) {
            0 -> {
                linearApproximate(st)
                updateState(st, Calculating(1))
            }
            1 -> {
                sqrtApproximate(st)
                updateState(st, Calculating(2))
            }
            2 -> {
                cubeApproximate(st)
                updateState(st, Calculating(3))
            }
            3 -> {
                powApproximate(st)
                updateState(st, Calculating(4))
            }
            4 -> {
                expApproximate(st)
                updateState(st, Calculating(5))
            }
            5 -> {
                lnApproximate(st)
                updateState(st, Calculated)
            }
            else -> {
                throw IllegalStateException("Invalid approximate function number: " +
                        "${(st.stateType as Calculating).numOfFunc + 1}.")
            }
        }
    }

    private suspend fun peekBestApproximateAndUpdate(st: ProgramState) {
        val mn =  min(st.possibleFunc.map { it.midEq })
        updateState(st, Finished(st.possibleFunc.first {
            it.midEq == mn
        }.type))
    }

    private suspend fun executeByState(st: ProgramState) {
        when (st.stateType) {
            is New -> updateState(st, Started)
            is Started -> updateState(st, ReadingInput)
            is ReadingInput -> {
                readInput(st)
                updateState(st, Calculating())
            }
            is Calculating -> {
                peekCalculatingAndUpdate(st)
            }
            is Calculated -> {
                peekBestApproximateAndUpdate(st)
            }
            else -> throw IllegalStateException("Invalid program state.")
        }
    }

    suspend fun execute(st: ProgramState) {
        while (!(st.stateType is Finished || st.stateType is Terminated)) {
            executeByState(st)
        }
        println("Best approximate: "+(st.stateType as Finished).resFunc)
        printPossibleFunc(st)
        draw(st)
    }
}