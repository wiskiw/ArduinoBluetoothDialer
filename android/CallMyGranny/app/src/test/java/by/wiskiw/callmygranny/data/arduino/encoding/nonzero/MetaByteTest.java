package by.wiskiw.callmygranny.data.arduino.encoding.nonzero;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link MetaByte}
 *
 * @author Andrey Yablonsky on 10.01.2020
 */
public class MetaByteTest {

    @Test
    public void getUpBitCountTest() {
        MetaByte emptyMeta = new MetaByte();
        assertEquals(0, emptyMeta.getUpBitCount());

        MetaByte fullMeta = new MetaByte((byte) 0b11111111);
        assertEquals(MetaByte.PAYLOAD_SIZE, fullMeta.getUpBitCount());

        MetaByte metaByte = new MetaByte((byte) 0b10011010); // не считаем первый бит
        assertEquals(3, metaByte.getUpBitCount());
    }

}