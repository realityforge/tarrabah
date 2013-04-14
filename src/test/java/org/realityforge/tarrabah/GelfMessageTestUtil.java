package org.realityforge.tarrabah;

public final class GelfMessageTestUtil
{
  private GelfMessageTestUtil()
  {
  }

  public static GelfMessageChunk newChunk( final int messageID,
                                           final int chunkSequence,
                                           final int chunkCount,
                                           final String data )
  {
    final byte[] payload = new byte[ GelfMessageChunk.HEADER_SIZE + data.length() ];
    payload[ 0 ] = (byte) GelfHandler.CHUNKED_BYTE_PREFIX[ 0 ];
    payload[ 1 ] = (byte) GelfHandler.CHUNKED_BYTE_PREFIX[ 1 ];
    payload[ 2 ] = (byte) messageID;
    payload[ 10 ] = (byte) chunkSequence;
    payload[ 11 ] = (byte) chunkCount;
    System.arraycopy( data.getBytes(), 0, payload, GelfMessageChunk.HEADER_SIZE, data.length() );

    return new GelfMessageChunk( payload );
  }
}
