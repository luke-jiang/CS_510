import org.junit.*;
import static org.junit.Assert.*;

import java.io.PrintStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class TestM {

    /* add your test code here */
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
	System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void NCnotEC() {
	M tester = new M();
	
	tester.m("", 0);
	Assert.assertEquals("zero", outputStreamCaptor.toString().trim());
	
	tester.m("a", 0);
	Assert.assertEquals("a", outputStreamCaptor.toString().trim());
	
	tester.m("aa", 0);
	Assert.assertEquals("b", outputStreamCaptor.toString().trim());
	
	tester.m("aaa", 0);
	Assert.assertEquals("b", outputStreamCaptor.toString().trim());
    }
    
}

class M {
	public static void main(String [] argv){
		M obj = new M();
		if (argv.length > 0)
			obj.m(argv[0], argv.length);
	}
	
	public void m(String arg, int i) {
		int q = 1;
		A o = null;
		Impossible nothing = new Impossible();
		if (i == 0)
			q = 4;
		q++;
		switch (arg.length()) {
			case 0: q /= 2; break;
			case 1: o = new A(); new B(); q = 25; break;
			case 2: o = new A(); q = q * 100;
			default: o = new B(); break; 
		}
		if (arg.length() > 0) {
			o.m();
		} else {
			System.out.println("zero");
		}
		nothing.happened();
	}
}

class A {
	public void m() { 
		System.out.println("a");
	}
}

class B extends A {
	public void m() { 
		System.out.println("b");
	}
}

class Impossible{
	public void happened() {
		// "2b||!2b?", whatever the answer nothing happens here
	}
}
