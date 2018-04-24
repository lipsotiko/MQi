package io.egia.mqi.patient;

import io.egia.mqi.chunk.Chunk;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes={Patient.class})
public class PatientTest {

	Patient p = new Patient();
	Chunk c = new Chunk();

	@Before
	public void setPatient() {
		p.setFirstName("Evangelos");
		p.setChunk(c);
		p.setPatientId(1L);
	}
	
	@Test
	public void testPatientId() {
		assertEquals(Long.valueOf(1L), Long.valueOf(p.getPatientId()));
	}
	
	@Test
	public void testFirstName() {
		assertEquals("Evangelos", p.getFirstName());
	}
}
