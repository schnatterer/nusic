/* --------------------------------------------------------------------------

   MusicBrainz -- The Internet music metadatabase

   Copyright (C) 2006 Matthias Friedrich
   
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

     $Id: LibDiscId.java,v 1.1 2010-11-26 01:54:18 andy Exp $
*/
package org.musicbrainz.discid;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Direct translator for MusicBrainz libdiscid, as found at
 * <a href="http://musicbrainz.org/doc/libdiscid">http://musicbrainz.org/doc/libdiscid</a>
 * <p/>
 * While it is possible, this class is not intended to be used directly.  Please use the {@link DiscInfo} utility
 * class.
 * @see DiscInfo
 * @author Andrew Taylor <andy@benow.ca> 
 */
public class LibDiscId {
  private static final String LIBNAME = "discid-java";

  static {
    System.loadLibrary(LIBNAME);
  }

  /** erro value for newly created index */
  private static final int INDEX_ERROR = -1;

  /** index of a native discid instance */
  private int m_index = 0;

  /**
   * The version intended to be used with libdiscid-java
   * @return version
   */
  public static String getVersion() {
    return "0.2.2";
  }

  /**
   * Read from the default device
   * @throws DiscIdException
   */
  public LibDiscId() throws DiscIdException {
    m_index = init();
    if (m_index == INDEX_ERROR)
      throw new DiscIdException("New shout instance could not be created. Possibly maximum number of shout instances reached");
    read();
  }

  /**
   * Read from a given device
   * 
   * @param device
   * @throws DiscIdException
   */
  public LibDiscId(String device) throws DiscIdException {
    m_index = init();
    if (m_index == INDEX_ERROR)
      throw new DiscIdException("New shout instance could not be created. Possibly maximum number of shout instances reached");
    read(device);
  }

  public LibDiscId(int first, int last, int[] offsets) throws DiscIdException {
    m_index = init();
    if (m_index == INDEX_ERROR)
      throw new DiscIdException("New shout instance could not be created. Possibly maximum number of shout instances reached");
    put(first, last, offsets);
  }

  /**
     * Initialize structs, etc
   * @return index of shout instance.
   */
  private native synchronized int init();

  /**
   * @see java.lang.Object#finalize()
   */
  @Override
  protected void finalize() throws Throwable {
    free(m_index);
    super.finalize();
  }

  public void close() {
    free(m_index);
  }

  /**
   * Cleans up the library. Called during finalize
   * @param index - index of discid instance to shut down.
   */
  private native synchronized void free(
      int index);

  /**
   * Gets the host to which shout is to connect
   * @return the host to connect to
   * @throws DiscIdException 
   */
  private void read() throws DiscIdException {
    read(null);
  }

  private void read(
      String device) throws DiscIdException {
    if (!read(m_index, device))
      assertError();

  }

  /**
   * Read the disc in the given CD-ROM/DVD-ROM drive.
   *
   * This function reads the disc in the drive specified by the given device
   * identifier. If the device is NULL, the default drive, as returned by
   * discid_get_default_device() is used.
   *
   * On error, this function returns false and sets the error message which you
   * can access using discid_get_error_msg(). In this case, the other functions
   * won't return meaningful values and should not be used.
   *
   * This function may be used multiple times with the same DiscId object.
   *
   * @param d a DiscId object created by discid_new()
   * @param device an operating system dependent device identifier, or NULL
   * @return true if successful, or false on error.
   */
  private native boolean read(
      int m_index2,
      String device);

  /**
   * Get throw exception on errors
   * @throws DiscIdException 
   */
  private void assertError() throws DiscIdException {
    String err = getErrorMsg(m_index);
    if (err != null && err.length() > 0)
      throw new DiscIdException(err);
  }

  private String assertErrorReturning(
      String ifGoodReturn) throws DiscIdException {
    assertError();
    return ifGoodReturn;
  }

  private int assertErrorReturning(
      int ifGoodReturn) throws DiscIdException {
    assertError();
    return ifGoodReturn;
  }

  /**
   * Return a human-readable error message.
   *
   * This function may only be used if discid_read() failed. The returned
   * error message is only valid as long as the DiscId object exists.
   *
   * @param d a DiscId object created by discid_new()
   * @return a string describing the error that occurred
   */
  private native String getErrorMsg(
      int index);

  /**
   * Provides the TOC of a known CD.
   *
   * This function may be used if the TOC has been read earlier and you
   * want to calculate the disc ID afterwards, without accessing the disc
   * drive. It replaces the discid_read function in this case.
   *
   * The offsets parameter points to an array which contains the track offsets
   * for each track. The first element, offsets[0], is the leadout track. It
   * must contain the total number of sectors on the disc.
   *
   * @param first the number of the first audio track on disc (usually one)
   * @param last the number of the last audio track on the disc
   * @param offsets a pointer to an array of 100 track offsets
   */
  private void put(
      int first,
      int last,
      int[] offsets) throws DiscIdException {
    if (!put(m_index, first, last, offsets))
      assertError();
  }

