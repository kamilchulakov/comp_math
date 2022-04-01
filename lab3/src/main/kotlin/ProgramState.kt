import kotlin.properties.Delegates
import kotlin.reflect.KProperty

object ProgramState {
    lateinit var function: (Double) -> Double
    var a by Delegates.notNull<Double>()
    var b by Delegates.notNull<Double>()
    var n by DefaultPartition()
    var method by Delegates.notNull<Int>()
    var solved = false
    var result = 0.0
    var eps = 0.01


    class DefaultPartition {
        private var partition = 4.0
        operator fun getValue(thisRef: ProgramState, property: KProperty<*>): Double {
            return partition
        }
        operator fun setValue(thisRef: ProgramState, property: KProperty<*>, value: Double) {
            partition = value
        }
    }
}

fun interface ProceedProgram {
    operator fun invoke(state: ProgramState): ProgramState
}


