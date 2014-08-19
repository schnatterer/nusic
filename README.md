# coverartarchive-api

Java data bindings for the [Cover Art Archive](http://coverartarchive.org/).

## Things to know

* Provides access to cover art through the `/release/{mbid}/` end point.
* The Javadoc can be generated by running `mvn javadoc:javadoc`.

## Configuration

The recommended way of using `coverartarchive-api` is through Maven. Add the `coverartarchive-api` dependency to your project's POM:

    <dependency>
      <groupId>fm.last</groupId>
      <artifactId>coverartarchive-api</artifactId>
      <version>2.0.0</version>
    </dependency>

(Alternatively, a JAR file can be obtained from Maven Central: [fm.last:coverartarchive-api:2.0.0](http://search.maven.org/#artifactdetails%7Cfm.last%7Ccoverartarchive-api%7C2.0.0%7Cjar))

## Usage example

Get all cover art for Portishead's 'Dummy' release:

    CoverArtArchiveClient client = new DefaultCoverArtArchiveClient();
    UUID mbid = UUID.fromString("76df3287-6cda-33eb-8e9a-044b5e15ffdd");

    CoverArt coverArt = null;
    try {
      coverArt = client.getByMbid(mbid);
      if (coverArt != null) {
        for (CoverArtImage coverArtImage : coverArt.getImages()) {
          File output = new File(mbid.toString() + "_" + coverArtImage.getId() + ".jpg");
          FileUtils.copyInputStreamToFile(coverArtImage.getImage(), output);
        }
      }
    } catch (Exception e) {
      // ...
    }

## Running the tests

* Run `mvn clean verify`

## Contributing

All contributions are welcome. Please use the [Last.fm codeformatting profile](https://github.com/lastfm/lastfm-oss-config/blob/master/src/main/resources/fm/last/last.fm.eclipse-codeformatter-profile.xml) found in the `lastfm-oss-config` project for formatting your changes.

## License

Copyright 2012 [Last.fm](http://www.last.fm/)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 
[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
 
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## Kthxbye

Last.fm <3 MusicBrainz