  private native boolean put(
      int m_index2,
      int first,
      int last,
      int[] offsets);

  /**
   * Return a MusicBrainz DiscID after a read has been done.
   *
   * @return a string containing a MusicBrainz DiscID
   * @throws DiscIdException 
   */
  public String getId() throws DiscIdException {
    return assertErrorReturning(getId(m_index));
  }

  private native String getId(
      int m_index2);

  /**
   * Return a FreeDB DiscID.
   *
   * @return a string containing a FreeDB DiscID
   * @throws DiscIdException 
   */
  public String getFreeDBId() throws DiscIdException {
    return assertErrorReturning(getFreeDBId(m_index));
  }
  
  private native String getFreeDBId(
      int m_index2);

  /**
   * Return an URL for submitting the DiscID to MusicBrainz.
   *
   * The URL leads to an interactive disc submission wizard that guides the
   * user through the process of associating this disc's DiscID with a
   * release in the MusicBrainz database.
   *
   * The returned string is only valid as long as the DiscId object exists.
   *
   * @param d a DiscId object created by discid_new()
   * @return a string containing an URL
   * @throws DiscIdException 
   */
  public URL getSubmissionURL() throws DiscIdException {
    String urlStr = assertErrorReturning(getSubmissionURL(m_index));
    if (urlStr == null)
      return null;
    try {
      return new URL(urlStr);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Unexpected error; should be a url: " + urlStr);
    }
  }

  private native String getSubmissionURL(
      int m_index2);

  /**
   * Return an URL for retrieving CD information from MusicBrainz' web service
   *
   * The URL provides the CD information in XML. 
   * See http://musicbrainz.org/development/mmd for details.
   *
   * The returned string is only valid as long as the DiscId object exists.
   *
   * @return a string containing an URL
   * @throws DiscIdException 
   */
  public String getWebServiceURL() throws DiscIdException {
    return assertErrorReturning(getWebServiceURL(m_index));
  }

  
  private native String getWebServiceURL(
      int m_index2);

  /**
   * Return the name of the default disc drive for this operating system.
   *
   * @return a string containing an operating system dependent device identifier
   * @throws DiscIdException 
   */
  public String getDefaultDevice() throws DiscIdException {
    String result = getDefaultDevice(m_index);
    assertError();
    return result;
  }

  private native String getDefaultDevice(
      int m_index2);

  /**
   * Return the number of the first track on this disc.
   *
   * @return the number of the first track
   * @throws DiscIdException 
   */
  public int getFirstTrackNum() throws DiscIdException {
    return assertErrorReturning(getFirstTrackNum(m_index));
  }

  private native int getFirstTrackNum(
      int m_index2);

  /**
   * Return the number of the last track on this disc.
   *
   * @return the number of the last track
   * @throws DiscIdException 
   */
  public int getLastTrackNum() throws DiscIdException {
    return assertErrorReturning(getLastTrackNum(m_index));
  }

  private native int getLastTrackNum(
      int m_index2);

  /**
   * Return the length of the disc in sectors.
   *
   * @return the length of the disc in sectors
   * @throws DiscIdException 
   */
  public int getSectors() throws DiscIdException {
    return assertErrorReturning(getSectors(m_index));
  }

  private native int getSectors(
      int m_index2);

  /**
   * Return the sector offset of a track.
   *
   * Only track numbers between (and including) discid_get_first_track_num()
   * and discid_get_last_track_num() may be used.
   *
   * @param trackNum the number of a track
   * @return sector offset of the specified track
   * @throws DiscIdException 
   */
  public int getTrackOffset(
      int trackNum) throws DiscIdException {
    return assertErrorReturning(getTrackOffset(m_index, trackNum));
  }

  private native int getTrackOffset(
      int m_index2,
      int trackNum);

  /**
   * Return the length of a track in sectors.
   *
   * Only track numbers between (and including) discid_get_first_track_num()
   * and discid_get_last_track_num() may be used.
   *
   * @param d a DiscId object created by discid_new()
   * @param track_num the number of a track
   * @return length of the specified track
   * @throws DiscIdException 
   */
  public int getTrackLength(
      int trackNum) throws DiscIdException {
    return assertErrorReturning(getTrackLength(m_index, trackNum));
  }

  private native int getTrackLength(
      int m_index2,
      int trackNum);

}
