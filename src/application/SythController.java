package application;

import java.net.URL;
import java.util.ResourceBundle;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.scope.AudioScope;
import com.jsyn.unitgen.EnvelopeDAHDSR;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.TunableFilter;
import com.jsyn.unitgen.UnitOscillator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

public class SythController implements Initializable {

	@FXML AnchorPane ap = new AnchorPane();
	@FXML Slider freqSlider = new Slider();
	@FXML Slider filterFreq = new Slider();
	@FXML Button start = new Button();
	@FXML Button stop = new Button();
	@FXML Label oscLabel = new Label();
	@FXML Label filterLabel = new Label();
	@FXML Slider leftOutSlider = new Slider(0.0, 1.0, 0.5);
	@FXML Label leftAmpOutLabel = new Label();
	@FXML Slider rightOutSlider = new Slider(0.0, 1.0, 0.5);
	@FXML Label rightAmpOutLabel = new Label();
	@FXML ChoiceBox<String> cb = new ChoiceBox<String>();
	@FXML ChoiceBox<String> filterBox = new ChoiceBox<String>();
	
	@FXML Button c = new Button();
	@FXML Button b = new Button();
	@FXML Button bFlat = new Button();
	@FXML Button a = new Button();
	@FXML Button aFlat = new Button();
	@FXML Button g = new Button();
	@FXML Button gFlat = new Button();
	@FXML Button f = new Button();
	@FXML Button e = new Button();
	@FXML Button eFlat = new Button();
	@FXML Button d = new Button();
	@FXML Button dFlat = new Button();
	@FXML Button C = new Button();
	@FXML Button B = new Button();
	@FXML Button BFlat = new Button();
	@FXML Button A = new Button();
	
	double[] freqs = {261.626, 246.942, 233.082, 220.000, 207.652, 195.998, 184.997, 
						174.614, 164.814, 155.563, 146.832, 138.591, 130.813, 123.471,
						116.541, 110.000};
	String[] oscChoices = {"Sine", "Square", "Saw", "Triangle"};
	String[] filterChoices = {"LowPass", "HighPass", "BandPass", "StateVariable"};
	double freq = 0.0;
	double amp = 0.2;
	
	Synthesizer synth = JSyn.createSynthesizer();
	AudioScope scope = null;
	CoreOscillator core = new CoreOscillator();
	CoreFilter coreFilter = new CoreFilter();
	EnvelopeDAHDSR adsr = new EnvelopeDAHDSR();
	UnitOscillator osc;
	TunableFilter filter;
	LineOut out = new LineOut();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		keyboardSetup();
		osc = core.setMainOsc("sine");
		filter = coreFilter.setMainFilter("LowPass");
		cb.getItems().addAll(oscChoices);
		cb.getSelectionModel().selectFirst();
		cb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() { 
            public void changed(ObservableValue ov, Number value, Number nv) { 
            	osc.stop();
            	osc.flattenOutputs();
            	osc.amplitude.set(0.0);
            	out.stop();
            	synth.stop();
            	synth.remove(osc);
            	osc = null;
                osc = core.setMainOsc(oscChoices[nv.intValue()].toLowerCase()); 
                synth.add(osc);
                changeOsc();
            }
        });
		filterBox.getItems().addAll(filterChoices);
		filterBox.getSelectionModel().selectFirst();
		filterBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue ov, Number value, Number nv) {
				osc.stop();
				osc.amplitude.set(0.0);
				osc.flattenOutputs();
				out.stop();
				synth.stop();
				filter.stop();
				synth.remove(filter);
				filter = null;
				filter = coreFilter.setMainFilter(filterChoices[nv.intValue()]);
				changeFilter();
			}
		});
		rightOutSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> obv, Number oldValue, Number newValue) {
				double newVal = newValue.doubleValue();
				Double val = newVal * newVal * newVal;
				osc.amplitude.set(val);
				amp = val;
				rightAmpOutLabel.setText(String.format("%.2f%n", val.doubleValue()) + " dB");
			}
		});
		rightOutSlider.setValue(0.5);
		leftOutSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> obv, Number oldValue, Number newValue) {
				double newVal = newValue.doubleValue();
				Double val = newVal * newVal * newVal;
				amp = val;
				osc.amplitude.set(val);
				leftAmpOutLabel.setText(String.format("%.2f%n", val.doubleValue()) + " dB");
			}
		});
		leftOutSlider.setValue(0.5);
		freqSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> obv, Number oldValue, Number newValue) {
				Double val = Math.pow(10, newValue.doubleValue());
				osc.frequency.set(val);
				oscLabel.setText(String.valueOf(val.intValue()) + " Hz");
			}
		});
		filterFreq.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> obv, Number oldValue, Number newValue) {
				Double val = Math.pow(10, newValue.doubleValue());
				filter.frequency.set(val);
				filterLabel.setText(String.valueOf(val.intValue()) + " Hz");
			}
		});
