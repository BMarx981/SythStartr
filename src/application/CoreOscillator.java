package application;

import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SquareOscillator;
import com.jsyn.unitgen.TriangleOscillator;
import com.jsyn.unitgen.UnitOscillator;

public class CoreOscillator extends UnitOscillator {

	SineOscillator sine = new SineOscillator();
	SawtoothOscillator saw = new SawtoothOscillator();
	SquareOscillator square = new SquareOscillator();
	TriangleOscillator tri = new TriangleOscillator();

	UnitOscillator mainOsc;
	
	private String name;
	
	public CoreOscillator() {
		mainOsc = sine;
		name = "Sine";
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public UnitOscillator setMainOsc(String oscName) {
		mainOsc = null;
		UnitOscillator osc;
		switch (oscName) {
		case "sine": mainOsc = sine;
			osc = sine;
			break;
		case "square": mainOsc = square;
			osc = square;
			break;
		case "saw": mainOsc = saw;
			osc = saw;
			break;
		case "triangle": mainOsc = tri;
			osc = tri;
			break;
		default: mainOsc = sine;
			osc = sine;
			break;
		}
		return osc;
	}

	@Override
	public void generate(int start, int limit) {
		// TODO Auto-generated method stub
		
	}
	
}
