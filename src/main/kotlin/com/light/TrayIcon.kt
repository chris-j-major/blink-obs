package com.light

import com.thingm.blink1.PatternLine
import org.slf4j.Logger
import java.awt.*
import java.awt.image.BufferedImage
import javax.swing.Icon
import javax.swing.UIManager
import kotlin.system.exitProcess

class TrayIcon:StateReceiver {
    private val log: Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    private val iconFactory = IconFactory(UIManager.getIcon("InternalFrame.icon"))
    private val iconCache = IconCache<LightColor>{ this.makeCircleIcon(it.color) }
    private val iconPatternCache = IconCache<LightPattern>{ this.makeCircleIcon(it.patternLines) }
    private val activeTrayIcon = makeTrayIcon()

    init {
        EventQueue.invokeLater {
            runCatching {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            }.onFailure {
                it.printStackTrace()
                Toolkit.getDefaultToolkit().beep()
            }

            if (SystemTray.isSupported()) {
                runCatching {
                    SystemTray.getSystemTray().add(activeTrayIcon)
                }
            }else{
                log.warn("No system tray available!")
            }
        }
    }

    private fun makeTrayIcon(): java.awt.TrayIcon {
        val menuItemExit = MenuItem("Shutdown")
        menuItemExit.addActionListener {
            val tray = SystemTray.getSystemTray()
            for (icon in tray.trayIcons) {
                tray.remove(icon)
            }
            exitProcess(1)
        }
        val popup = PopupMenu()
        popup.add(menuItemExit)

        val img = makeCircleIcon( Color.RED )
        return TrayIcon(img, "OBS - blink(1)", popup)
    }


    private fun makeCircleIcon(c: Color):Image =
        iconFactory.apply { w, h, g2 ->
            g2.color = c
            g2.fillArc( 0 , 0 , w , h ,0 , 360)
        }

    private fun makeCircleIcon(c: Array<PatternLine>): Image {
        val totalTime = c.sumOf { it.fadeMillis }.toFloat()
        val msConvert = 360.0 / totalTime
        var start = 0
        return iconFactory.apply { w, h, g2 ->
            c.forEach {
                val delta = (msConvert * it.fadeMillis).toInt()
                g2.color = Color( it.r , it.g, it.b )
                g2.fillArc( 0, 0 , w , h ,start , delta)
                start+= delta
            }
        }
    }

    override fun setTo(target: LightPatternOrColor) {
        activeTrayIcon.image = when (target){
            is LightColor -> iconCache.get( target )
            is LightPattern -> iconPatternCache.get( target )
            else -> makeCircleIcon(Color.RED)
        }
    }


    class IconFactory( baseIcon: Icon) {
        private val w = baseIcon.iconWidth
        private val h = baseIcon.iconHeight

        fun apply( f:(w:Int,h:Int,g2:Graphics2D)->Unit ):BufferedImage{
            val bi = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
            val g2 = bi.createGraphics()
            f(w,h,g2)
            g2.dispose()
            return bi
        }
    }

    class IconCache<T>( private val generate:(T)->Image ){
        private val cache:MutableMap<T,Image> = mutableMapOf()
        fun get(input: T): Image = cache.getOrPut( input ){ generate(input) }
    }

}