package filesync;

/**
 * @author aaron
 * @date 7th April 2013
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.commons.codec.binary.Base64;

/*
 * The SynchronisedFile provides efficient file synchronisation to
 * another SynchronisedFile.
 */
public class SynchronisedFile {

    private int BlockSize = 1024; // 1 kilobyte
    private String filename = "";

    private HashMap<String, Block> blockMap;
    private HashMap<String, Block> receivedBlockMap;
    private boolean receivedMinorChange;
    private boolean processing = false;
    private FileInputStream in = null;
    private FileOutputStream out = null;
    private byte[] readBuffer = new byte[BlockSize];

    private int InstBufferSize = 1024 * 1024; // (xBlockSize) = in the order of 1GB peak memory usage
    private ArrayBlockingQueue<Instruction> instQ = new ArrayBlockingQueue<Instruction>(InstBufferSize);

    SynchronisedFile() {

    }

    SynchronisedFile(String f) throws IOException {
        filename = f;
        InitializeFileState();
    }

    // The block size should be reasonably larger than the SHA-256 hash, which is 32 bytes.
    // The size of an instruction is at most the size of a block plus some overhead.
    SynchronisedFile(String f, int bs) throws IOException {
        this.BlockSize = bs;
        this.readBuffer = new byte[BlockSize];
        this.filename = f;
        InitializeFileState();
    }

    /*
     * If the filename to be synchronised has changed then this
     * method must be called to initialise the file state.
     * The method creates a new block map for the file.
     */
    public void InitializeFileState() throws IOException {
        Block b = null;
        int i = 0;
        CloseFile(); // close any existing file
        OpenFile(); // open a file, the filename may have changed
        this.blockMap = new HashMap<String, Block>(); // reset the block map
        while ((b = ReadNextBlock()) != null) {
            b.setOffset(i); // set the offset into the file
            this.blockMap.put(b.getHash(), b); // add the block to the block map
            if (!blockMap.containsKey(b.getHash())) {
                System.err.println("Hash map failure.");
                System.exit(-1);
            }
            /*
             * If the block already exists at another offset then it will be overwritten.
             * Very rare for large block sizes and shouldn't cause a problem in any case.
             */
            i += b.getLength();
        }
        CloseFile(); // close the file
    }

