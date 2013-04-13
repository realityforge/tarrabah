package org.realityforge.tarrabah;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Representation of a Gelf Message Chunk.
 *
 * NOTE: This is the compressed chunk format present in Graylog2 > 0.9.6
 * as the old format is no longer used.
 */
public final class GelfMessageChunk
{
  static final int MESSAGE_ID_OFFSET = GelfHandler.CHUNKED_BYTE_PREFIX.length;
  static final int MESSAGE_ID_LENGTH = 8;
  static final int CHUNK_SEQUENCE_OFFSET = MESSAGE_ID_OFFSET + MESSAGE_ID_LENGTH;
  static final int SEQUENCE_LENGTH = 1;
  static final int CHUNK_COUNT_OFFSET = CHUNK_SEQUENCE_OFFSET + SEQUENCE_LENGTH;
  static final int HEADER_SIZE = CHUNK_COUNT_OFFSET + SEQUENCE_LENGTH;

  @Nullable
  private Long _messageID;

  @Nonnull
  private final byte[] _chunk;

  public GelfMessageChunk( @Nonnull final byte[] chunk )
  {
    if ( HEADER_SIZE > chunk.length )
    {
      throw new IllegalArgumentException( "Chunk too small to contain header." );
    }
    _chunk = chunk;
  }

  @SuppressWarnings( { "PointlessBitwiseExpression", "PointlessArithmeticExpression" } )
  @Nonnull
  public Long getMessageID()
  {
    if ( null == _messageID )
    {
      _messageID =
        ( ( _chunk[ MESSAGE_ID_OFFSET + 0 ] & 0xFFL ) << 0L ) +
        ( ( _chunk[ MESSAGE_ID_OFFSET + 1 ] & 0xFFL ) << 8L ) +
        ( ( _chunk[ MESSAGE_ID_OFFSET + 2 ] & 0xFFL ) << 16L ) +
        ( ( _chunk[ MESSAGE_ID_OFFSET + 3 ] & 0xFFL ) << 24L ) +
        ( ( _chunk[ MESSAGE_ID_OFFSET + 4 ] & 0xFFL ) << 32L ) +
        ( ( _chunk[ MESSAGE_ID_OFFSET + 5 ] & 0xFFL ) << 40L ) +
        ( ( _chunk[ MESSAGE_ID_OFFSET + 6 ] & 0xFFL ) << 48L ) +
        ( ( _chunk[ MESSAGE_ID_OFFSET + 7 ] & 0xFFL ) << 56L );
    }
    return _messageID;
  }

  public int getChunkSequence()
  {
    return _chunk[ CHUNK_SEQUENCE_OFFSET ];
  }

  public int getChunkCount()
  {
    return _chunk[ CHUNK_COUNT_OFFSET ];
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();

    sb.append( "GelfMessageChunk[MessageID= " );
    sb.append( getMessageID() );
    sb.append( ",ChunkSequence=" );
    sb.append( getChunkSequence() + 1 );
    sb.append( "/" );
    sb.append( getChunkCount() );
    sb.append( ",ChunkSize=" );
    sb.append( _chunk.length );
    sb.append( "]" );

    return sb.toString();
  }
}
