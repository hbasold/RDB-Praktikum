package parsing;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * 
 */

/**
 * @author henning
 *
 */
public class InputStreamIterator implements Iterator<Integer> {
    InputStreamReader stream;
    Integer buffer;
    
    InputStreamIterator(InputStreamReader stream_){
        stream = stream_;
        buffer = null;
    }

    @Override
    public boolean hasNext() {
        if(buffer == null){
            return read();
        }
        else{
            return buffer.intValue() != -1;
        }
    }

    @Override
    public Integer next() {
        if(buffer == null){
            assert read();
        }
        Integer buf = buffer;
        buffer = null;
        return buf;
    }
    
    private boolean read() {
        try {
            buffer = new Integer(stream.read());
            return buffer.intValue() != -1;
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public void remove() { }

}
