package name.seanpayne.util.id3tool

import com.mpatric.mp3agic.*
import java.util.*

data class Mp3FileMetadata(
        val length:Long,
        val metdata: Id3Metadata?)

fun Mp3File.jsonMetadata() = Mp3FileMetadata(
        this.lengthInMilliseconds,
        Id3Metadata.convertId3(this)
)

data class Id3Metadata(
        val album:String?,
        val artist:String?,
        val title:String?,
        val track:String?,
        val year:String?,
        val comment:String?,
        val genre:Int?,
        val images: Array<Id3Picture> = emptyArray()) {
    companion object {
        fun convertId3(mp3File: Mp3File) : Id3Metadata? {
            val wrapper = ID3Wrapper(mp3File.id3v1Tag, mp3File.id3v2Tag)
            return Id3Metadata(
                    album = wrapper.album,
                    artist = wrapper.artist,
                    title = wrapper.title,
                    track = wrapper.track,
                    year = wrapper.year,
                    comment = wrapper.comment,
                    genre = if (wrapper.genre == -1) null else wrapper.genre,
                    images = mp3File.id3v2Tag?.frameSets?.get("APIC")?.frames.orEmpty()
                            .map { if (mp3File.id3v2Tag.obseleteFormat) ID3v2ObseletePictureFrameData(false, it.data) else ID3v2PictureFrameData(false, it.data) }
                            .map { Id3Picture.convertId3PictureData(it) }
                            .toTypedArray()
            )
        }
    }
}

data class Id3Picture(
        val pictureType: Id3PictureType,
        val description: String,
        val mimeType: String,
        val data: String
) {
    companion object {
        fun convertId3PictureData(frameData: ID3v2PictureFrameData) = Id3Picture(
                pictureType = Id3PictureType.fromFlag(frameData.pictureType),
                mimeType = frameData.mimeType,
                description = frameData.description?.toString() ?: "",
                data = Base64.getEncoder().encodeToString(frameData.imageData)
        )
    }
}

enum class Id3PictureType(val value: Byte) {
    OTHER(0x00),        //$00     Other
    FILE_ICON(0x01),    //$01     32x32 pixels 'file icon' (PNG only)
    OTHER_ICON(0x02),   //$02     Other file icon
    COVER_FRONT(0x03),  //$03     Cover (front)
    COVER_BACK(0x04),   //$04     Cover (back)
    LEAFLET(0x05),      //$05     Leaflet page
    MEDIA(0x06),        //$06     Media (e.g. lable side of CD)
    ARTIST_LEAD(0x07),  //$07     Lead artist/lead performer/soloist
    ARTIST_PERFORMER(0x08), //$08     Artist/performer
    CONDUCTOR(0x09),    //$09     Conductor
    BAND(0x0A),         //$0A     Band/Orchestra
    COMPOSER(0x0B),     //$0B     Composer
    LYRICIST(0x0C),     //$0C     Lyricist/text writer
    RECORDING_LOCATION(0x0D), //$0D     Recording Location
    DURING_RECORDING(0x0E), //$0E     During recording
    DURING_PERFORMANCE(0x0F), //$0F     During performance
    SCREEN_CAP(0x10),   //$10     Movie/video screen capture
    //$11     A bright coloured fish
    ILLUSTRATION(0x12), //$12     Illustration
    LOGO_ARTIST(0x13),  //$13     Band/artist logotype
    LOGO_PUBLISHER(0x14); //$14     Publisher/Studio logotype

    companion object {
        fun fromFlag(value: Byte) : Id3PictureType {
            val result = Id3PictureType.values()
                    .filter { it.value == value }
                    .firstOrNull()
            return result ?: OTHER
        }
    }

}

data class Id3ChapterTOC(
        val id: String,
        val root: Boolean,
        val chapters: Array<Id3Chapter>
)

data class Id3Chapter(
        val id: String,
        val startTime: Long?,
        val startOffset: Long?,
        val endTime: Long?,
        val endOffset: Long?,
        val attachments: Array<ChapterAttachment>
) {
    companion object {
        fun convertId3Chapter(chapterFrameData: ID3v2ChapterFrameData) : Id3Chapter {
            return Id3Chapter(
                    chapterFrameData.id,
                    if (chapterFrameData.startTime == -1) null else chapterFrameData.startTime.toLong(),
                    if (chapterFrameData.startOffset == -1) null else chapterFrameData.startOffset.toLong(),
                    if (chapterFrameData.endTime == -1) null else chapterFrameData.endTime.toLong(),
                    if (chapterFrameData.endOffset == -1) null else chapterFrameData.endOffset.toLong(),
                    chapterFrameData.subframes
                            ?.map {
                                when (ChapterAttachmentType.fromFlag(it.id)) {
                                    ChapterAttachmentType.TITLE -> TitleChapterAttachment(ID3v2TextFrameData(true, it.data).text.toString())
                                    ChapterAttachmentType.URL -> {
                                        val data = ID3v2UrlFrameData(true, it.data)
                                        UrlChapterAttachment(data.description?.toString() ?: "", data.url)
                                    }
                                    ChapterAttachmentType.OTHER -> OtherChapterAttachment(it.id)
                                }
                            }
                            ?.toTypedArray() ?: emptyArray()
            )
        }
    }
}

interface ChapterAttachment {
    val type: ChapterAttachmentType
}

enum class ChapterAttachmentType(val flag: String) {
    TITLE("TIT2"),
    URL("WXXX"),
    OTHER("XXX");

    companion object {
        fun fromFlag(flag: String) : ChapterAttachmentType {
            val result = values()
                    .filter { it.flag == flag }
                    .firstOrNull()
            return result ?: OTHER
        }
    }
}

data class TitleChapterAttachment(val text: String) : ChapterAttachment {
    override val type: ChapterAttachmentType = ChapterAttachmentType.TITLE
}

data class UrlChapterAttachment(val description: String, val url: String) : ChapterAttachment {
    override val type: ChapterAttachmentType = ChapterAttachmentType.URL
}

data class OtherChapterAttachment(val id: String) : ChapterAttachment {
    override val type: ChapterAttachmentType = ChapterAttachmentType.OTHER
}