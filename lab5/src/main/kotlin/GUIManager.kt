import Utils.getRandomIntInRange
import javafx.application.Platform
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.vis.swing.jfx.DefaultPlotPanelJfx
import jetbrains.letsPlot.geom.geomPoint
import jetbrains.letsPlot.intern.Plot
import jetbrains.letsPlot.intern.toSpec
import jetbrains.letsPlot.letsPlot
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import javax.swing.JFrame.EXIT_ON_CLOSE

object GUIManager {
    private const val n = 2000
    private const val eps = 10.0 / n

    private fun getRandomColor(): Color {
        return when (getRandomIntInRange(0, 5)) {
            0 -> Color.RED
            1 -> Color.GREEN
            2 -> Color.ORANGE
            3 -> Color.LIGHT_PINK
            else -> Color.BLACK
        }
    }
    fun draw(st: ProgramState) {
        val interpolationValues: List<InterpolationValue> = st.interpolationValues
        val plots = HashMap<String, Plot>()
        val data2 = mapOf<String, List<*>>(
            "xvar" to List(st.n) { i:Int-> st.x[i] },
            "yvar" to List(st.n) { i:Int-> st.y[i] }
        )
        var i = 1
        for (it in interpolationValues) {
            val data = mapOf<String, List<*>>(
                "xvar" to listOf(st.interpolationParam) + List(st.n) { i:Int-> st.x[i] },
                "yvar" to listOf(it.result) + List(st.n) { i:Int-> st.y[i] }
            )
            plots[it.method] = letsPlot(data) { x = "xvar"; y = "yvar" } + geomPoint(shape = i++, color = getRandomColor())
        }
        plots["Points"] = letsPlot(data2) { x = "xvar"; y = "yvar" } + geomPoint(shape = i, color = getRandomColor())

        val selectedPlotKey = plots.keys.first()
        val controller = Controller(
            plots,
            selectedPlotKey,
            false
        )

        val window = JFrame("Lets plot Kotlin")
        window.defaultCloseOperation = EXIT_ON_CLOSE
        window.contentPane.layout = BoxLayout(window.contentPane, BoxLayout.Y_AXIS)

        // Add controls
        val controlsPanel = Box.createHorizontalBox().apply {
            // Plot selector
            val plotButtonGroup = ButtonGroup()
            for (key in plots.keys) {
                plotButtonGroup.add(
                    JRadioButton(key, key == selectedPlotKey).apply {
                        addActionListener {
                            controller.plotKey = this.text
                        }
                    }
                )
            }

            this.add(Box.createHorizontalBox().apply {
                border = BorderFactory.createTitledBorder("Plot")
                for (elem in plotButtonGroup.elements) {
                    add(elem)
                }
            })

            // Preserve aspect ratio selector
            val aspectRadioButtonGroup = ButtonGroup()
            aspectRadioButtonGroup.add(JRadioButton("Original", false).apply {
                addActionListener {
                    controller.preserveAspectRadio = true
                }
            })
            aspectRadioButtonGroup.add(JRadioButton("Fit container", true).apply {
                addActionListener {
                    controller.preserveAspectRadio = false
                }
            })

            this.add(Box.createHorizontalBox().apply {
                border = BorderFactory.createTitledBorder("Aspect ratio")
                for (elem in aspectRadioButtonGroup.elements) {
                    add(elem)
                }
            })
        }
        window.contentPane.add(controlsPanel)

        // Add plot panel
        val plotContainerPanel = JPanel(GridLayout())
        window.contentPane.add(plotContainerPanel)

        controller.plotContainerPanel = plotContainerPanel
        controller.rebuildPlotComponent()

        SwingUtilities.invokeLater {
            window.pack()
            window.size = Dimension(850, 400)
            window.setLocationRelativeTo(null)
            window.isVisible = true
        }
    }
}

private class Controller(
    private val plots: Map<String, Plot>,
    initialPlotKey: String,
    initialPreserveAspectRadio: Boolean
) {
    var plotContainerPanel: JPanel? = null
    var plotKey: String = initialPlotKey
        set(value) {
            field = value
            rebuildPlotComponent()
        }
    var preserveAspectRadio: Boolean = initialPreserveAspectRadio
        set(value) {
            field = value
            rebuildPlotComponent()
        }

    fun rebuildPlotComponent() {
        plotContainerPanel?.let {
            val container = plotContainerPanel!!
            // cleanup
            for (component in container.components) {
                if (component is Disposable) {
                    component.dispose()
                }
            }
            container.removeAll()

            // build
            container.add(createPlotPanel())
            container.parent?.revalidate()
        }
    }

    fun createPlotPanel(): JPanel {
        // Make sure JavaFX event thread won't get killed after JFXPanel is destroyed.
        Platform.setImplicitExit(false)

        val rawSpec = plots[plotKey]!!.toSpec()
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)

        return DefaultPlotPanelJfx(
            processedSpec = processedSpec,
            preserveAspectRatio = preserveAspectRadio,
            preferredSizeFromPlot = false,
            repaintDelay = 10,
        ) { messages ->
            for (message in messages) {
                println("[Example App] $message")
            }
        }
    }
}