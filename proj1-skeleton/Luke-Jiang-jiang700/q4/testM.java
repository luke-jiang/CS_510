import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.PrintStream;
// import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

class testM {
	
	private final PrintStream standardOut = System.out;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	
	@BeforeEach
	public void setUp() {
		System.setOut(new PrintStream(outContent));
	}
	
	@AfterEach
	public void restoreStreams() {
		System.setOut(standardOut);
	}
	
	
	/** (1) node coverage but not edge coverage
	  * TRs satisfied: all feasible edges except [1,3]
	  */
	@Test
	public void NCnotEC() {
		M tested = new M();
		String currOut = "";
		
		// Test Path: [1,2,3,4,8,10,11]
		tested.m("", 0);
		currOut += "zero\n";
		assertEquals(currOut, outContent.toString());
		
		// Test Path: [1,2,3,5,8,9,11]
		tested.m("x", 0);
		currOut += "a\n";
		assertEquals(currOut, outContent.toString());
		
		// Test Path: [1,2,3,6,7,8,9,11]
		tested.m("xx", 0);
		currOut += "b\n";
		assertEquals(currOut, outContent.toString());
		
		// Test Path: [1,2,3,7,8,9,11]
		tested.m("xxx", 0);
		currOut += "b\n";
		assertEquals(currOut, outContent.toString());
	}
	
	
	/** (2) edge coverage but not edge-pair coverage
	  * TRs satisfied: {
	  * 	[1,2,3],
	  *     [1,3,4],
	  * 	[2,3,4],[2,3,6],[2,3,7]
	  *		[3,4,8],
	  *		[3,5,8],
	  *		[3,6,7],
	  *		[3,7,8],
	  *		[4,8,10],
	  * 	[5,8,9],
	  *		[6,7,8],
	  *		[7,8,9],
	  *		[8,9,11],
	  *		[8,10,11]
	  * }
	  * TRs unsatisfied: {
	  *		[1,3,5],[1,3,6],[1,3,7],
	  *		[2,3,5]
	  * }
	  */
	@Test
	public void ECnotEPC() {
		M tested = new M();
		String currOut = "";
		
		// Test Path: [1,2,3,4,8,10,11]
		// Covers: [[1,2,3],[2,3,4],[4,8,10],[8,10,11]]
		tested.m("", 0);
		currOut += "zero\n";
		assertEquals(currOut, outContent.toString());
		
		// Test Path: [1,2,3,5,8,9,11]
		// Covers: [[1,2,3],[3,5,8],[5,8,9],[8,9,11]]
		tested.m("x", 0);
		currOut += "a\n";
		assertEquals(currOut, outContent.toString());
		
		// Test Path: [1,2,3,6,7,8,9,11]
		// Covers: [[1,2,3],[2,3,6],[3,6,7],[6,7,8],[7,8,9],[8,9,11]]
		tested.m("xx", 0);
		currOut += "b\n";
		assertEquals(currOut, outContent.toString());
		
		// Test Path: [1,2,3,7,8,9,11]
		// Covers: [[1,2,3],[2,3,7],[3,7,8],[7,8,9],[8,9,11]]
		tested.m("xxx", 0);
		currOut += "b\n";
		assertEquals(currOut, outContent.toString());
		
		// Test Path: [1,3,4,8,10,11]
		// Covers: [[1,3,4],[3,4,8],[4,8,10],[8,10,11]]
		tested.m("", 1);
		currOut += "zero\n";
		assertEquals(currOut, outContent.toString());
	}
	
	/** (3) edge-pair coverage but not prime path coverage
	 *  Not Possible
	 */
	
	/** (4) prime path coverage
	 */
	public void PPC() {
		M tested = new M();
		String currOut = "";
		
		// Test Path: [1,2,3,4,8,10,11]
		tested.m("", 0);
		currOut += "zero\n";
		assertEquals(currOut, outContent.toString());
		
		// Test Path: [1,2,3,5,8,9,11]
		tested.m("x", 0);
		currOut += "a\n";
		assertEquals(currOut, outContent.toString());
		
		// Test Path: [1,2,3,6,7,8,9,11]
		tested.m("xx", 0);
		currOut += "b\n";
		assertEquals(currOut, outContent.toString());
		
		// Test Path: [1,2,3,7,8,9,11]
		tested.m("xxx", 0);
		currOut += "b\n";
		assertEquals(currOut, outContent.toString());
		
		// Test Path: [1,3,4,8,10,11]
		tested.m("", 1);
		currOut += "zero\n";
		assertEquals(currOut, outContent.toString());
		
		// Test Path: [1,3,5,8,9,11]
		tested.m("x", 1);
		currOut += "a\n";
		assertEquals(currOut, outContent.toString());
		
		// Test Path: [1,3,6,7,8,9,11]
		tested.m("xx", 1);
		currOut += "b\n";
		assertEquals(currOut, outContent.toString());
				
		// Test Path: [1,3,7,8,9,11]
		tested.m("xxx", 1);
		currOut += "b\n";
		assertEquals(currOut, outContent.toString());
	}
}
