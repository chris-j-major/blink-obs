package com.light

import com.thingm.blink1.PatternLine
import java.awt.Color

interface LightPatternOrColor

object LightPatternOrColorHelper{

    fun colorOrPatternFromString(input: String): LightPatternOrColor =
        if (input.contains(",")){
            LightPattern( parseLightPatterns( input.split("," ) ) )
        }else{
            LightColor( Color.decode(input) )
        }

    private fun parseLightPatterns(parts: List<String>): Array<PatternLine> =
        parts.map{ line -> parseLightPatternLine(line ) }.toTypedArray()

    private fun parseLightPatternLine( line:String ):PatternLine {
        val parts = line.split(":")
        val colorString = parts.getOrElse(0,"ff00ff")
        val delay = parts.getOrElse(1 , "100").toInt()
        val led = parts.getOrElse(2 , "0").toInt()
        val color = Color.decode(colorString)
        return PatternLine(  delay , color.red , color.green , color.blue , led )
    }
}

private fun <E> List<E>.getOrElse(index: Int, defaultValue: E): E =
    if ( index >= 0 && index < this.size ) this[index] else defaultValue

class LightColor( val color: Color) : LightPatternOrColor {
    override fun toString() = "LightColor[$color]"
}

class LightPattern( val patternLines: Array<PatternLine>) : LightPatternOrColor {
    override fun toString() = "LightPattern[${ patternLines.joinToString { it.toString() } }]"
}