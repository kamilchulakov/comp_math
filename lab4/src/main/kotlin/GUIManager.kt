import javafx.application.Platform
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.vis.swing.jfx.DefaultPlotPanelJfx
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.SwingUtilities
import jetbrains.letsPlot.geom.geomPoint
import jetbrains.letsPlot.intern.Plot
import jetbrains.letsPlot.intern.toSpec
import jetbrains.letsPlot.letsPlot
import javax.swing.*
import javax.swing.JFrame.EXIT_ON_CLOSE

object GUIManager {
    private const val n = 200
    private const val eps = 1.0 / n

    fun draw(funcList: List<PossibleFunc>) {
        val plots = HashMap<String, Plot>()
        for (func in funcList) {
            val data = mapOf<String, List<*>>(
                "xvar" to List(n) { i:Int-> i*eps },
                "yvar" to List(n) { i:Int-> func.func(i*eps) }
            )
            plots[func.type] = letsPlot(data) { x = "xvar"; y = "yvar" } + geomPoint(shape = 1)
        }

        val selectedPlotKey = plots.keys.first()
        val controller = Controller(
            plots,
            selectedPlotKey,
            false
        )

        val window = JFrame("Example App (Swing-JavaFX)")
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