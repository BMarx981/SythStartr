package application;

import com.jsyn.unitgen.FilterBandPass;
import com.jsyn.unitgen.FilterHighPass;
import com.jsyn.unitgen.FilterLowPass;
import com.jsyn.unitgen.FilterStateVariable;
import com.jsyn.unitgen.TunableFilter;
import com.jsyn.unitgen.UnitFilter;

public class CoreFilter extends TunableFilter {
	
	FilterHighPass hp = new FilterHighPass();
	FilterLowPass lp = new FilterLowPass();
	FilterStateVariable sv = new FilterStateVariable();
	FilterBandPass bp = new FilterBandPass();
	
	UnitFilter mainFilter;
	String name = "";
	public double frequency = 0.0;

	@Override
	public void generate(int start, int limit) {
		// TODO Auto-generated method stub

	}
	
	public CoreFilter() {
		mainFilter = lp;
		name = "LowPass";
	}
	
	public TunableFilter setMainFilter(String filterName) {
		mainFilter = null;
		TunableFilter filter;
		switch (filterName.toLowerCase()) {
		case "lowpass": filter = lp; mainFilter = lp; break;
		case "highpass": filter = hp; mainFilter = hp; break;
		case "bandpass": filter = bp; mainFilter = bp; break;
		case "statevariable": filter = sv; mainFilter = sv; break;
		default: filter = lp; mainFilter = lp; break;
		}
		return filter;
	}

}
