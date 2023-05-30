package com.tristana.library

import com.blankj.utilcode.util.RegexUtils
import java.lang.Exception
import java.util.Scanner


/**
 * @author koala
 * @version 1.0
 * @date 2023/5/30 18:06
 * @description
 */
object VersionGenerator {
    @JvmStatic
    fun main(args: Array<String>) {
        val s = Scanner(System.`in`)
        println("请输入VersionName：")
        var line = ""
        var versionCodeData = ""
        while (true) {
            line = s.nextLine()
            if (line == "ok") break
            println(">>>$line")
            println(">>>now start")
            val patternFilter = "(?<=\\().*(?=\\))"
            var expandVersion = ""
            var mainVersion = ""
            var mainVersionCode = ""
            var exppandVersionCode = ""
            RegexUtils.getMatches(patternFilter, line)?.let { expandData ->
                if (expandData.size >= 1) {
                    expandVersion = expandData[0]
                    println(">>>expand version: $expandVersion")
                    mainVersion = line.replace("($expandVersion)", "")
                    println(">>>main version: $mainVersion")
                    mainVersion.split(".").forEach {
                        versionCodeData += getNumber(it)
                    }
                }
                val numberPatternFilter = "[^0-9]+"
                RegexUtils.getMatches(numberPatternFilter, expandVersion)
                    ?.let { numberPatternData ->
                        var cVersion = ""
                        var eVersion = ""
                        var rVersion = ""
                        var pVersion = ""
                        numberPatternData.forEachIndexed { index, _ ->
                            if (index <= numberPatternData.size - 2) {
                                when (index) {
                                    0 -> {
                                        RegexUtils.getMatches(
                                            "(?<=${numberPatternData[index]}).*(?=${
                                                numberPatternData[index + 1]
                                            })", expandVersion
                                        )?.let {
                                            if (it.isNotEmpty()) {
                                                cVersion = it[0]
                                            }
                                        }
                                    }

                                    1 -> {
                                        RegexUtils.getMatches(
                                            "(?<=${numberPatternData[index]}).*(?=${
                                                numberPatternData[index + 1]
                                            })", expandVersion
                                        )?.let {
                                            if (it.isNotEmpty()) {
                                                eVersion = it[0]
                                            }
                                        }
                                    }

                                    2 -> {
                                        RegexUtils.getMatches(
                                            "(?<=${numberPatternData[index]}).*(?=${
                                                numberPatternData[index + 1]
                                            })", expandVersion
                                        )?.let {
                                            if (it.isNotEmpty()) {
                                                rVersion = it[0]
                                            }
                                        }
                                    }
                                }
                            } else {
                                pVersion = expandVersion
                                    .replace("C$cVersion", "")
                                    .replace("E$eVersion", "")
                                    .replace("R$rVersion", "")
                                    .replace("P", "")
                            }
                        }
                        println(">>>expand version data: [C: $cVersion; E: $eVersion; R: $rVersion; P: $pVersion]")
                        mainVersionCode = versionCodeData
                        versionCodeData += getNumber(cVersion)
                        versionCodeData += getNumber(eVersion)
                        versionCodeData += getNumber(rVersion)
                        versionCodeData += getNumber(pVersion)
                        exppandVersionCode += getNumber(cVersion)
                        exppandVersionCode += getNumber(eVersion)
                        exppandVersionCode += getNumber(rVersion)
                        exppandVersionCode += getNumber(pVersion)
                    }
            }
            while (versionCodeData.startsWith("0")) {
                versionCodeData = versionCodeData.replaceFirst("0", "")
            }
            while (mainVersionCode.startsWith("0")) {
                mainVersionCode = mainVersionCode.replaceFirst("0", "")
            }
            while (exppandVersionCode.startsWith("0")) {
                exppandVersionCode = exppandVersionCode.replaceFirst("0", "")
            }
            println(
                ">>>result>>>\n" +
                        "ext.version_name = '$line'\n" +
                        "ext.main_version_code = $mainVersionCode\n" +
                        "ext.expand_version_code = $exppandVersionCode\n" +
                        "ext.app_version_code = $versionCodeData\n" +
                        "ext.main_version_name = '$mainVersion'\n" +
                        "ext.expand_version_name = '$expandVersion'"

            )
        }
    }

    private fun getNumber(input: String): String {
        return try {
            if (input.toInt() < 10) {
                "00${input.toInt()}"
            } else if (input.toInt() in 10..99) {
                "0${input.toInt()}"
            } else {
                input
            }
        } catch (e: Exception) {
            "000"
        }

    }
}