package de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperExporterImpl;

public class TCFExporter extends PepperExporterImpl implements PepperExporter{
	public TCFExporter(){
		super();
		this.setName("TCFExporter");
		this.setVersion("0.0.1");
		this.addSupportedFormat("TCF", "0.4", null);
		this.setProperties(new TCFExporterProperties());
	}
}
