import LabConfiguration.badWaiting
import LabConfiguration.progressNum
import LabConfiguration.sleepTime
import LabConfiguration.uiMult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Math.max

object CLIManager {
    suspend fun progressBar(programState: ProgramState) {
        var i = stateProgressNum(programState.stateType)
        while (i <= progressNum) {
            print("[")
            for (j in 0 until i) {
                print("#")
            }
            for (j in 0 until progressNum - i - 1) {
                print(" ")
            }
            print("] " + i * uiMult + "%")
            if (i < progressNum) {
                var proceed = false
                var cnt = 0
                while (!proceed) {
                    try {
                        cnt++
                        i = stateProgressNum(programState.stateChannel.tryReceive().getOrThrow())
                        proceed = true
                        print("\r")
                    } catch (ex: IllegalStateException) {
                        withContext(Dispatchers.IO) {
                            Thread.sleep(sleepTime)
                        }
                    } finally {
                        if (cnt > badWaiting) {
                            throw IllegalStateException("Waited $cnt times for new state.")
                        }
                    }
                }
            }
            if (i == progressNum) break
        }
        println()
    }

    fun printPossibleFunc(st: ProgramState) {
        var bigType = 0
        var bigStr = 0
        for (i in st.possibleFunc) {
            bigType = bigType.coerceAtLeast(i.type.length)
            bigStr = bigStr.coerceAtLeast(i.funcString.length)
        }
        st.possibleFunc.forEach {
            println("${it.type}${" ".repeat(bigType-it.type.length)} | " +
                    "${it.funcString}${" ".repeat(bigStr-it.funcString.length)} | ${it.midEq}")
        }
    }
}