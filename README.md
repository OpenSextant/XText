XText
=================
```
    Author: Marc. C. Ubaldino, MITRE Corporation
    Date: 2013-March
    Updated: 2021-April
    Copyright MITRE Corporation, 2012-2021
```

Apache Tika is awesome, and...
----------------------
Tika provides all sorts of solid content conversion and parsing capabilities.  But it is a 
developer's tool -- for those who want to get close to all the gory details.  There is some 
amount of simplification and readiness for operationalizing things like Tika for more direct use in pipelines.

XText wraps around Tika APIs and other parser APIs with the objective of providing a uniform 
`ConvertedDocument` API class that provides the following benefits:

- streamlined meta-data model, using a simple `Properties` map and fewer fields, e.g., `getProperty("title")`. 
  Other stock fields are `pub_date`, `filepath`, `filesize`, `author`, `conversion_date`, `url`
- simple and obvious "text" buffer
- serialization of "plain text" + "metadata" output in a single text file.  An XText output saved to the filesystem, 
  contains the textual content followed by an `XTEXT:<payload>` footer where the payload is an encoded JSON
  property sheet for the conversion.  In this way you can easily carry the complete output of converting an individual 
  file in one-to-one fashion for most files.  The `XTEXT` footer remains at the bottom of the file and should not 
  interfere with any NLP operations on the text.

The converter execution in the `XText` API class facilitates conversion tasks in a few ways:

- Provides options for caching conversions to the filesystem (or not). In the filesystem, 
  conversions can be archived alongside archive or maintained in a parallel folder hierarchy
- Provides a solution for managing unpacking and converting compound file types such as ZIP, TAR, Email and 
  other containers.
- Provides  `ConversionListener` and `CollectionListener` classes to extend XText for pipeline integration, e.g., 
  when a document is collected, record it;  when a document is converted to text process it. Such extensions
  are useful for application streaming where you do not want a lot of File I/O.

  
Supported Document Conversions
------------------------------
Major file conversions supported include:
* Anything Tika can do. By default the Tika AutoDetectParser is employed.
* Email message archiving (traverse RFC822 MIME message, saving attachments, etc) and traversal, conversion, etc.
* Limited web crawl and archiving crawls
* JPEG EXIF parsing (saves full EXIF header as text; EXIF location & time as metadata)
* Support for Embedded Object extraction

Major features added beyond Tika
------------------------------

Conversion caching/archiving: conversions can be maintained close to originals or in parallel structure
Metadata preservation: metadata about original and the conversion process are persisted with conversions
XText adds some typical conventions for integrators who wish to use a document conversion tool rather than the 
bare Tika library.   Such features include:

- document file type filters
- logging and metrics
- input/output options for saving converted documents and related metadata
- lightweight listener design so you can unpack, convert and process all in the same loop
- formalizing the document meta-data practices: that is, what metadata is really important and how do we store it with the converted document


Supported customizations:

* PDF metadata harvesting (from Tika/PDFBox);  Detecting of encrypted PDFs
* Web content scrapping;  Default HTML parser is Tika's, but for web articles, Boilerplate parser is better.
* Decomposing and extracting text from compound documents
* Content is normalized to UTF-8 with unix line endings ('\n') only.
* Java Documentation contains what you need to know for development.

## Documentation 

See [Java API](./doc/apidocs/index.html) for details on metadata fields and API usage.

## Usage

Running it, from a release try the example below.

```shell

    ./script/README_convert.txt
    ./script/convert.sh or convert.bat script

    USAGE: 
    ./script/convert.sh   -input FOLDER -output FOLDER  [ other options ]

    ANT: 
    ant -f ./script/xtext-ant.xml -Dinputfile=./test/somestuff/  convert


An example:
    mkdir /tmp/conversions/

   cd ./XText-3.5/
   ./script/convert.sh --input ./test --output /tmp/conversions

```
       

## Build

```shell
    // Build, then make a distribution that is more easily distributed.
    ant build
    ant dist  
```


## Publish via Maven:

```shell
    // 
    //  Fix all versions to be release versions.
    //  Ensure GPG key is known...
    // and OSSRH login is set in settings.xml
    mvn clean deploy -P release
```
  


# RELEASE NOTES

### v3.5 NEW BEGINNING

- Xponents v3.5.4 series released for major updates on versions and Log4J resolution
- Tika 1.28 and PDFBox 2.0.25 are upgraded here, in part due to Log4J updates

### v3.4  VAMP

- Xponents Core API 3.4 updated.

### v3.3  HOLLY JOLLY

- Xponents Core API 3.3 updated.

### v3.2  DEAD HEAT

- Xponents core API 3.2 streamlines dependencies -- No solr needed here.

### v3.1.0 SUMMER SOLSTICE 2019

- Tika v1.21
- Xponents 3.1.0

### v3.0.6 SUPERBOWL 2019

- Tika 1.19+
- Jodd JSON library 5.x
- bug fix in ConvertedDocument properties API 

### v3.0  INDEPENDENCE DAY 2018

- OpenSextant Xponents 3.0 release

### v2.10.7 

- Moved XText out of Xponents folder.

### v2.6 through v2.10 

- Just keeping pace with the rest of Xponents releases

### v2.5.1  SUMMER, 2014

- PDFBox updated
- JavaDoc improvements, looking to Java 8 stringent javadoc checking
- Added Outlook PST support (initial). via java-libpst.  This support is planned for Tika 1.6.
- PathManager construct added to offload complexities of dealing with caching, crawling, collecting.

### v1.4  ST PATRICK's DAY, 2014

- Added Tika 1.5 as primary conversion tool
- Introduced content collectors: Email, web, Sharepoint
- Added MessageConverter for email traversal, conversion and archiving. 
- Added OLEConverter to support MS object conversion, e.g. Outlook message files (untested)
- Added ImageConverter which saves full EXIF header as text and preserves interesting GPS location and 
  date/time as formal metadata that can be retrieved later.

### v1.0  ST PATRICK's  DAY, 2013

- initial design
- added Testing archive -- not released;  UBL Letters from SOCOM where released Fall 2012.  
  They are PDFs and Word docs in English and Arabic.  They offer a good test opportunity.


