Java Bindings for the MusicBrainz XML Web Service Version 2 (Ws2)
-----------------------------------------------------------------

This Java package contains various classes for accessing the MusicBrainz
web service version 2, as well as parsing the MusicBrainz Metadata XML (MMD).

please visit the project home: http://code.google.com/p/musicbrainzws2-java/

visit http://musicbrainz.org/doc/XML_Web_Service/Version_2

and  http://musicbrainz.org/doc/Next_Generation_Schema/SearchServerXML
for model descriptions and details.

This is an upgrade of the original (WS1) project jmb-wsc by Patrick Ruhkopf.

I resolved to make a new one after many trial to contact the author and get 
a release working in Ws2.

The contact he left (http://dev.ruhkopf.name/jmb-wsc) is not a valid link anymore.

Here some note from the original README.TXT:

This java implementation was inspired by python-musicbrainz-2,
Copyright (c) 2006, Matthias Friedrich.

In future, this project will possibly be hosted at the MusicBrainz'
homepage. For now, visit 

    http://dev.ruhkopf.name/jmb-wsc
    
to find new releases or ask questions.


KNOWN LIMITATIONS OF THIS VERSION
------------------------------
This version ob jmb-wsc does not provide functionality for calculating
DiscIDs from Audio CDs. Furthermore, it does not yet include an xml
writer, so posting data to the web service (like puids) is not possible
at the moment.

While the unit tests pass, it is in no way guaranteed that they cover
everything. Please inform me about any issues that may come up. 
