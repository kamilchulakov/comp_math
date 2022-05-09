import CLIManager.progressBar
import ExecutionManager.execute
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

// 1, 3 => многочлен Лагранжа и Гаусса
fun main(args: Array<String>) {
    val st = ProgramState(scanner =
        when (args.isNotEmpty()) {
            // TODO: System.in
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