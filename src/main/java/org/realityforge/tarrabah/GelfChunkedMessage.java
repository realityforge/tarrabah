package org.realityforge.tarrabah;

import javax.annotation.Nonnull;

/**
 * Representation of a chunked Gelf message.
 * <p/>
 * This class contains all the received chunks, the last time a chunk was received etc.
 */
public class GelfChunkedMessage
{
  @Nonnull
  private final Long _messageID;
  @Nonnull
  private final GelfMessageChunk[] _chunks;
  private int _receivedChunksCount;
  private long _lastUpdateTime;

  public GelfChunkedMessage( @Nonnull final GelfMessageChunk initialChunk )
  {
    _messageID = initialChunk.getMessageID();
    _chunks = new GelfMessageChunk[ initialChunk.getChunkCount() ];
    _chunks[ initialChunk.getChunkSequence() ] = initialChunk;
    _lastUpdateTime = currentTime();
    _receivedChunksCount = 1;
  }

  @Nonnull
  public Long getMessageID()
  {
    return _messageID;
  }

  public int getReceivedChunksCount()
  {
    return _receivedChunksCount;
  }

  public long getLastUpdateTime()
  {
    return _lastUpdateTime;
  }

  public void insert( final @Nonnull GelfMessageChunk chunk )
  {
    final int sequence = chunk.getChunkSequence();
    if ( sequence >= _chunks.length )
    {
      throw new IllegalArgumentException( "Chunk sequence '" + sequence + "' exceeds " +
                                          "expected chunk count '" + _chunks.length + "' " +
                                          "for message '" + Long.toHexString( getMessageID() ) + "'" );
    }
    if ( chunk.getChunkCount() != _chunks.length )
    {
      throw new IllegalArgumentException( "Chunk count '" + chunk.getChunkCount() + "' does not match " +
                                          "expected chunk count '" + _chunks.length + "' " +
                                          "for message '" + Long.toHexString( getMessageID() ) + "'" );
    }
    if ( null != _chunks[ sequence ] )
    {
      throw new IllegalArgumentException( "Chunk sequence '" + sequence + "' already received " +
                                          "for message '" + Long.toHexString( getMessageID() ) + "'" );
    }
    _chunks[ sequence ] = chunk;
    _receivedChunksCount++;
    _lastUpdateTime = currentTime();
  }

  public boolean isComplete()
  {
    return _receivedChunksCount == _chunks.length;
  }

  protected long currentTime()
  {
    return System.currentTimeMillis();
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();

    sb.append( "GelfChunkedMessage[MessageID= " );
    sb.append( getMessageID() );
    sb.append( ",ReceivedChunksCount=" );
    sb.append( _receivedChunksCount );
    sb.append( "/" );
    sb.append( _chunks.length );
    sb.append( ",LastUpdate=" );
    sb.append( _lastUpdateTime );
    sb.append( "]" );

    return sb.toString();
  }
}
