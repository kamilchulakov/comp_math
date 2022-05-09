object LabConfiguration {
    const val N = 10
    const val interpolationMethods = 2
    const val stateNum = 4
    const val progressNum = stateNum + interpolationMethods - 1
    const val uiMult = 100.0 / (progressNum - 1)
    const val badWaiting = 50
    const val sleepTime = 100L
    const val delimiter = " "
    // TODO: add default functions to show user
}