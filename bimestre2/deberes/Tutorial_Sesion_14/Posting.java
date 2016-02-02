import java.awt.*;
import java.io.*;

public class Posting
{
  private String text;
  private int x;
  private int y;
  private Color color;

  private Posting() {
  }

  public Posting( String text, int x, int y, Color color ) {
    this.text = text;
    this.x = x;
    this.y = y;
    this.color = color;
  }

  public Posting( String text, int x, int y ) {
    this( text, x, y, null );
  }

  static Posting read( DataInputStream din ) throws IOException {
    Posting posting = new Posting();
    posting.text = din.readUTF();
    posting.x = din.readInt();
    posting.y = din.readInt();
    int r = din.readInt();
    if (r==-1) {
      // no color
    } else {
      int g = din.readInt();
      int b = din.readInt();
      posting.color = new Color( r, g, b );
    }

System.out.println( "read "+posting );
    return posting;
  }

  public void write( DataOutputStream dout ) throws IOException {
    dout.writeUTF( text );
    dout.writeInt( x );
    dout.writeInt( y );
    if (color==null) {
      dout.writeInt( -1 );
    } else {
      dout.writeInt( color.getRed() );
      dout.writeInt( color.getGreen() );
      dout.writeInt( color.getBlue() );
    }
System.out.println( "wrote "+this );
    dout.flush();
  }

  public String text() {
    return text;
  }

  public void text( String text ) {
    this.text = text;
  }

  public int x() {
    return x;
  }

  public void x( int x ) {
    this.x = x;
  }

  public int y() {
    return y;
  }

  public void y( int y ) {
    this.y = y;
  }

  public Color color() {
    return color;
  }

  public void color( Color color ) {
    this.color = color;
  }

  public String toString() {
    return text+"@("+x+","+y+")";
  }
}