    /*
     * This method needs to be called regularly, or whenever the file has
     * changed. It will detect changes in the file and generate instructions
     * to synchronise the changes at the receiver.
     * 
     * It scans the file and generates instructions to recreate file
     * from either blocks found in the block map, or newly generated blocks.
     * The block map is updated to reflect the new file contents.
     * The instructions are put in the blocking queue.
     * If the queue is full then CheckFileState will block.
     * The caller must service the queue in another thread to ensure it
     * does not block.
     */
    public void CheckFileState() throws IOException, InterruptedException {
        Block b;
        HashMap<String, Block> updatedBlockMap = new HashMap<String, Block>();
        int i = 0;

        OpenFile();

        // emit a start update instruction
        StartUpdateInstruction sui = new StartUpdateInstruction();
        this.instQ.put(sui);
        boolean minorChanges = false;
        while ((b = ReadNextBlock()) != null) {
            // set the offset of the block
            b.setOffset(i);
            // just pick up the actual bytes read from the file
            byte[] b1bytes = (byte[]) readBuffer.clone();

            // if the read block is in the hash map then we can generate
            // a copy block instruction
            if (blockMap.containsKey(b.getHash())) {
                // a match
                // The block is at offset i
                b.setBytes(b1bytes);
                CopyBlockInstruction cbi = new CopyBlockInstruction(b);
                instQ.put(cbi);
                b.setBytes(null);
                updatedBlockMap.put(b.getHash(), b);
                i += b.getLength();
                continue;
            } else {

                // we didn't get a match
                if (b.getLength() < BlockSize) {
                    // the last block of the file
                    Block lastBlock = new Block(b1bytes, i, GenerateHash(b1bytes));
                    lastBlock.setBytes(b1bytes);
                    lastBlock.setLength(b.getLength());
                    CopyBlockInstruction cbi = new CopyBlockInstruction(lastBlock);
                    this.instQ.put(cbi);
                    lastBlock.setBytes(null);
                    updatedBlockMap.put(lastBlock.getHash(), lastBlock);
                    i += lastBlock.getLength();
                    break;
                }

                // now scan byte by byte
                byte[] newBlockBytes = new byte[BlockSize];
                int newBlockLength = 0;
                byte[] nextbyte = new byte[1];
                boolean breakout = false;
                while (in.read(nextbyte) == 1) {
                    newBlockBytes[newBlockLength++] = b1bytes[0];
                    if (newBlockLength == BlockSize) {
                        // The new block will be at offset i
                        Block nb = new Block(newBlockBytes, i, GenerateHash(newBlockBytes));
                        nb.setBytes(newBlockBytes.clone());
                        NewBlockInstruction nbi = new NewBlockInstruction(nb);
                        this.instQ.put(nbi);
                        nb.setBytes(null);
                        updatedBlockMap.put(nb.getHash(), nb);
                        newBlockBytes = new byte[BlockSize];
                        newBlockLength = 0;
                        i += nb.getLength();
                    }
                    for (int j = 0; j < b1bytes.length - 1; j++) {
                        b1bytes[j] = b1bytes[j + 1];
                    }
                    b1bytes[b1bytes.length - 1] = nextbyte[0];
                    String hash = GenerateHash(b1bytes);
                    if (blockMap.containsKey(hash)) {
                        if (newBlockLength > 0) {
                            if (newBlockLength < BlockSize) {
                                minorChanges = true;
                            }
                            // The new block will be at offset i
                            Block nb = new Block(newBlockBytes, i, GenerateHash(newBlockBytes));
                            nb.setBytes(newBlockBytes.clone());
                            nb.setLength(newBlockLength);
                            NewBlockInstruction nbi = new NewBlockInstruction(nb);
                            this.instQ.put(nbi);
                            nb.setBytes(null);
                            updatedBlockMap.put(nb.getHash(), nb);
                            i += newBlockLength;
                            newBlockBytes = new byte[BlockSize];
                            newBlockLength = 0;
                        }

                        // The block will be at offeset i
                        Block foundBlock = blockMap.get(hash).clone();
                        foundBlock.setBytes(b1bytes);
                        foundBlock.setOffset(i);
                        CopyBlockInstruction cbi = new CopyBlockInstruction(foundBlock);
                        instQ.put(cbi);
                        foundBlock.setBytes(null);
                        updatedBlockMap.put(foundBlock.getHash(), foundBlock);
                        i += foundBlock.getLength();
                        breakout = true;
                        break; // start reading blocks again
                    }
                }	// while scanning
                if (!breakout) {
                    if (newBlockLength > 0) {
                        if (newBlockLength < BlockSize) {
                            minorChanges = true;
                        }
                        // The new block will be at offset i
                        Block nb = new Block(newBlockBytes, i, GenerateHash(newBlockBytes));
                        nb.setBytes(newBlockBytes.clone());
                        nb.setLength(newBlockLength);
                        NewBlockInstruction nbi = new NewBlockInstruction(nb);
                        this.instQ.put(nbi);
                        nb.setBytes(null);
                        updatedBlockMap.put(nb.getHash(), nb);
                        i += newBlockLength;
                        newBlockBytes = new byte[BlockSize];
                        newBlockLength = 0;
                    }
                    Block lastBlock = new Block(b1bytes, i, GenerateHash(b1bytes));
                    lastBlock.setBytes(b1bytes);
                    CopyBlockInstruction cbi = new CopyBlockInstruction(lastBlock);
                    this.instQ.put(cbi);
                    lastBlock.setBytes(null);
                    updatedBlockMap.put(lastBlock.getHash(), lastBlock);
                    i += lastBlock.getLength();
                }
            } // while reading blocks
        }
        CloseFile();
        // emit a end update instruction
        EndUpdateInstruction eui = new EndUpdateInstruction();
        this.instQ.put(eui);
        // update the block map
        this.blockMap = updatedBlockMap;
        if (minorChanges) {
            InitializeFileState();
        }
    }

