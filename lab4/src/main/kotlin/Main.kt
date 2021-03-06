import ExecutionManager.execute
import CLIManager.progressBar
import java.util.*
import kotlinx.coroutines.*
import java.io.File

fun main(args: Array<String>) {
    val st = ProgramState(scanner =
        when (args.isNotEmpty()) {
            false -> Scanner(System.`in`)
            else -> Scanner(File(args[0]))
        }
    )
    try {
        runBlocking {
            launch {
                progressBar(st)
            }
            launch {
                execute(st)
            }
        }
    } catch (ex: IllegalStateException) {
        println()
        println(ex.message)
    }
}