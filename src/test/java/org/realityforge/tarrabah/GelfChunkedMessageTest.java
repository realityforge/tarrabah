package org.realityforge.tarrabah;

import javax.annotation.Nonnull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class GelfChunkedMessageTest
{
  private int _currentTime;

  @BeforeMethod
  public void resetTime()
  {
    _currentTime = 1;
  }

  @Test
  public void basicWorkflow()
    throws Exception
  {
    final GelfMessageChunk initialChunk = GelfMessageTestUtil.newChunk( 1, 0, 3, "a" );
    final GelfChunkedMessage message = new TestGelfChunkedMessage( initialChunk );

    assertEquals( message.getReceivedChunksCount(), 1 );
    assertEquals( message.isComplete(), false );
    assertEquals( message.getLastUpdateTime(), 1 );
    assertEquals( message.getMessageID(), initialChunk.getMessageID() );

    message.insert( GelfMessageTestUtil.newChunk( 1, 1, 3, "b" ) );

    assertEquals( message.getReceivedChunksCount(), 2 );
    assertEquals( message.isComplete(), false );
    assertEquals( message.getLastUpdateTime(), 2 );
    assertEquals( message.getMessageID(), initialChunk.getMessageID() );

    message.insert( GelfMessageTestUtil.newChunk( 1, 2, 3, "c" ) );

    assertEquals( message.getReceivedChunksCount(), 3 );
    assertEquals( message.isComplete(), true );
    assertEquals( message.getLastUpdateTime(), 3 );

    final byte[] data = message.toPayload();

    assertEquals( data.length, 3 );
    assertEquals( data[ 0 ], 'a' );
    assertEquals( data[ 1 ], 'b' );
    assertEquals( data[ 2 ], 'c' );
  }

  @Test( expectedExceptions = IllegalArgumentException.class,
         expectedExceptionsMessageRegExp = "Chunk sequence '3' exceeds expected chunk count '3' for message '1'" )
  public void invalidChunkSequence()
    throws Exception
  {
    final GelfChunkedMessage message = new TestGelfChunkedMessage( GelfMessageTestUtil.newChunk( 1, 0, 3, "a" ) );
    message.insert( GelfMessageTestUtil.newChunk( 1, 3, 3, "b" ) );
  }

  @Test( expectedExceptions = IllegalArgumentException.class,
         expectedExceptionsMessageRegExp = "Chunk count '4' does not match expected chunk count '3' for message '1'" )
  public void invalidChunkCount()
    throws Exception
  {
    final GelfChunkedMessage message = new TestGelfChunkedMessage( GelfMessageTestUtil.newChunk( 1, 0, 3, "a" ) );
    message.insert( GelfMessageTestUtil.newChunk( 1, 2, 4, "b" ) );
  }

  @Test( expectedExceptions = IllegalArgumentException.class,
         expectedExceptionsMessageRegExp = "Chunk sequence '0' already received for message '1'" )
  public void duplicateChunkMessage()
    throws Exception
  {
    final GelfChunkedMessage message = new TestGelfChunkedMessage( GelfMessageTestUtil.newChunk( 1, 0, 3, "a" ) );
    message.insert( GelfMessageTestUtil.newChunk( 1, 0, 3, "a" ) );
  }

  final class TestGelfChunkedMessage
    extends GelfChunkedMessage
  {
    TestGelfChunkedMessage( @Nonnull final GelfMessageChunk initialChunk )
    {
      super( initialChunk );
    }

    @Override
    protected long currentTime()
    {
      return _currentTime++;
    }
  }
}