//		adsr.amplitude.set(amp);
//		adsr.attack.set(0.2);
//		adsr.decay.set(0.5);
//		adsr.sustain.set(0.5);
//		adsr.release.set(0.1);

		osc.amplitude.set(0.07);
		osc.output.connect(adsr.input);
		adsr.output.connect(0, osc.amplitude, 0);
        filter.output.connect(0, out.input, 0);
        filter.output.connect(0, out.input, 1);
        synth.add(osc);
        synth.add(adsr);
        synth.add(filter);
		synth.add(out);
		scope = new AudioScope(synth);
		out.start();
	}
	
	public void startPressed(ActionEvent e) {
		synth.start();
		out.start();
	}
	
	public void stopPressed(ActionEvent e) {
		synth.stop();
	}
	
	public void cPressed(ActionEvent e) {
		freq = freqs[0];
		setFreq(freq);
	}
	
	public void bPressed(ActionEvent e) {
		freq = freqs[1];
		setFreq(freq);
	}
	
	public void bFlatPressed(ActionEvent e) {
		freq = freqs[2];
		setFreq(freq);
	}
	
	public void aPressed(ActionEvent e) {
		freq = freqs[3];
		setFreq(freq);
	}
	
	public void aFlatPressed(ActionEvent e) {
		freq = freqs[4];
		setFreq(freq);
	}
	
	public void gPressed(ActionEvent e) {
		freq = freqs[5];
		setFreq(freq);
	}
	
	public void gFlatPressed(ActionEvent e) {
		freq = freqs[6];
		setFreq(freq);
	}
	
	public void fPressed(ActionEvent e) {
		freq = freqs[7];
		setFreq(freq);
	}
	
	public void ePressed(ActionEvent e) {
		freq = freqs[8];
		setFreq(freq);
	}
	
	public void eFlatPressed(ActionEvent e) {
		freq = freqs[9];
		setFreq(freq);
	}
	
	public void dPressed(ActionEvent e) {
		freq = freqs[10];
		setFreq(freq);
	}
	
	public void dFlatPressed(ActionEvent e) {
		freq = freqs[11];
		setFreq(freq);
	}
	
	public void CPressed(ActionEvent e) {
		freq = freqs[12];
		setFreq(freq);
	}
	
	public void BPressed(ActionEvent e) {
		freq = freqs[13];
		setFreq(freq);
	}
	
	public void BFlatPressed(ActionEvent e) {
		freq = freqs[14];
		setFreq(freq);
	}
	
	public void APressed(ActionEvent e) {
		freq = freqs[15];
		setFreq(freq);
	}
	
	public double getFreq() {
		return this.freq;
	}
	
	private void setFreq(double input) {
		osc.noteOn(input, amp);
	}
	
	private void changeOsc() {
		osc.output.connect(0,filter.input, 0);
		osc.amplitude.set(amp);
        synth.add(osc);
		out.start();
		synth.start();
	}
	
	private void changeFilter() {
		synth.add(filter);
		osc.amplitude.set(amp);
		filter.output.connect(0, out.input, 0);
		filter.output.connect(0, out.input, 1);
		out.start();
		synth.start();
	}
	
	public void releaseNote() {
		osc.noteOff();
	}
	
	private void keyboardSetup() {
		ap.setOnKeyPressed(keyEvent -> {
			if (keyEvent.getCode() == KeyCode.A) {
				APressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.W) {
				BFlatPressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.S) {
				BPressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.D) {
				CPressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.R) {
				dFlatPressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.F) {
				dPressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.T) {
				eFlatPressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.G) {
				ePressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.H) {
				fPressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.U) {
				gFlatPressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.J) {
				gPressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.I) {
				aFlatPressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.K) {
				aPressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.O) {
				bFlatPressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.L) {
				bPressed(new ActionEvent());
				keyEvent.consume();
			} else if (keyEvent.getCode() == KeyCode.SEMICOLON) {
				cPressed(new ActionEvent());
				keyEvent.consume();
			}
		});
	}

}
