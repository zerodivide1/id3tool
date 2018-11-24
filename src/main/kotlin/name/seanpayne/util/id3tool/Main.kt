package name.seanpayne.util.id3tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.mpatric.mp3agic.Mp3File
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Id3Tool : CliktCommand() {
    override fun run() = Unit
}

class Id3ToJson: CliktCommand(help = "Output ID3 to JSON") {
    val inputFile by argument(help = "Input MP3 file", name = "input")

    override fun run() {
        val mp3File = Mp3File(inputFile)

        val metadata = mp3File.jsonMetadata()

        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        val jsonAdapter = moshi.adapter(Mp3FileMetadata::class.java)
        val jsonResult = jsonAdapter.toJson(metadata)

        println(jsonResult)
    }
}



fun main(args: Array<String>) = Id3Tool()
        .subcommands(Id3ToJson())
        .main(args)