import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import javax.net.*;
import javax.net.ssl.*;
import javax.swing.*;

public class Client extends JFrame implements Runnable
{
  /**
   * Connection to the client
   */
  private DataInputStream din;

  /**
   * Connection to the client
   */
  private DataOutputStream dout;

  /**
   * KeyStore for storing our public/private key pair
   */
  private KeyStore clientKeyStore;
  
  /**
   * KeyStore for storing the server's public key
   */
  private KeyStore serverKeyStore;

  /**
   * Used to generate a SocketFactory
   */
  private SSLContext sslContext;
  
  /**
   * A list of visible postings
   */
  private Set postings = new HashSet();

  /**
   * The font used for all postings
   */
  private Font font = new Font( "TimesRoman", Font.PLAIN, 18 );

  /**
   * Passphrase for accessing our authentication keystore
   */
  static private final String passphrase = "clientpw";

  /**
   * A source of secure random numbers
   */
  static private SecureRandom secureRandom;

  public Client( String host, int port ) {
    super( "Whiteboard" );

    setupGUI();

    connect( host, port );

    new Thread( this ).start();
  }

  private void setupServerKeystore() throws GeneralSecurityException, IOException {
    serverKeyStore = KeyStore.getInstance( "JKS" );
    serverKeyStore.load( new FileInputStream( "server.public" ), 
                        "public".toCharArray() );
  }

  private void setupClientKeyStore() throws GeneralSecurityException, IOException {
    clientKeyStore = KeyStore.getInstance( "JKS" );
    clientKeyStore.load( new FileInputStream( "client.private" ),
                       passphrase.toCharArray() );
  }

  private void setupSSLContext() throws GeneralSecurityException, IOException {
    TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
    tmf.init( serverKeyStore );

    KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
    kmf.init( clientKeyStore, passphrase.toCharArray() );

    sslContext = SSLContext.getInstance( "TLS" );
    sslContext.init( kmf.getKeyManagers(),
                     tmf.getTrustManagers(),
                     secureRandom );
  }

  private void connect( String host, int port ) {
    try {
      setupServerKeystore();
      setupClientKeyStore();
      setupSSLContext();

      SSLSocketFactory sf = sslContext.getSocketFactory();
      SSLSocket socket = (SSLSocket)sf.createSocket( host, port );

      InputStream in = socket.getInputStream();
      OutputStream out = socket.getOutputStream();

      din = new DataInputStream( in );
      dout = new DataOutputStream( out );
    } catch( GeneralSecurityException gse ) {
      gse.printStackTrace();
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  private void setupGUI() {
    getContentPane().setLayout( new BorderLayout() );

    ClientCanvas cc = new ClientCanvas();

    getContentPane().add( cc, BorderLayout.CENTER );

    setSize( 400, 250 );
    setVisible( true );

    cc.requestFocus();
  }

  public void run() {
    try {
      while (true) {
        Posting posting = Posting.read( din );
        postings.add( posting );
        repaint();
      }
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }
  
  private void newPosting( Posting posting ) {
    postings.add( posting );
    try {
      posting.write( dout );
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  class ClientCanvas extends JPanel
  {
    private String currentString;
    private int currentX=-1;
    private int currentY=-1;

    public ClientCanvas() {
      setBackground( Color.white );

      addMouseListener( new MouseListener() {
        public void mousePressed( MouseEvent me ) {
        }
        public void mouseEntered( MouseEvent me ) {
        }
        public void mouseExited( MouseEvent me ) {
        }
        public void mouseReleased( MouseEvent me ) {
          clickAt( me.getX(), me.getY() );
        }
        public void mouseClicked( MouseEvent me ) {
        }
      } );

    addKeyListener( new KeyListener() {
      public void keyPressed( KeyEvent ke ) {
      }
      public void keyReleased( KeyEvent ke ) {
      }
      public void keyTyped( KeyEvent ke ) {
        key( ke.getKeyChar() );
      }
    } );
    }

    public void paintComponent( Graphics g ) {
      super.paintComponent( g );

      g.setFont( font );
      for (Iterator it=postings.iterator(); it.hasNext();) {
        Posting posting = (Posting)it.next();
        Color color = posting.color();
        if (color==null) {
          color = Color.black;
        }
        g.setColor( color );
        g.drawString( posting.text(), posting.x(), posting.y() );
      }
      if (currentString != null) {
        g.setColor( Color.black );
        g.drawString( currentString+"|", currentX, currentY );
      }
    }

    private void finishPosting() {
      if (currentString!=null && !currentString.equals( "" )) {
        Posting posting = new Posting( currentString, currentX, currentY );
        newPosting( posting );
      }

      currentString = null;
      repaint();
    }

    private void backspace() {
      if (currentString != null && currentString.length()>0) {
        currentString = currentString.substring( 0, currentString.length()-1 );
        repaint();
      }
    }

    private void clickAt( int x, int y ) {
      finishPosting();

      currentX = x;
      currentY = y;

      currentString = "";
      repaint();
    }

    private void key( int key ) {
      if (currentString==null || currentX==-1) {
        return;
      }

      if (key=='\n') {
        finishPosting();
      } else if (key==8) {
        backspace();
      } else {
        if (currentString==null) {
          currentString = "";
        }

        currentString += (char)key;
        repaint();
      }
    }
  }

  static public void main( String args[] ) {
    if (args.length != 2) {
      System.err.println( "Usage: java Client [hostname] [port number]" );
      System.exit( 1 );
    }

    String host = args[0];
    int port = Integer.parseInt( args[1] );

    System.out.println( "Wait while secure random numbers are initialized...." );
    secureRandom = new SecureRandom();
    secureRandom.nextInt();
    System.out.println( "Done." );

    new Client( host, port );
  }
}