    /*
     * ProcessInstruction is called at the receiver and is given the instructions
     * that are emitted by the sender. The instructions will be used to update receiver's
     * file to make it identical to the sender's file.
     * 
     * Throws a BlockUnavailableException if an instruction refers to a requested
     * block that cannot be found. If this happens then the next instruction given MUST be
     * a NewBlockInstruction to fill in for the unavailable block; otherwise file 
     * corruption occurs. File corruption however can be repaired by subsequent calls
     * to CheckFileState() at the sender.
     */
    public void ProcessInstruction(Instruction inst) throws IOException, BlockUnavailableException {
        if (inst.Type().equals("StartUpdate")) {
            if (processing) {
                System.err.println("ProcessInstruction: received StartUpdate without completing previous update.");
            }
            this.processing = true;
            this.out = new FileOutputStream(filename + ".tmp");
            this.receivedBlockMap = new HashMap<String, Block>();
            this.receivedMinorChange = false;
        } else if (inst.Type().equals("EndUpdate")) {
            if (!processing) {
                System.err.println("ProcessInstruction: received EndUpdate without having started an update.");
            }
            this.processing = false;
            this.out.close();
            // update the block map
            this.blockMap = receivedBlockMap;
            File file = new File(filename);
            if (!file.delete()) {
                System.err.println("SynchronisedFile: file.delete() failed.");
                System.exit(-1);
            }
            File file2 = new File(this.filename + ".tmp");
            if (!file2.renameTo(file)) {
                System.err.println("SynchronisedFile: file.2.renameTo(file) failed.");
                System.exit(-1);
            }
            if (this.receivedMinorChange) {
                InitializeFileState();
            }
        } else if (inst.Type().equals("CopyBlock")) {
            if (!this.processing) {
                System.err.println("ProcessInstruction: received CopyBlock without having started an update.");
            }
            Block cb = ((CopyBlockInstruction) inst).getBlock();
            byte[] bytes = GetBlockBytes(cb.getHash());
            FileChannel ch = out.getChannel();
            ch.position(cb.getOffset());
            this.out.write(bytes, 0, (int) cb.getLength());
            cb.setBytes(null);
            this.receivedBlockMap.put(cb.getHash(), cb);
        } else if (inst.Type().equals("NewBlock")) {
            if (!this.processing) {
                System.err.println("ProcessInstruction: received NewBlock without having started an update.");
            }
            Block nb = ((NewBlockInstruction) inst).getBlock();
            if (nb.getLength() < BlockSize) {
                this.receivedMinorChange = true;
            }
            FileChannel ch = out.getChannel();
            ch.position(nb.getOffset());
            this.out.write(nb.getBytes(), 0, (int) nb.getLength());
            nb.setBytes(null);
            this.receivedBlockMap.put(nb.getHash(), nb);
        }
        assert (false); // we should never get an unknown instruction
    }

    /*
     * Take an instruction from the queue.
     * Block if nothing available.
     * Returns null if interrupted.
     * NOTE: The instruction will contain a copy of any bytes pertaining
     * to the instruction. It is therefore important to regularly service
     * the queue.
     */
    public Instruction NextInstruction() {
        Instruction i = null;
        try {
            i = instQ.take();
        } catch (InterruptedException e) {
            // print an alert
            e.printStackTrace();
        }
        return i;
    }

    /*
     * Support methods
     */
    private void OpenFile() throws FileNotFoundException {
        CloseFile(); // just a precaution
        this.in = new FileInputStream(this.filename);
    }

    private void CloseFile() {
        try {
            if (this.in != null) {
                this.in.close();
            }
            this.in = null;
        } catch (IOException e) {
            // alert the user
            // Since we're closing the file, this exception may
            // not be a big problem and we choose to go on.
            e.printStackTrace();
        }
    }

    // low level function to read a block from the current file position
    private Block ReadNextBlock() throws IOException {
        byte[] bytes = new byte[BlockSize];
        int size = in.read(bytes);
        this.readBuffer = bytes.clone();
        if (size > 0) {
            String hash = GenerateHash(bytes);
            Block b = new Block(bytes, 0, hash);
            b.setLength(size);
            return b;
        }
        return null;
    }

    // low level function to generate a hash of a byte array
    private String GenerateHash(byte[] b) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(b);
            String encryptedString = new String(Base64.encodeBase64(messageDigest.digest()));
            return encryptedString;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        assert (false);
        return null;
    }

    // try to get a block's content from the file
    private byte[] GetBlockBytes(String hash) throws BlockUnavailableException, IOException {

        OpenFile();
        if (blockMap.containsKey(hash)) {
            Block foundBlock = blockMap.get(hash);
            byte[] b = new byte[BlockSize];

            FileChannel ch = in.getChannel();
            ch.position(foundBlock.getOffset());
            in.read(b, 0, (int) foundBlock.getLength());

            if (GenerateHash(b).equals(hash)) {
                CloseFile();
                return b;
            }
        }
        CloseFile();
        throw (new BlockUnavailableException());

    }

    /*
     * Getters and setters
     */
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
