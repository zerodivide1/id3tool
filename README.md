# id3tool
Command-line-based utility designed to help build scripts which read or modify ID3 tags in MP3 files.

_Work in progress_

## Usage

### Output ID3 data to JSON
```bash
id3tool id3tojson <input MP3 filename>
```
Outputs some ID3v2 data in JSON format, including chapters and album images (with Base64-encoded image data embedded in output).

Currently-supported output:
* Album name
* Artist name
* Title name
* Track number
* Year
* Comment
* Genre ID
* Chapter TOC
  * TOC ID
  * Chapters in TOC
    * Start/end time/offset
    * Chapter attachments
      * Title
      * URLs